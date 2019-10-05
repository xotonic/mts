SET default_storage_engine=MEMORY;

CREATE TABLE IF NOT EXISTS users
(
    id   INTEGER PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(32) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS currencies
(
    code CHAR(3) PRIMARY KEY     NOT NULL
);

CREATE TABLE IF NOT EXISTS balances
(
    id       INTEGER PRIMARY KEY AUTO_INCREMENT,
    user_id  INTEGER NOT NULL,
    currency CHAR(3) NOT NULL,
    balance  DECIMAL(19, 4) NOT NULL DEFAULT 0,
    FOREIGN KEY (currency) REFERENCES currencies (code),
    FOREIGN KEY (user_id) REFERENCES users (id),
    UNIQUE (user_id, currency)
);


CREATE TABLE IF NOT EXISTS transactions
(
    id           INTEGER PRIMARY KEY AUTO_INCREMENT,
    sender       INTEGER NOT NULL,
    receiver     INTEGER NOT NULL,
    type         ENUM ('deposit', 'transfer')        NOT NULL,
    status       ENUM ('new', 'pending', 'finished') NOT NULL,
    time_created TIMESTAMP                           NOT NULL,
    currency     CHAR(3) NOT NULL,
    amount       DECIMAL(19, 4)                      NOT NULL,
    FOREIGN KEY (sender) REFERENCES users (id),
    FOREIGN KEY (receiver) REFERENCES users (id)
);


INSERT INTO currencies(code) VALUES ('USD'), ('EUR'), ('BTC'), ('ETH');

DELIMITER $
CREATE PROCEDURE send(
    IN sender VARCHAR(32),
    IN source_amount DECIMAL(19, 4),
    IN source_currency CHAR(3),
    IN receiver VARCHAR(32),
    IN target_amount DECIMAL(19, 4),
    IN target_currency CHAR(3)
)
BEGIN

    DECLARE EXIT HANDLER FOR SQLEXCEPTION BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    # Check input arguments
    IF ((SELECT COUNT(*) FROM users WHERE name = sender) != 1) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'The sender does not exist';
    ELSEIF ((SELECT COUNT(*) FROM users WHERE name = receiver) != 1) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'The receiver does not exist';
    ELSEIF ((SELECT COUNT(*) FROM currencies WHERE code = source_currency) != 1) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'The source currency does not exist';
    ELSEIF ((SELECT COUNT(*) FROM currencies WHERE code = target_currency) != 1) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'The target currency does not exist';
    END IF;

    START TRANSACTION;

    # Decrement balance of the sender
    UPDATE balances SET balance = balance - source_amount
    WHERE user_id = (SELECT id FROM users WHERE name = sender)
      AND currency = source_currency;


    # Increment balance of the receiver
    INSERT INTO balances(user_id, currency, balance)
    SELECT id, target_currency, target_amount
    FROM users
    WHERE name = receiver
    ON DUPLICATE KEY UPDATE balance = balance + target_amount;

    COMMIT;

END $
DELIMITER ;
