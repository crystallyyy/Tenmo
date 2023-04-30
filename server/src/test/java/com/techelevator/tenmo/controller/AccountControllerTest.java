package com.techelevator.tenmo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techelevator.tenmo.dao.Account;
import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.model.Transaction;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@RunWith(SpringRunner.class)
@WebMvcTest(AccountController.class)
public class AccountControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    private AccountDao dao;
    private JdbcTemplate jdbcTemplate;

    public AccountControllerTest() {
        dao = new JdbcAccountDao(jdbcTemplate);
    }

    @Test
    public void getBalance_should_return_status_ok() throws Exception {
        mvc.perform(get("/balance").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

    @Test
    public void getBalance_should_return_correct_count() throws Exception {
        //TODO: test with a username
        int count = dao.getAccounts("").size();
        mvc.perform(get("/balance").accept(MediaType.APPLICATION_JSON)).andExpect(jsonPath("$", hasSize(count)));
    }


    @Test
    public void transfer_should_throw_exception_when_request_body_dne() throws Exception {
        mvc.perform(post("/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void transfer_should_add_new_transaction_and_expect_status_created() throws Exception {
        Transaction transaction = new Transaction(3001, 1001, 2001, new BigDecimal(100.00), 1002, "Approved");
        mvc.perform(post("/transfer").contentType(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                .content(toJson(transaction))).andExpect(status().isCreated());
    }

    @Test
    public void request_should_throw_exception_when_request_body_dne() throws Exception {
        mvc.perform(post("/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void request_should_add_new_transaction_and_expect_status_created() throws Exception {
        Transaction transaction = new Transaction(3002, 1001, 2001, new BigDecimal(100.00), 1002, "Pending");

        mvc.perform(post("/request").contentType(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                .content(toJson(transaction))).andExpect(status().isCreated());
    }

    @Test
    public void create_should_throw_exception_when_request_body_dne() throws Exception {
        mvc.perform(post("/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void create_should_add_new_account_and_expect_status_created() throws Exception {
        Account account = new Account(1002, new BigDecimal(1500.00), false);

        mvc.perform(post("/create").contentType(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                .content(toJson(account))).andExpect(status().isCreated()).andExpect(jsonPath("$.account_id").value(3002));
    }


    private String toJson(Transaction transaction) throws JsonProcessingException {
        return mapper.writeValueAsString(transaction);
    }

    private String toJson(Account account) throws JsonProcessingException {
        return mapper.writeValueAsString(account);
    }
}