package com.example.templates.controller;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.validation.Valid;

import com.example.templates.model.Category;
import com.example.templates.model.Game;
import com.example.templates.model.StringAndListWrapper;
import com.example.templates.model.User;
import com.example.templates.service.CategoryService;
import com.example.templates.service.GameService;
import com.example.templates.service.HomeService;
import com.example.templates.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/admin")
public class AdminController {

  @Autowired
  private GameService gameService;

  @Autowired
  private HomeService homeService;
  @Autowired
  private UserService userService;
  @Autowired
  private CategoryService categoryService;

  @GetMapping(value="/create/game")
  public ModelAndView addGame(){
    ModelAndView modelAndView = new ModelAndView();
    Game game = new Game();
    modelAndView.addObject("game", game);
    modelAndView.setViewName("game");
    return modelAndView;
  }
  @GetMapping(value="/edit/game")
  public ModelAndView editGame(@Valid Integer id){
    ModelAndView modelAndView = new ModelAndView();
    Game game = gameService.findById(id);
    modelAndView.addObject("game", game);
    modelAndView.setViewName("game");
    return modelAndView;
  }

  @PostMapping(value="/create/game")
  public ModelAndView addGame(@Valid Integer id, @Valid Game game, BindingResult bindingResult){
    ModelAndView modelAndView = new ModelAndView();

    boolean gameNameExists = gameService.findByName(game.getName()) != null;
    if (gameNameExists & id == null) {
      bindingResult
          .rejectValue("name", "error.game",
              "Такая игра уже есть");
    }
    if (bindingResult.hasErrors()) {
      modelAndView.setViewName("game");
    } else {
      if (id != null) {
        gameService.editGame(game);
      } else {
        gameService.saveGame(game);
      }
      return homeService.goHome();
    }
    return modelAndView;
  }
  @GetMapping(value="/delete/game")
  public ModelAndView deleteGame(@Valid Integer id){
      gameService.deleteGameById(id);
      return homeService.goHome();
    }
  @GetMapping(value="/delete/category")
  public ModelAndView deleteCategory(@Valid Integer id){
    categoryService.deleteCategoryById(id);
    return homeService.goHome();
  }
  @GetMapping(value="/create/manager")
  public ModelAndView addManager(){
    ModelAndView modelAndView = new ModelAndView();
    User user = new User();
    modelAndView.addObject("user", user);
    modelAndView.addObject("admin", "admin");
    modelAndView.setViewName("createManager");
    return modelAndView;
  }
  @PostMapping(value="/create/manager")
  public ModelAndView addManager(@Valid User user, BindingResult bindingResult){
    ModelAndView modelAndView = new ModelAndView();
    boolean usernameExists = userService.findUserByUserName(user.getUserName()) != null;
    boolean emailExists = userService.findUserByEmail(user.getEmail()) != null;
    if (usernameExists) {
      bindingResult
          .rejectValue("userName", "error.user",
              "There is already a user registered with the user name provided");
    }
    if (emailExists) {
      bindingResult
          .rejectValue("email", "error.user",
              "There is already a user registered with the email provided");
    }
    if (user.getPassword().length() < 6) {
      bindingResult
          .rejectValue("password", "error.user",
              "Password too short");
    }

    if (bindingResult.hasErrors()) {
      modelAndView.setViewName("createManager");
    } else {
      userService.saveUser(user,"MANAGER");
      modelAndView.addObject("successMessage", "Аккаунт успешно создан");
      modelAndView.setViewName("createManager");
    }
    return modelAndView;
  }
  @GetMapping(value="/create/category")
  public ModelAndView addCategory(){
    ModelAndView modelAndView = new ModelAndView();
    Category category = new Category();
    List<Game> games = gameService.getAllGames();
    games.forEach(g -> g.setTagsArray(List.of(g.getTags().split(","))));
    modelAndView.addObject("games", games);
    modelAndView.addObject("category", category);
    modelAndView.setViewName("category");
    return modelAndView;
  }
  @GetMapping(value="/edit/category")
  public ModelAndView editCategory(@Valid Integer id){
    ModelAndView modelAndView = new ModelAndView();
    Category category = categoryService.findById(id);
    List<StringAndListWrapper> templates = categoryService.createTemplates(id);
    List<Game> games = gameService.getAllGames();
    games.forEach(g -> g.setTagsArray(List.of(g.getTags().split(","))));
    modelAndView.addObject("games", games);
    modelAndView.addObject("category", category);
    modelAndView.addObject("templates", templates);
    modelAndView.setViewName("editCategory");
    return modelAndView;
  }

  @PostMapping(value="/create/category")
  public ModelAndView addCategory(@Valid String templates, @Valid String subTemplates, @Valid Category category, BindingResult bindingResult){
    ModelAndView modelAndView = new ModelAndView();
    boolean categoryNameExists = categoryService.findByName(category.getName()) != null;
    if (categoryNameExists && category.getId() == null) {
      bindingResult
          .rejectValue("name", "error.category",
              "Такая категория уже есть");
    }
    if (category.getGame_id() < 1) {
      bindingResult
          .rejectValue("game_id", "error.category",
              "Выберите игру");
    }
    if (bindingResult.hasErrors()) {
      List<Game> games = gameService.getAllGames();
      games.forEach(g -> g.setTagsArray(List.of(g.getTags().split(","))));
      category.setTemplates("");
      category.setSubTemplates(Collections.singletonList(""));
      modelAndView.addObject("games", games);
      modelAndView.setViewName("category");
    } else {
      if (category.getId() != null) {
        Category c = category;
        categoryService.deleteCategoryById(category.getId());
        categoryService.saveCategory(c);
      } else {
        categoryService.saveCategory(category);
      }
      return homeService.goHome();
    }
    return modelAndView;
  }

}
