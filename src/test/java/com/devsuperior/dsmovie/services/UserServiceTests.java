package com.devsuperior.dsmovie.services;

import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.projections.UserDetailsProjection;
import com.devsuperior.dsmovie.repositories.UserRepository;
import com.devsuperior.dsmovie.tests.UserDetailsFactory;
import com.devsuperior.dsmovie.tests.UserFactory;
import com.devsuperior.dsmovie.utils.CustomUserUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
public class UserServiceTests {

    @InjectMocks
    private UserService service;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CustomUserUtil userUtil;


    private String exitingUsername, nonExistingUsername;
    private UserEntity user;
    private List<UserDetailsProjection> userDetails = new ArrayList<>();

    @BeforeEach
    void setUp() {
        exitingUsername = "maria@gmail.com";
        nonExistingUsername = "user@gmail.com";
        user = UserFactory.createUserEntity();

        userDetails = UserDetailsFactory.createCustomAdminUser(exitingUsername);

        when(userRepository.searchUserAndRolesByUsername(exitingUsername)).thenReturn(userDetails);
        when(userRepository.searchUserAndRolesByUsername(nonExistingUsername)).thenReturn(new ArrayList<>());

        when(userRepository.findByUsername(exitingUsername)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername(nonExistingUsername)).thenReturn(Optional.empty());
    }

    @Test
    public void authenticatedShouldReturnUserEntityWhenUserExists() {
        when(userUtil.getLoggedUsername()).thenReturn(exitingUsername);

        UserEntity result = service.authenticated();
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getUsername(), exitingUsername);
    }

    @Test
    public void authenticatedShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExists() {
        Assertions.assertThrows(UsernameNotFoundException.class,() -> {
            when(userUtil.getLoggedUsername()).thenReturn(nonExistingUsername);

            UserEntity result = service.authenticated();
        });
    }

    @Test
    public void loadUserByUsernameShouldReturnUserDetailsWhenUserExists() {
        UserDetails result = service.loadUserByUsername(exitingUsername);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(userDetails.get(0).getUsername(), result.getUsername());
    }

    @Test
    public void loadUserByUsernameShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExists() {
        Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            UserDetails result = service.loadUserByUsername(nonExistingUsername);
        });
    }
}
