package com.example.demo.controllers;

import java.util.Optional;
import java.util.stream.IntStream;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;

@Slf4j
@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final ItemRepository itemRepository;

    public CartController(UserRepository userRepository, CartRepository cartRepository, ItemRepository itemRepository) {
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.itemRepository = itemRepository;
    }

    @PostMapping("/addToCart")
    public ResponseEntity<Cart> addToCart(@RequestBody ModifyCartRequest request) {
        log.debug("Start of method addToCart, params: request - {}", request);

        User user = userRepository.findByUsername(request.getUsername());
        if (user == null) {
            log.error("Could not find user with username {}", request.getUsername());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Cart cart = user.getCart();

        Optional<Item> item = itemRepository.findById(request.getItemId());
        if (!item.isPresent()) {
            log.error("Could not find item with Id {}", request.getItemId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        IntStream.range(0, request.getQuantity())
                .forEach(i -> cart.addItem(item.get()));
        cartRepository.save(cart);

        log.debug("End of method addToCart, value returned - {}", cart);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/removeFromCart")
    public ResponseEntity<Cart> removeFromCart(@RequestBody ModifyCartRequest request) {
		log.debug("Beginning of method removeFromCart, params: request - {}", request);

        User user = userRepository.findByUsername(request.getUsername());
        if (user == null) {
			log.error("Could not find user with username {}", request.getUsername());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Optional<Item> item = itemRepository.findById(request.getItemId());
        if (!item.isPresent()) {
			log.error("Could not find item with Id {}", request.getItemId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Cart cart = user.getCart();
        IntStream.range(0, request.getQuantity())
                .forEach(i -> cart.removeItem(item.get()));
        cartRepository.save(cart);
        return ResponseEntity.ok(cart);
    }

}
