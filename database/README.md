# Database

Glamoraa uses its **own** database, `glamoraa_db`. The scripts never drop anything.

| File | Purpose |
|---|---|
| `schema.sql` | Creates `glamoraa_db` and 16 tables (all `CREATE ... IF NOT EXISTS`, safe to re-run) |
| `seed-data.sql` | Demo catalogue: 6 categories, 39 products, 5 apparel sizes + 5 shoe sizes + One Size, 14 colours, 78 images, 397 variants. **Run once.** Contains no users, orders or reviews |

## Setup
```bash
mysql -u root -p < database/schema.sql
mysql -u root -p < database/seed-data.sql
```
or open each file in MySQL Workbench and run it (lightning bolt). Run `schema.sql` first.

To start over with a clean database (this deletes only Glamoraa's own data):
```sql
DROP DATABASE glamoraa_db;
```
then run both scripts again.

## Key design decisions
- **Money** is `DECIMAL(10,2)`. `discount_percent` is a percentage (0-90) and `sale_price` is a **generated, stored column** (`ROUND(price * (100 - discount_percent) / 100, 2)`), so every query and total uses the same value.
- **Stock** is stored per variant (`product_variants.stock_quantity`); `UNIQUE(product_id, size_id, color_id)` stops duplicate variants.
- `cart_items` has `UNIQUE(cart_id, variant_id)` and `wishlist` has `UNIQUE(user_id, product_id)` - duplicates are impossible at the database level.
- `orders` and `order_items` store **snapshots** of the address, product name, SKU, size, colour and price. `orders.idempotency_key` is unique, and `stock_restored` guarantees stock is returned only once on cancellation.
- `reviews` allow one review per user and product; the application only accepts them from customers whose order was delivered.

## Troubleshooting
| Symptom | Fix |
|---|---|
| `Access denied for user` | Check `db.user` / `db.password` in `glamoraa-local.properties` |
| `Unknown database 'glamoraa_db'` | Run `schema.sql` first |
| `Communications link failure` | MySQL is not running, or the port in `db.url` is wrong |
| `Duplicate entry` while seeding | The seed script was already run - run it only once on a fresh database |
| Server clock shows odd times | `db.url` uses `connectionTimeZone=LOCAL&forceConnectionTimeZoneToSession=true`, so the JVM and MySQL session agree |
