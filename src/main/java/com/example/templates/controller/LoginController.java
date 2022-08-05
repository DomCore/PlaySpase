package com.example.templates.controller;


import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import com.example.templates.EMail;
import com.example.templates.model.ActiveUserStore;
import com.example.templates.model.User;
import com.example.templates.service.GameService;
import com.example.templates.service.HomeService;
import com.example.templates.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

@Controller
public class LoginController {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd.MM");
    @Autowired
    private UserService userService;
    @Autowired
    private GameService gameService;
    @Autowired
    private HomeService homeService;
    @Autowired
    private EMail eMail;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping(value =  "/login")
    public ModelAndView login(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        return modelAndView;
    }
    @PostMapping(value =  "/fail_login")
    public ModelAndView failLogin(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("error",true);
        modelAndView.setViewName("login");
        return modelAndView;
    }
    @GetMapping(value =  "/reset")
    public ModelAndView repear(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("error",true);
        modelAndView.setViewName("repear");
        return modelAndView;
    }

    @PostMapping(value =  "/reset")
    public ModelAndView postRepear(@Valid String user_name){
        ModelAndView modelAndView = new ModelAndView();
        User user = userService.findUserByEmail(user_name);
        modelAndView.addObject("success","error");
        if (user == null) {
            modelAndView.addObject("success","cant");
        } else {
            String password = new Random().ints(10, 33, 122).mapToObj(i -> String.valueOf((char)i)).collect(Collectors.joining());
            user.setPassword(bCryptPasswordEncoder.encode(password));
            userService.saveUser(user);
            if (eMail.sendmail(user.getEmail(),
                "Ваш новый пароль: "+password+", рекомендуем войти в аккаунт и сменить пароль",
                "Восстановление пароля")) {
                modelAndView.addObject("success","success");
            }
        }
        modelAndView.setViewName("repear");
        return modelAndView;
    }

    @GetMapping(value="/")
    public ModelAndView main(){
        ModelAndView modelAndView = new ModelAndView();
        homeService.fillGames(modelAndView,false);
        homeService.checkAuth(modelAndView);
        modelAndView.setViewName("main");
        return modelAndView;
    }
    @GetMapping(value="/landing")
    public ModelAndView maind(){
        ModelAndView modelAndView = new ModelAndView();
        homeService.fillGames(modelAndView,false);
        homeService.checkAuth(modelAndView);
        modelAndView.setViewName("main");
        return modelAndView;
    }
    @GetMapping(value="/emailExists")
    public ModelAndView emailExist(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("emailExists");
        return modelAndView;
    }

    @GetMapping(value="/registration")
    public ModelAndView registration(@Valid String code){
        ModelAndView modelAndView = new ModelAndView();
        User user = new User();
        modelAndView.addObject("user", user);
        if (code != null)
        modelAndView.addObject("code", code);

        modelAndView.setViewName("registration");
        return modelAndView;
    }

    @PostMapping(value = "/registration")
    public ModelAndView createNewUser(@Valid User user,@Valid boolean check,@Valid String code, BindingResult bindingResult) {
        ModelAndView modelAndView = new ModelAndView();
        boolean usernameExists = userService.findUserByUserName(user.getUserName()) != null;
        boolean emailExists = userService.findUserByEmail(user.getEmail()) != null;
        if (usernameExists) {
            bindingResult
                    .rejectValue("userName", "error.user",
                            "Имя занято");
        }
        if (emailExists) {
            bindingResult
                .rejectValue("email", "error.user",
                    "Почта занята");
        }
        if (user.getPassword().length() < 1) {
            bindingResult
                .rejectValue("password", "error.user",
                    "Пароль слишком короткий");
        }
        if (!user.isCheck()) {
            bindingResult
                .rejectValue("check", "error.user",
                    "Примите правила сайта");
        }
        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("registration");
        } else {
            eMail.sendmail(user.getEmail(),
                "Благодарим за регистрацию в нашем сервисе",
                "Успешная регистрация");
            Date date = new Date();
            user.setDate(sdf.format(new Timestamp(date.getTime())));
            userService.saveUser(user, "USER");
            modelAndView.addObject("successMessage", "Аккаунт успешно создан");
            modelAndView.addObject("user", new User());
            if (code != null && code.length() > 0) {
                User ref = userService.findById(Integer.valueOf(code));
                ref.getReferals().add(String.valueOf(user.getId()));
                userService.saveUser(ref);
            }
            homeService.configureHome(modelAndView,user);
            modelAndView.addObject("success", true);
            modelAndView.setViewName("login");
        }
        return modelAndView;
    }

    @GetMapping(value="/home")
    public ModelAndView home(){
        return homeService.goHome();
    }

    @GetMapping(value = "/agent")
    public ModelAndView agent() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("agent");
        modelAndView.addObject("agent", true);
        return modelAndView;
    }
    @GetMapping(value = "/agreement")
    public ModelAndView agreement() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("agent");
        modelAndView.addObject("agreement", true);
        return modelAndView;
    }

}
