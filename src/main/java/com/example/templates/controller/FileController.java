package com.example.templates.controller;

import java.util.List;
import java.util.stream.Collectors;

import java.io.IOException;

import com.example.templates.model.FileDB;
import com.example.templates.model.ResponseFile;
import com.example.templates.model.ResponseMessage;
import com.example.templates.model.User;
import com.example.templates.service.FileStorageService;
import com.example.templates.service.HomeService;
import com.example.templates.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Controller
@CrossOrigin("http://localhost:8081")
public class FileController {

  @Autowired
  private FileStorageService storageService;

  @Autowired
  private UserService userService;

  @Autowired
  private HomeService homeService;

  @PostMapping("/upload")
  public ModelAndView uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
    ModelAndView modelAndView = new ModelAndView();
    User user = userService.getUser();
      user.setLogo(storageService.store(file).getId());
      userService.saveUser(user);
    modelAndView.addObject("email", user.getEmail());
    modelAndView.addObject("password", user.getPassword());
      modelAndView.setViewName("home");
      homeService.checkAuth(modelAndView);
      return modelAndView;
  }

  @GetMapping("/files")
  public ResponseEntity<List<ResponseFile>> getListFiles() {
    List<ResponseFile> files = storageService.getAllFiles().map(dbFile -> {
      String fileDownloadUri = ServletUriComponentsBuilder
          .fromCurrentContextPath()
          .path("/files/")
          .path(dbFile.getId())
          .toUriString();
      return new ResponseFile(
          dbFile.getName(),
          fileDownloadUri,
          dbFile.getType(),
          dbFile.getData().length);
    }).collect(Collectors.toList());
    return ResponseEntity.status(HttpStatus.OK).body(files);
  }
  @GetMapping("/files/{id}")
  public ResponseEntity<byte[]> getFile(@PathVariable String id) {
    FileDB fileDB = storageService.getFile(id);
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileDB.getName() + "\"")
        .body(fileDB.getData());
  }
}