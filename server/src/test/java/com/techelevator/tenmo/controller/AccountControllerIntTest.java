package com.techelevator.tenmo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.Assert.*;
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class AccountControllerIntTest {

    @Autowired
    AccountController controller;

    @Autowired
    ObjectMapper mapper;

    MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        System.out.println("setup()...");
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

}