SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema choose_your_fate_test
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema choose_your_fate_test
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `choose_your_fate_test` DEFAULT CHARACTER SET utf8mb3 ;
USE `choose_your_fate_test` ;

-- -----------------------------------------------------
-- Table `choose_your_fate_test`.`account`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `choose_your_fate_test`.`account` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(50) NOT NULL UNIQUE,
  `character_limit` INT NOT NULL,
  `email` VARCHAR(100) NOT NULL UNIQUE,
  `password` VARCHAR(100) NOT NULL,
  `role` VARCHAR(50) NOT NULL,
  PRIMARY KEY (`id`)
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;

-- -----------------------------------------------------
-- Table `choose_your_fate_test`.`audit_log`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `choose_your_fate_test`.`audit_log` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `table_name` VARCHAR(100) NOT NULL,
  `entity_id` VARCHAR(100) NOT NULL,
  `action_type` VARCHAR(20) NOT NULL,
  `database_user` VARCHAR(100) NOT NULL,
  `changed_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `old_data` JSON NULL,
  `new_data` JSON NULL,
  PRIMARY KEY (`id`),
  INDEX `idx_audit_log_table_name` (`table_name` ASC) VISIBLE,
  INDEX `idx_audit_log_entity_id` (`entity_id` ASC) VISIBLE,
  INDEX `idx_audit_log_changed_at` (`changed_at` ASC) VISIBLE
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;

-- -----------------------------------------------------
-- Table `choose_your_fate_test`.`chapter`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `choose_your_fate_test`.`chapter` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NULL DEFAULT NULL,
  `starting_scene_id` INT NULL, -- Has to not be NOT NULL, since it otherwise creates a loop with scenes table which needs a chapter to be created
  PRIMARY KEY (`id`),
  INDEX `starting_scene_idx` (`starting_scene_id` ASC) VISIBLE,
  CONSTRAINT `fk_starting_scene_chapter`
    FOREIGN KEY (`starting_scene_id`)
    REFERENCES `choose_your_fate_test`.`scene` (`id`)
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;

-- -----------------------------------------------------
-- Table `choose_your_fate_test`.`race_details`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `choose_your_fate_test`.`race_details` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT 'BOB rules them all!',
  `name` VARCHAR(100) NULL DEFAULT NULL,
  `starting_chapter_id` INT NOT NULL, -- needed to determine where the character starts on creating new character
  PRIMARY KEY (`id`),
  INDEX `starting_chapter_idx` (`starting_chapter_id` ASC) VISIBLE,
  CONSTRAINT `fk_starting_chapter_race_details`
    FOREIGN KEY (`starting_chapter_id`)
    REFERENCES `choose_your_fate_test`.`chapter` (`id`)
  )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `choose_your_fate_test`.`scene`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `choose_your_fate_test`.`scene` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `chapter_id` INT NOT NULL,
  `name` VARCHAR(100) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `chapter_id_idx` (`chapter_id` ASC) VISIBLE,
  CONSTRAINT `fk_scene_chapter`
    FOREIGN KEY (`chapter_id`)
    REFERENCES `choose_your_fate_test`.`chapter` (`id`)
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;

-- -----------------------------------------------------
-- Table `choose_your_fate_test`.`character_avatar`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `choose_your_fate_test`.`character_avatar` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `account_id` INT NOT NULL,
  `chapter_id` INT NOT NULL,
  `scene_id` INT NOT NULL,
  `race_detail_id` INT NOT NULL,
  `name` VARCHAR(50) NOT NULL,
  `flag` JSON NOT NULL COMMENT 'Lost/gain of rep, debuff/buff, BOB?... etc',
  PRIMARY KEY (`id`),
  INDEX `player_id_idx` (`account_id` ASC) VISIBLE,
  INDEX `chapter_id_idx` (`chapter_id` ASC) VISIBLE,
  INDEX `scene_id_idx` (`scene_id` ASC) VISIBLE,
  INDEX `race_detail_id_idx` (`race_detail_id` ASC) VISIBLE,
  CONSTRAINT `fk_chapter_character_avatar`
    FOREIGN KEY (`chapter_id`)
    REFERENCES `choose_your_fate_test`.`chapter` (`id`),
  CONSTRAINT `fk_player_character_avatar`
    FOREIGN KEY (`account_id`)
    REFERENCES `choose_your_fate_test`.`account` (`id`),
  CONSTRAINT `fk_race_detail_character_avatar`
    FOREIGN KEY (`race_detail_id`)
    REFERENCES `choose_your_fate_test`.`race_details` (`id`),
  CONSTRAINT `fk_scene_character_avatar`
    FOREIGN KEY (`scene_id`)
    REFERENCES `choose_your_fate_test`.`scene` (`id`)
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;

-- -----------------------------------------------------
-- Table `choose_your_fate_test`.`character_details`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `choose_your_fate_test`.`character_details` (
  `character_id` INT NOT NULL,
  `intelligence` INT NULL DEFAULT NULL,
  `charisma` INT NULL DEFAULT NULL,
  `fashion` INT NULL DEFAULT NULL,
  PRIMARY KEY (`character_id`),
  CONSTRAINT `fk_character_details_character`
    FOREIGN KEY (`character_id`)
    REFERENCES `choose_your_fate_test`.`character_avatar` (`id`)
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;

-- -----------------------------------------------------
-- Table `choose_your_fate_test`.`quest`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `choose_your_fate_test`.`quest` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `scene_id` INT NOT NULL,
  `description` MEDIUMTEXT NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `scene_id_idx` (`scene_id` ASC) VISIBLE,
  CONSTRAINT `fk_quest_scene`
    FOREIGN KEY (`scene_id`)
    REFERENCES `choose_your_fate_test`.`scene` (`id`)
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;

-- -----------------------------------------------------
-- Table `choose_your_fate_test`.`character_has_quest`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `choose_your_fate_test`.`character_has_quest` (
  `character_id` INT NOT NULL,
  `quest_id` INT NOT NULL,
  `status` TINYINT NOT NULL,
  PRIMARY KEY (`character_id`, `quest_id`),
  INDEX `quest_id_idx` (`quest_id` ASC) VISIBLE,
  CONSTRAINT `fk_character_has_quest_character`
    FOREIGN KEY (`character_id`)
    REFERENCES `choose_your_fate_test`.`character_avatar` (`id`),
  CONSTRAINT `fk_character_has_quest_quest`
    FOREIGN KEY (`quest_id`)
    REFERENCES `choose_your_fate_test`.`quest` (`id`)
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;

-- -----------------------------------------------------
-- Table `choose_your_fate_test`.`character_path`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `choose_your_fate_test`.`character_path` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `character_id` INT NOT NULL,
  `summary` LONGTEXT NULL DEFAULT NULL,
  `audio_blob` mediumblob,
  `summary_updated_at` datetime DEFAULT NULL,
  `audio_blob_updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `character_id_idx` (`character_id` ASC) VISIBLE,
  CONSTRAINT `fk_character_path_character`
    FOREIGN KEY (`character_id`)
    REFERENCES `choose_your_fate_test`.`character_avatar` (`id`)
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;

-- -----------------------------------------------------
-- Table `choose_your_fate_test`.`choice`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `choose_your_fate_test`.`choice` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `scene_id` INT NOT NULL,
  `destination_scene_id` INT NULL DEFAULT NULL,
  `description` MEDIUMTEXT NULL DEFAULT NULL,
  `consequence` VARCHAR(50) NULL,
  `target_id` INT NULL COMMENT 'Give quest, give item(s)... etc',
  `value_int` INT NULL COMMENT 'Stat change, lose hp, fashion!!!... etc\n\n\n\n\nze BOB race!',
  `story_weight` SMALLINT(255) NOT NULL COMMENT 'Value/weight of the recap or story telling for the AI to use.',
  `requirements` JSON NULL COMMENT 'Requirements is what gives or takes away choices/ quests because you either meet the requirements or dont',
  PRIMARY KEY (`id`),
  INDEX `scene_id_idx` (`scene_id` ASC) VISIBLE,
  INDEX `fk_choice_destination_scene_idx` (`destination_scene_id` ASC) VISIBLE,
  CONSTRAINT `fk_choice_destination_scene`
    FOREIGN KEY (`destination_scene_id`)
    REFERENCES `choose_your_fate_test`.`scene` (`id`),
  CONSTRAINT `fk_choice_scene`
    FOREIGN KEY (`scene_id`)
    REFERENCES `choose_your_fate_test`.`scene` (`id`)
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;

-- -----------------------------------------------------
-- Table `choose_your_fate_test`.`character_path_choice`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `choose_your_fate_test`.`character_path_choice` (
  `character_path_id` INT NOT NULL,
  `choice_id` INT NOT NULL,
  PRIMARY KEY (`character_path_id`, `choice_id`),
  INDEX `choice_id_idx` (`choice_id` ASC) VISIBLE,
  CONSTRAINT `fk_character_path_choice_character_path`
    FOREIGN KEY (`character_path_id`)
    REFERENCES `choose_your_fate_test`.`character_path` (`id`),
  CONSTRAINT `fk_character_path_choice_choice`
    FOREIGN KEY (`choice_id`)
    REFERENCES `choose_your_fate_test`.`choice` (`id`)
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;

-- -----------------------------------------------------
-- Table `choose_your_fate_test`.`item`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `choose_your_fate_test`.`item` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NULL DEFAULT NULL,
  `description` TEXT NULL DEFAULT NULL,
  `type` VARCHAR(45) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `idx_item_type` (`type` ASC) VISIBLE
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;

-- -----------------------------------------------------
-- Table `choose_your_fate_test`.`equipment`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `choose_your_fate_test`.`equipment` (
  `character_id` INT NOT NULL,
  `head` INT NULL DEFAULT NULL,
  `legs` INT NULL DEFAULT NULL,
  `chest` INT NULL DEFAULT NULL,
  PRIMARY KEY (`character_id`),
  INDEX `fk_equipment_character1_idx` (`character_id` ASC) VISIBLE,
  INDEX `fk_equipment_item_idx` (`head` ASC) VISIBLE,
  INDEX `fk_equipment_chest_idx` (`chest` ASC) VISIBLE,
  INDEX `fk_equipment_legs_idx` (`legs` ASC) VISIBLE,
  CONSTRAINT `fk_equipment_character`
    FOREIGN KEY (`character_id`)
    REFERENCES `choose_your_fate_test`.`character_avatar` (`id`),
  CONSTRAINT `fk_equipment_chest`
    FOREIGN KEY (`chest`)
    REFERENCES `choose_your_fate_test`.`item` (`id`),
  CONSTRAINT `fk_equipment_head`
    FOREIGN KEY (`head`)
    REFERENCES `choose_your_fate_test`.`item` (`id`),
  CONSTRAINT `fk_equipment_legs`
    FOREIGN KEY (`legs`)
    REFERENCES `choose_your_fate_test`.`item` (`id`)
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;

-- -----------------------------------------------------
-- Table `choose_your_fate_test`.`inventory`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `choose_your_fate_test`.`inventory` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `character_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_inventory_character1_idx` (`character_id` ASC) VISIBLE,
  CONSTRAINT `fk_inventory_character`
    FOREIGN KEY (`character_id`)
    REFERENCES `choose_your_fate_test`.`character_avatar` (`id`)
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;

-- -----------------------------------------------------
-- Table `choose_your_fate_test`.`inventory_has_item`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `choose_your_fate_test`.`inventory_has_item` (
  `item_id` INT NOT NULL,
  `inventory_id` INT NOT NULL,
  `amount` INT NULL DEFAULT NULL,
  PRIMARY KEY (`item_id`, `inventory_id`),
  INDEX `fk_item_has_inventory_inventory1_idx` (`inventory_id` ASC) VISIBLE,
  INDEX `fk_item_has_inventory_item1_idx` (`item_id` ASC) VISIBLE,
  CONSTRAINT `fk_inventory_has_item_inventory`
    FOREIGN KEY (`inventory_id`)
    REFERENCES `choose_your_fate_test`.`inventory` (`id`),
  CONSTRAINT `fk_inventory_has_item_item`
    FOREIGN KEY (`item_id`)
    REFERENCES `choose_your_fate_test`.`item` (`id`)
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;

-- -----------------------------------------------------
-- Table `choose_your_fate_test`.`npc`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `choose_your_fate_test`.`npc` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `race_details_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_race_details_idx` (`race_details_id` ASC) VISIBLE,
  CONSTRAINT `fk_npc_race_details`
    FOREIGN KEY (`race_details_id`)
    REFERENCES `choose_your_fate_test`.`race_details` (`id`)
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;

-- -----------------------------------------------------
-- Table `choose_your_fate_test`.`quest_has_item`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `choose_your_fate_test`.`quest_has_item` (
  `quest_id` INT NOT NULL,
  `item_id` INT NOT NULL,
  PRIMARY KEY (`quest_id`, `item_id`),
  INDEX `fk_quest_has_item_item1_idx` (`item_id` ASC) VISIBLE,
  INDEX `fk_quest_has_item_quest1_idx` (`quest_id` ASC) VISIBLE,
  CONSTRAINT `fk_quest_has_item_item`
    FOREIGN KEY (`item_id`)
    REFERENCES `choose_your_fate_test`.`item` (`id`),
  CONSTRAINT `fk_quest_has_item_quest`
    FOREIGN KEY (`quest_id`)
    REFERENCES `choose_your_fate_test`.`quest` (`id`)
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;

-- -----------------------------------------------------
-- Table `choose_your_fate_test`.`scene_has_npc`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `choose_your_fate_test`.`scene_has_npc` (
  `scene_id` INT NOT NULL,
  `npc_id` INT NOT NULL,
  PRIMARY KEY (`scene_id`, `npc_id`),
  INDEX `fk_scene_has_npc_npc1_idx` (`npc_id` ASC) VISIBLE,
  INDEX `fk_scene_has_npc_scene1_idx` (`scene_id` ASC) VISIBLE,
  CONSTRAINT `fk_scene_has_npc_npc`
    FOREIGN KEY (`npc_id`)
    REFERENCES `choose_your_fate_test`.`npc` (`id`),
  CONSTRAINT `fk_scene_has_npc_scene`
    FOREIGN KEY (`scene_id`)
    REFERENCES `choose_your_fate_test`.`scene` (`id`)
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;

-- -----------------------------------------------------
-- Table `choose_your_fate_test`.`choice_has_item`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `choose_your_fate_test`.`choice_has_item` (
  `choice_id` INT NOT NULL,
  `item_id` INT NOT NULL,
  PRIMARY KEY (`choice_id`, `item_id`),
  INDEX `fk_choice_has_item_item1_idx` (`item_id` ASC) VISIBLE,
  INDEX `fk_choice_has_item_choice1_idx` (`choice_id` ASC) VISIBLE,
  CONSTRAINT `fk_choice_has_item_choice1`
    FOREIGN KEY (`choice_id`)
    REFERENCES `choose_your_fate_test`.`choice` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_choice_has_item_item1`
    FOREIGN KEY (`item_id`)
    REFERENCES `choose_your_fate_test`.`item` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;

USE `choose_your_fate_test`;

-- -----------------------------------------------------
-- View `choose_your_fate_test`.`v_character`
-- -----------------------------------------------------
DROP VIEW IF EXISTS `choose_your_fate_test`.`v_character`;
USE `choose_your_fate_test`;
CREATE  OR REPLACE VIEW `v_character` AS (
    SELECT avatar.name, deets.intelligence, deets.charisma, deets.fashion, avatar.flag
    FROM character_details deets
    JOIN character_avatar avatar
      ON deets.character_id = avatar.id
        );


DELETE FROM character_path_choice;
DELETE FROM character_has_quest;
DELETE FROM inventory_has_item;
DELETE FROM choice_has_item;
DELETE FROM quest_has_item;
DELETE FROM scene_has_npc;
DELETE FROM character_path;
DELETE FROM inventory;
DELETE FROM character_details;
DELETE FROM equipment;
DELETE FROM character_avatar;
DELETE FROM choice;
DELETE FROM quest;
DELETE FROM npc;
DELETE FROM item;
DELETE FROM scene;
DELETE FROM chapter;
DELETE FROM account;
DELETE FROM race_details;

ALTER TABLE account AUTO_INCREMENT = 1;
ALTER TABLE chapter AUTO_INCREMENT = 1;
ALTER TABLE race_details AUTO_INCREMENT = 1;
ALTER TABLE scene AUTO_INCREMENT = 1;
ALTER TABLE character_avatar AUTO_INCREMENT = 1;
ALTER TABLE quest AUTO_INCREMENT = 1;
ALTER TABLE character_path AUTO_INCREMENT = 1;
ALTER TABLE choice AUTO_INCREMENT = 1;
ALTER TABLE item AUTO_INCREMENT = 1;
ALTER TABLE inventory AUTO_INCREMENT = 1;
ALTER TABLE npc AUTO_INCREMENT = 1;

INSERT INTO account (id, username, character_limit, email, password, role) VALUES
    (1, 'astra', 3, 'astra@chooseyourfate.dk', '$2b$10$Ruvc2k.FR5tK3GDvs2wU5OuIxZ/MBpvdCt.yPyYcQ9IQ5VVMX/08C', 'ROLE_USER'),
    (2, 'bjorn', 3, 'bjorn@chooseyourfate.dk', '$2b$10$JTD7.poDApqumMdqEb.hlODpLI/QVx2T1U7EYlE8prUm6XpabmU1G', 'ROLE_USER'),
    (3, 'cora', 9999, 'cora@chooseyourfate.dk', '$2b$10$iZ9YNxMMbTNypAe9bbc9iO2Gx6Vry//6EvkvaqzoaOQeZkslRQl42', 'ROLE_USER'),
    (4, 'admin', 9999, 'admin@chooseyourfate.dk', '$2b$10$oRncOoF8dWCtNtBanr/NxOVzSrK5ZxyHZthp9CIzo44W3fKgS..sK', 'ROLE_ADMIN');

INSERT INTO chapter (id, name, starting_scene_id) VALUES
    (1, 'The Festival Gate', NULL),
    (2, 'Moonlit Ruins', NULL),
    (3, 'bobs playground', NULL),
    (4, 'Tall forest, small people', NULL),
    (5, 'Small forest, tall people', NULL);

INSERT INTO race_details (id, name, starting_chapter_id) VALUES
    (1, 'Bobs', 3),
    (2, 'Bobiticus laviticus', 3),
    (3, 'smalls', 4),
    (4, 'TALLS', 5),
    (5, 'MeDiUmS', 2);

INSERT INTO scene (id, chapter_id, name) VALUES
    (1, 1, 'Town Gate'),
    (2, 2, 'Market Square'),
    (3, 3, 'Watchtower'),
    (4, 4, 'Forest Path'),
    (5, 5, 'Moon Shrine');

INSERT INTO item (id, name, description, type) VALUES
    (1, 'Rusty Sword', 'Old sword, still sharp enough for fights.', 'weapon'),
    (2, 'Bronze Helmet', 'Dented helmet from the city guard.', 'armor_head'),
    (3, 'Leather Jerkin', 'Light armor favored by scouts.', 'armor_chest'),
    (4, 'Traveler Pants', 'Sturdy pants with useful pockets.', 'armor_legs'),
    (5, 'Healing Potion', 'Restores strength and confidence.', 'consumable'),
    (6, 'Moon Key', 'Silver key etched with moon symbols.', 'quest'),
    (7, 'Festival Token', 'Brass token used for festival entry.', 'quest'),
    (8, 'Iron Helmet', 'Sturdy helmet forged by a city blacksmith.', 'armor_head'),
    (9, 'Chain Vest', 'Interlocked rings offer decent protection.', 'armor_chest'),
    (10, 'Iron Greaves', 'Heavy leg armor slowing your movement slightly.', 'armor_legs');

INSERT INTO npc (id, name, race_details_id) VALUES
    (1, 'Captain Elira', 1),
    (2, 'Merchant Vonn', 2),
    (3, 'Archivist Nyx', 3);

INSERT INTO scene_has_npc (scene_id, npc_id) VALUES
    (1, 1),
    (2, 2),
    (5, 3);

INSERT INTO quest (id, scene_id, description) VALUES
    (1, 1, 'Earn a festival token by helping the guard inspect travelers at the gate.'),
    (2, 2, 'Find the hidden moon key before the shrine doors are sealed at dusk.'),
    (3, 5, 'Return the moon key to Archivist Nyx and unlock the shrine archive.');

INSERT INTO quest_has_item (quest_id, item_id) VALUES
    (1, 7),
    (2, 6),
    (3, 5);

INSERT INTO choice (id, scene_id, destination_scene_id, description, consequence, target_id, value_int, story_weight, requirements) VALUES
    (1, 1, 2, 'Show the guard your invitation and step into the festival crowd.', 'gain_quest', 1, 1, 8, '{"requires":[],"grants":["festival-access"]}'),
    (2, 1, 3, 'Climb the watchtower to get a better view of the city.', 'gain_stat', NULL, 1, 5, '{"requires":[],"grants":["watchtower-visited"]}'),
    (3, 2, 4, 'Follow the suspicious footprints leading away from the market.', 'travel', 2, 0, 9, '{"requires":["festival-access"],"grants":["forest-route"]}'),
    (4, 3, 4, 'Leap down from the tower and cut through the forest trail.', 'lose_hp', NULL, -1, 7, '{"requires":["watchtower-visited"],"grants":["bruised"]}'),
    (5, 4, 5, 'Use the moon key to open the shrine gate.', 'complete_quest', 3, 1, 10, '{"requires":["item:6"],"grants":["shrine-open"]}'),
    (6, 2, 1, 'Return to the gate and report what you learned.', 'travel', NULL, 0, 4, '{"requires":[],"grants":[]}');

INSERT INTO choice_has_item (choice_id, item_id) VALUES
    (3, 6),
    (5, 5);

INSERT INTO character_avatar (id, account_id, chapter_id, scene_id, race_detail_id, name, flag) VALUES
    (1, 1, 1, 1, 1, 'Lyra', '{"reputation":{"guard":1},"statusEffects":[],"storyFlags":["festival-access"]}'),
    (2, 2, 1, 1, 2, 'Torben', '{"reputation":{"market":0},"statusEffects":[],"storyFlags":["watchtower-visited"]}'),
    (3, 3, 5, 5, 3, 'Mira', '{"reputation":{"archive":2},"statusEffects":[],"storyFlags":["shrine-open"]}');

INSERT INTO inventory (id, character_id) VALUES
    (1, 1),
    (2, 2),
    (3, 3);

INSERT INTO equipment (character_id, head, legs, chest) VALUES 
    (1, null, null, null),
    (2, null, null, null),
    (3, null, null, null);

UPDATE character_details
SET intelligence = 7, charisma = 6, fashion = 5
WHERE character_id = 1;

UPDATE character_details
SET intelligence = 4, charisma = 5, fashion = 7
WHERE character_id = 2;

UPDATE character_details
SET intelligence = 9, charisma = 8, fashion = 6
WHERE character_id = 3;

UPDATE character_path
SET summary = 'Lyra entered through the gate and started uncovering the mystery behind the moon shrine.'
WHERE character_id = 1;

UPDATE character_path
SET summary = 'Torben prefers scouting from high ground before taking risks.'
WHERE character_id = 2;

UPDATE character_path
SET summary = 'Mira has already reached the shrine and is close to resolving the archive questline.'
WHERE character_id = 3;

INSERT INTO character_has_quest (character_id, quest_id, status) VALUES
    (1, 1, 0),
    (1, 2, 0),
    (2, 1, 0),
    (3, 2, 0),
    (3, 3, 0);

UPDATE character_has_quest
SET status = 1
WHERE character_id = 1 AND quest_id = 1;

UPDATE character_has_quest
SET status = 1
WHERE character_id = 3 AND quest_id = 2;

UPDATE character_has_quest
SET status = 1
WHERE character_id = 3 AND quest_id = 3;

INSERT INTO inventory_has_item (item_id, inventory_id, amount) VALUES
    (1, 1, 1),
    (5, 1, 2),
    (2, 2, 1),
    (3, 3, 1),
    (4, 3, 1),
    (8, 3, 1),
    (9, 3, 1),
    (10, 3, 1)
ON DUPLICATE KEY UPDATE amount = VALUES(amount);

UPDATE equipment
SET head = 2, chest = 3, legs = 4
WHERE character_id = 3;

UPDATE chapter
SET starting_scene_id = 1
WHERE id = 1;

UPDATE chapter
SET starting_scene_id = 2
WHERE id = 2;

UPDATE chapter
SET starting_scene_id = 3
WHERE id = 3;

UPDATE chapter
SET starting_scene_id = 4
WHERE id = 4;

UPDATE chapter
SET starting_scene_id = 5
WHERE id = 5;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
