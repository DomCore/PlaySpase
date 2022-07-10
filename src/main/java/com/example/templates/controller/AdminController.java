package com.example.templates.controller;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

import java.io.File;
import java.io.IOException;
import javax.validation.Valid;

import com.example.templates.configuration.FileUploadUtil;
import com.example.templates.model.Category;
import com.example.templates.model.FileDB;
import com.example.templates.model.Game;
import com.example.templates.model.Lot;
import com.example.templates.model.Project;
import com.example.templates.model.StringAndListWrapper;
import com.example.templates.model.User;
import com.example.templates.service.CategoryService;
import com.example.templates.service.FileStorageService;
import com.example.templates.service.GameService;
import com.example.templates.service.HomeService;
import com.example.templates.service.LotService;
import com.example.templates.service.ProjectService;
import com.example.templates.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
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
  private LotService lotService;
  @Autowired
  private CategoryService categoryService;
  @Autowired
  private FileStorageService storageService;
  @Autowired
  private ProjectService projectService;

  @GetMapping(value = "/create/game")
  public ModelAndView addGame() {
    ModelAndView modelAndView = new ModelAndView();
    Game game = new Game();
    modelAndView.addObject("game", game);
    modelAndView.setViewName("game");
    homeService.checkAuth(modelAndView);
    return modelAndView;
  }

  @GetMapping(value = "/edit/game")
  public ModelAndView editGame(@Valid Integer id) {
    ModelAndView modelAndView = new ModelAndView();
    Game game = gameService.findById(id);
    modelAndView.addObject("game", game);
    modelAndView.setViewName("game");
    homeService.checkAuth(modelAndView);
    return modelAndView;
  }

  @PostMapping(value = "/create/game")
  public ModelAndView addGame(@Valid MultipartFile file, @Valid Integer id, @Valid Game game, BindingResult bindingResult) throws IOException {
    ModelAndView modelAndView = new ModelAndView();
    boolean gameNameExists = gameService.findByName(game.getName()) != null;
    if (gameNameExists & id == null) {
      bindingResult
          .rejectValue("name", "error.game",
              "Такая игра уже есть");
      modelAndView.setViewName("game");
      return modelAndView;
    }
    if (file != null) {

      game.setLogo(storageService.store(file).getId());
    }
    gameService.saveGame(game);
    homeService.checkAuth(modelAndView);
    return homeService.goHome();
  }

  @PostMapping(value = "/delete/game")
  public ModelAndView deleteGame(@Valid Integer id) {
    gameService.deleteGameById(id);
    return homeService.goHome();
  }

  @PostMapping(value = "/delete/category")
  public ModelAndView deleteCategory(@Valid Integer id) {
    categoryService.deleteCategoryById(id);
    return homeService.goHome();
  }

  @GetMapping(value = "/create/manager")
  public ModelAndView addManager() {
    ModelAndView modelAndView = new ModelAndView();
    User user = new User();
    modelAndView.addObject("userTemplate", user);
    modelAndView.addObject("admin", "admin");
    modelAndView.setViewName("createManager");
    homeService.checkAuth(modelAndView);
    return modelAndView;
  }

  @PostMapping(value = "/create/tax")
  public ModelAndView addTax(@Valid String tax) {
    ModelAndView modelAndView = new ModelAndView();

    Project project = projectService.findById(1);
    project.setTax(tax);
    projectService.saveProject(project);
    homeService.fillGames(modelAndView, true);
    modelAndView.addObject("admin", "admin");
    modelAndView.setViewName("home");
    homeService.checkAuth(modelAndView);
    return modelAndView;
  }

  @PostMapping(value = "/create/manager")
  public ModelAndView addManager(@Valid User userTemplate, BindingResult bindingResult) {
    ModelAndView modelAndView = new ModelAndView();
    boolean usernameExists = userService.findUserByUserName(userTemplate.getUserName()) != null;
    boolean emailExists = userService.findUserByEmail(userTemplate.getEmail()) != null;
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
    if (userTemplate.getPassword().length() < 6) {
      bindingResult
          .rejectValue("password", "error.user",
              "Password too short");
    }

    if (bindingResult.hasErrors()) {
      modelAndView.setViewName("createManager");
    } else {
      userService.saveUser(userTemplate, "MANAGER");
      modelAndView.addObject("successMessage", "Аккаунт успешно создан");
      modelAndView.setViewName("home");
      homeService.fillGames(modelAndView, true);
      homeService.checkAuth(modelAndView);
    }
    return modelAndView;
  }

  @GetMapping(value = "/create/category")
  public ModelAndView addCategory() {
    ModelAndView modelAndView = new ModelAndView();
    Category category = new Category();
    List<Game> games = gameService.getAllGames();
    games.forEach(g -> g.setTagsArray(List.of(g.getTags().split(","))));
    modelAndView.addObject("games", games);
    modelAndView.addObject("category", category);
    modelAndView.setViewName("category");
    homeService.checkAuth(modelAndView);
    return modelAndView;
  }

  @GetMapping(value = "/edit/category")
  public ModelAndView editCategory(@Valid Integer id) {
    ModelAndView modelAndView = new ModelAndView();
    Category category = categoryService.findById(id);
    List<StringAndListWrapper> templates = categoryService.createTemplates(id);
    List<Game> games = gameService.getAllGames();
    games.forEach(g -> g.setTagsArray(List.of(g.getTags().split(","))));
    modelAndView.addObject("games", games);
    modelAndView.addObject("category", category);
    modelAndView.addObject("templates", templates);
    homeService.checkAuth(modelAndView);
    modelAndView.setViewName("editCategory");
    return modelAndView;
  }

  @PostMapping(value = "/create/category")
  public ModelAndView addCategory(@Valid String templates, @Valid String subTemplates, @Valid Category category, BindingResult bindingResult) {
    ModelAndView modelAndView = new ModelAndView();
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
        if (!category.equals(categoryService.findById(category.getId()))) {
          categoryService.saveCategory(category);
          category = categoryService.findById(category.getId());
          Collections.reverse(category.getSubTemplates());
          categoryService.saveCategory(category);
          lotService.deleteByCategoryId(category.getId());
        }
      } else {
        categoryService.saveCategory(category);
      }
      homeService.checkAuth(modelAndView);
      homeService.fillGames(modelAndView, true);
      modelAndView.setViewName("home");
    }
    return modelAndView;
  }

}
