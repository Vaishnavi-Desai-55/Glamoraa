-- =============================================================================
-- Glamoraa - MySQL 8.x schema
-- Creates a NEW database called glamoraa_db. Nothing is dropped, so running this
-- twice is harmless (every statement uses IF NOT EXISTS).
-- =============================================================================
CREATE DATABASE IF NOT EXISTS glamoraa_db CHARACTER SET utf8mb4;
USE glamoraa_db;

-- ---------------------------------------------------------------- users ------
CREATE TABLE IF NOT EXISTS users (
    id            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    full_name     VARCHAR(120)  NOT NULL,
    email         VARCHAR(190)  NOT NULL,
    password_hash VARCHAR(255)  NOT NULL,
    phone         VARCHAR(20)   NULL,
    role          ENUM('CUSTOMER','ADMIN') NOT NULL DEFAULT 'CUSTOMER',
    active        TINYINT(1)    NOT NULL DEFAULT 1,
    created_at    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_users_email (email),
    KEY idx_users_role (role)
) ENGINE=InnoDB;

-- ----------------------------------------------------------- categories ------
CREATE TABLE IF NOT EXISTS categories (
    id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    name        VARCHAR(80)  NOT NULL,
    slug        VARCHAR(100) NOT NULL,
    subtitle    VARCHAR(160) NULL,
    description TEXT         NULL,
    image_path  VARCHAR(255) NULL,
    sort_order  INT          NOT NULL DEFAULT 0,
    active      TINYINT(1)   NOT NULL DEFAULT 1,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_categories_name (name),
    UNIQUE KEY uq_categories_slug (slug)
) ENGINE=InnoDB;

-- ------------------------------------------------------------- products ------
-- Discount semantics: discount_percent is a PERCENTAGE (0-90) off `price`.
-- sale_price is a generated column, so every query, sort and order total uses
-- exactly the same number:  sale_price = ROUND(price * (100 - discount_percent) / 100, 2)
CREATE TABLE IF NOT EXISTS products (
    id               BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    category_id      BIGINT UNSIGNED NOT NULL,
    name             VARCHAR(160)  NOT NULL,
    slug             VARCHAR(190)  NOT NULL,
    description      TEXT          NULL,
    specifications   TEXT          NULL COMMENT 'One "Label: value" per line',
    price            DECIMAL(10,2) NOT NULL,
    discount_percent TINYINT NOT NULL DEFAULT 0,   -- signed on purpose: keeps the generated column deterministic
    sale_price       DECIMAL(10,2) AS (ROUND(price * (100 - discount_percent) / 100, 2)) STORED,
    is_featured      TINYINT(1)    NOT NULL DEFAULT 0,
    is_new_arrival   TINYINT(1)    NOT NULL DEFAULT 0,
    active           TINYINT(1)    NOT NULL DEFAULT 1,
    created_at       TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_products_slug (slug),
    KEY idx_products_category (category_id, active),
    KEY idx_products_sale_price (sale_price),
    KEY idx_products_flags (is_featured, is_new_arrival, active),
    CONSTRAINT fk_products_category FOREIGN KEY (category_id) REFERENCES categories (id),
    CONSTRAINT chk_products_price CHECK (price >= 0),
    CONSTRAINT chk_products_discount CHECK (discount_percent BETWEEN 0 AND 90)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS product_images (
    id         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    product_id BIGINT UNSIGNED NOT NULL,
    image_path VARCHAR(255) NOT NULL,
    alt_text   VARCHAR(200) NULL,
    sort_order INT          NOT NULL DEFAULT 0,
    is_primary TINYINT(1)   NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_product_images_product (product_id, is_primary, sort_order),
    CONSTRAINT fk_product_images_product FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------------------------------------------------- sizes / colors ---------
CREATE TABLE IF NOT EXISTS sizes (
    id         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    label      VARCHAR(20) NOT NULL,
    sort_order INT         NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uq_sizes_label (label)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS colors (
    id       BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    name     VARCHAR(40) NOT NULL,
    hex_code CHAR(7)     NOT NULL DEFAULT '#CCCCCC',
    PRIMARY KEY (id),
    UNIQUE KEY uq_colors_name (name)
) ENGINE=InnoDB;

-- ------------------------------------------------------ product_variants ----
-- Stock lives here (per size + colour). One row per real, purchasable combination.
CREATE TABLE IF NOT EXISTS product_variants (
    id             BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    product_id     BIGINT UNSIGNED NOT NULL,
    size_id        BIGINT UNSIGNED NOT NULL,
    color_id       BIGINT UNSIGNED NOT NULL,
    sku            VARCHAR(60)  NOT NULL,
    stock_quantity INT UNSIGNED NOT NULL DEFAULT 0,
    active         TINYINT(1)   NOT NULL DEFAULT 1,
    PRIMARY KEY (id),
    UNIQUE KEY uq_variant_sku (sku),
    UNIQUE KEY uq_variant_combo (product_id, size_id, color_id),
    KEY idx_variants_stock (stock_quantity),
    CONSTRAINT fk_variants_product FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE,
    CONSTRAINT fk_variants_size    FOREIGN KEY (size_id)    REFERENCES sizes (id),
    CONSTRAINT fk_variants_color   FOREIGN KEY (color_id)   REFERENCES colors (id)
) ENGINE=InnoDB;

-- ------------------------------------------------------------- cart ---------
CREATE TABLE IF NOT EXISTS cart (
    id         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    user_id    BIGINT UNSIGNED NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_cart_user (user_id),
    CONSTRAINT fk_cart_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS cart_items (
    id         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    cart_id    BIGINT UNSIGNED NOT NULL,
    variant_id BIGINT UNSIGNED NOT NULL,
    quantity   INT UNSIGNED    NOT NULL,
    added_at   TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_cart_variant (cart_id, variant_id),      -- no duplicate lines for a variant
    CONSTRAINT fk_cart_items_cart    FOREIGN KEY (cart_id)    REFERENCES cart (id) ON DELETE CASCADE,
    CONSTRAINT fk_cart_items_variant FOREIGN KEY (variant_id) REFERENCES product_variants (id) ON DELETE CASCADE,
    CONSTRAINT chk_cart_items_qty CHECK (quantity >= 1)
) ENGINE=InnoDB;

-- ---------------------------------------------------------- wishlist --------
CREATE TABLE IF NOT EXISTS wishlist (
    id         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    user_id    BIGINT UNSIGNED NOT NULL,
    product_id BIGINT UNSIGNED NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_wishlist_user_product (user_id, product_id),
    CONSTRAINT fk_wishlist_user    FOREIGN KEY (user_id)    REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_wishlist_product FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- --------------------------------------------------------- addresses --------
CREATE TABLE IF NOT EXISTS addresses (
    id             BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    user_id        BIGINT UNSIGNED NOT NULL,
    label          VARCHAR(40)  NOT NULL DEFAULT 'Home',
    recipient_name VARCHAR(120) NOT NULL,
    phone          VARCHAR(20)  NOT NULL,
    line1          VARCHAR(190) NOT NULL,
    line2          VARCHAR(190) NULL,
    city           VARCHAR(80)  NOT NULL,
    state          VARCHAR(80)  NOT NULL,
    postal_code    VARCHAR(12)  NOT NULL,
    country        VARCHAR(60)  NOT NULL DEFAULT 'India',
    is_default     TINYINT(1)   NOT NULL DEFAULT 0,
    created_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_addresses_user (user_id),
    CONSTRAINT fk_addresses_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ------------------------------------------------------------ orders --------
-- The shipping address and every purchased line are SNAPSHOTS, so later edits to
-- addresses, products or prices never change history.
CREATE TABLE IF NOT EXISTS orders (
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    order_number    VARCHAR(24)  NOT NULL,
    user_id         BIGINT UNSIGNED NOT NULL,
    status          ENUM('PLACED','CONFIRMED','SHIPPED','DELIVERED','CANCELLED') NOT NULL DEFAULT 'PLACED',
    payment_method  ENUM('COD') NOT NULL DEFAULT 'COD',
    payment_status  ENUM('PENDING','PAID','FAILED','REFUNDED') NOT NULL DEFAULT 'PENDING',
    subtotal        DECIMAL(10,2) NOT NULL,
    shipping_fee    DECIMAL(10,2) NOT NULL DEFAULT 0,
    total           DECIMAL(10,2) NOT NULL,
    ship_name       VARCHAR(120) NOT NULL,
    ship_phone      VARCHAR(20)  NOT NULL,
    ship_line1      VARCHAR(190) NOT NULL,
    ship_line2      VARCHAR(190) NULL,
    ship_city       VARCHAR(80)  NOT NULL,
    ship_state      VARCHAR(80)  NOT NULL,
    ship_postal     VARCHAR(12)  NOT NULL,
    ship_country    VARCHAR(60)  NOT NULL,
    idempotency_key CHAR(36)     NOT NULL COMMENT 'Blocks duplicate form submissions',
    stock_restored  TINYINT(1)   NOT NULL DEFAULT 0 COMMENT 'Set once when a cancellation returns stock',
    placed_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_orders_number (order_number),
    UNIQUE KEY uq_orders_idem (idempotency_key),
    KEY idx_orders_user (user_id, placed_at),
    KEY idx_orders_status (status, placed_at),
    CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS order_items (
    id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    order_id     BIGINT UNSIGNED NOT NULL,
    variant_id   BIGINT UNSIGNED NULL,
    product_id   BIGINT UNSIGNED NULL,
    product_name VARCHAR(160) NOT NULL,
    sku          VARCHAR(60)  NOT NULL,
    size_label   VARCHAR(20)  NOT NULL,
    color_name   VARCHAR(40)  NOT NULL,
    image_path   VARCHAR(255) NULL,
    unit_price   DECIMAL(10,2) NOT NULL,
    quantity     INT UNSIGNED  NOT NULL,
    line_total   DECIMAL(10,2) NOT NULL,
    PRIMARY KEY (id),
    KEY idx_order_items_order (order_id),
    KEY idx_order_items_product (product_id),
    CONSTRAINT fk_order_items_order   FOREIGN KEY (order_id)   REFERENCES orders (id) ON DELETE CASCADE,
    CONSTRAINT fk_order_items_variant FOREIGN KEY (variant_id) REFERENCES product_variants (id) ON DELETE SET NULL,
    CONSTRAINT fk_order_items_product FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- ------------------------------------------------------------ reviews -------
CREATE TABLE IF NOT EXISTS reviews (
    id         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    product_id BIGINT UNSIGNED NOT NULL,
    user_id    BIGINT UNSIGNED NOT NULL,
    rating     TINYINT UNSIGNED NOT NULL,
    title      VARCHAR(120) NULL,
    body       TEXT         NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_reviews_user_product (user_id, product_id),
    KEY idx_reviews_product (product_id),
    CONSTRAINT fk_reviews_product FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE,
    CONSTRAINT fk_reviews_user    FOREIGN KEY (user_id)    REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT chk_reviews_rating CHECK (rating BETWEEN 1 AND 5)
) ENGINE=InnoDB;

-- ------------------------------------------- newsletter / contact ------------
CREATE TABLE IF NOT EXISTS newsletter_subscriptions (
    id            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    email         VARCHAR(190) NOT NULL,
    subscribed_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_newsletter_email (email)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS contact_messages (
    id         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    name       VARCHAR(120) NOT NULL,
    email      VARCHAR(190) NOT NULL,
    subject    VARCHAR(160) NOT NULL,
    message    TEXT         NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB;
