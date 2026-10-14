-- ============================================================================
-- MASTER CODE LIST (corrected) — every TABLE, VIEW, TRIGGER, and INDEX name
-- AND every ATTRIBUTE/COLUMN name is suffixed _012, per the instructor's
-- requirement. Matches the report exactly.
--
-- Setup is one big block (no screenshot needed). Every code after that is
-- a small SELECT (or action+proof) with its own screenshot destination.
-- Run in order, from an empty database. Nothing will ever error.
-- ============================================================================


-- ============================================================================
-- CODE 1: Build everything — tables, indexes, views, triggers, all sample
-- data, in one paste. No screenshot needed for this code itself.
-- ============================================================================
DROP DATABASE IF EXISTS db_uni_012;
CREATE DATABASE db_uni_012 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE db_uni_012;

CREATE TABLE suppliers_012 (
    supplier_id_012    INT           NOT NULL AUTO_INCREMENT,
    supplier_name_012  VARCHAR(100)  NOT NULL,
    contact_person_012 VARCHAR(50),
    phone_012          VARCHAR(20),
    contract_info_012  TEXT,
    CONSTRAINT pk_suppliers_012 PRIMARY KEY (supplier_id_012)
);

CREATE TABLE customers_012 (
    customer_id_012     INT           NOT NULL AUTO_INCREMENT,
    full_name_012       VARCHAR(50)   NOT NULL,
    email_012           VARCHAR(80)   NOT NULL,
    phone_012           VARCHAR(20),
    address_012         TEXT,
    member_discount_012 DECIMAL(3,2)  DEFAULT 0.00,
    total_points_012    INT           DEFAULT 0,
    user_role_012       VARCHAR(20)   NOT NULL,
    CONSTRAINT pk_customers_012  PRIMARY KEY (customer_id_012),
    CONSTRAINT uq_customers_email_012 UNIQUE (email_012)
);

CREATE TABLE products_012 (
    product_id_012        INT           NOT NULL AUTO_INCREMENT,
    product_name_012      VARCHAR(100)  NOT NULL,
    retail_price_012      DECIMAL(10,2) NOT NULL,
    purchase_cost_012     DECIMAL(10,2) NOT NULL,
    member_discount_012   DECIMAL(3,2)  NOT NULL,
    reorder_threshold_012 INT           NOT NULL,
    status_012            VARCHAR(20)   DEFAULT 'Available',
    supplier_id_012       INT,
    CONSTRAINT pk_products_012   PRIMARY KEY (product_id_012),
    CONSTRAINT fk_products_supplier_012
        FOREIGN KEY (supplier_id_012) REFERENCES suppliers_012(supplier_id_012)
);

CREATE TABLE inventory_012 (
    product_id_012        INT  NOT NULL,
    stock_qty_012         INT  NOT NULL,
    last_restock_date_012 DATE,
    CONSTRAINT pk_inventory_012 PRIMARY KEY (product_id_012),
    CONSTRAINT fk_inventory_product_012
        FOREIGN KEY (product_id_012) REFERENCES products_012(product_id_012)
);

CREATE TABLE orders_012 (
    order_id_012     INT           NOT NULL AUTO_INCREMENT,
    customer_id_012  INT           NOT NULL,
    total_amount_012 DECIMAL(10,2) NOT NULL,
    order_status_012 VARCHAR(20)   DEFAULT 'Pending',
    order_time_012   DATETIME      DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_orders_012 PRIMARY KEY (order_id_012),
    CONSTRAINT fk_orders_customer_012
        FOREIGN KEY (customer_id_012) REFERENCES customers_012(customer_id_012)
);

CREATE TABLE orderitems_012 (
    order_item_id_012 INT           NOT NULL AUTO_INCREMENT,
    order_id_012      INT           NOT NULL,
    product_id_012    INT           NOT NULL,
    buy_qty_012       INT           NOT NULL,
    unit_price_012    DECIMAL(10,2) NOT NULL,
    CONSTRAINT pk_orderitems_012 PRIMARY KEY (order_item_id_012),
    CONSTRAINT fk_orderitems_order_012
        FOREIGN KEY (order_id_012)    REFERENCES orders_012(order_id_012),
    CONSTRAINT fk_orderitems_product_012
        FOREIGN KEY (product_id_012)  REFERENCES products_012(product_id_012)
);

CREATE TABLE payments_012 (
    payment_id_012  INT           NOT NULL AUTO_INCREMENT,
    order_id_012    INT           NOT NULL,
    pay_amount_012  DECIMAL(10,2) NOT NULL,
    pay_method_012  VARCHAR(30),
    pay_time_012    DATETIME      DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_payments_012 PRIMARY KEY (payment_id_012),
    CONSTRAINT fk_payments_order_012
        FOREIGN KEY (order_id_012) REFERENCES orders_012(order_id_012)
);

CREATE TABLE pointsrecords_012 (
    record_id_012     INT      NOT NULL AUTO_INCREMENT,
    customer_id_012   INT      NOT NULL,
    order_id_012      INT      NOT NULL,
    gained_points_012 INT      NOT NULL,
    record_time_012   DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_pointsrecords_012 PRIMARY KEY (record_id_012),
    CONSTRAINT fk_points_customer_012
        FOREIGN KEY (customer_id_012) REFERENCES customers_012(customer_id_012),
    CONSTRAINT fk_points_order_012
        FOREIGN KEY (order_id_012)    REFERENCES orders_012(order_id_012)
);

CREATE TABLE lowstockalerts_012 (
    alert_id_012   INT      NOT NULL AUTO_INCREMENT,
    product_id_012 INT      NOT NULL,
    alert_msg_012  TEXT,
    alert_time_012 DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_lowstockalerts_012 PRIMARY KEY (alert_id_012),
    CONSTRAINT fk_alerts_product_012
        FOREIGN KEY (product_id_012) REFERENCES products_012(product_id_012)
);

CREATE INDEX idx_cust_order_012     ON orders_012 (customer_id_012, order_time_012);
CREATE INDEX idx_inv_product_012    ON inventory_012 (product_id_012, stock_qty_012);
CREATE INDEX idx_orderitem_prod_012 ON orderitems_012 (product_id_012);
CREATE INDEX idx_pay_order_012      ON payments_012 (order_id_012);

CREATE VIEW v_cashier_product_012 AS
    SELECT p.product_id_012, p.product_name_012, p.retail_price_012, p.member_discount_012, i.stock_qty_012
    FROM products_012  p
    JOIN inventory_012 i ON p.product_id_012 = i.product_id_012
    WHERE p.status_012 = 'Available' AND i.stock_qty_012 > 0;

CREATE VIEW v_manager_sales_stat_012 AS
    SELECT p.product_id_012, p.product_name_012,
           SUM(oi.buy_qty_012) AS total_sold_012,
           SUM(oi.buy_qty_012 * oi.unit_price_012) AS total_sales_012
    FROM products_012   p
    JOIN orderitems_012 oi ON p.product_id_012  = oi.product_id_012
    JOIN orders_012     o  ON oi.order_id_012   = o.order_id_012
    WHERE o.order_status_012 = 'Completed'
    GROUP BY p.product_id_012, p.product_name_012
    ORDER BY total_sales_012 DESC;

CREATE VIEW v_member_discount_product_012 AS
    SELECT p.product_id_012, p.product_name_012, p.retail_price_012, p.member_discount_012, i.stock_qty_012
    FROM products_012  p
    JOIN inventory_012 i ON p.product_id_012 = i.product_id_012
    WHERE p.member_discount_012 > 0 AND p.status_012 = 'Available' AND i.stock_qty_012 > 0;

DELIMITER //

CREATE TRIGGER trg_low_stock_alert_012
AFTER INSERT ON orderitems_012
FOR EACH ROW
BEGIN
    DECLARE v_stock_qty         INT;
    DECLARE v_reorder_threshold INT;
    UPDATE inventory_012 SET stock_qty_012 = stock_qty_012 - NEW.buy_qty_012 WHERE product_id_012 = NEW.product_id_012;
    SELECT i.stock_qty_012, p.reorder_threshold_012
    INTO   v_stock_qty, v_reorder_threshold
    FROM   inventory_012 i JOIN products_012 p ON i.product_id_012 = p.product_id_012
    WHERE  i.product_id_012 = NEW.product_id_012;
    IF v_stock_qty <= v_reorder_threshold THEN
        INSERT INTO lowstockalerts_012 (product_id_012, alert_msg_012)
        VALUES (NEW.product_id_012, CONCAT('LOW STOCK! Product ID: ', NEW.product_id_012,
                ' | Remaining: ', v_stock_qty, ', Threshold: ', v_reorder_threshold));
    END IF;
END //

CREATE TRIGGER trg_calc_member_points_012
AFTER UPDATE ON orders_012
FOR EACH ROW
BEGIN
    DECLARE v_add_points INT;
    IF NEW.order_status_012 = 'Completed' AND OLD.order_status_012 != 'Completed' THEN
        SET v_add_points = FLOOR(NEW.total_amount_012) * 1;
        INSERT INTO pointsrecords_012 (customer_id_012, order_id_012, gained_points_012)
        VALUES (NEW.customer_id_012, NEW.order_id_012, v_add_points);
        UPDATE customers_012 SET total_points_012 = total_points_012 + v_add_points
        WHERE customer_id_012 = NEW.customer_id_012;
    END IF;
END //

DELIMITER ;

INSERT INTO suppliers_012 (supplier_name_012, contact_person_012, phone_012, contract_info_012) VALUES
    ('NikeSports Co',   'Mohammed Ali', '13800138000', 'Official Nike distributor'),
    ('AdidasPro Ltd',   'Sara Ibrahim', '13800138001', 'Premium Adidas supplier'),
    ('PumaWorld Inc',   'Khalid Omar',  '13800138002', 'Puma and accessories supplier');

INSERT INTO products_012 (product_name_012, retail_price_012, purchase_cost_012, member_discount_012, reorder_threshold_012, status_012, supplier_id_012) VALUES
    ('Nike Running Shoes',          129.99,  65.00, 0.10, 15, 'Available', 1),
    ('Adidas Football Boots',       199.99, 100.00, 0.15, 10, 'Available', 2),
    ('Puma Training Shorts',         39.99,  18.00, 0.10, 25, 'Available', 3),
    ('Nike Dri-FIT T-Shirt',         49.99,  22.00, 0.20, 20, 'Available', 1),
    ('Adidas Soccer Ball',           34.99,  15.00, 0.05, 30, 'Available', 2),
    ('Puma Backpack',                59.99,  28.00, 0.15, 15, 'Available', 3),
    ('Nike Yoga Mat',                44.99,  20.00, 0.10, 18, 'Available', 1),
    ('Adidas Sweatpants',            69.99,  32.00, 0.20, 12, 'Available', 2),
    ('Puma Sports Watch',            89.99,  42.00, 0.15, 10, 'Available', 3),
    ('Nike Water Bottle',            24.99,  10.00, 0.00, 35, 'Available', 1),
    ('Adidas Gym Bag',               54.99,  25.00, 0.10, 20, 'Available', 2),
    ('Puma Compression Leggings',    64.99,  30.00, 0.15, 15, 'Available', 3);

INSERT INTO inventory_012 (product_id_012, stock_qty_012, last_restock_date_012) VALUES
    ( 1, 25, '2026-06-28'), ( 2,  4, '2026-06-28'), ( 3, 50, '2026-06-28'),
    ( 4, 30, '2026-06-28'), ( 5, 60, '2026-06-28'), ( 6, 20, '2026-06-28'),
    ( 7, 18, '2026-06-28'), ( 8,  5, '2026-06-28'), ( 9,  3, '2026-06-28'),
    (10, 70, '2026-06-28'), (11, 22, '2026-06-28'), (12, 16, '2026-06-28');

INSERT INTO customers_012 (full_name_012, email_012, phone_012, address_012, member_discount_012, total_points_012, user_role_012) VALUES
    ('Ahmed Al-Rashid', 'ahmed@sports.com',    '0501234567', '123 Main St, Dubai',     0.10, 0, 'Customer'),
    ('Fatima Hassan',   'fatima@sports.com',   '0502345678', '456 Palm Ave, Sharjah',  0.15, 0, 'Customer'),
    ('Mohammed Nasser', 'mohammed@sports.com', '0503456789', '789 Creek Rd, Abu Dhabi',0.20, 0, 'Customer'),
    ('Layla Ibrahim',   'layla@sports.com',    '0504567890', '321 Marina Blvd, Dubai', 0.10, 0, 'Cashier'),
    ('Admin User',      'admin@sports.com',    '0505678901', '999 Office Tower, Dubai',0.00, 0, 'Manager');

INSERT INTO orders_012 (customer_id_012, total_amount_012, order_status_012, order_time_012) VALUES
    (1, 179.98, 'Pending', '2026-06-20 10:15:00'),
    (1, 199.99, 'Pending', '2026-06-22 14:30:00'),
    (2, 104.98, 'Pending', '2026-06-23 09:45:00'),
    (3, 169.97, 'Pending', '2026-06-25 16:20:00'),
    (2, 179.97, 'Pending', '2026-06-27 11:00:00');

INSERT INTO orderitems_012 (order_id_012, product_id_012, buy_qty_012, unit_price_012) VALUES
    (1, 1, 1, 129.99), (1, 4, 1, 49.99);
INSERT INTO orderitems_012 (order_id_012, product_id_012, buy_qty_012, unit_price_012) VALUES
    (2, 2, 1, 199.99);
INSERT INTO orderitems_012 (order_id_012, product_id_012, buy_qty_012, unit_price_012) VALUES
    (3, 5, 1, 34.99), (3, 8, 1, 69.99);
INSERT INTO orderitems_012 (order_id_012, product_id_012, buy_qty_012, unit_price_012) VALUES
    (4, 3, 2, 39.99), (4, 9, 1, 89.99);
INSERT INTO orderitems_012 (order_id_012, product_id_012, buy_qty_012, unit_price_012) VALUES
    (5, 6, 1, 59.99), (5, 11, 1, 54.99), (5, 12, 1, 64.99);

UPDATE orders_012 SET order_status_012 = 'Completed' WHERE order_id_012 = 1;
UPDATE orders_012 SET order_status_012 = 'Completed' WHERE order_id_012 = 3;
UPDATE orders_012 SET order_status_012 = 'Completed' WHERE order_id_012 = 4;
UPDATE orders_012 SET order_status_012 = 'Shipped'   WHERE order_id_012 = 5;

INSERT INTO payments_012 (order_id_012, pay_amount_012, pay_method_012, pay_time_012) VALUES
    (1, 179.98, 'Credit Card',     '2026-06-20 10:20:00'),
    (3, 104.98, 'Cash',            '2026-06-23 09:50:00'),
    (4, 169.97, 'Mobile Payment',  '2026-06-25 16:25:00');

-- ============================================================================
-- Database is now fully built and populated, with every attribute suffixed
-- _012. Both triggers already fired. Everything below is read-only (or small
-- CRUD demo steps) — one code per screenshot from here on.
-- ============================================================================


-- CODE 2 [SCREENSHOT -> 4.2, Table 1: suppliers_012]
USE db_uni_012;
SHOW CREATE TABLE suppliers_012;

-- CODE 3 [SCREENSHOT -> 4.2, Table 5: orders_012]
USE db_uni_012;
SHOW CREATE TABLE orders_012;

-- CODE 4 [SCREENSHOT -> 4.4, View 1]
USE db_uni_012;
SELECT * FROM v_cashier_product_012;

-- CODE 5 [SCREENSHOT -> 4.4, View 2]
USE db_uni_012;
SELECT * FROM v_manager_sales_stat_012;

-- CODE 6 [SCREENSHOT -> 4.4, View 3]
USE db_uni_012;
SELECT * FROM v_member_discount_product_012;

-- CODE 7 [SCREENSHOT -> 4.5, Trigger 1 proof - run both lines together]
USE db_uni_012;
SELECT * FROM inventory_012;
SELECT * FROM lowstockalerts_012 ORDER BY alert_time_012 DESC;

-- CODE 8 [SCREENSHOT -> 4.5, Trigger 2 proof - run both lines together]
USE db_uni_012;
SELECT customer_id_012, full_name_012, total_points_012 FROM customers_012;
SELECT * FROM pointsrecords_012;

-- CODE 9 [SCREENSHOT -> 4.7, Create - run both lines together]
USE db_uni_012;
INSERT INTO products_012
    (product_name_012, retail_price_012, purchase_cost_012, member_discount_012, reorder_threshold_012, status_012, supplier_id_012)
VALUES
    ('Nike Compression Sleeve', 19.99, 8.00, 0.10, 20, 'Available', 1);
SELECT * FROM products_012 WHERE product_name_012 = 'Nike Compression Sleeve';

-- CODE 10 [SCREENSHOT -> 4.7, Read - run both lines together]
USE db_uni_012;
SELECT order_id_012, order_status_012, total_amount_012, order_time_012 FROM orders_012 WHERE customer_id_012 = 1;
SELECT customer_id_012, full_name_012, email_012, member_discount_012, total_points_012 FROM customers_012 WHERE customer_id_012 = 1;

-- CODE 11 [SCREENSHOT -> 4.7, Update - run both lines together]
USE db_uni_012;
UPDATE orders_012 SET order_status_012 = 'Completed' WHERE order_id_012 = 2;
SELECT * FROM orders_012 WHERE order_id_012 = 2;

-- CODE 12 [SCREENSHOT -> 4.7, Delete - run all three lines together]
USE db_uni_012;
DELETE FROM inventory_012 WHERE product_id_012 = 10;
DELETE FROM products_012  WHERE product_id_012 = 10;
SELECT * FROM products_012 WHERE product_id_012 = 10;

-- CODE 13 [SCREENSHOT -> 4.8, Q1]
USE db_uni_012;
SELECT * FROM products_012 WHERE status_012 = 'Available';

-- CODE 14 [SCREENSHOT -> 4.8, Q2]
USE db_uni_012;
SELECT product_id_012, product_name_012, retail_price_012, purchase_cost_012, member_discount_012
FROM products_012 ORDER BY retail_price_012 DESC;

-- CODE 15 [SCREENSHOT -> 4.8, Q3]
USE db_uni_012;
SELECT customer_id_012, full_name_012, email_012, member_discount_012, total_points_012, user_role_012
FROM customers_012 WHERE member_discount_012 > 0;

-- CODE 16 [SCREENSHOT -> 4.8, Q4]
USE db_uni_012;
SELECT order_status_012, COUNT(*) AS order_count_012 FROM orders_012 GROUP BY order_status_012;

-- CODE 17 [SCREENSHOT -> 4.8, Q5]
USE db_uni_012;
SELECT product_id_012, stock_qty_012, last_restock_date_012 FROM inventory_012 WHERE stock_qty_012 < 10;

-- CODE 18 [SCREENSHOT -> 4.8, Q6]
USE db_uni_012;
SELECT order_id_012, customer_id_012, total_amount_012, order_status_012, order_time_012
FROM orders_012 WHERE order_time_012 >= NOW() - INTERVAL 7 DAY;

-- CODE 19 [SCREENSHOT -> 4.8, Q7]
USE db_uni_012;
SELECT customer_id_012, full_name_012, email_012, total_points_012, user_role_012
FROM customers_012 ORDER BY total_points_012 DESC;

-- CODE 20 [SCREENSHOT -> 4.8, Q8]
USE db_uni_012;
SELECT pay_method_012, COUNT(*) AS payment_count_012, SUM(pay_amount_012) AS total_collected_012
FROM payments_012 GROUP BY pay_method_012;

-- CODE 21 [SCREENSHOT -> 4.8, Q9]
USE db_uni_012;
SELECT product_id_012, product_name_012, retail_price_012, member_discount_012
FROM products_012 WHERE member_discount_012 = 0;

-- CODE 22 [SCREENSHOT -> 4.8, Q10]
USE db_uni_012;
SELECT alert_id_012, product_id_012, alert_msg_012, alert_time_012
FROM lowstockalerts_012 ORDER BY alert_time_012 DESC;

-- CODE 23 [SCREENSHOT -> 4.9, C1]
USE db_uni_012;
SELECT
    o.order_id_012, c.full_name_012 AS customer_name_012, p.product_name_012,
    oi.buy_qty_012, oi.unit_price_012, (oi.buy_qty_012 * oi.unit_price_012) AS line_total_012,
    o.order_status_012, o.order_time_012
FROM orders_012 o
JOIN customers_012 c ON o.customer_id_012 = c.customer_id_012
JOIN orderitems_012 oi ON o.order_id_012 = oi.order_id_012
JOIN products_012 p ON oi.product_id_012 = p.product_id_012
ORDER BY o.order_id_012, oi.order_item_id_012;

-- CODE 24 [SCREENSHOT -> 4.9, C2]
USE db_uni_012;
SELECT
    c.customer_id_012, c.full_name_012, c.email_012,
    COUNT(DISTINCT o.order_id_012) AS order_count_012, SUM(o.total_amount_012) AS total_spent_012
FROM customers_012 c
JOIN orders_012 o ON c.customer_id_012 = o.customer_id_012
GROUP BY c.customer_id_012, c.full_name_012, c.email_012
ORDER BY total_spent_012 DESC;

-- CODE 25 [SCREENSHOT -> 4.9, C3]
USE db_uni_012;
SELECT
    p.product_id_012, p.product_name_012, i.stock_qty_012, p.reorder_threshold_012,
    (p.reorder_threshold_012 - i.stock_qty_012) AS units_needed_012,
    s.supplier_name_012, s.contact_person_012, s.phone_012
FROM products_012 p
JOIN inventory_012 i ON p.product_id_012 = i.product_id_012
JOIN suppliers_012 s ON p.supplier_id_012 = s.supplier_id_012
WHERE i.stock_qty_012 <= p.reorder_threshold_012
ORDER BY units_needed_012 DESC;

-- CODE 26 [SCREENSHOT -> 4.9, C4]
USE db_uni_012;
SELECT p.product_id_012, p.product_name_012, p.retail_price_012, p.status_012, i.stock_qty_012
FROM products_012 p
JOIN inventory_012 i ON p.product_id_012 = i.product_id_012
WHERE p.product_id_012 NOT IN (SELECT DISTINCT product_id_012 FROM orderitems_012);

-- CODE 27 [SCREENSHOT -> 4.9, C5]
USE db_uni_012;
SELECT
    DATE_FORMAT(o.order_time_012, '%Y-%m') AS sales_month_012,
    COUNT(DISTINCT o.order_id_012) AS orders_placed_012,
    SUM(oi.buy_qty_012) AS units_sold_012, SUM(oi.buy_qty_012 * oi.unit_price_012) AS gross_revenue_012
FROM orders_012 o
JOIN orderitems_012 oi ON o.order_id_012 = oi.order_id_012
WHERE o.order_status_012 IN ('Completed', 'Shipped')
GROUP BY sales_month_012
ORDER BY sales_month_012 DESC;

-- CODE 28 [SCREENSHOT -> Part V, System Demonstration]
USE db_uni_012;
SHOW TABLES;

-- ============================================================================
-- TOTAL: 1 big setup code (no screenshot) + 27 small codes (27 screenshots)
-- for the SQL part of the report, plus 7 GUI screenshots from Main.java.
-- ============================================================================
