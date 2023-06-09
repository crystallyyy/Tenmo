BEGIN TRANSACTION;

DROP TABLE IF EXISTS tenmo_user, account, transactions;

DROP SEQUENCE IF EXISTS seq_user_id, seq_account_id, seq_transaction_id;

-- Sequence to start user_id values at 1001 instead of 1
CREATE SEQUENCE seq_user_id
  INCREMENT BY 1
  START WITH 1001
  NO MAXVALUE;

CREATE TABLE tenmo_user (
	user_id int NOT NULL DEFAULT nextval('seq_user_id'),
	username varchar(50) NOT NULL,
	password_hash varchar(200) NOT NULL,
	CONSTRAINT PK_tenmo_user PRIMARY KEY (user_id),
	CONSTRAINT UQ_username UNIQUE (username)
);

-- Sequence to start account_id values at 2001 instead of 1
-- Note: Use similar sequences with unique starting values for additional tables
CREATE SEQUENCE seq_account_id
  INCREMENT BY 1
  START WITH 2001
  NO MAXVALUE;

CREATE TABLE account (
	account_id int NOT NULL DEFAULT nextval('seq_account_id'),
	user_id int NOT NULL,
	balance numeric(13, 2) NOT NULL,
	is_primary boolean NOT NULL,
	CONSTRAINT PK_account PRIMARY KEY (account_id),
	CONSTRAINT FK_account_tenmo_user FOREIGN KEY (user_id) REFERENCES tenmo_user (user_id)
);

CREATE SEQUENCE seq_transaction_id
  INCREMENT BY 1
  START WITH 3001
  NO MAXVALUE;

CREATE TABLE transactions (
	transaction_id int NOT NULL DEFAULT nextval('seq_transaction_id'),
	user_id int NOT NULL,
	account_id int NOT NULL,
	amount money NOT NULL,
	target_id int NOT NULL,
	status varchar NOT NULL DEFAULT 'Approved',
	CONSTRAINT pk_transaction PRIMARY KEY (transaction_id),
	CONSTRAINT fk_transactions_tenmo_user FOREIGN KEY (user_id) REFERENCES tenmo_user (user_id),
	CONSTRAINT fk_transactions_account FOREIGN KEY (account_id) REFERENCES account (account_id),
	CONSTRAINT chk_amount CHECK (amount > cast(0 as money))
);

COMMIT;

INSERT INTO tenmo_user (username, password_hash) VALUES ('dumbass', 'dumbass123');
INSERT INTO account (user_id, balance) VALUES ((SELECT user_id FROM tenmo_user WHERE username = 'dumbass'), 1000);
INSERT INTO transactions (account_id, amount, date_and_time, target_id, status) VALUES ((SELECT account_id from account JOIN tenmo_user as t ON t.user_id = account.user_id WHERE username = 'dumbass'), 500, '04/26/2023', 5, DEFAULT);

INSERT INTO account (user_id, balance) VALUES ((SELECT user_id FROM tenmo_user WHERE username = 'bobert'), 1000);
INSERT INTO transactions (account_id, amount, date_and_time, target_id, status) VALUES ((SELECT account_id from account JOIN tenmo_user as t ON t.user_id = account.user_id WHERE username = 'bobert'), 500, '04/26/2023', 5, DEFAULT);
INSERT INTO transactions (account_id, amount, date_and_time, target_id, status) VALUES ((SELECT account_id from account JOIN tenmo_user as t ON t.user_id = account.user_id WHERE username = 'bobert'), 700, '04/26/2023', 5, DEFAULT);

SELECT * FROM account
SELECT * FROM transactions;
SELECT tenmo_user.user_id FROM tenmo_user JOIN account ON tenmo_user.user_id = account.user_id WHERE account_id = 2003;

