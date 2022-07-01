package com.example.templates.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.templates.model.Category;
import com.example.templates.model.Game;
import com.example.templates.model.GameWrapper;
import com.example.templates.model.Role;
import com.example.templates.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

@Service
public class HomeService {

  @Autowired
  private UserService userService;
  @Autowired
  private GameService gameService;
  @Autowired
  private CategoryService categoryService;

  public void fillGames(ModelAndView modelAndView, boolean forAdmin) {
    List<Game> games;
    if (forAdmin) {
      games = gameService.getAllGames();
    } else {
      List<Game> games1 = gameService.getAllDownTop();
      Collections.sort(games1);
      games = gameService.getAllTop();
      Collections.sort(games);
      games.addAll(games1);
    }
    List<GameWrapper> gameWrappers = new ArrayList<>();
    games.forEach(g -> g.setTagsArray(List.of(g.getTags().split(","))));
    games.forEach(g -> {
      List<Category> categories;
      categories = categoryService.findByGameId(g.getId());
      if (!forAdmin && categories.size() > 0) {
        gameWrappers.add( new GameWrapper(g,categories));
      } else if(forAdmin) {
        gameWrappers.add( new GameWrapper(g,categories));
      }
    });
    modelAndView.addObject("gameWrappers", gameWrappers);
  }
  public void fillGames(ModelAndView modelAndView, String name) {
    List<Game> games = gameService.getAllGames();
    List<GameWrapper> gameWrappers = new ArrayList<>();
    games.forEach(g -> g.setTagsArray(List.of(g.getTags().split(","))));
    if (name != null) {
      games = (games.stream().filter(g -> g.getTags().contains(name))).collect(Collectors.toList());
    }
    games.forEach(g -> {
      List<Category> categories;
      categories = categoryService.findByGameId(g.getId());
      if (categories.size() > 0) {
        gameWrappers.add( new GameWrapper(g,categories));
      }
    });
    modelAndView.addObject("gameWrappers", gameWrappers);
  }

  public ModelAndView goHome(String name) {
    ModelAndView modelAndView = new ModelAndView();
    User user = userService.getUser();

    Set<Role> roles = user.getRoles();
    modelAndView.addObject("userName", "Welcome " + user.getUserName() + " (" + user.getEmail() + ")");
    for (Role role : roles) {
      if (role.getRole().equals("ADMIN")) {
        modelAndView.addObject("admin", "true");
      }
    }
    List<Game> games = gameService.getAllGames();
    List<GameWrapper> gameWrappers = new ArrayList<>();
    games.forEach(g -> g.setTagsArray(List.of(g.getTags().split(","))));
    if (name != null) {
      games = (games.stream().filter(g -> g.getTags().contains(name))).collect(Collectors.toList());
    }
    games.forEach(g -> {
      gameWrappers.add( new GameWrapper(g,categoryService.findByGameId(g.getId())));
    });
    modelAndView.addObject("gameWrappers", gameWrappers);
    modelAndView.setViewName("home");
    return modelAndView;
  }

  public ModelAndView goHome() {
    ModelAndView modelAndView = new ModelAndView();
    User user = userService.getUser();
    configureHome(modelAndView,user);
    modelAndView.addObject("email", user.getEmail());
    modelAndView.addObject("password", user.getPassword());
    modelAndView.addObject("logo", user.getPhotosImagePath());
    checkAuth(modelAndView);
    return modelAndView;
  }

  public void checkAuth(ModelAndView modelAndView) {
    User user = userService.getUser();
    if (user != null) {
      String role = (user.getRoles().stream().findAny()).get().getRole();
      modelAndView.addObject("userLogo", user.getPhotosImagePath());
      modelAndView.addObject("user", role);
      modelAndView.addObject("id", user.getId());
      modelAndView.addObject("userName", user.getUserName());
    }
  }




  public void configureHome(ModelAndView modelAndView, User user) {
    Set<Role> roles = user.getRoles();
    modelAndView.addObject("userName", "Welcome " + user.getUserName() + " (" + user.getEmail() + ")");
    for (Role role : roles) {
      if (role.getRole().equals("ADMIN")) {
        modelAndView.addObject("admin", "true");
      }
    }
    List<Game> games = gameService.getAllGames();
    List<GameWrapper> gameWrappers = new ArrayList<>();
    games.forEach(g -> g.setTagsArray(List.of(g.getTags().split(","))));
    games.forEach(g -> {
      gameWrappers.add( new GameWrapper(g,categoryService.findByGameId(g.getId())));
    });
    modelAndView.addObject("gameWrappers", gameWrappers);
    modelAndView.setViewName("home");
  }
}
