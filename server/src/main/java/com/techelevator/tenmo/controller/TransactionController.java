package com.techelevator.tenmo.controller;
import com.techelevator.tenmo.dao.Account;
import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransactionDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.exception.TenmoException;
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

    @RequestMapping(path = "/user", method = RequestMethod.GET)
    public List<User> listUsers() {
        return userDao.findAll();
    }

    //List All transactions for the specific user
    @RequestMapping(path = "/transactions", method = RequestMethod.GET)
    public List<Transaction> listAllTransactions() {
        //Check username
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipal = authentication.getName();

        return transactionDao.listTransactions(currentPrincipal);
    }

    @RequestMapping(path = "/transactions/pending", method = RequestMethod.GET)
    public List<Transaction> getPendingTransactions(Principal principal){

        return transactionDao.getPendingTransactions(principal.getName());
    }

    @RequestMapping(path = "/transactions/{id}", method = RequestMethod.GET)
    public Transaction getTransaction(@PathVariable int id, Principal principal) {

        return transactionDao.getTransaction(id, principal.getName());
        //TODO: check for userID
    }
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/createtransaction", method = RequestMethod.POST)
    public void createTransaction(@RequestBody Transaction transaction, Principal principal) {
        transactionDao.createTransaction(transaction, principal.getName());
    }
    @RequestMapping(path = "/approverequest", method = RequestMethod.PUT)
    public void approveRequest(@RequestBody Transaction transaction, Principal principal){

        if(transaction.getUser_id() != transaction.getTarget_id()) {
            transactionDao.approveRequest(transaction, principal.getName());
        } else{
            throw new TenmoException("You are not authorized to approve this request.");
        }

        if(accountDao.getPrimaryAccount(transaction.getTarget_id()).getBalance().compareTo(transaction.getAmount()) != -1) {
            accountDao.decreaseBal(transaction.getTarget_id(), accountDao.getPrimaryAccount(transaction.getTarget_id()).getAccount_id(), transaction.getAmount());
        } else{
            throw new TenmoException("Insufficient funds. Cannot complete transaction.");
    }
        accountDao.addBal(transaction.getUser_id(), transaction.getAccount_id(), transaction.getAmount());
        
    }

    @RequestMapping(path = "/request/{transaction_id}", method = RequestMethod.DELETE)
    public void denyRequest(@PathVariable(value = "transaction_id") int transaction_id, Principal principal){
        transactionDao.denyRequest(transaction_id, principal.getName());
    }

}