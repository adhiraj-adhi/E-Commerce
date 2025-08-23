---- Insert a sample user
--INSERT INTO users (username, password) VALUES ('john', 'password123');
--
---- Insert roles for that user
--INSERT INTO authorities (role, user_id) VALUES ('ROLE_USER', 1);
--INSERT INTO authorities (role, user_id) VALUES ('ROLE_ADMIN', 1);



-- Insert Categories
INSERT INTO categories (category_name) VALUES ('Electronics');
INSERT INTO categories (category_name) VALUES ('Clothing');
INSERT INTO categories (category_name) VALUES ('Books');
INSERT INTO categories (category_name) VALUES ('Furniture');

-- Insert Products
INSERT INTO product (product_name, description, quantity, price, discount, special_price, category_id)
VALUES ('iPhone 14', 'Latest Apple iPhone', 10, 999.99, 5, 949.99, 1);

INSERT INTO product (product_name, description, quantity, price, discount, special_price, category_id)
VALUES ('T-Shirt', 'Cotton casual T-shirt', 50, 19.99, 10, 17.99, 2);

INSERT INTO product (product_name, description, quantity, price, discount, special_price, category_id)
VALUES ('Microwave Oven', '800W Kitchen microwave', 15, 120.00, 15, 102.00, 1);

INSERT INTO product (product_name, description, quantity, price, discount, special_price, category_id)
VALUES ('Harry Potter', 'Fantasy Novel', 100, 15.00, 20, 12.00, 3);

INSERT INTO product (product_name, description, quantity, price, discount, special_price, category_id)
VALUES ('Football', 'FIFA Approved Football', 30, 50.00, 10, 45.00, 2);