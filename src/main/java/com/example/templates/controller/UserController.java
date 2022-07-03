package com.example.templates.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import javax.validation.Valid;

import com.example.templates.configuration.FileUploadUtil;
import com.example.templates.model.ChatMessage;
import com.example.templates.model.ChatWrapper;
import com.example.templates.model.Game;
import com.example.templates.model.GameWrapper;
import com.example.templates.model.Lot;
import com.example.templates.model.LotWrapper;
import com.example.templates.model.LotsWrapper;
import com.example.templates.model.StringAndListWrapper;
import com.example.templates.model.User;
import com.example.templates.service.CategoryService;
import com.example.templates.service.ChatMessageService;
import com.example.templates.service.GameService;
import com.example.templates.service.HomeService;
import com.example.templates.service.LotService;
import com.example.templates.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/user")
public class UserController {

  @Autowired
  private LotService lotService;
  @Autowired
  ChatMessageService chatMessageService;
  @Autowired
  private HomeService homeService;
  @Autowired
  private GameService gameService;
  @Autowired
  private UserService userService;
  @Autowired
  private CategoryService categoryService;

  private static final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd.MM");

  @GetMapping(value = "/create/lot")
  public ModelAndView addLot(@RequestParam(value = "id", required = false) Integer id) {
    ModelAndView modelAndView = new ModelAndView();
    User user = userService.getUser();
    if (user != null) {
      Lot lot = new Lot();
      List<StringAndListWrapper> templates = categoryService.createTemplates(id);
      LotWrapper lotWrapper = new LotWrapper(lot, categoryService.findById(id).getName(), templates);
      modelAndView.addObject("category_id", id);
      modelAndView.addObject("lot", lotWrapper);
      homeService.checkAuth(modelAndView);
      modelAndView.setViewName("lot");
    } else {
      modelAndView.setViewName("login");
    }
    return modelAndView;
  }

  @GetMapping(value = "/create/subLot")
  public ModelAndView addPreLot() {
    ModelAndView modelAndView = new ModelAndView();
    List<Game> games = gameService.getAllGames();
    List<GameWrapper> categories = new ArrayList<>();
    games.forEach(g -> {
      categoryService.findByGameId(g.getId()).forEach(c -> categories.add(new GameWrapper(String.valueOf(g.getId()),c)));
    });
    modelAndView.addObject("categories",categories);
    modelAndView.addObject("games",games);
    homeService.checkAuth(modelAndView);
    modelAndView.setViewName("lot");
    return modelAndView;
  }
  @GetMapping(value = "/rules/site")
  public ModelAndView rulesSite() {
    ModelAndView modelAndView = new ModelAndView();
    modelAndView.setViewName("/rules");
    modelAndView.addObject("rule", "site");
    homeService.checkAuth(modelAndView);
    return modelAndView;
  }

  @GetMapping(value = "/rules/buy")
  public ModelAndView rulesBuy() {
    ModelAndView modelAndView = new ModelAndView();
    modelAndView.setViewName("/rules");
    modelAndView.addObject("rule", "buy");
    homeService.checkAuth(modelAndView);
    return modelAndView;
  }

  @GetMapping(value = "/rules/sell")
  public ModelAndView rulesSell() {
    ModelAndView modelAndView = new ModelAndView();
    modelAndView.setViewName("/rules");
    modelAndView.addObject("rule", "sell");
    homeService.checkAuth(modelAndView);
    return modelAndView;
  }

  @PostMapping(value = "/search")
  public ModelAndView searchGame(@RequestParam(value = "name", required = false) String name) {
    ModelAndView modelAndView = new ModelAndView();
    if (name != null) {
      homeService.fillGames(modelAndView, name);
    }
    modelAndView.setViewName("main");
    homeService.checkAuth(modelAndView);
    return modelAndView;
  }

  @PostMapping(value = "/update/password")
  public ModelAndView updatePassword(@RequestParam(value = "password", required = false) String password) {
    ModelAndView modelAndView = new ModelAndView();
    User user = userService.getUser();
    user.setPassword(password);
    userService.saveUser(user, "USER");
    modelAndView.addObject("successMessage", "Пароль обновлен");
    modelAndView.setViewName("home");
    homeService.checkAuth(modelAndView);
    return modelAndView;
  }
  @PostMapping(value = "/update/logo")
  public ModelAndView updateLogo(@Valid MultipartFile image) throws IOException {
    ModelAndView modelAndView = new ModelAndView();
    User user = userService.getUser();
    String fileName = StringUtils.cleanPath(Objects.requireNonNull(image.getOriginalFilename()));
    user.setLogo(fileName);
    user.setLogo(user.getLogo());
    String uploadDir = "user_logos/" + user.getId();
    FileUploadUtil.saveFile(uploadDir, fileName, image);
    userService.saveUser(user, "USER");
    modelAndView.addObject("successMessage", "Фото профиля обновлено");
    modelAndView.setViewName("home");
    homeService.checkAuth(modelAndView);
    return modelAndView;
  }
  @PostMapping(value = "/update/email")
  public ModelAndView updateEmail(@RequestParam(value = "email", required = false) String email) {
    ModelAndView modelAndView = new ModelAndView();
    if (userService.findUserByEmail(email) == null) {
      User user = userService.getUser();
      user.setEmail(email);
      userService.saveUser(user, "USER");
      modelAndView.addObject("successMessage", "Почта обновлена");
      modelAndView.addObject("email", email);
    } else {
      modelAndView.addObject("successMessage", "Почта занята");
    }
    modelAndView.setViewName("home");
    homeService.checkAuth(modelAndView);
    return modelAndView;
  }

  @GetMapping(value = "/goHome")
  public ModelAndView goHome() {
    ModelAndView modelAndView = new ModelAndView();
    homeService.fillGames(modelAndView, true);
    homeService.checkAuth(modelAndView);
    User user = userService.getUser();
    modelAndView.addObject("email", user.getEmail());
    modelAndView.addObject("password", user.getPassword());
    modelAndView.setViewName("home");
    return modelAndView;
  }

  @PostMapping(value = "/create/lot")
  public ModelAndView addLot(@Valid String cost,
                             @Valid String templates,
                             @Valid String subTemplates,
                             @Valid Integer category_id) {
    User user = userService.getUser();
    Lot lot = new Lot();
    lot.setCost(cost);
    lot.setSeller_id(user.getId());
    lot.setCategory_id(category_id);
    if (templates != null) {
      lot.setTemplates(List.of(templates.split(",")));
    }
    if (subTemplates != null) {
      lot.setSubTemplates(List.of(subTemplates.split(",")));
    }
    lot.setStatus("Продажа");
    lotService.saveLot(lot);
    ModelAndView modelAndView = getMyLots();
    homeService.checkAuth(modelAndView);
    modelAndView.setViewName("myLots");
    return modelAndView;
  }

  @PostMapping(value = "/edit/lot")
  public ModelAndView addLot(@Valid Lot lot) {
    User user = userService.getUser();
    if (Objects.equals(lotService.getById(lot.getId()).getSeller_id(), user.getId())) {
      lot.setStatus("Продажа");
      lotService.saveLot(lot);
    }
    ModelAndView modelAndView = getMyLots();
    modelAndView.setViewName("myLots");
    homeService.checkAuth(modelAndView);
    return modelAndView;
  }

  @GetMapping(value = "/delete/lot")
  public ModelAndView deleteLot(@Valid Integer id) {
    User user = userService.getUser();
    if (Objects.equals(lotService.getById(id).getSeller_id(), user.getId())) {
      lotService.deleteById(id);
    }
    ModelAndView modelAndView = getMyLots();
    modelAndView.setViewName("myLots");
    homeService.checkAuth(modelAndView);
    return modelAndView;
  }

  @GetMapping(value = "/watch/chats")
  public ModelAndView watchChats() {
    ModelAndView modelAndView = new ModelAndView();
    homeService.checkAuth(modelAndView);
    List<ChatMessage> messages = chatMessageService.findByUser(userService.getUser().getId());
    if (messages.size() > 0) {

    List<ChatWrapper> chats = new ArrayList<>();
    List<String> chatsNames = new ArrayList<>();
    List<String> finalChatsNames = chatsNames;
    messages.forEach(m -> {
      finalChatsNames.add(m.getChat_id());
    });
    chatsNames = finalChatsNames.stream().distinct().collect(Collectors.toList());
    chatsNames.forEach(n -> {
      String name = "person";
      Integer user;
      List<ChatMessage> sub = messages.stream().filter(m -> m.getChat_id().equals(n)).collect(Collectors.toList());
      ChatMessage last = sub.get(sub.size() -1);
      if (last.getSender_id().equals(userService.getUser().getId())) {
        user = last.getReceiver_id();
      } else {
        user = last.getSender_id();
      }
      chats.add(new ChatWrapper(sub,
          name + chats.size(),
          userService.findById(user).getPhotosImagePath(),
          last.getContent().substring(0,Math.min(last.getContent().length(),30)),
          userService.findById(user).getUserName(),
          last.getTime(),
          last.getId(),
          last.isChecked()));
    });
    Collections.sort(chats);
    Collections.reverse(chats);
    modelAndView.addObject("chats", chats);
    }
    modelAndView.addObject("me", userService.getUser().getId());
    modelAndView.setViewName("chat");
    return modelAndView;
  }
  @PostMapping(value = "/watch/selectedChats")
  public ModelAndView watchSChats(@Valid String chatName) {
    ModelAndView modelAndView = new ModelAndView();
    homeService.checkAuth(modelAndView);
    List<ChatMessage> messages = chatMessageService.findByUser(userService.getUser().getId());
    List<ChatWrapper> chats = new ArrayList<>();
    List<String> chatsNames = new ArrayList<>();
    List<String> finalChatsNames = chatsNames;
    if (messages.size() > 0) {
    messages.forEach(m -> {
      finalChatsNames.add(m.getChat_id());
    });
    chatsNames = finalChatsNames.stream().distinct().collect(Collectors.toList());
    chatsNames.forEach(n -> {
      String name = "person";
      Integer user;
      List<ChatMessage> sub = messages.stream().filter(m -> m.getChat_id().equals(n)).collect(Collectors.toList());
      ChatMessage last = sub.get(sub.size() -1);
      if (last.getSender_id().equals(userService.getUser().getId())) {
        user = last.getReceiver_id();
      } else {
        user = last.getSender_id();
      }
      chats.add(new ChatWrapper(sub,
          name + chats.size(),
          userService.findById(user).getPhotosImagePath(),
          last.getContent().substring(0,Math.min(last.getContent().length(),30)),
          userService.findById(user).getUserName(),
          last.getTime(),
          last.getId(),
          last.isChecked()));
    });
    List<ChatWrapper> chatsSelected = chats.stream().filter(c -> c.getUser().contains(chatName)).collect(Collectors.toList());
    if (chatsSelected.size() > 0) {
      Collections.sort(chatsSelected);
      Collections.reverse(chatsSelected);
      modelAndView.addObject("chats", chatsSelected);
    }
    }
    modelAndView.addObject("me", userService.getUser().getId());
    modelAndView.setViewName("chat");
    return modelAndView;
  }
  @GetMapping(value = "/help")
  public ModelAndView help() {
    ModelAndView modelAndView = new ModelAndView();
    homeService.checkAuth(modelAndView);
    List<ChatMessage> messages = chatMessageService.findByUser(userService.getUser().getId());
    if (!messages.stream().anyMatch(m -> m.getSender_id().equals(364))) {
      ChatMessage chatMessage = new ChatMessage();
      Date date = new Date();
      chatMessage.setSender("Поддержка");
      chatMessage.setSender_id(364);
      chatMessage.setReceiver(userService.getUser().getUserName());
      chatMessage.setReceiver_id(userService.getUser().getId());
      chatMessage.setContent("Здравствуйте, опишите вашу проблему");
      chatMessage.setTime(sdf.format(new Timestamp(date.getTime())));
      chatMessage.setSender_id(userService.findUserByUserName(chatMessage.getSender()).getId());
      chatMessage.setReceiver_id(userService.findUserByUserName(chatMessage.getReceiver()).getId());
      chatMessage.setChat_id("{"+Math.min(chatMessage.getSender_id(),chatMessage.getReceiver_id())+"} {"+Math.max(chatMessage.getSender_id(),chatMessage.getReceiver_id())+"}");
      chatMessageService.save(chatMessage);
      messages.add(chatMessage);
    }

    List<ChatWrapper> chats = new ArrayList<>();
    List<String> chatsNames = new ArrayList<>();
    List<String> finalChatsNames = chatsNames;
    messages.forEach(m -> {
      finalChatsNames.add(m.getChat_id());
    });
    chatsNames = finalChatsNames.stream().distinct().collect(Collectors.toList());
    int index = chatsNames.indexOf("{"+userService.getUser().getId()+"} {364}");
    if (index == -1) {
      index = chatsNames.indexOf( "{364} {"+userService.getUser().getId()+"}");
    }
    if (chatsNames.size() > 1)
      Collections.swap(chatsNames, 0, index);
    chatsNames.forEach(n -> {
      String name = "person";
      Integer user;
      List<ChatMessage> sub = messages.stream().filter(m -> m.getChat_id().equals(n)).collect(Collectors.toList());
      ChatMessage last = sub.get(sub.size() -1);
      if (last.getSender_id().equals(userService.getUser().getId())) {
        user = last.getReceiver_id();
      } else {
        user = last.getSender_id();
      }
      chats.add(new ChatWrapper(sub,
          name + chats.size(),
          userService.findById(user).getPhotosImagePath(),
          last.getContent().substring(0,Math.min(last.getContent().length(),30)),
          userService.findById(user).getUserName(),
          last.getTime(),
          last.getId()));
    });

    modelAndView.addObject("chats", chats);
    modelAndView.addObject("me", userService.getUser().getId());
    modelAndView.setViewName("chat");
    return modelAndView;
  }
  @GetMapping(value = "/watch/lots")
  public ModelAndView watchLots(@RequestParam(value = "id", required = false) Integer id) {
    ModelAndView modelAndView = new ModelAndView();
    List<Lot> lots = lotService.getByCategoryId(id);
    if (lots.size() > 0) {
      List<StringAndListWrapper> templates = categoryService.createTemplates(id);
      List<LotsWrapper> lotss = new ArrayList<>();
      lots.forEach(l -> {
        Map<String, String> map = new HashMap<>();
        int a = 0;
        int b = 0;
        for (int i = 0; i < templates.size(); i++) {
          if (templates.get(i).getArrString() != null) {
            map.put(templates.get(i).string, l.getSubTemplates().get(b));
            b++;
          } else {
            map.put(templates.get(i).string, l.getTemplates().get(a));
            a++;
          }
        }
        try {
          lotss.add(new LotsWrapper(l.getId(),
              l.getCost(),
              userService.findById(l.getSeller_id()).getUserName(),
              new ArrayList<>(map.keySet()),
              new ArrayList<>(map.values())));
        } catch (Exception e) {
        }
      });
      if (lotss.stream().anyMatch(l -> l.getSeller() != null)) {
        modelAndView.addObject("lots", lotss);
        modelAndView.addObject("category", categoryService.findById(id));
        modelAndView.addObject("categories", categoryService.findByGameId(categoryService.findById(id).getGame_id()));
      } else {
        modelAndView.addObject("lots", null);
        modelAndView.addObject("category", categoryService.findById(id));
        modelAndView.addObject("categories", categoryService.findByGameId(categoryService.findById(id).getGame_id()));
      }
    } else {
      modelAndView.addObject("lots", null);
      modelAndView.addObject("category", categoryService.findById(id));
      modelAndView.addObject("categories", categoryService.findByGameId(categoryService.findById(id).getGame_id()));
    }
    modelAndView.setViewName("mainLots");
    homeService.checkAuth(modelAndView);
    return modelAndView;
  }

  @GetMapping(value = "/watch/myLots")
  public ModelAndView watchMyLots() {
    ModelAndView modelAndView = getMyLots();
    modelAndView.setViewName("myLots");
    return modelAndView;
  }


  @GetMapping(value = "/profile")
  public ModelAndView watchProfile(@RequestParam(value = "id", required = false) Integer id, @RequestParam(value = "name", required = false) String name) {
    ModelAndView modelAndView = new ModelAndView();
    if (id != null) {
      modelAndView = getUserLots(id);
      modelAndView.addObject("userObject", userService.findById(id));
    } else if (name != null) {
      modelAndView = getUserLots(userService.findUserByUserName(name).getId());
      modelAndView.addObject("userObject", userService.findUserByUserName(name));
    }
    if (userService.getUser() != null) {
      List<ChatMessage> messages = chatMessageService.findByUsers(userService.getUser().getId(), id);
      modelAndView.addObject("messages", messages);
    }
    modelAndView.setViewName("profile");
    homeService.checkAuth(modelAndView);
    return modelAndView;
  }


  @GetMapping(value = "/watch/currentLot")
  public ModelAndView watchCurrentLot(@RequestParam(value = "id", required = false) Integer id) {
    return
        getLot(id);
  }

  public ModelAndView getMyLots() {
    User user = userService.getUser();
    ModelAndView modelAndView = new ModelAndView();
    List<Lot> lots = lotService.getBySeller_id(user.getId());
    List<LotsWrapper> lotss = new ArrayList<>();
    lots.forEach(l -> {
      List<StringAndListWrapper> templates = categoryService.createTemplates(l.getCategory_id());
      Map<String, String> map = new HashMap<>();
      int a = 0;
      int b = 0;
      map.put("Цена", l.getCost());
      for (int i = 0; i < templates.size(); i++) {
        if (templates.get(i).getArrString() != null) {
          map.put(templates.get(i).string, l.getSubTemplates().get(b));
          b++;
        } else {
          map.put(templates.get(i).string, l.getTemplates().get(a));
          a++;
        }
      }
      lotss.add(new LotsWrapper(l.getId(), gameService.findById(categoryService.findById(l.getCategory_id()).getGame_id()).getName(),
          categoryService.findById(l.getCategory_id()).getName(),
          new ArrayList<String>(map.keySet()),
          new ArrayList<String>(map.values())));

      /*      lotWrapers.add(new LotWrapper(l,templates));*/
    });
    modelAndView.addObject("lots", lotss);
    homeService.checkAuth(modelAndView);
    return modelAndView;
  }

  public ModelAndView getUserLots(Integer id) {
    User user = userService.findById(id);
    ModelAndView modelAndView = new ModelAndView();
    List<Lot> lots = lotService.getBySeller_id(user.getId());
    List<LotsWrapper> lotss = new ArrayList<>();
    lots.forEach(l -> {
      List<StringAndListWrapper> templates = categoryService.createTemplates(l.getCategory_id());
      Map<String, String> map = new HashMap<>();
      int a = 0;
      int b = 0;
      map.put("Цена", l.getCost());
      for (int i = 0; i < templates.size(); i++) {
        if (templates.get(i).getArrString() != null) {
          map.put(templates.get(i).string, l.getSubTemplates().get(b));
          b++;
        } else {
          map.put(templates.get(i).string, l.getTemplates().get(a));
          a++;
        }
      }
      lotss.add(new LotsWrapper(l.getId(), gameService.findById(categoryService.findById(l.getCategory_id()).getGame_id()).getName(),
          categoryService.findById(l.getCategory_id()).getName(),
          new ArrayList<String>(map.keySet()),
          new ArrayList<String>(map.values())));

      /*      lotWrapers.add(new LotWrapper(l,templates));*/
    });
    modelAndView.addObject("lots", lotss);
    homeService.checkAuth(modelAndView);
    return modelAndView;
  }

  public ModelAndView getLot(Integer id) {
    ModelAndView modelAndView = new ModelAndView();
    Lot l = lotService.getById(id);
    LotsWrapper lot;
    List<StringAndListWrapper> templates = categoryService.createTemplates(l.getCategory_id());
    Map<String, String> map = new HashMap<>();
    int a = 0;
    int b = 0;
    map.put("Цена", l.getCost());
    for (int i = 0; i < templates.size(); i++) {
      if (templates.get(i).getArrString() != null) {
        map.put(templates.get(i).string, l.getSubTemplates().get(b));
        b++;
      } else {
        map.put(templates.get(i).string, l.getTemplates().get(a));
        a++;
      }
    }
    lot = new LotsWrapper(l.getId(), gameService.findById(categoryService.findById(l.getCategory_id()).getGame_id()).getName(),
        categoryService.findById(l.getCategory_id()).getName(),
        new ArrayList<String>(map.keySet()),
        new ArrayList<String>(map.values()));
    modelAndView.addObject("lot", lot);
    List<ChatMessage> messages = chatMessageService.findByUsers(userService.getUser().getId(),l.getSeller_id());
    modelAndView.addObject("messages", messages);
    modelAndView.addObject("seller", userService.findById(l.getSeller_id()).getUserName());
    homeService.checkAuth(modelAndView);
    modelAndView.setViewName("watchLot");
    return modelAndView;
  }

  @GetMapping(value = "/edit/lot")
  public ModelAndView editLot(@Valid Integer id) {
    User user = userService.getUser();
    ModelAndView modelAndView = new ModelAndView();
    if (Objects.equals(lotService.getById(id).getSeller_id(), user.getId())) {
      Lot lot = lotService.getById(id);
      List<StringAndListWrapper> templates = categoryService.createTemplates(lot.getCategory_id());
      modelAndView.addObject("category_id", id);
      List<String> t = new ArrayList<>();
      int j = 0;
      for (int i = 0; i < templates.size(); i++) {
        if (templates.get(i).getArrString() == null) {
          t.add(lot.getTemplates().get(j));
          j++;
        } else {
          t.add(null);
        }
      }
      lot.setTemplates(t);
      LotWrapper lotWrapper = new LotWrapper(lot, "", templates);
      modelAndView.addObject("lot", lotWrapper);
      homeService.checkAuth(modelAndView);
      modelAndView.setViewName("editLot");
    } else {
      modelAndView.setViewName("login");
    }
    return modelAndView;
  }


}
