-- V2__modify_user_table_add_pass_field.sql

ALTER TABLE Usuario ADD COLUMN password VARCHAR(255) NOT NULL;