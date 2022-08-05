package com.example.templates.service;

import com.example.templates.OAuth2.Provider;
import com.example.templates.model.FileDB;
import com.example.templates.model.Role;
import com.example.templates.model.User;
import com.example.templates.repository.RoleRepository;
import com.example.templates.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;

import java.io.IOException;

@Service
public class UserService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private FileStorageService storageService;
    @Autowired
    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    public User findById(Integer id) {
        return userRepository.findById(id);
    }

    public User findUserByUserName(String userName) {
        return userRepository.findByUserName(userName);
    }

    public User saveUser(User user, String role) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setActive(true);
        Role userRole = roleRepository.findByRole(role);
        user.setRoles(new HashSet<Role>(Arrays.asList(userRole)));
        return userRepository.save(user);
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }
    public boolean processOAuthPostLogin(DefaultOidcUser userOid) {
        boolean saved = false;
        if (userRepository.findByEmail(userOid.getEmail()) == null) {
            User user = new User();
            user.setProvider(Provider.GOOGLE);
            user.setActive(true);
            user.setUserName(userOid.getEmail()!=null ? userOid.getEmail(): "");
            user.setEmail(userOid.getEmail()!=null ? userOid.getEmail(): "");
            user.setPassword(bCryptPasswordEncoder.encode("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"));
            Role userRole = roleRepository.findByRole("USER");
            user.setRoles(new HashSet<Role>(Arrays.asList(userRole)));
            userRepository.save(user);
            saved = true;
        }
        return saved;
    }

    public User getUser() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = findUserByUserName(auth.getName());
            if (user == null) {
                DefaultOidcUser oauthUser = (DefaultOidcUser) auth.getPrincipal();
                user = findUserByEmail(oauthUser.getEmail());
            }
            return user;
        }catch (Exception e){
            return null;
        }

    }
    public String getPath(User user) {
        try {
            FileDB file = storageService.getFile(user.getLogo());
            byte[] encodeBase64 = Base64.getEncoder().encode(file.getData());
            String base64Encoded = new String(encodeBase64, "UTF-8");
            user.setPath("data:image/jpeg;base64," + base64Encoded);
        } catch (Exception e) {

        }
        return user.getPath() != null ? user.getPath() : getDefaultPath();
    }

    public String getDefaultPath(){
        try {
        FileDB file = storageService.getFile("f4daa6c7-271b-48b3-acbd-b95d238602fa");
        byte[] encodeBase64 = Base64.getEncoder().encode(file.getData());
        String base64Encoded = new String(encodeBase64, "UTF-8");
        return ("data:image/jpeg;base64," + base64Encoded);
        } catch (Exception e) {

        }
        return null;
    }

}