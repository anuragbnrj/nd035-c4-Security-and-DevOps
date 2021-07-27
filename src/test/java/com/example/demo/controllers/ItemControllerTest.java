package com.example.demo.controllers;

import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemController itemController;

    private List<Item> itemList;

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
    }

    @Test
    void getItems() {
        // given
        when(itemRepository.findAll()).thenReturn(itemList);

        // when
        ResponseEntity<List<Item>> response = itemController.getItems();

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(itemList, response.getBody());
    }

    @Test
    void getItemById() {
        // given
        when(itemRepository.findById(1L)).thenReturn(Optional.of(itemList.get(0)));

        // when
        ResponseEntity<Item> response = itemController.getItemById(1L);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(itemList.get(0), response.getBody());
    }

    @Test
    void getItemsByName() {
        // given
        String itemName = "itemTwo";
        when(itemRepository.findByName(itemName)).thenReturn(Collections.singletonList(itemList.get(1)));

        // when
        ResponseEntity<List<Item>> response = itemController.getItemsByName(itemName);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(itemList.get(1), response.getBody().get(0));
    }

    @Test
    void getItemsByName_nameDoesNotExist_404() {
        // given
        String itemName = "itemDoesNotExist";
        when(itemRepository.findByName(itemName)).thenReturn(Collections.emptyList());

        // when
        ResponseEntity<List<Item>> response = itemController.getItemsByName(itemName);

        // then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

}