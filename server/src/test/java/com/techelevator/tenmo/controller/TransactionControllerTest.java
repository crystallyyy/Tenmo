package com.techelevator.tenmo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.techelevator.dao.BaseDaoTests;
import com.techelevator.tenmo.dao.JdbcUserDao;
import com.techelevator.tenmo.model.Transaction;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


import static com.techelevator.dao.LoginUtils.getTokenForLogin;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import static org.junit.Assert.*;
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TransactionControllerTest extends BaseDaoTests {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    private JdbcUserDao jdbcUserDao;

    @Before
    public void setUp() {
        SecurityContextHolder.clearContext();
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcUserDao = new JdbcUserDao(jdbcTemplate);
        jdbcUserDao.create("user", "password");
    }

    @Test
    public void methods_ExpectUnauthorized() throws Exception {
        Transaction transaction = new Transaction(3001, 1001, 2001, new BigDecimal("200.00"), 1002, "Approved");
        mvc.perform(get("/transactions")).andExpect(status().isUnauthorized());
        mvc.perform(get("/transactions/pending")).andExpect(status().isUnauthorized());
        mvc.perform(post("/createtransaction").contentType(MediaType.APPLICATION_JSON).content(toJson(transaction))).andExpect(status().isUnauthorized());
        mvc.perform(put("/approverequest").contentType(MediaType.APPLICATION_JSON).content(toJson(transaction))).andExpect(status().isUnauthorized());
    }
//    @Test
//    public void create() throws Exception {
//
//        Transaction transaction = new Transaction(3001, 1001, 2001, new BigDecimal("200.00"), 1002, "Approved");
//        final String userToken = getTokenForLogin("user", "password", mvc);
//
//        mvc.perform(post("/createtransactions").contentType(MediaType.APPLICATION_JSON).content(toJson(transaction)).header("Authorization", "Bearer " + userToken)).andExpect(status().isCreated());
//    }

    private String toJson(Transaction transaction) throws JsonProcessingException {
        return mapper.writeValueAsString(transaction);
    }
    private boolean isControllerSecure() throws InvocationTargetException, IllegalAccessException {
        boolean isControllerSecure = false;
        for (Annotation annotation : TransactionController.class.getAnnotations()) {
            Class<? extends Annotation> type = annotation.annotationType();
            if (type.getName().equals("org.springframework.security.access.prepost.PreAuthorize")) {
                for (Method method : type.getDeclaredMethods()) {
                    Object value = method.invoke(annotation, (Object[])null);
                    if (value.equals("isAuthenticated()")) {
                        isControllerSecure = true;
                        break;
                    }
                }
            }
        }
        return isControllerSecure;
    }

    private boolean isMethodSecure(String methodName) throws InvocationTargetException, IllegalAccessException {
        boolean isMethodSecure = false;
        for (Method method : TransactionController.class.getMethods()) {
            if (method.getName().contains(methodName)) {
                for (Annotation annotation : method.getAnnotations()) {
                    Class<? extends Annotation> type = annotation.annotationType();
                    if (type.getName().equals("org.springframework.security.access.prepost.PreAuthorize")) {
                        for (Method annotationMethod : type.getDeclaredMethods()) {
                            Object value = annotationMethod.invoke(annotation, (Object[])null);
                            if (value.toString().contains("hasRole") || value.toString().contains("hasAnyRole")) {
                                isMethodSecure = true;
                                break;
                            }
                        }
                    }
                }
            }
        }
        return isMethodSecure;
    }
}