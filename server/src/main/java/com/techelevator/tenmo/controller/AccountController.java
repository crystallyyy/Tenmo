package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.Account;
import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransactionDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.exception.TenmoException;
import com.techelevator.tenmo.model.Transaction;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@PreAuthorize("isAuthenticated()")
@RestController
public class AccountController {
    private UserDao userDao;
    private AccountDao accountDao;
    private TransactionDao transactionDao;
    private Principal principal;
    private static final BigDecimal ZERO = new BigDecimal("0.00");


    public AccountController(UserDao userDao, AccountDao accountDao) {
        this.userDao = userDao;
        this.accountDao = accountDao;
    }

    @RequestMapping(path = "/balance", method = RequestMethod.GET)
    public List<Account> getBalance(Principal principal) {
        return accountDao.getAccounts(principal.getName());

    }


    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/transfer", method = RequestMethod.POST)
    public void createTransaction(@Valid @RequestBody Transaction transaction, Principal principal) {

        if (transaction.getAmount().compareTo(ZERO) == 1 && accountDao.getAccount(transaction.getUser_id()).getBalance().compareTo(transaction.getAmount()) == 1 || accountDao.getAccount(transaction.getUser_id()).getBalance().compareTo(transaction.getAmount()) == 0) {
            accountDao.transferTEBucks(transaction);
            accountDao.decreaseBal(transaction.getUser_id(), transaction.getAccount_id(), transaction.getAmount());

            accountDao.addBal(transaction.getTarget_id(), accountDao.getPrimaryAccount(transaction.getTarget_id()).getAccount_id(), transaction.getAmount());
        } else {
            throw new TenmoException("Amount must be greater than 0");

        }

    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/request", method = RequestMethod.POST)
    public void requestTransfer(@Valid @RequestBody Transaction transaction){
        //Cannot Request from same user
        if (transaction.getUser_id() != transaction.getTarget_id()) {
            accountDao.requestTEBucks(transaction);
        } else {
            throw new RuntimeException("Please input a different target user!");
        }

    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/create", method = RequestMethod.POST)

    public Account create(@Valid @RequestBody Account account, Principal principal) {
        //User_id must be the one logged in to create account

        return accountDao.createAccount(account, principal.getName());


    }
}
