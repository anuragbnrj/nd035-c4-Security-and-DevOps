package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private UserController userController;

    private User user;

    UserControllerTest() {
    }

    @BeforeEach
    void setUp() {
        // setup user
        user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setPassword("hashedPassword");
        user.setCart(new Cart());
    }

    @Test
    void findById() {
        // given
        Long userId = 1L;
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        // when
        ResponseEntity<User> response = userController.findById(userId);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @Test
    void findByUserName() {
        // given
        String username = "testUser";
        when(userRepository.findByUsername(anyString())).thenReturn(user);

        // when
        ResponseEntity<User> response = userController.findByUserName(username);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @Test
    void findByUserName_userDoesNotExist_404() {
        // given
        String username = "userDoesNotExist";
        when(userRepository.findByUsername(username)).thenReturn(null);

        // when
        ResponseEntity<User> response = userController.findByUserName(username);

        // then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void createUser() {
        // given
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("testUser");
        createUserRequest.setPassword("password");
        createUserRequest.setConfirmPassword("password");
        when(cartRepository.save(any(Cart.class))).thenReturn(new Cart());
        when(bCryptPasswordEncoder.encode("password")).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // when
        ResponseEntity<User> response = userController.createUser(createUserRequest);

        // then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @Test
    void createUser_passwordLengthInvalid_400() {
        // given
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("testUser");
        createUserRequest.setPassword("passwor");
        createUserRequest.setConfirmPassword("passwor");

        // when
        ResponseEntity<User> response = userController.createUser(createUserRequest);

        // then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void createUser_passwordConfirmPasswordMismatch_400() {
        // given
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("testUser");
        createUserRequest.setPassword("password");
        createUserRequest.setConfirmPassword("passwors");

        // when
        ResponseEntity<User> response = userController.createUser(createUserRequest);

        // then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

}