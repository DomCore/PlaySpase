package com.example.templates.configuration;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.templates.OAuth2.CustomOAuth2User;
import com.example.templates.OAuth2.CustomOAuth2UserService;
import com.example.templates.service.MyUserDetailsService;
import com.example.templates.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

  @Autowired
  private BCryptPasswordEncoder bCryptPasswordEncoder;

  @Autowired
  private MyUserDetailsService userDetailsService;

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
          .antMatchers("/", "/login", "/oauth/**").permitAll()
          .antMatchers("/registration","/emailExists").permitAll()
          .antMatchers("/admin/**").hasAuthority("ADMIN")
          .anyRequest().authenticated()
          .and().csrf().disable()
          .formLogin().permitAll()
          .loginPage(loginPage)
          .loginPage("/")
          .failureUrl("/login?error=true")
          .usernameParameter("user_name")
          .passwordParameter("password")
          .defaultSuccessUrl("/home")
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
              if (!userService.processOAuthPostLogin(oauthUser)) {
                response.sendRedirect("/home");
              } else {
                response.sendRedirect("/home");
              }

            }
          })
          .and().logout()
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
        .antMatchers("/resources/**", "/static/**", "/css/**", "/js/**", "/images/**")
        .antMatchers(HttpMethod.GET,"/users/**")
        .antMatchers(HttpMethod.GET,"/watch/**")
        .antMatchers(HttpMethod.POST,"/users/**")
        .antMatchers(HttpMethod.POST,"/admin/edit/category")
        .antMatchers(HttpMethod.POST,"/users/create/lot");
  }

}
