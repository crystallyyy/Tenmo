package com.techelevator.tenmo.controller;
import com.techelevator.tenmo.dao.Account;
import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransactionDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Transaction;
import com.techelevator.tenmo.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;


//@PreAuthorize("isAuthenticated()")
@RestController
public class TransactionController {
    private UserDao userDao;
    private TransactionDao transactionDao;
    private AccountDao accountDao;

    public TransactionController (UserDao userDao, TransactionDao transactionDao, AccountDao accountDao) {
        this.userDao = userDao;
        this.transactionDao = transactionDao;
        this.accountDao = accountDao;

    }

    @RequestMapping(path = "/transfer", method = RequestMethod.GET)
    public List<User> listUsers() {
        return userDao.findAll();
    }

    @RequestMapping(path = "/transactions", method = RequestMethod.GET)
    public List<Transaction> listAllTransactions() {
        return transactionDao.listTransactions();
    }

    @RequestMapping(path = "/balance", method = RequestMethod.GET)
    public Account getBalance(Principal principal) {
        return accountDao.getAccount(principal);
    }
}
