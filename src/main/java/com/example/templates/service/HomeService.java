package com.example.templates.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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


  public ModelAndView goHome(String name) {
    ModelAndView modelAndView = new ModelAndView();
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    User user = userService.findUserByUserName(auth.getName());

    if (user == null) {
      DefaultOidcUser oauthUser = (DefaultOidcUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      user = userService.findUserByUserName(oauthUser.getEmail());
    }

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
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    User user = userService.findUserByUserName(auth.getName());

    if (user == null) {
      DefaultOidcUser oauthUser = (DefaultOidcUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      user = userService.findUserByUserName(oauthUser.getEmail());
    }

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
    return modelAndView;
  }
}
