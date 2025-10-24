CREATE TABLE IF NOT EXISTS brands (
                                      id BIGSERIAL PRIMARY KEY,
                                      name VARCHAR(255) NOT NULL
    );

CREATE TABLE IF NOT EXISTS categories (
                                          id BIGSERIAL PRIMARY KEY,
                                          name VARCHAR(255) NOT NULL
    );

CREATE TABLE IF NOT EXISTS products (
                                        id BIGSERIAL PRIMARY KEY,
                                        name VARCHAR(255) NOT NULL,
    brand_id BIGINT,
    category_id BIGINT,
    price DECIMAL(10, 2),
    volume VARCHAR(50),
    percentage VARCHAR(50),
    description TEXT,
    image_url VARCHAR(500),
    in_stock BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (brand_id) REFERENCES brands(id),
    FOREIGN KEY (category_id) REFERENCES categories(id)
    );

INSERT INTO brands (name) VALUES
                              ('Простоквашино'), ('Домик в деревне'), ('Coca-Cola'),
                              ('PepsiCo'), ('Lay''s'), ('Nestle'), ('Danone');

INSERT INTO categories (name) VALUES
                                  ('Молочные продукты'), ('Напитки'), ('Снеки'), ('Хлеб и выпечка');

INSERT INTO products (name, brand_id, category_id, price, volume, percentage, description, in_stock) VALUES
                                                                                                         ('Кефир', 1, 1, 89.90, '1л', '2.5%', 'Кефир Простоквашино 2.5%', true),
                                                                                                         ('Молоко', 1, 1, 95.50, '1л', '3.2%', 'Молоко Простоквашино 3.2%', true),
                                                                                                         ('Сметана', 2, 1, 120.00, '300г', '20%', 'Сметана Домик в деревне 20%', true),
                                                                                                         ('Кола', 3, 2, 99.90, '2л', '0%', 'Coca-Cola 2 литра', true),
                                                                                                         ('Чипсы', 5, 3, 149.90, '150г', null, 'Lay''s с солью', true);
