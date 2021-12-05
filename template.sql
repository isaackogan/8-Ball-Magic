CREATE TABLE IF NOT EXISTS who_has_question(id bigint NOT NULL PRIMARY KEY AUTO_INCREMENT, guild_id bigint NOT NULL, message TEXT NOT NULL);
CREATE TABLE IF NOT EXISTS why_question(id bigint NOT NULL PRIMARY KEY AUTO_INCREMENT, guild_id bigint NOT NULL, message TEXT NOT NULL);
CREATE TABLE IF NOT EXISTS gay_question(id bigint NOT NULL PRIMARY KEY AUTO_INCREMENT, guild_id bigint NOT NULL, message TEXT NOT NULL);
CREATE TABLE IF NOT EXISTS too_short_is_question (id bigint NOT NULL PRIMARY KEY AUTO_INCREMENT,guild_id bigint NOT NULL, message TEXT NOT NULL);
CREATE TABLE IF NOT EXISTS too_short_not_question(id bigint NOT NULL PRIMARY KEY AUTO_INCREMENT, guild_id bigint NOT NULL, message TEXT NOT NULL);
CREATE TABLE IF NOT EXISTS good_length_not_question(id bigint NOT NULL PRIMARY KEY AUTO_INCREMENT, guild_id bigint NOT NULL, message TEXT NOT NULL);
CREATE TABLE IF NOT EXISTS good_length_is_question(id bigint NOT NULL PRIMARY KEY AUTO_INCREMENT, guild_id bigint NOT NULL, message TEXT NOT NULL);
CREATE TABLE IF NOT EXISTS who_is_question(id bigint NOT NULL PRIMARY KEY AUTO_INCREMENT, guild_id bigint NOT NULL, message TEXT NOT NULL);