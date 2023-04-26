# Module 2 Capstone - TEnmo

Congratulations—you've landed a job with TEnmo, whose product is an online payment service for transferring "TE bucks" between friends. However, they don't have a product yet. You've been tasked with finalizing the server side of the application: a database and a RESTful API server.

You will need to add controllers, models, DAOs, and database tables to implement the following features:

## Use cases

### Required use cases

You should attempt to complete all of the following required use cases.

1. **[COMPLETE]** As a user of the system, I need to be able to register myself with a username and password.
   1. The ability to register has been provided in your starter code.
2. **[COMPLETE]** As a user of the system, I need to be able to log in using my registered username and password.
   1. Logging in returns an Authentication Token. I need to include this token with all my subsequent interactions with the system outside of registering and logging in.
   2. The ability to log in has been provided in your starter code.
   3. User ids start at 1001.
3. As a user, when I register a new account is created for me.
   1. The new account has an initial balance of $1000.
   2. Account ids start at 2001.
4. As an authenticated user of the system, I need to be able to see my Account Balance.
5. As an authenticated user of the system, I need to be able to *send* a transfer of a specific amount of TE Bucks to a registered user.
   1. I should be able to choose from a list of users to send TE Bucks to.
   2. I must not be allowed to send money to myself.
   3. A transfer includes the usernames of the from and to users and the amount of TE Bucks.
   4. The receiver's account balance is increased by the amount of the transfer.
   5. The sender's account balance is decreased by the amount of the transfer.
   6. I can't send more TE Bucks than I have in my account.
   7. I can't send a zero or negative amount.
   8. A Sending Transfer has an initial status of *Approved*.
   9. Transfer ids start at 3001.
6. As an authenticated user of the system, I need to be able to see transfers I have sent or received.
7. As an authenticated user of the system, I need to be able to retrieve the details of any transfer based upon the transfer ID.

Validate all of the API's endpoints using Postman.  *No sensitive information (i.e. account numbers, user ids, etc) should be passed in the URL.*  Integration testing is required on each method that connects to the database -- remember WET!  Write everything at least twice!

###  Challenge use cases

8. As an authenticated user of the system, I need to be able to *request* a transfer of a specific amount of TE Bucks from another registered user.
   1. I should be able to choose from a list of users to request TE Bucks from.
   2. I must not be allowed to request money from myself.
   3. I can't request a zero or negative amount.
   4. A transfer includes the usernames of the from and to users and the amount of TE Bucks.
   5. A Request Transfer has an initial status of *Pending*.
   6. No account balance changes until the request is approved.
   7. The transfer request should appear in both users' list of transfers (use case #5).
9.  As an authenticated user of the system, I need to be able to see my *Pending* transfers.
10. As an authenticated user of the system, I need to be able to either approve or reject a Request Transfer.
   8. I can't "approve" a given Request Transfer for more TE Bucks than I have in my account.
   9. The Request Transfer status is *Approved* if I approve, or *Rejected* if I reject the request.
   10. If the transfer is approved, the requester's account balance is increased by the amount of the request.
   11. If the transfer is approved, the requestee's account balance is decreased by the amount of the request.
   12. If the transfer is rejected, no account balance changes.
   13. A single TEnmo user may choose to have multiple accounts associated with them (this will make the database relationship a one-to many relationship).

### Bonus Challenge

If you complete all of the required and challenge use cases and are looking for yet another challenge, create a Command Line Interface (CLI) client application for TEnmo. The file CLI.md contains sample user experiences for inspiration. 

## How to set up the database

Create a new Postgres database called `tenmo`. Run the `database/tenmo.sql` script in pgAdmin to set up the database that you'll begin to work from. You should make structure changes in this script and not the database directly. Additionally, both you and your team members need to run this script each time after making changes to it. 

## Database schema

The following tables are created by the provided `tenmo.sql` script. 

### `tenmo_user` table

Stores the login information for users of the system.

| Field           | Description                                                                    |
| --------------- | ------------------------------------------------------------------------------ |
| `user_id`       | Unique identifier of the user                                                  |
| `username`      | String that identifies the name of the user; used as part of the login process |
| `password_hash` | Hashed version of the user's password                                          |

### `account` table

Stores the accounts of users in the system.

| Field           | Description                                                             |
| --------------- | ----------------------------------------------------------------------- |
| `account_id`    | Unique identifier of the account                                        |
| `user_id`       | Foreign key to the `tenmo_user` table; identifies user who owns account |
| `balance`       | The amount of TE bucks currently in the account                       |

