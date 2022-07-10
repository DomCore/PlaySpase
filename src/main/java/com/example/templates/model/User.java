package com.example.templates.model;

import com.example.templates.OAuth2.Provider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    private Integer id;
    @Column(name = "user_name")
    @Length(min = 1, message = "*Ваше имя пользователя должно содержать минимум 5 символов")
    @NotEmpty(message = "*Введите имя пользователя")
    private String userName;
    @Column(name = "email")
    @Email(message = "*Введите корректную почту")
    @NotEmpty(message = "*Введите почту")
    private String email;
    @Column(name = "password")
    @NotEmpty(message = "*Введите пароль")
    private String password;
    private Boolean active;
    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;
    @Enumerated(EnumType.STRING)
    private Provider provider;
    @Column(name = "logo",nullable = true, length = 64)
    private String logo;
    @Column(name = "balance")
    private int balance;
    @Column(name = "balance_charge")
    private int balance_charge;
    @Column(name = "haveMessage")
    private boolean haveMessage;
    @Transient
    private String path;
}
