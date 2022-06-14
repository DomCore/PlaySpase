package com.example.templates.service;

import com.example.templates.OAuth2.Provider;
import com.example.templates.model.Role;
import com.example.templates.model.User;
import com.example.templates.repository.RoleRepository;
import com.example.templates.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;

@Service
public class UserService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

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

}