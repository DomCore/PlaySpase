package com.example.templates.service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import javax.servlet.http.HttpSession;
import javax.xml.bind.DatatypeConverter;

import com.example.templates.configuration.SessionCounter;
import com.example.templates.model.ActiveUserStore;
import com.example.templates.model.Category;
import com.example.templates.model.Feedback;
import com.example.templates.model.FileDB;
import com.example.templates.model.Game;
import com.example.templates.model.GameWrapper;
import com.example.templates.model.Role;
import com.example.templates.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
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
  @Autowired
  private FileStorageService storageService;
  @Autowired
  private LotService lotService;
  @Autowired
  private FeedbackService feedbackService;
  @Bean
  public ActiveUserStore activeUserStore(){
    return new ActiveUserStore();
  }
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
      try {
        FileDB file = storageService.getFile(g.getLogo());
        byte[] encodeBase64 = Base64.getEncoder().encode(file.getData());
        String base64Encoded = new String(encodeBase64, "UTF-8");
        g.setPath("data:image/jpeg;base64," + base64Encoded);
      } catch (Exception e) {

      }
      categories = categoryService.findByGameId(g.getId());
      if (!forAdmin && categories.size() > 0) {
        gameWrappers.add(new GameWrapper(g, categories));
      } else if (forAdmin) {
        gameWrappers.add(new GameWrapper(g, categories));
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
      try {
        FileDB file = storageService.getFile(g.getLogo());
        byte[] encodeBase64 = Base64.getEncoder().encode(file.getData());
        String base64Encoded = new String(encodeBase64, "UTF-8");
        g.setPath("data:image/jpeg;base64," + base64Encoded);
      } catch (Exception e) {

      }
      categories = categoryService.findByGameId(g.getId());
      if (categories.size() > 0) {
        gameWrappers.add(new GameWrapper(g, categories));
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
      gameWrappers.add(new GameWrapper(g, categoryService.findByGameId(g.getId())));
    });
    modelAndView.addObject("gameWrappers", gameWrappers);
    modelAndView.setViewName("home");
    return modelAndView;
  }

  public ModelAndView goHome() {
    ModelAndView modelAndView = new ModelAndView();
    User user = userService.getUser();
    configureHome(modelAndView, user);
    modelAndView.addObject("email", user.getEmail());
    modelAndView.addObject("password", user.getPassword());
    try {
      modelAndView.addObject("userlogo", userService.getPath(user));
    } catch (Exception e) {

    }
    checkAuth(modelAndView);
    return modelAndView;
  }

  public int getStars(Integer id) {
    List<Feedback> feedbacks = feedbackService.findByTargetId(id);
    if (feedbacks != null && feedbacks.size() > 0) {
      Integer stars = 0;
      for (int i = 0; i < feedbacks.size(); i++) {
        stars += feedbacks.get(i).getStars();
      }
      return stars / feedbacks.size();
    }
    return 0;
  }

  public int getFeedsCount(Integer id) {
    List<Feedback> feedbacks = feedbackService.findByTargetId(id);
    return feedbacks != null && feedbacks.size() > 0 ? feedbacks.size() : 0;
  }



  public void checkAuth(ModelAndView modelAndView) {
    User user = userService.getUser();
    if (user != null) {
      String role = (user.getRoles().stream().findAny()).get().getRole();
      try {
        modelAndView.addObject("userLogo", userService.getPath(user));
      } catch (Exception e) {

      }if (user.isBan()) {

      } else {
        modelAndView.addObject("user", role);
        modelAndView.addObject("id", user.getId());
        modelAndView.addObject("userName", user.getUserName());
        int charge = user.getBalance_charge();
        String balance;
        if (charge < 0) {
          balance = String.valueOf(user.getBalance() + user.getBalance_charge());
        } else if (charge > 0) {
          balance = user.getBalance() + " (" + user.getBalance_charge() + ")";
        } else {
          balance = String.valueOf(user.getBalance());
        }
        modelAndView.addObject("userBalance", balance);
        modelAndView.addObject("messagesCount", user.getMessages());
        modelAndView.addObject("sellsCount", lotService.getSells(user.getId()));
        modelAndView.addObject("buysCount", lotService.getBuys(user.getId()));
      }
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
      gameWrappers.add(new GameWrapper(g, categoryService.findByGameId(g.getId())));
    });
    modelAndView.addObject("gameWrappers", gameWrappers);
    modelAndView.setViewName("home");
  }
}
