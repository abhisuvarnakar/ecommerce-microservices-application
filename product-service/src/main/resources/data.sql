INSERT INTO t_category (name, description, is_active, created_at, parent_id)
VALUES
('Electronics', 'All electronic devices', true, NOW(), NULL),
('Clothing', 'Apparel and garments', true, NOW(), NULL),
('Home & Kitchen', 'Home appliances and kitchenware', true, NOW(), NULL),
('Books', 'Books across genres', true, NOW(), NULL),
('Sports', 'Sporting goods and accessories', true, NOW(), NULL),
('Toys', 'Toys for kids of all ages', true, NOW(), NULL),
('Beauty', 'Beauty and personal care', true, NOW(), NULL),
('Automotive', 'Automotive parts and accessories', true, NOW(), NULL),
('Garden', 'Gardening tools and supplies', true, NOW(), NULL),
('Music', 'Musical instruments and accessories', true, NOW(), NULL);

INSERT INTO t_category (name, description, is_active, created_at, parent_id)
VALUES
('Mobile Phones', 'Smartphones and mobiles', true, NOW(), (SELECT id FROM t_category WHERE name = 'Electronics')),
('Laptops', 'Portable computers and accessories', true, NOW(), (SELECT id FROM t_category WHERE name = 'Electronics')),
('Cameras', 'Digital and DSLR cameras', true, NOW(), (SELECT id FROM t_category WHERE name = 'Electronics')),

('Men Clothing', 'Clothing for men', true, NOW(), (SELECT id FROM t_category WHERE name = 'Clothing')),
('Women Clothing', 'Clothing for women', true, NOW(), (SELECT id FROM t_category WHERE name = 'Clothing')),
('Kids Clothing', 'Clothing for kids', true, NOW(), (SELECT id FROM t_category WHERE name = 'Clothing')),

('Kitchen Appliances', 'Appliances for kitchen', true, NOW(), (SELECT id FROM t_category WHERE name = 'Home & Kitchen')),
('Furniture', 'Home furniture items', true, NOW(), (SELECT id FROM t_category WHERE name = 'Home & Kitchen')),
('Decor', 'Home decorative items', true, NOW(), (SELECT id FROM t_category WHERE name = 'Home & Kitchen')),

('Fiction', 'Fictional books', true, NOW(), (SELECT id FROM t_category WHERE name = 'Books')),
('Non-Fiction', 'Non-fiction books', true, NOW(), (SELECT id FROM t_category WHERE name = 'Books')),
('Academic', 'Educational books', true, NOW(), (SELECT id FROM t_category WHERE name = 'Books')),

('Fitness', 'Fitness equipment and accessories', true, NOW(), (SELECT id FROM t_category WHERE name = 'Sports')),
('Outdoor Sports', 'Outdoor sports gear', true, NOW(), (SELECT id FROM t_category WHERE name = 'Sports')),
('Indoor Games', 'Indoor game accessories', true, NOW(), (SELECT id FROM t_category WHERE name = 'Sports')),

('Action Figures', 'Collectible figures and toys', true, NOW(), (SELECT id FROM t_category WHERE name = 'Toys')),
('Educational Toys', 'Toys for learning', true, NOW(), (SELECT id FROM t_category WHERE name = 'Toys')),
('Puzzles', 'Puzzle games and toys', true, NOW(), (SELECT id FROM t_category WHERE name = 'Toys')),

('Skincare', 'Skin care products', true, NOW(), (SELECT id FROM t_category WHERE name = 'Beauty')),
('Haircare', 'Hair care products', true, NOW(), (SELECT id FROM t_category WHERE name = 'Beauty')),
('Makeup', 'Makeup products', true, NOW(), (SELECT id FROM t_category WHERE name = 'Beauty')),

('Car Accessories', 'Accessories for cars', true, NOW(), (SELECT id FROM t_category WHERE name = 'Automotive')),
('Motorcycle Parts', 'Parts for motorcycles', true, NOW(), (SELECT id FROM t_category WHERE name = 'Automotive')),
('Tools & Equipment', 'Automotive tools', true, NOW(), (SELECT id FROM t_category WHERE name = 'Automotive')),

('Plants', 'Indoor and outdoor plants', true, NOW(), (SELECT id FROM t_category WHERE name = 'Garden')),
('Gardening Tools', 'Tools for gardening', true, NOW(), (SELECT id FROM t_category WHERE name = 'Garden')),
('Garden Decor', 'Decorative garden items', true, NOW(), (SELECT id FROM t_category WHERE name = 'Garden')),

('Guitars', 'All types of guitars', true, NOW(), (SELECT id FROM t_category WHERE name = 'Music')),
('Keyboards', 'Musical keyboards', true, NOW(), (SELECT id FROM t_category WHERE name = 'Music')),
('Drums', 'Drum kits and percussion', true, NOW(), (SELECT id FROM t_category WHERE name = 'Music'));

INSERT INTO t_product (name, description, price, category_id, sku, weight, dimensions, is_active, created_at, updated_at)
VALUES
('iPhone 14', 'Latest Apple iPhone', 999.99, (SELECT id FROM t_category WHERE name = 'Mobile Phones'), 'SKU00001', 0.174, '146.7x71.5x7.8 mm', true, NOW(), NOW()),
('Samsung Galaxy S23', 'Latest Samsung phone', 899.99, (SELECT id FROM t_category WHERE name = 'Mobile Phones'), 'SKU00002', 0.168, '146.3x70.9x7.6 mm', true, NOW(), NOW()),
('Dell XPS 13', '13 inch laptop', 1200.00, (SELECT id FROM t_category WHERE name = 'Laptops'), 'SKU00003', 1.2, '295x199x14.8 mm', true, NOW(), NOW()),
('Canon EOS R6', 'Mirrorless camera', 2500.00, (SELECT id FROM t_category WHERE name = 'Cameras'), 'SKU00004', 0.675, '138x98x88 mm', true, NOW(), NOW()),
('Men T-Shirt', 'Cotton t-shirt', 19.99, (SELECT id FROM t_category WHERE name = 'Men Clothing'), 'SKU00005', 0.2, 'L size', true, NOW(), NOW()),
('Women Dress', 'Evening gown', 59.99, (SELECT id FROM t_category WHERE name = 'Women Clothing'), 'SKU00006', 0.3, 'M size', true, NOW(), NOW()),
('Kids Jeans', 'Denim jeans for kids', 25.00, (SELECT id FROM t_category WHERE name = 'Kids Clothing'), 'SKU00007', 0.4, 'XS size', true, NOW(), NOW()),
('Blender', 'Kitchen blender', 45.00, (SELECT id FROM t_category WHERE name = 'Kitchen Appliances'), 'SKU00008', 2.0, '30x15x20 cm', true, NOW(), NOW()),
('Sofa Set', '3-seat sofa', 499.99, (SELECT id FROM t_category WHERE name = 'Furniture'), 'SKU00009', 25.0, '200x90x85 cm', true, NOW(), NOW()),
('Wall Painting', 'Decorative wall art', 79.99, (SELECT id FROM t_category WHERE name = 'Decor'), 'SKU00010', 1.5, '60x40 cm', true, NOW(), NOW()),

('The Great Gatsby', 'Classic fiction book', 12.99, (SELECT id FROM t_category WHERE name = 'Fiction'), 'SKU00011', 0.5, '20x13x2 cm', true, NOW(), NOW()),
('Educated', 'Memoir by Tara Westover', 14.99, (SELECT id FROM t_category WHERE name = 'Non-Fiction'), 'SKU00012', 0.4, '22x14x3 cm', true, NOW(), NOW()),
('Calculus Textbook', 'Advanced mathematics book', 39.99, (SELECT id FROM t_category WHERE name = 'Academic'), 'SKU00013', 1.0, '28x22x4 cm', true, NOW(), NOW()),
('Treadmill', 'Home fitness machine', 599.00, (SELECT id FROM t_category WHERE name = 'Fitness'), 'SKU00014', 55.0, '150x75x140 cm', true, NOW(), NOW()),
('Football', 'Standard size football', 29.99, (SELECT id FROM t_category WHERE name = 'Outdoor Sports'), 'SKU00015', 0.43, '22 cm diameter', true, NOW(), NOW()),
('Chess Set', 'Wooden chess board and pieces', 49.99, (SELECT id FROM t_category WHERE name = 'Indoor Games'), 'SKU00016', 2.0, '40x40x5 cm', true, NOW(), NOW()),
('Superhero Action Figure', 'Collectible toy', 24.99, (SELECT id FROM t_category WHERE name = 'Action Figures'), 'SKU00017', 0.3, '15x7x5 cm', true, NOW(), NOW()),
('Learning Blocks', 'Educational toy blocks', 19.99, (SELECT id FROM t_category WHERE name = 'Educational Toys'), 'SKU00018', 0.7, '25x15x15 cm', true, NOW(), NOW()),
('1000 Piece Puzzle', 'Jigsaw puzzle', 14.99, (SELECT id FROM t_category WHERE name = 'Puzzles'), 'SKU00019', 1.2, '40x30x5 cm', true, NOW(), NOW()),
('Moisturizer', 'Skin care cream', 15.99, (SELECT id FROM t_category WHERE name = 'Skincare'), 'SKU00020', 0.2, '50 ml', true, NOW(), NOW());