package com.example.demo.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserController(UserRepository userRepository,
                          CartRepository cartRepository,
                          BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<User> findById(@PathVariable Long id) {
        return ResponseEntity.of(userRepository.findById(id));
    }

    @GetMapping("/{username}")
    public ResponseEntity<User> findByUserName(@PathVariable String username) {
        User user = userRepository.findByUsername(username);
        return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
    }

    @PostMapping("/create")
    public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
        log.info("Start of method createUser for username - {}", createUserRequest.getUsername());

        if (createUserRequest.getPassword().length() < 8) {
            log.error("Error in createUser for username - {}. Password Invalid!! Length less than 8 characters.",
                    createUserRequest.getUsername());
            return ResponseEntity.badRequest().build();
        }
        if (!createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())) {
            log.error("Error in createUser for username - {}. Password and confirmPassword do not match!!",
                    createUserRequest.getUsername());
            return ResponseEntity.badRequest().build();
        }

        Cart cart = new Cart();
        cartRepository.save(cart);

        User user = new User();
        user.setUsername(createUserRequest.getUsername());
        user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));
        user.setCart(cart);
        user = userRepository.save(user);

        log.info("End of method createUser. User created for username - {}, value returned - {}",
                user.getUsername(), user);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(user);
    }

}
