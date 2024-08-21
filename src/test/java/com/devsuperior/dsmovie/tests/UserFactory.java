package com.devsuperior.dsmovie.tests;

import com.devsuperior.dsmovie.entities.RoleEntity;
import com.devsuperior.dsmovie.entities.UserEntity;

public class UserFactory {

    public static UserEntity createUserEntity() {
        UserEntity user = new UserEntity(2L, "Maria", "maria@gmail.com", "$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG");
        user.addRole(new RoleEntity(1L, "ROLE_CLIENT"));
        return user;
    }

    public static UserEntity createUserEntityADM() {
        UserEntity user = new UserEntity(1L, "Jose", "jose@gmail.com", "$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG");
        user.addRole(new RoleEntity(2L, "ROLE_ADMIN"));
        return user;
    }

    public static UserEntity createCustomAdminUser(Long id, String username) {
        UserEntity user = new UserEntity(id, "Felipe", username, "$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG");
        user.addRole(new RoleEntity(2L, "ROLE_ADMIN"));
        return user;
    }
}
