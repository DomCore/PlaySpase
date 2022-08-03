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
import java.io.UnsupportedEncodingException;
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
import com.example.templates.model.RefHistory;
import com.example.templates.model.StringAndListWrapper;
import com.example.templates.model.User;
import com.example.templates.service.CategoryService;
import com.example.templates.service.ChatMessageService;
import com.example.templates.service.FileStorageService;
import com.example.templates.service.GameService;
import com.example.templates.service.HomeService;
import com.example.templates.service.LotService;
import com.example.templates.service.RefService;
import com.example.templates.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
  private FileStorageService storageService;
  @Autowired
  private LotService lotService;
  @Autowired
  ChatMessageService chatMessageService;
  @Autowired
  private HomeService homeService;
  @Autowired
  private RefService refService;
  @Autowired
  private GameService gameService;
  @Autowired
  private UserService userService;
  @Autowired
  private CategoryService categoryService;
  @Autowired
  private SimpMessagingTemplate template;
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
      modelAndView.addObject("category", categoryService.findById(id));
      modelAndView.addObject("tax", categoryService.findById(id).getTax());
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
      categoryService.findByGameId(g.getId()).forEach(c -> categories.add(new GameWrapper(String.valueOf(g.getId()), c)));
    });
    modelAndView.addObject("categories", categories);
    modelAndView.addObject("games", games);
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
                             @Valid double count,
                             @Valid String templates,
                             @Valid String subTemplates,
                             @Valid Integer category_id) {
    User user = userService.getUser();
    Lot lot = new Lot();
    lot.setCost(cost);
    lot.setCount(count);
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
    return new ModelAndView("redirect:/user/watch/myLots");
  }

  @PostMapping(value = "/edit/lot")
  public ModelAndView addLot(@Valid Lot lot) {
    User user = userService.getUser();
    if (Objects.equals(lotService.getById(lot.getId()).getSeller_id(), user.getId())) {
      lot.setStatus("Продажа");
      lotService.saveLot(lot);
    }
    return new ModelAndView("redirect:/user/watch/myLots");
  }

  @GetMapping(value = "/delete/lot")
  public ModelAndView deleteLot(@Valid Integer id) {
    User user = userService.getUser();
    if (Objects.equals(lotService.getById(id).getSeller_id(), user.getId())) {
      lotService.deleteById(id);
    }
    return new ModelAndView("redirect:/user/watch/myLots");
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
        ChatMessage last = sub.get(sub.size() - 1);
        if (last.getSender_id().equals(userService.getUser().getId())) {
          user = last.getReceiver_id();
        } else {
          user = last.getSender_id();
        }

        chats.add(new ChatWrapper(sub,
            name + chats.size(),
            userService.getPath(userService.findById(user)),
            last.getContent().substring(0, Math.min(last.getContent().length(), 30)),
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
    User user = userService.getUser();
    user.setMessages(0);
    userService.saveUser(user);
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
        ChatMessage last = sub.get(sub.size() - 1);
        if (last.getSender_id().equals(userService.getUser().getId())) {
          user = last.getReceiver_id();
        } else {
          user = last.getSender_id();
        }
        chats.add(new ChatWrapper(sub,
            name + chats.size(),
            userService.getPath(userService.findById(user)),
            last.getContent().substring(0, Math.min(last.getContent().length(), 30)),
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
      chatMessage.setChat_id("{" + Math.min(chatMessage.getSender_id(), chatMessage.getReceiver_id()) + "} {" + Math.max(chatMessage.getSender_id(), chatMessage.getReceiver_id()) + "}");
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
    int index = chatsNames.indexOf("{" + userService.getUser().getId() + "} {364}");
    if (index == -1) {
      index = chatsNames.indexOf("{364} {" + userService.getUser().getId() + "}");
    }
    if (chatsNames.size() > 1)
      Collections.swap(chatsNames, 0, index);
    chatsNames.forEach(n -> {
      String name = "person";
      Integer user;
      List<ChatMessage> sub = messages.stream().filter(m -> m.getChat_id().equals(n)).collect(Collectors.toList());
      ChatMessage last = sub.get(sub.size() - 1);
      if (last.getSender_id().equals(userService.getUser().getId())) {
        user = last.getReceiver_id();
      } else {
        user = last.getSender_id();
      }
      chats.add(new ChatWrapper(sub,
          name + chats.size(),
          userService.getPath(userService.findById(user)),
          last.getContent().substring(0, Math.min(last.getContent().length(), 30)),
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
  public ModelAndView watchLots(@RequestParam(value = "id", required = false) Integer id,
                                @RequestParam(value = "filters", required = false) List<String> filters,
                                @RequestParam(value = "value", required = false) String filterValue
                               ) {
    ModelAndView modelAndView = new ModelAndView();
    List<Lot> lots = lotService.getByCategoryId(id);
    lots = lots.stream().filter(l -> l.getStatus().equals("Продажа")).collect(Collectors.toList());
    if (lots.size() > 0) {
      List<StringAndListWrapper> templates = categoryService.createTemplates(id);
      List<StringAndListWrapper> templatesWithSub = templates.stream().filter(t -> t.getArrString() != null).collect(Collectors.toList());
      modelAndView.addObject("subs", templatesWithSub);
      Map<String, String> filtersMap = new HashMap<>();
      if (filters != null)
        filters.forEach(f -> {
          filtersMap.put(f.split(" filteredValue ")[0], f.split(" filteredValue ")[1]);
          ;
        });
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
          if (l.getCount() > 0) {
            if (filtersMap.size() > 0) {
              if (map.keySet().containsAll(filtersMap.keySet()) && map.values().containsAll(filtersMap.values())) {
                lotss.add(new LotsWrapper(l.getId(),
                    l.getCost(),
                    userService.findById(l.getSeller_id()).getUserName(),
                    l.getCount(),
                    new ArrayList<>(map.keySet()),
                    new ArrayList<>(map.values())));
              }
            } else {
              lotss.add(new LotsWrapper(l.getId(),
                  l.getCost(),
                  userService.findById(l.getSeller_id()).getUserName(),
                  l.getCount(),
                  new ArrayList<>(map.keySet()),
                  new ArrayList<>(map.values())));
            }
          }
        } catch (Exception e) {
        }
      });

      if (lotss.stream().anyMatch(l -> l.getCategory() != null)) {
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
    modelAndView.addObject("mode", 1);
    modelAndView.setViewName("myLots");
    return modelAndView;
  }


  @GetMapping(value = "/profile")
  public ModelAndView watchProfile(@RequestParam(value = "id", required = false) Integer id, @RequestParam(value = "name", required = false) String name) throws UnsupportedEncodingException {
    ModelAndView modelAndView = new ModelAndView();
    if (id == null) {
      id = userService.findUserByUserName(name).getId();
    }
    modelAndView = getUserLots(id);
    try {
      modelAndView.addObject("seller_id", id);
      modelAndView.addObject("me", userService.getUser().getId());
      modelAndView.addObject("seller_logo", userService.getPath(userService.findById(id)));
      modelAndView.addObject("my_logo", userService.getPath(userService.getUser()));
    } catch (Exception e) {
    }
    modelAndView.addObject("logo", userService.getPath(userService.findById(id)));
    modelAndView.addObject("userObject", userService.findById(id));
    long deals = 0;
    deals += lotService.getBySeller_id(id).stream().filter(s -> s.getStatus().equals("Продано")).count();
    deals += lotService.getByBuyer_id(id).stream().filter(s -> s.getStatus().equals("Продано")).count();
    List<Integer> sold = new ArrayList<>();
    List<Integer> buyed = new ArrayList<>();
    lotService.getBySeller_id(id).stream().filter(s -> s.getStatus().equals("Продано")).forEach(s -> {
      sold.add(Integer.valueOf(s.getCost()));
    });
    lotService.getByBuyer_id(id).stream().filter(s -> s.getStatus().equals("Продано")).forEach(s -> {
      buyed.add(Integer.valueOf(s.getCost()));
    });
    Integer sumSold = 0;
    sumSold += sold.stream().mapToInt(Integer::intValue).sum();
    Integer sumBuyed = 0;
    sumBuyed += buyed.stream().mapToInt(Integer::intValue).sum();
    modelAndView.addObject("deals", deals);
    modelAndView.addObject("sold", sumSold);
    modelAndView.addObject("buyed", sumBuyed);
    if (userService.getUser() != null) {
      List<ChatMessage> messages = chatMessageService.findByUsers(userService.getUser().getId(), id);
      Integer finalId = id;
      messages.forEach(m -> {
        if (Objects.equals(m.getSender_id(), finalId)) {
          m.setLogo(userService.getPath(userService.findById(finalId)));
        } else {
          m.setLogo(userService.getPath(userService.findById(userService.getUser().getId())));
        }
      });
      modelAndView.addObject("messages", messages);
    }

    homeService.checkAuth(modelAndView);

    modelAndView.setViewName("profile");
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
    lots = lots.stream().filter(l -> l.getStatus().equals("Продажа") && l.getCount() > 0).collect(Collectors.toList());
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
      lotss.add(new LotsWrapper(l.getId(),
          gameService.findById(categoryService.findById(l.getCategory_id()).getGame_id()).getName(),
          categoryService.findById(l.getCategory_id()).getName(),
          l.getCount(),
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
    try {

      List<Lot> lots = lotService.getBySeller_id(user.getId());
      lots = lots.stream().filter(l -> l.getStatus().equals("Продажа") && l.getCount() > 0).collect(Collectors.toList());
      lots = lots.stream().filter(l -> l.getCount() > 0).collect(Collectors.toList());
      List<LotsWrapper> lotss = new ArrayList<>();
      lots.forEach(l -> {
        List<StringAndListWrapper> templates = categoryService.createTemplates(l.getCategory_id());
        if (templates != null) {
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
          lotss.add(new LotsWrapper(l.getId(),
              gameService.findById(categoryService.findById(l.getCategory_id()).getGame_id()).getName(),
              categoryService.findById(l.getCategory_id()).getName(),
              l.getCount(),
              new ArrayList<String>(map.keySet()),
              new ArrayList<String>(map.values())));

          /*      lotWrapers.add(new LotWrapper(l,templates));*/
        }
      });
      modelAndView.addObject("lots", lotss);
    } catch (Exception e) {
      modelAndView.addObject("lots", null);
    }
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
    List<ChatMessage> messages = chatMessageService.findByUsers(userService.getUser().getId(), l.getSeller_id());
    messages.forEach(m -> {
      if (Objects.equals(m.getSender_id(), l.getSeller_id())) {
        m.setLogo(userService.getPath(userService.findById(l.getSeller_id())));
      } else {
        m.setLogo(userService.getPath(userService.findById(userService.getUser().getId())));
      }
    });
    modelAndView.addObject("messages", messages);
    modelAndView.addObject("decimal", categoryService.findById(l.getCategory_id()).isAllowDecimal());
    modelAndView.addObject("sub", categoryService.findById(l.getCategory_id()).getSubCost());
    modelAndView.addObject("cost", l.getCost());
    modelAndView.addObject("count", l.getCount());
    modelAndView.addObject("seller", userService.findById(l.getSeller_id()).getUserName());
    modelAndView.addObject("seller_id", l.getSeller_id());
    modelAndView.addObject("me", userService.getUser().getId());
    modelAndView.addObject("seller_logo", userService.getPath(userService.findById(l.getSeller_id())));
    modelAndView.addObject("my_logo", userService.getPath(userService.getUser()));
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

  @Autowired
  ChatController chatController;

  @PostMapping(value = "/deal/buy")
  public ModelAndView buyLot(@Valid Integer id, @Valid String count) {
    User user = userService.getUser();
    Lot lot = lotService.getById(id);
    User seller = userService.findById(lot.getSeller_id());
    Date date = new Date();
    ModelAndView modelAndView = new ModelAndView();
    if (user.getBalance() >= Integer.parseInt(lot.getCost())) {
      ChatMessage chatMessage = new ChatMessage();
      chatMessage.setTime(sdf.format(new Timestamp(date.getTime())));
      chatMessage.setSender_id(user.getId());
      chatMessage.setReceiver_id(seller.getId());
      chatMessage.setSender(user.getUserName());
      chatMessage.setReceiver(seller.getUserName());
      chatMessage.setChecked(false);
      chatMessage.setSystem(true);
      chatMessage.setLogo(null);
      if (count != null) {
        chatMessage.setContent("Пользователь " + user.getUserName() + " оплатил заказ: " + categoryService.findById(lot.getCategory_id())
            .getName() + " " + count + " " + categoryService.findById(lot.getCategory_id()).getSubCost() +
            " из " + gameService.findById(categoryService.findById(lot.getCategory_id()).getGame_id())
            .getName() + " за " + Double.valueOf(lot.getCost()) * Double.valueOf(count) + "₽ у пользователя " + seller.getUserName() + ", покупатель " +
            user.getUserName() + " не забудьте перейти в раздел «Покупки» и «Подтвердить выполнение заказа»");
      } else {
        chatMessage.setContent("Пользователь " + user.getUserName() + " оплатил заказ: " + categoryService.findById(lot.getCategory_id()).getName() +
            " из " + gameService.findById(categoryService.findById(lot.getCategory_id()).getGame_id())
            .getName() + " за " + lot.getCost() + "₽ у пользователя " + seller.getUserName() + ", покупатель " +
            user.getUserName() + " не забудьте перейти в раздел «Покупки» и «Подтвердить выполнение заказа»");
      }


      chatMessage.setChat_id("{" + Math.min(user.getId(), seller.getId()) + "} {" + Math.max(user.getId(), seller.getId()) + "}");
      chatMessageService.save(chatMessage);
      seller.setHaveMessage(true);
      user.setHaveMessage(true);
      this.template.convertAndSend("/topic/public", chatMessage);
      Lot myLot = new Lot();
      BeanUtils.copyProperties(lot, myLot);
      myLot.setId(null);
      myLot.setStatus("Подтверждение");
      myLot.setSeller_id(lot.getSeller_id());
      myLot.setBuyer_id(user.getId());
      myLot.setDate(sdf.format(new Timestamp(date.getTime())));
      myLot.setCategory_id(lot.getCategory_id());
      myLot.setTemplates(new ArrayList<>(lot.getTemplates()));
      myLot.setSubTemplates(new ArrayList<>(lot.getSubTemplates()));
      if (count != null) {
        String sum = String.valueOf(Double.valueOf(count) * Double.valueOf(myLot.getCost()));
        String finalSum = sum.substring(0, sum.indexOf("."));
        myLot.setCount(Double.valueOf(count));
        lot.setCount(lot.getCount() - Double.valueOf(count));
        myLot.setCost(finalSum);
      } else {
        myLot.setCount(1);
        lot.setCount(lot.getCount() - 1);
      }
      lotService.saveLot(lot);
      lotService.saveLot(myLot);
      user.setBalance(user.getBalance() - Integer.parseInt(myLot.getCost()));
      int c = 100;
      if (categoryService.findById(lot.getCategory_id()).getTax() != null) {
        c = (100 - categoryService.findById(lot.getCategory_id()).getTax());
      }
      seller.setBalance_charge(seller.getBalance_charge() + Integer.valueOf(c * Integer.valueOf(myLot.getCost()) / 100));
      seller.setSells(seller.getSells() + 1);
      user.setBuys(user.getBuys() + 1);
      userService.saveUser(user);
      userService.saveUser(seller);
    } else {
      modelAndView = getLot(id);
      modelAndView.addObject("message", "Недостаточно средств");
      return modelAndView;
    }
    return new ModelAndView("redirect:/user/watch/chats");
  }

  @GetMapping(value = "/watch/myBuys")
  public ModelAndView watchMyBuys() {
    ModelAndView modelAndView = getMyBuys("Подтверждение");
    modelAndView.setViewName("buys");
    User user = userService.getUser();
    user.setBuys(0);
    userService.saveUser(user);
    return modelAndView;
  }

  @SendTo("/topic/public")
  public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
    return chatMessage;
  }

  public ModelAndView getMyBuys(String status) {
    User user = userService.getUser();
    ModelAndView modelAndView = new ModelAndView();
    List<Lot> lots = lotService.getByBuyer_id(user.getId());
    if (status != null) {
      lots = lots.stream().filter(l -> l.getStatus().equals(status)).collect(Collectors.toList());
    }
    Collections.reverse(lots);
    List<LotsWrapper> lotss = new ArrayList<>();
    lots.forEach(l -> {
      List<StringAndListWrapper> templates = categoryService.createTemplates(l.getCategory_id());
      Map<String, String> map = new HashMap<>();
      int a = 0;
      int b = 0;
      map.put("Цена", l.getCost());
      if (categoryService.findById(l.getCategory_id()).isAllowDecimal())
        map.put("Количество", String.valueOf(l.getCount()));
      for (int i = 0; i < templates.size(); i++) {
        if (templates.get(i).getArrString() != null) {
          map.put(templates.get(i).string, l.getSubTemplates().get(b));
          b++;
        } else {
          try {
            map.put(templates.get(i).string, l.getTemplates().get(a));
            a++;
          } catch (Exception e) {

          }

        }
      }
      lotss.add(new LotsWrapper(l.getId(),
          l.getDate(),
          userService.findById(l.getSeller_id()).getUserName(),
          gameService.findById(categoryService.findById(l.getCategory_id()).getGame_id()).getName(),
          categoryService.findById(l.getCategory_id()).getName(),
          new ArrayList<String>(map.keySet()),
          new ArrayList<String>(map.values())));
    });
    modelAndView.addObject("lots", lotss);
    homeService.checkAuth(modelAndView);
    return modelAndView;
  }

  public ModelAndView getMyHistory(String mode) {
    User user = userService.getUser();
    ModelAndView modelAndView = new ModelAndView();
    List<Lot> lots;
    if (mode.equals("Продажи")) {
      lots = lotService.getBySeller_id(user.getId());
      lots = lots.stream().filter(l -> (l.getStatus().equals("Продано") || l.getStatus().equals("Возврат"))).collect(Collectors.toList());
    } else if (mode.equals("Покупки")) {
      lots = lotService.getByBuyer_id(user.getId());
      lots = lots.stream().filter(l -> (l.getStatus().equals("Продано") || l.getStatus().equals("Возврат") || l.getStatus().equals("Подтверждение")))
          .collect(Collectors.toList());
    } else {
      lots = lotService.getBySeller_id(user.getId());
      lots = lots.stream().filter(l -> (l.getStatus().equals("Подтверждение"))).collect(Collectors.toList());
    }
    List<LotsWrapper> lotss = new ArrayList<>();
    Collections.sort(lots);
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
          try {
            map.put(templates.get(i).string, l.getTemplates().get(a));
            a++;
          } catch (Exception e) {

          }

        }
      }
      if (mode.equals("Продажи")) {
        lotss.add(new LotsWrapper(
            l.getId(),
            l.getStatus(),
            l.getDate(),
            userService.findById(l.getBuyer_id()).getUserName(),
            gameService.findById(categoryService.findById(l.getCategory_id()).getGame_id()).getName(),
            categoryService.findById(l.getCategory_id()).getName(),
            new ArrayList<String>(map.keySet()),
            new ArrayList<String>(map.values())));
      } else {
        lotss.add(new LotsWrapper(
            l.getId(),
            l.getStatus(),
            l.getDate(),
            userService.findById(l.getSeller_id()).getUserName(),
            gameService.findById(categoryService.findById(l.getCategory_id()).getGame_id()).getName(),
            categoryService.findById(l.getCategory_id()).getName(),
            new ArrayList<String>(map.keySet()),
            new ArrayList<String>(map.values())));
      }
    });
    Collections.reverse(lotss);
    modelAndView.addObject("lots", lotss);
    homeService.checkAuth(modelAndView);
    return modelAndView;
  }

  @GetMapping(value = "/approve/lot")
  public ModelAndView approveLot(@Valid Integer id) {
    User user = userService.getUser();
    Lot lot = lotService.getById(id);
    if (lot.getBuyer_id().equals(user.getId())) {

      User seller = userService.findById(lot.getSeller_id());

      lot.setStatus("Продано");

      lotService.saveLot(lot);

      Date date = new Date();
      ChatMessage chatMessage = new ChatMessage();
      chatMessage.setTime(sdf.format(new Timestamp(date.getTime())));
      chatMessage.setSender_id(user.getId());
      chatMessage.setReceiver_id(seller.getId());
      chatMessage.setSender(user.getUserName());
      chatMessage.setReceiver(seller.getUserName());
      chatMessage.setChecked(false);
      chatMessage.setSystem(true);
      chatMessage.setLogo(null);
      chatMessage.setContent("Покупатель " + user.getUserName() + " потдвердил успешное выполнение заказа: " + categoryService.findById(lot.getCategory_id())
          .getName() +
          " из " + gameService.findById(categoryService.findById(lot.getCategory_id()).getGame_id())
          .getName() + " и отправил деньги продавцу " + seller.getUserName());
      chatMessage.setChat_id("{" + Math.min(user.getId(), seller.getId()) + "} {" + Math.max(user.getId(), seller.getId()) + "}");
      chatMessageService.save(chatMessage);
      this.template.convertAndSend("/topic/public", chatMessage);
      seller.setHaveMessage(true);
      user.setHaveMessage(true);

      int c = 100;
      if (categoryService.findById(lot.getCategory_id()).getTax() != null) {
        c = (100 - categoryService.findById(lot.getCategory_id()).getTax());
      }
      seller.setBalance_charge(seller.getBalance_charge() - Integer.valueOf(c * Integer.valueOf(lot.getCost()) / 100));
      seller.setBalance(seller.getBalance() + Integer.valueOf(c * Integer.valueOf(lot.getCost()) / 100));
      seller.setRefValue(user.getRefValue() + Integer.valueOf(c * Integer.valueOf(lot.getCost()) / 100) / 100);
      user.setRefValue(user.getRefValue() + Integer.valueOf(c * Integer.valueOf(lot.getCost()) / 100) / 50);
      userService.saveUser(user);
      userService.saveUser(seller);
    }
    return new ModelAndView("redirect:/user/watch/myBuys");
  }

  @GetMapping(value = "/dismiss/lot")
  public ModelAndView dismissLot(@Valid Integer id) {
    User user = userService.getUser();
    Lot lot = lotService.getById(id);
    if (lot.getSeller_id().equals(user.getId())) {

      User seller = userService.findById(lot.getSeller_id());
      User buyer = userService.findById(lot.getBuyer_id());

      lot.setStatus("Возврат");

      lotService.saveLot(lot);
      int c = 100;
      Date date = new Date();
      ChatMessage chatMessage = new ChatMessage();
      chatMessage.setTime(sdf.format(new Timestamp(date.getTime())));
      chatMessage.setSender_id(user.getId());
      chatMessage.setReceiver_id(buyer.getId());
      chatMessage.setSender(user.getUserName());
      chatMessage.setReceiver(buyer.getUserName());
      chatMessage.setChecked(false);
      chatMessage.setSystem(true);
      chatMessage.setLogo(null);
      chatMessage.setContent("Продавец " + seller.getUserName() + " вернул деньги покупателю " + buyer.getUserName());
      chatMessage.setChat_id("{" + Math.min(user.getId(), buyer.getId()) + "} {" + Math.max(user.getId(), buyer.getId()) + "}");
      chatMessageService.save(chatMessage);
      this.template.convertAndSend("/topic/public", chatMessage);
      seller.setHaveMessage(true);
      user.setHaveMessage(true);
      if (categoryService.findById(lot.getCategory_id()).getTax() != null) {
        c = (100 - categoryService.findById(lot.getCategory_id()).getTax());
      }
      buyer.setBalance(buyer.getBalance() + Integer.valueOf(c * Integer.valueOf(lot.getCost()) / 100));
      seller.setBalance_charge(seller.getBalance_charge() - Integer.valueOf(c * Integer.valueOf(lot.getCost()) / 100));
      userService.saveUser(buyer);
      userService.saveUser(seller);
    }
    return new ModelAndView("redirect:/user/watch/myLotsActive");
  }

  @GetMapping(value = "/watch/myLotsHistory")
  public ModelAndView buysHistory() {
    ModelAndView modelAndView = getMyHistory("Продажи");
    modelAndView.setViewName("buys2");
    modelAndView.addObject("mode", "sell");
    return modelAndView;
  }

  @GetMapping(value = "/watch/myLotsActive")
  public ModelAndView buysActive() {
    ModelAndView modelAndView = getMyHistory("Ожидается");
    modelAndView.setViewName("buys2");
    User user = userService.getUser();
    user.setSells(0);
    userService.saveUser(user);
    modelAndView.addObject("mode", "active");
    return modelAndView;
  }

  @GetMapping(value = "/watch/myBuysHistory")
  public ModelAndView sellsHistory() {
    ModelAndView modelAndView = getMyHistory("Покупки");
    modelAndView.setViewName("buys2");
    modelAndView.addObject("mode", "buy");
    return modelAndView;
  }

  @GetMapping(value = "/referals")
  public ModelAndView referals() {
    ModelAndView modelAndView = new ModelAndView();
    modelAndView.setViewName("referals");
    List<User> users = new ArrayList<>();
    User user = userService.getUser();
    user.getReferals().forEach(r ->
        users.add(userService.findById(Integer.valueOf(r))));
    modelAndView.addObject("users", users);
    homeService.checkAuth(modelAndView);
    return modelAndView;
  }

  @GetMapping(value = "/referalsHistory")
  public ModelAndView referalsHistory() {
    ModelAndView modelAndView = new ModelAndView();
    modelAndView.setViewName("referalsHistory");
    User user = userService.getUser();
    List<RefHistory> reports = refService.findByTargetId(user.getId());
    modelAndView.addObject("reports", reports);
    homeService.checkAuth(modelAndView);
    return modelAndView;
  }

  @GetMapping(value = "/getRef")
  public ModelAndView getRef(@Valid Integer id) {
    ModelAndView modelAndView = new ModelAndView();
    modelAndView.setViewName("referals");
    User user = userService.getUser();
    User target = userService.findById(id);
    user.setBalance(user.getBalance() + target.getRefValue());
    RefHistory refHistory = new RefHistory();
    refHistory.setTarget_id(user.getId());
    refHistory.setUserName((user.getUserName()));
    refHistory.setDate(target.getDate());
    refHistory.setRefValue(target.getRefValue());
    target.setRefValue(0);
    userService.saveUser(user);
    userService.saveUser(target);
    refService.saveRefHistory(refHistory);
    List<User> users = new ArrayList<>();
    user.getReferals().forEach(r ->
        users.add(userService.findById(Integer.valueOf(r))));
    modelAndView.addObject("users", users);
    homeService.checkAuth(modelAndView);
    return modelAndView;
  }

  @GetMapping(value = "/ref")
  public ModelAndView ref() {
    ModelAndView modelAndView = new ModelAndView();
    modelAndView.setViewName("ref");
    homeService.checkAuth(modelAndView);
    return modelAndView;
  }

}