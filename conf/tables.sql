


CREATE TABLE business (
  business_id INT(8) NOT NULL AUTO_INCREMENT,
  business_name VARCHAR(256) NOT NULL,
  PRIMARY KEY (business_id)
) ENGINE = InnoDB;

INSERT INTO business(business_name) VALUES ("shahantrade");

CREATE TABLE shop (
  shop_id INT(12) NOT NULL AUTO_INCREMENT,
  shop_name VARCHAR(256) NOT NULL,
  address VARCHAR(256) NOT NULL,
  business_id INT(8) NOT NULL,
  PRIMARY KEY (shop_id),
  FOREIGN KEY (business_id) REFERENCES business(business_id)
) ENGINE = InnoDB;

INSERT INTO shop(shop_name, address, business_id) VALUES ("mill", "Lalmonirhat", 1);


CREATE TABLE account_head (
  head_id INT(12) NOT NULL AUTO_INCREMENT,
  head_name VARCHAR(256) NOT NULL,
  shop_id INT(12) NOT NULL,
  PRIMARY KEY (head_id),
  FOREIGN KEY (shop_id) REFERENCES shop(shop_id)
) ENGINE = InnoDB;

INSERT INTO account_head(head_name, shop_id) VALUES ("Paddy Head", 1);


CREATE TABLE account (
  account_id INT(12) NOT NULL AUTO_INCREMENT,
  account_name VARCHAR(256) NOT NULL,
  address VARCHAR(256) NOT NULL,
  phone_number VARCHAR(20) NOT NULL,
  head_id INT(12) NOT NULL,
  shop_id INT(12) NOT NULL,
  PRIMARY KEY (account_id),
  FOREIGN KEY (head_id) REFERENCES account_head(head_id),
  FOREIGN KEY (shop_id) REFERENCES shop(shop_id)
) ENGINE = InnoDB;

INSERT INTO account(account_name, head_id, shop_id) VALUES ("Md. Robiul Islam", 1, 1);

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
insert INTO user(user_name, password) VALUES ('tizarah', 'tizarah123');
insert INTO user(user_name, password) VALUES ('tizarah123', 'tizarah');

CREATE TABLE working_shop (
  shop_id INT(12) NOT NULL,
  user_id INT(12) NOT NULL,
  PRIMARY KEY (shop_id, user_id),
  FOREIGN KEY (shop_id) REFERENCES shop(shop_id),
  FOREIGN KEY (user_id) REFERENCES user(user_id)
) ENGINE = InnoDB;

INSERT INTO working_shop VALUES (1, 1);


create table employee(
  id INT(3) NOT NULL AUTO_INCREMENT,
  name VARCHAR(256) NOT NULL,
  email VARCHAR(256) NOT NULL,
  company_name VARCHAR(256) NOT NULL,
  position VARCHAR(256) NOT NULL,
  PRIMARY KEY(email)
)ENGINE = InnoDB;




create table `user` (
  `name` TEXT NOT NULL,
  `email` TEXT,
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY);


create table `room` (
  `title` TEXT NOT NULL,
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY);

create table `occupant` (
  `room` BIGINT NOT NULL,
  `user` BIGINT NOT NULL);

alter table `occupant` add constraint `occ_room_user_pk` primary key(`room`,`user`);

create table `message` (
  `sender` BIGINT NOT NULL,
  `content` TEXT NOT NULL,
  `ts` TIMESTAMP NOT NULL,
  `room` BIGINT,
  `to` BIGINT,
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY);

alter table `occupant` add constraint `occ_room_fk` foreign key(`room`)
references `room`(`id`) on update NO ACTION on delete NO ACTION;

alter table `occupant` add constraint `occ_user_fk` foreign key(`user`)
references `user`(`id`) on update NO ACTION on delete NO ACTION;

alter table `message` add constraint `msg_room_fk` foreign key(`room`)
references `room`(`id`) on update NO ACTION on delete NO ACTION;

alter table `message` add constraint `msg_sender_fk` foreign key(`sender`)
references `user`(`id`) on update NO ACTION on delete NO ACTION;

alter table `message` add constraint `msg_to_fk` foreign key(`to`)
references `user`(`id`) on update NO ACTION on delete NO ACTION;
