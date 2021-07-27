package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderController orderController;

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
    void submitOrder() {
        // given
        String username = "testUser";
        UserOrder expectedOrder = UserOrder.createFromCart(user.getCart());
        when(userRepository.findByUsername(username)).thenReturn(user);
        when(orderRepository.save(expectedOrder)).thenReturn(expectedOrder);

        // when
        ResponseEntity<UserOrder> response = orderController.submitOrder(username);

        // then
        verify(orderRepository, times(1)).save(expectedOrder);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expectedOrder, response.getBody());
    }

    @Test
    void getOrdersForUser() {
        // given
        String username = "testUser";
        UserOrder expectedOrder = UserOrder.createFromCart(user.getCart());
        when(userRepository.findByUsername(username)).thenReturn(user);
        when(orderRepository.findByUser(user)).thenReturn(Collections.singletonList(expectedOrder));

        // when
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(username);

        // then
        verify(orderRepository, times(1)).findByUser(user);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Collections.singletonList(expectedOrder), response.getBody());
    }

}