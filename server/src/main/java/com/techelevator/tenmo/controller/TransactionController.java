package com.techelevator.tenmo.controller;
import com.techelevator.tenmo.dao.Account;
import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransactionDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Transaction;
import com.techelevator.tenmo.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;



@RestController
@PreAuthorize("isAuthenticated()")
public class TransactionController {
    private UserDao userDao;
    private TransactionDao transactionDao;
    private AccountDao accountDao;
    private Principal principal;

    public TransactionController(UserDao userDao, TransactionDao transactionDao, AccountDao accountDao) {
        this.userDao = userDao;
        this.transactionDao = transactionDao;
        this.accountDao = accountDao;

    }

    @RequestMapping(path = "/transfer", method = RequestMethod.GET)
    public List<User> listUsers() {
        return userDao.findAll();
    }

    //List All transactions for the specific user
    @RequestMapping(path = "/transactions", method = RequestMethod.GET)
    public List<Transaction> listAllTransactions() {
        //Check username
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipal = authentication.getName();

        //TODO: case of no currentPrincipal because user is not logged in
        return transactionDao.listTransactions(currentPrincipal);
    }


    // transaction specific
    //TODO: not good practice
    @RequestMapping(path = "/transactions/{id}", method = RequestMethod.GET)
    public Transaction getTransaction(@PathVariable int id) {
        return transactionDao.getTransaction(id);
    }

    @RequestMapping(path = "/balance", method = RequestMethod.GET)
    public Account getBalance(Principal principal) {
        return accountDao.getAccount(principal);

    }
}