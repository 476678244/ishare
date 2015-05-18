use ishare;

CREATE TABLE IF NOT EXISTS `ishare`.`order_chatgroup_map` (
  `order_id` BIGINT NOT NULL,
  `chatgroup_id` VARCHAR(128) NULL,
  `delete_time` DATE NULL,
  CONSTRAINT `fk_order_chatgroupid_pool_in_process_order1`
    FOREIGN KEY (`order_id`)
    REFERENCES `ishare`.`pool_in_process_order` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;