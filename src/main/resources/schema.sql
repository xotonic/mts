CREATE TABLE IF NOT EXISTS users
(
    id   INTEGER PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS currencies
(
    code CHAR(3) PRIMARY KEY NOT NULL,
    type ENUM('crypto', 'fiat') NOT NULL,
);

CREATE TABLE IF NOT EXISTS balances
(
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    user_id INTEGER,
    currency CHAR(3),
    balance DECIMAL(19, 4) NOT NULL DEFAULT 0,
    FOREIGN KEY (currency) REFERENCES currencies(code),
    FOREIGN KEY (user_id) REFERENCES users(id),
    UNIQUE (user_id, currency)
);

CREATE TABLE IF NOT EXISTS transactions
(
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    sender INTEGER,
    receiver INTEGER,
    type ENUM('deposit', 'transfer') NOT NULL,
    status ENUM('new', 'pending', 'finished') NOT NULL,
    time_created TIMESTAMP NOT NULL,
    currency CHAR(3),
    amount DECIMAL(19, 4) NOT NULL,
    FOREIGN KEY (sender) REFERENCES users(id),
    FOREIGN KEY (receiver) REFERENCES users(id),
);