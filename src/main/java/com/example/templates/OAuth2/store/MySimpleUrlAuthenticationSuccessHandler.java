package com.example.templates.OAuth2.store;

import java.util.concurrent.TimeUnit;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.example.templates.model.ActiveUserStore;
import com.example.templates.model.LoggedUser;
import com.example.templates.model.User;
import com.example.templates.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component("myAuthenticationSuccessHandler")

public class MySimpleUrlAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

  @Autowired
  ActiveUserStore activeUserStore;
  @Autowired
  UserService userService;
  private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
  @Override
  public void onAuthenticationSuccess(HttpServletRequest request,
                                      HttpServletResponse response, Authentication authentication)
      throws IOException {
    HttpSession session = request.getSession(true);
    if (session != null) {
      LoggedUser user = new LoggedUser(authentication.getName(), activeUserStore);
      session.setAttribute("user", user);
      User user2 = userService.getUser();
      redirectStrategy.sendRedirect(request, response, "/");
    }
  }
}