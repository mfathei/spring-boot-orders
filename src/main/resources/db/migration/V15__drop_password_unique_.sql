ALTER TABLE order_items
ALTER
COLUMN quantity TYPE DECIMAL USING (quantity::DECIMAL);

ALTER TABLE users
DROP
CONSTRAINT uc_users_password;