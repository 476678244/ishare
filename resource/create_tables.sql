create database IF NOT EXISTS ishare DEFAULT CHARACTER SET 'utf8';

use `ishare`;

DROP TABLE IF EXISTS `ishare`.`message`;
DROP TABLE IF EXISTS `ishare`.`pool_order_joiner_map`;
DROP TABLE IF EXISTS `ishare`.`pool_order`;
DROP TABLE IF EXISTS `ishare`.`pool_order_prepare_joiner_map`;
DROP TABLE IF EXISTS `ishare`.`pool_order_prepare`;
DROP TABLE IF EXISTS `ishare`.`pool_history_order_joiner_map`;
DROP TABLE IF EXISTS `ishare`.`pool_history_order`;
DROP TABLE IF EXISTS `ishare`.`pool_in_process_order_joiner_map`;
DROP TABLE IF EXISTS `ishare`.`order_chatgroup_map`;
DROP TABLE IF EXISTS `ishare`.`pool_in_process_order`;
DROP TABLE IF EXISTS `ishare`.`pool_subject`;
DROP TABLE IF EXISTS `ishare`.`pool_joiner`;
DROP TABLE IF EXISTS `ishare`.`user_route_map`;
DROP TABLE IF EXISTS `ishare`.`route`;
DROP TABLE IF EXISTS `ishare`.`user_baidu_push`;
DROP TABLE IF EXISTS `ishare`.`user_token`;
DROP TABLE IF EXISTS `ishare`.`user`;
DROP TABLE IF EXISTS `ishare`.`payment`;
DROP TABLE IF EXISTS `ishare`.`car`;
DROP TABLE IF EXISTS `ishare`.`identity`;

CREATE TABLE IF NOT EXISTS `ishare`.`identity` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `identification_num` VARCHAR(45) NOT NULL,
  `real_name` VARCHAR(45) NOT NULL,
  `driver_license_front` VARCHAR(256) NULL,
  `driver_licese_back` VARCHAR(256) NULL,
  `status` VARCHAR(45) NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `ishare`.`car` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `paizhao` VARCHAR(45) NULL,
  `type` VARCHAR(45) NULL DEFAULT 'private_car',
  `taxi_company` VARCHAR(256) NULL,
  `employee_num` VARCHAR(45) NULL,
  `employee_identification_pic` VARCHAR(256) NULL,
  `status` VARCHAR(45) NULL,
  `driving_license_front` VARCHAR(256) NULL,
  `driving_license_back` VARCHAR(256) NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `ishare`.`payment` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `type` VARCHAR(45) NOT NULL,
  `account` VARCHAR(128) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `ishare`.`user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(128) NOT NULL,
  `password` VARCHAR(45) NOT NULL,
  `age` INT NULL,
  `gender` VARCHAR(32) NULL,
  `nickname` VARCHAR(256) NULL,
  `role` VARCHAR(45) NULL DEFAULT 'passenger',
  `job` VARCHAR(45) NULL,
  `charactor` VARCHAR(45) NULL,
  `payment_id` BIGINT NULL,
  `car_id` BIGINT NULL,
  `identity_id` BIGINT NULL,
  `head_pic` VARCHAR(256) NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_user_car1_idx` (`car_id` ASC),
  INDEX `fk_user_identity1_idx` (`identity_id` ASC),
  UNIQUE INDEX `username_UNIQUE` (`username` ASC),
  INDEX `fk_user_payment1_idx` (`payment_id` ASC),
  CONSTRAINT `fk_user_car1`
    FOREIGN KEY (`car_id`)
    REFERENCES `ishare`.`car` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_user_identity1`
    FOREIGN KEY (`identity_id`)
    REFERENCES `ishare`.`identity` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_user_payment1`
    FOREIGN KEY (`payment_id`)
    REFERENCES `ishare`.`payment` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE  TABLE IF NOT EXISTS `ishare`.`user_token` (
  `user_id` BIGINT NOT NULL ,
  `token` VARCHAR(256) NOT NULL ,
  `start_date` DATETIME NULL ,
  INDEX `fk_user_token_user1_idx` (`user_id` ASC) ,
  CONSTRAINT `fk_user_token_user1`
    FOREIGN KEY (`user_id` )
    REFERENCES `ishare`.`user` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `ishare`.`user_baidu_push` (
  `user_id` BIGINT NOT NULL,
  `baidu_user` VARCHAR(256) NULL,
  `baidu_channel` VARCHAR(256) NULL,
  INDEX `fk_user_baidu_push_user1_idx` (`user_id` ASC),
  PRIMARY KEY (`user_id`),
  CONSTRAINT `fk_user_baidu_push_user1`
    FOREIGN KEY (`user_id`)
    REFERENCES `ishare`.`user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `ishare`.`route` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `start_longtitude` BIGINT NULL,
  `start_latitude` BIGINT NULL,
  `start_address` VARCHAR(256) NULL,
  `end_longtitude` BIGINT NULL,
  `end_latitude` BIGINT NULL,
  `end_address` VARCHAR(256) NULL,
  `type` VARCHAR(45) NULL,
  `status` VARCHAR(45) NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `ishare`.`user_route_map` (
  `iduser_route_map` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `route_id` BIGINT NOT NULL,
  PRIMARY KEY (`iduser_route_map`),
  INDEX `fk_user_route_map_user1_idx` (`user_id` ASC),
  INDEX `fk_user_route_map_route1_idx` (`route_id` ASC),
  CONSTRAINT `fk_user_route_map_user1`
    FOREIGN KEY (`user_id`)
    REFERENCES `ishare`.`user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_user_route_map_route1`
    FOREIGN KEY (`route_id`)
    REFERENCES `ishare`.`route` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `ishare`.`pool_joiner` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NULL,
  `seats_count` INT NULL,
  `route_id` BIGINT NULL,
  `status` VARCHAR(45) NULL,
  `paid` TINYINT(1) NULL,
  `fee` INT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `ishare`.`pool_subject` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `gender` VARCHAR(45) NULL,
  `atmosphere` VARCHAR(45) NULL,
  `job` VARCHAR(45) NULL,
  `age` VARCHAR(45) NULL,
  `status` VARCHAR(45) NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;

CREATE  TABLE IF NOT EXISTS `ishare`.`pool_in_process_order` (
  `id` BIGINT NOT NULL AUTO_INCREMENT ,
  `order_type` VARCHAR(45) NULL ,
  `start_time` DATETIME NULL ,
  `total_seats` INT NULL ,
  `diver_user_id` BIGINT NULL ,
  `captain_user_id` BIGINT NULL ,
  `status` VARCHAR(45) NULL ,
  `pool_subject_id` BIGINT NULL ,
  `start_longtitude` BIGINT NULL ,
  `start_latitude` BIGINT NULL ,
  `last_middle_longtitude` BIGINT NULL ,
  `last_middle_latitude` BIGINT NULL ,
  `end_longtitude` BIGINT NULL ,
  `end_latitude` BIGINT NULL ,
  `start_address` VARCHAR(256) NULL ,
  `middle_address` VARCHAR(256) NULL ,
  `end_address` VARCHAR(256) NULL ,
  `likeTaxiOnly` TINYINT(1) NULL ,
  `note` VARCHAR(256) NULL ,
  `distance` BIGINT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `fk_pool_order_prepare_pool_subject1_idx` (`pool_subject_id` ASC) ,
  CONSTRAINT `fk_pool_order_prepare_pool_subject1`
    FOREIGN KEY (`pool_subject_id` )
    REFERENCES `ishare`.`pool_subject` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `ishare`.`pool_in_process_order_joiner_map` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `pool_joiner_id` BIGINT NULL,
  `pool_in_process_order_id` BIGINT NULL,
  `user_id` BIGINT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_pool_order_joiner_map_pool_joiner1_idx` (`pool_joiner_id` ASC),
  INDEX `fk_pool_in_process_order_joiner_map_pool_in_process_order1_idx` (`pool_in_process_order_id` ASC),
  CONSTRAINT `fk_pool_order_joiner_map_pool_joiner1`
    FOREIGN KEY (`pool_joiner_id`)
    REFERENCES `ishare`.`pool_joiner` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_pool_in_process_order_joiner_map_pool_in_process_order1`
    FOREIGN KEY (`pool_in_process_order_id`)
    REFERENCES `ishare`.`pool_in_process_order` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE  TABLE IF NOT EXISTS `ishare`.`pool_history_order` (
  `id` BIGINT NOT NULL AUTO_INCREMENT ,
  `order_type` VARCHAR(45) NULL ,
  `start_time` DATETIME NULL ,
  `total_seats` INT NULL ,
  `diver_user_id` BIGINT NULL ,
  `captain_user_id` BIGINT NULL ,
  `staus` VARCHAR(45) NULL ,
  `pool_subject_id` BIGINT NULL ,
  `start_longtitude` BIGINT NULL ,
  `start_latitude` BIGINT NULL ,
  `start_address` VARCHAR(256) NULL ,
  `end_longtitude` BIGINT NULL ,
  `end_latitude` BIGINT NULL ,
  `end_address` VARCHAR(256) NULL ,
  `likeTaxiOnly` TINYINT(1) NULL ,
  `note` VARCHAR(256) NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `fk_pool_order_pool_subject1_idx` (`pool_subject_id` ASC) ,
  CONSTRAINT `fk_pool_order_pool_subject1`
    FOREIGN KEY (`pool_subject_id` )
    REFERENCES `ishare`.`pool_subject` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `ishare`.`pool_history_order_joiner_map` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `pool_joiner_id` BIGINT NULL,
  `pool_history_order_id` BIGINT NULL,
  `user_id` BIGINT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_pool_order_joiner_map_pool_joiner2_idx` (`pool_joiner_id` ASC),
  INDEX `fk_pool_history_order_joiner_map_pool_history_order1_idx` (`pool_history_order_id` ASC),
  CONSTRAINT `fk_pool_order_joiner_map_pool_joiner2`
    FOREIGN KEY (`pool_joiner_id`)
    REFERENCES `ishare`.`pool_joiner` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_pool_history_order_joiner_map_pool_history_order1`
    FOREIGN KEY (`pool_history_order_id`)
    REFERENCES `ishare`.`pool_history_order` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;
CREATE  TABLE IF NOT EXISTS `ishare`.`message` (
  `id` BIGINT NOT NULL AUTO_INCREMENT ,
  `type` VARCHAR(45) NULL ,
  `content` VARCHAR(4096) NULL ,
  `from_user_name` VARCHAR(128) NULL DEFAULT 'system' ,
  `to_user_name` VARCHAR(128) NOT NULL ,
  `related_order` BIGINT NULL DEFAULT 0 ,
  PRIMARY KEY (`id`) ,
  INDEX `fk_message_user1_idx` (`to_user_name` ASC) ,
  CONSTRAINT `fk_message_user1`
    FOREIGN KEY (`to_user_name` )
    REFERENCES `ishare`.`user` (`username` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;