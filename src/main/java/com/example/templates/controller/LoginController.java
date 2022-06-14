package com.example.templates.controller;


import com.example.templates.model.User;
import com.example.templates.service.GameService;
import com.example.templates.service.HomeService;
import com.example.templates.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;
    @Autowired
    private GameService gameService;
    @Autowired
    private HomeService homeService;

    @GetMapping(value={"/", "/login"})
    public ModelAndView login(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        return modelAndView;
    }
    @GetMapping(value="/emailExists")
    public ModelAndView emailExist(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("emailExists");
        return modelAndView;
    }

    @GetMapping(value="/registration")
    public ModelAndView registration(){
        ModelAndView modelAndView = new ModelAndView();
        User user = new User();
        modelAndView.addObject("user", user);
        modelAndView.setViewName("registration");
        return modelAndView;
    }

    @PostMapping(value = "/registration")
    public ModelAndView createNewUser(@Valid User user, BindingResult bindingResult) {
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
            modelAndView.setViewName("registration");
        } else {
            userService.saveUser(user, "USER");
            modelAndView.addObject("successMessage", "User has been registered successfully");
            modelAndView.addObject("user", new User());
            modelAndView.setViewName("registration");

        }
        return modelAndView;
    }

    @GetMapping(value="/home")
    public ModelAndView home(){
        return homeService.goHome();
    }


}
