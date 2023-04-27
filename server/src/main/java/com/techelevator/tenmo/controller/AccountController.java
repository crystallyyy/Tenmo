package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.Account;
import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.UserDao;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;

@PreAuthorize("isAuthenticated()")
@RestController
public class AccountController {
    private UserDao userDao;
    private AccountDao accountDao;
    private Principal principal;


    public AccountController(UserDao userDao, AccountDao accountDao) {
        this.userDao = userDao;
        this.accountDao = accountDao;
    }

    @RequestMapping(path = "/balance", method = RequestMethod.GET)
    public Account getBalance(Principal principal) {
        return accountDao.getAccount(principal);

    }

    @RequestMapping(path = "/transfer", method = RequestMethod.PUT)
    public Account transferBucks(@PathVariable(value = "target_id") int id, @RequestBody Account account, @PathVariable(value = "amount") BigDecimal amountToTransfer){
        Account updatedAccount = accountDao.transferTEBucks(id, account, amountToTransfer);
        if(updatedAccount == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "account id not found");
        }

        return updatedAccount;
    }

//    @ResponseStatus(HttpStatus.CREATED)
//    @RequestMapping(path = "/request", method = RequestMethod.POST)
//    public void requestTransfer(@PathVariable("user_id") int id, @PathVariable BigDecimal amount,
//                                @PathVariable(value = "date_and_time") LocalDate date, @PathVariable(value = "target_id") int targetId){
//        return accountDao.requestTEBucks(id, amount, date, targetId);
//    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/create", method = RequestMethod.POST)
    public boolean create(@Valid @RequestBody Account account) {
        return accountDao.createAccount(account);
    }
}
