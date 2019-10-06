# This database is memory-only
# The daemon creates some temporary dirs though

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
    sender_id       INTEGER NOT NULL,
    receiver_id     INTEGER NOT NULL,
    status       ENUM ('new', 'pending', 'finished')NOT NULL,
    time_created TIMESTAMP                          NOT NULL,
    dst_currency     CHAR(3)                        NOT NULL,
    src_currency     CHAR(3)                        NOT NULL,
    src_amount       DECIMAL(19, 4)                 NOT NULL,
    FOREIGN KEY (sender_id) REFERENCES users (id),
    FOREIGN KEY (receiver_id) REFERENCES users (id),
    FOREIGN KEY (dst_currency) REFERENCES currencies (code),
    FOREIGN KEY (src_currency) REFERENCES currencies (code)
);


INSERT INTO currencies(code) VALUES ('USD'), ('EUR'), ('BTC'), ('ETH');

# Atomically swaps balances between two account
# Updates the transaction state, making impossible to call it twice
# with the same txid

DELIMITER $
CREATE PROCEDURE commit_tx(IN txid INTEGER)
BEGIN

    DECLARE tx_count BOOLEAN;
    DECLARE _sender_id VARCHAR(32);
    DECLARE _receiver_id VARCHAR(32);
    DECLARE _src_amount DECIMAL(19, 4);
    DECLARE _src_currency CHAR(3);
    DECLARE _dst_currency CHAR(3);
    DECLARE _status ENUM('new', 'pending', 'finished');

    DECLARE EXIT HANDLER FOR SQLEXCEPTION BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    SELECT COUNT(*),
           sender_id,
           receiver_id,
           src_amount,
           src_currency,
           dst_currency,
           status
    FROM transactions
    WHERE id = txid
    INTO tx_count,
        _sender_id,
        _receiver_id,
        _src_amount,
        _src_currency,
        _dst_currency,
        _status;

    # Check tx existence and its state
    IF (tx_count = 0) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'tx_not_found';
    ELSEIF (_status != 'new') THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'wrong_tx_state';
    END IF;

    START TRANSACTION;

    # Decrement balance of the sender
    UPDATE balances SET balance = balance - _src_amount
    WHERE user_id = _sender_id
      AND currency = _src_currency;

    # Increment balance of the receiver
    INSERT INTO balances(user_id, currency, balance)
    VALUES (_receiver_id, _dst_currency, _src_amount)
    ON DUPLICATE KEY UPDATE balance = balance + _src_amount;

    UPDATE transactions SET status = 'finished' WHERE id = txid;

    COMMIT;

END $
DELIMITER ;
