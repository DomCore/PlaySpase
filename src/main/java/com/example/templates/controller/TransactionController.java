package com.example.templates.controller;

import java.util.Base64;
import java.util.Collections;
import java.util.List;

import java.io.IOException;
import javax.validation.Valid;

import com.example.templates.model.Category;
import com.example.templates.model.FileDB;
import com.example.templates.model.Game;
import com.example.templates.model.Lot;
import com.example.templates.model.LotWrapper;
import com.example.templates.model.StringAndListWrapper;
import com.example.templates.model.User;
import com.example.templates.service.CategoryService;
import com.example.templates.service.FileStorageService;
import com.example.templates.service.GameService;
import com.example.templates.service.HomeService;
import com.example.templates.service.LotService;
import com.example.templates.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
@Controller
@RequestMapping("/transaction")
public class TransactionController {

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

  @PostMapping
  public ModelAndView info(@RequestParam(value = "AMOUNT", required = false) String AMOUNT) {
    ModelAndView modelAndView = new ModelAndView();
    User user = userService.getUser();
    if (user != null) {
      user.setBalance(user.getBalance()+Integer.valueOf(AMOUNT));
      userService.saveUser(user);
      modelAndView.setViewName("main");
    } else {
      modelAndView.setViewName("login");
    }
    return modelAndView;
  }
  @PostMapping("/success")
  public ModelAndView success() {
    ModelAndView modelAndView = new ModelAndView();
    homeService.checkAuth(modelAndView);
    modelAndView.addObject("result","nice");
    modelAndView.setViewName("main");
    return modelAndView;
  }
  @PostMapping("/failure")
  public ModelAndView failure() {
    ModelAndView modelAndView = new ModelAndView();
    homeService.checkAuth(modelAndView);
    modelAndView.addObject("result","bad");
    modelAndView.setViewName("main");
    return modelAndView;
  }
}
