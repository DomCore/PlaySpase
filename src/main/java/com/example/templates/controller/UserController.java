package com.example.templates.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.Valid;

import com.example.templates.model.Category;
import com.example.templates.model.Game;
import com.example.templates.model.Lot;
import com.example.templates.model.LotWrapper;
import com.example.templates.model.LotsWrapper;
import com.example.templates.model.StringAndListWrapper;
import com.example.templates.model.User;
import com.example.templates.model.Wrapper;
import com.example.templates.service.CategoryService;
import com.example.templates.service.GameService;
import com.example.templates.service.HomeService;
import com.example.templates.service.LotService;
import com.example.templates.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/user")
public class UserController {

  @Autowired
  private LotService lotService;

  @Autowired
  private HomeService homeService;
  @Autowired
  private UserService userService;
  @Autowired
  private CategoryService categoryService;

  @GetMapping(value = "/create/lot")
  public ModelAndView addLot(@RequestParam(value = "id", required = false) Integer id) {
    ModelAndView modelAndView = new ModelAndView();
    Lot lot = new Lot();
    List<StringAndListWrapper> templates = categoryService.createTemplates(id);
    LotWrapper lotWrapper = new LotWrapper(lot,templates);
    modelAndView.addObject("category_id", id);
    modelAndView.addObject("lot", lotWrapper);
    modelAndView.setViewName("lot");
    return modelAndView;
  }
  @GetMapping(value = "/search")
  public ModelAndView searchGame(@RequestParam(value = "name", required = false) String name) {
    return homeService.goHome(name);
  }



  @PostMapping(value = "/create/lot")
  public ModelAndView addLot(@Valid String cost,
                             @Valid String templates,
                             @Valid String subTemplates,
                             @Valid Integer category_id) {
      List<StringAndListWrapper> templatesWrappers = categoryService.createTemplates(category_id);
      Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      User user = userService.findUserByUserName(auth.getName());
      Lot lot = new Lot();
      lot.setCost(cost);
      lot.setSeller_id(user.getId());
      lot.setCategory_id(category_id);
      lot.setTemplates(List.of(templates.split(",")));
      lot.setSubTemplates(List.of(subTemplates.split(",")));
      lot.setStatus("Продажа");
      lotService.saveLot(lot);
      return homeService.goHome();
  }
  @GetMapping(value = "/watch/lots")
  public ModelAndView watchLots(@RequestParam(value = "id", required = false) Integer id) {

    ModelAndView modelAndView = new ModelAndView();
    List<Lot> lots = lotService.getByCategoryId(id);
    List<StringAndListWrapper> templates = categoryService.createTemplates(id);
    List<LotWrapper> lotWrapers = new ArrayList<>();
    /*List<Map<String, String>> maps = new ArrayList<>();*/
    List<LotsWrapper> lotss = new ArrayList<>();
    lots.forEach(l -> {
      Map<String, String> map = new HashMap<>();
      int a = 0;
      int b = 0;
      map.put("id", l.getId().toString());
      for (int i = 0; i < templates.size(); i++) {
        if (templates.get(i).getArrString() != null) {
          map.put(templates.get(i).string, l.getSubTemplates().get(b));
          b++;
        } else {
          map.put(templates.get(i).string, l.getTemplates().get(a));
          a++;
        }
      }
      map.put("cost", l.getCost());
      lotss.add(new LotsWrapper(l.getId(),
          new ArrayList<String>(map.keySet()),
          new ArrayList<String>(map.values())));

/*      lotWrapers.add(new LotWrapper(l,templates));*/
    });
    modelAndView.addObject("lots", lotss);
    modelAndView.addObject("category", categoryService.findById(id).getName());
    modelAndView.setViewName("/watchLots");
    return modelAndView;
  }

}
