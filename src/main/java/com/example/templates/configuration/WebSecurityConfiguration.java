package com.example.templates.configuration;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.example.templates.OAuth2.CustomOAuth2UserService;
import com.example.templates.model.ActiveUserStore;
import com.example.templates.model.LoggedUser;
import com.example.templates.service.HomeService;
import com.example.templates.service.MyUserDetailsService;
import com.example.templates.service.UserService;
import com.example.templates.OAuth2.store.MyLogoutSuccessHandler;
import com.example.templates.OAuth2.store.MySimpleUrlAuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

  @Autowired
  private BCryptPasswordEncoder bCryptPasswordEncoder;

  @Autowired
  private MyUserDetailsService userDetailsService;

  @Autowired
  private MyLogoutSuccessHandler myLogoutSuccessHandler;
  @Autowired
  private MySimpleUrlAuthenticationSuccessHandler mySimpleUrlAuthenticationSuccessHandler;
  @Autowired
  ActiveUserStore activeUserStore;

  @Autowired
  private HomeService homeService;
  @Bean
  public DaoAuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailsService());
    authProvider.setPasswordEncoder(bCryptPasswordEncoder);

    return authProvider;
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth
        .authenticationProvider(authenticationProvider())
        .userDetailsService(userDetailsService)
        .passwordEncoder(bCryptPasswordEncoder);

  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {

    String loginPage = "/login";
    String logoutPage = "/logout";

      http.authorizeRequests()
          .antMatchers("/agent","/agreement","/","/user/rules/site", "/chat.register","/chat.send","/user/profile", "/login", "/oauth/**","/reset", "/user/watch/lots").permitAll()
          .antMatchers("/deal/**","/deal/buy", "user/create/lot", "user/create/**", "/user/create/subLot","/registration","/emailExists", "/user/search" ,"/user/create/lot" ,"/user/goHome", "/user/search?**").permitAll()
          .antMatchers("/admin/**", "admin/create/manager").hasAuthority("ADMIN")
          .anyRequest().authenticated()
          .and().csrf().disable()
          .formLogin().permitAll()
          .loginPage(loginPage)
          .failureUrl("/login")
          .usernameParameter("user_name")
          .passwordParameter("password")
          .successHandler(mySimpleUrlAuthenticationSuccessHandler)
          .failureForwardUrl("/fail_login")
          .and()
          .oauth2Login()
          .loginPage("/login")
          .userInfoEndpoint()
          .userService(oauthUserService)
          .and()
          .successHandler(new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                                Authentication authentication) throws IOException, ServletException {
              DefaultOidcUser oauthUser = (DefaultOidcUser) authentication.getPrincipal();
              HttpSession session = request.getSession(false);
              if (session != null) {
                LoggedUser user = new LoggedUser(authentication.getName(), activeUserStore);
                session.setAttribute("user", user);
              }
              if (!userService.processOAuthPostLogin(oauthUser)) {
                response.sendRedirect("/user/goHome");
              } else {
                response.sendRedirect("/user/goHome");
              }

            }
          })
          .and().logout()
          .logoutSuccessHandler(myLogoutSuccessHandler)
          .logoutRequestMatcher(new AntPathRequestMatcher(logoutPage))
          .logoutSuccessUrl(loginPage).and().exceptionHandling();
    }

    @Autowired
    private CustomOAuth2UserService oauthUserService;

    @Autowired
    private UserService userService;

  @Override
  public void configure(WebSecurity web) throws Exception {
    web
        .ignoring()
        .antMatchers("/game_logos/**","/user_logos/**","/static/**", "/user/watch/lots/", "/css/**", "/js/**","/scripts/**", "/images/**", "/css/test.css", ".css", ".svg", ".mp3", "/beep.mp3","/favicon.ico", "/error");
  }

}
