package com.example.elainachat.netty.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author elaina
 * @since 2025-02-28
 */
@Getter
@Setter
public class Users implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String username;

    private String password;

    private String avatarUrl;

    private String email;

    private String token;

    private String phone;

    /**
     * 1:active, 0:inactive, 2:blocked
     */
    private Byte status;

    private LocalDateTime lastLoginTime;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    //分别用于注册和登录，注册时需要username，登录需要id
    public Users(Long id,String password){
        this.id = id;
        this.password = password;
        this.status = 1;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.lastLoginTime = LocalDateTime.now();
    }

    public Users(String username,String password){
        this.username = username;
        this.password = password;
        this.status = 1;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.lastLoginTime = LocalDateTime.now();
    }

}
