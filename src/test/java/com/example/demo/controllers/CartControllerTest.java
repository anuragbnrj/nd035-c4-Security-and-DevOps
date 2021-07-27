package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private CartController cartController;

    private List<Item> itemList;
    private Cart cart;
    private User user;

    @BeforeEach
    void setUp() {
        // setup itemList
        itemList = new ArrayList<>();

        Item itemOne = new Item();
        itemOne.setId(1L);
        itemOne.setName("itemOne");
        itemOne.setPrice(BigDecimal.valueOf(1.1));
        itemOne.setDescription("Item One");

        Item itemTwo = new Item();
        itemTwo.setId(2L);
        itemTwo.setName("itemTwo");
        itemTwo.setPrice(BigDecimal.valueOf(2.2));
        itemTwo.setDescription("Item Two");

        itemList.add(itemOne);
        itemList.add(itemTwo);

        // setup user
        user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setPassword("hashedPassword");

        // setup cart
        cart = Mockito.spy(new Cart());
        cart.setId(1L);
        cart.setUser(user);
        cart.addItem(itemList.get(0));
        cart.addItem(itemList.get(0));
        cart.addItem(itemList.get(1));

        // setup cart for user
        user.setCart(cart);
    }

    @Test
    void addToCart() {
        // given
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername("testUser");
        modifyCartRequest.setItemId(itemList.get(1).getId());
        modifyCartRequest.setQuantity(2);

        when(userRepository.findByUsername("testUser")).thenReturn(user);
        when(itemRepository.findById(modifyCartRequest.getItemId())).thenReturn(Optional.of(itemList.get(1)));

        Cart expectedCart = new Cart();
        expectedCart.setId(1L);
        expectedCart.setUser(user);
        expectedCart.addItem(itemList.get(0));
        expectedCart.addItem(itemList.get(0));
        expectedCart.addItem(itemList.get(1));
        IntStream.range(0, modifyCartRequest.getQuantity())
                .forEach(i -> expectedCart.addItem(itemList.get(1)));

        when(cartRepository.save(any(Cart.class))).thenReturn(expectedCart);

        // when
        ResponseEntity<Cart> response = cartController.addToCart(modifyCartRequest);

        // then
        verify(cart, times(modifyCartRequest.getQuantity() + 1)).addItem(itemList.get(1));
        verify(cartRepository, times(1)).save(any(Cart.class));
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedCart, response.getBody());
    }

    @Test
    void addToCart_userNotFound_404() {
        // given
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername("userDoesNotExist");
        modifyCartRequest.setItemId(itemList.get(1).getId());
        modifyCartRequest.setQuantity(2);

        when(userRepository.findByUsername("userDoesNotExist")).thenReturn(null);

        // when
        ResponseEntity<Cart> response = cartController.addToCart(modifyCartRequest);

        // then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void addToCart_itemNotFound_404() {
        // given
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername("testUser");
        modifyCartRequest.setItemId(3L);
        modifyCartRequest.setQuantity(2);

        when(userRepository.findByUsername("testUser")).thenReturn(user);
        when(itemRepository.findById(3L)).thenReturn(Optional.empty());

        // when
        ResponseEntity<Cart> response = cartController.addToCart(modifyCartRequest);

        // then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void removeFromCart() {
        // given
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername("testUser");
        modifyCartRequest.setItemId(itemList.get(0).getId());
        modifyCartRequest.setQuantity(1);

        when(userRepository.findByUsername("testUser")).thenReturn(user);
        when(itemRepository.findById(modifyCartRequest.getItemId())).thenReturn(Optional.of(itemList.get(0)));

        Cart expectedCart = new Cart();
        expectedCart.setId(1L);
        expectedCart.setUser(user);
        expectedCart.addItem(itemList.get(0));
        expectedCart.addItem(itemList.get(0));
        expectedCart.addItem(itemList.get(1));
        IntStream.range(0, modifyCartRequest.getQuantity())
                .forEach(i -> expectedCart.removeItem(itemList.get(0)));

        when(cartRepository.save(any(Cart.class))).thenReturn(expectedCart);

        // when
        ResponseEntity<Cart> response = cartController.removeFromCart(modifyCartRequest);

        // then
        verify(cart, times(modifyCartRequest.getQuantity())).removeItem(itemList.get(0));
        verify(cartRepository, times(1)).save(any(Cart.class));
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedCart, response.getBody());
    }
}