

DROP TABLE business;
CREATE TABLE business (
  business_id INT(8) NOT NULL AUTO_INCREMENT,
  business_name VARCHAR(256) NOT NULL,
  PRIMARY KEY (business_id)
) ENGINE = InnoDB;

DROP TABLE shop;
CREATE TABLE shop (
  shop_id INT(12) NOT NULL AUTO_INCREMENT,
  shop_name VARCHAR(256) NOT NULL,
  address VARCHAR(256) NOT NULL,
  business_id INT(8) NOT NULL,
  PRIMARY KEY (shop_id),
  FOREIGN KEY (business_id) REFERENCES business(business_id)
) ENGINE = InnoDB;


DROP TABLE account_head;
CREATE TABLE account_head (
  head_id INT(12) NOT NULL AUTO_INCREMENT,
  head_name VARCHAR(256) NOT NULL,
  shop_id INT(12) NOT NULL,
  PRIMARY KEY (head_id),
  FOREIGN KEY (shop_id) REFERENCES shop(shop_id)
) ENGINE = InnoDB;

CREATE TABLE account (
  account_id INT(12) NOT NULL AUTO_INCREMENT,
  account_name VARCHAR(256) NOT NULL,
  head_id INT(12) NOT NULL,
  shop_id INT(12) NOT NULL,
  PRIMARY KEY (account_id),
  FOREIGN KEY (head_id) REFERENCES account_head(head_id),
  FOREIGN KEY (shop_id) REFERENCES shop(shop_id)
) ENGINE = InnoDB;


CREATE TABLE transaction (
  transaction_id INT(12) NOT NULL AUTO_INCREMENT,
  transaction_name VARCHAR(256) NOT NULL,
  account_id INT(12) NOT NULL,
  shop_id INT(12) NOT NULL,
  PRIMARY KEY (transaction_id),
  FOREIGN KEY (account_id) REFERENCES account(account_id),
  FOREIGN KEY (shop_id) REFERENCES shop(shop_id)
) ENGINE = InnoDB;


CREATE TABLE user (
  user_id INT(12) NOT NULL AUTO_INCREMENT,
  user_name VARCHAR(256) NOT NULL,
  password VARCHAR(256) NOT NULL,
  PRIMARY KEY (user_id)
) ENGINE = InnoDB;
insert INTO user(user_name, password) VALUES ('tizarah', 'tizarah');

CREATE TABLE working_shop (
  shop_id INT(12) NOT NULL,
  user_id INT(12) NOT NULL,
  PRIMARY KEY (shop_id, user_id),
  FOREIGN KEY (shop_id) REFERENCES shop(shop_id),
  FOREIGN KEY (user_id) REFERENCES user(user_id)
) ENGINE = InnoDB;



create table employee(
  id INT(3) NOT NULL AUTO_INCREMENT,
  name VARCHAR(256) NOT NULL,
  email VARCHAR(256) NOT NULL,
  company_name VARCHAR(256) NOT NULL,
  position VARCHAR(256) NOT NULL,
  PRIMARY KEY(email)
);