# Test checklist

Tick these off on your own machine (Tomcat 10.1 + MySQL 8). Each item states the expected result.

## 0. Start-up
- [ ] Console shows `Glamoraa connected to database: jdbc:mysql://localhost:3306/glamoraa_db`
- [ ] Console shows `Bootstrap admin account created ...` on the first start only
- [ ] http://localhost:8080/glamoraa/ loads with products and category tiles

## 1. Authentication
- [ ] Register with empty/invalid fields -> field errors, nothing saved
- [ ] Register with a valid email -> redirected to `/account`
- [ ] Register again with the same email -> "already exists"
- [ ] In the DB, the new user's `role` is `CUSTOMER` and `password_hash` starts with `pbkdf2_sha256$`
- [ ] Log out (account menu) then open `/account` -> redirected to `/login`
- [ ] Wrong password and unknown email -> the *same* message, "Invalid email or password."
- [ ] Six wrong attempts in a row -> "Too many attempts"
- [ ] `/login?next=https://example.com` -> after login you land on the site, not example.com
- [ ] Logged in as a customer, open `/admin` -> 403 page

## 2. Catalogue
- [ ] `/products` shows 12 products and pagination; page 2, 3, 4 work
- [ ] `/category/women`, `/category/footwear` filter correctly; `/category/nope` -> 404 page
- [ ] `/search?q=linen` finds linen products; `/search?q=zzzz` shows the empty state
- [ ] Price filter (min/max), sort by price both directions, name sort; URLs are shareable
- [ ] `?page=999` and `?page=abc` do not crash (clamped to a valid page)
- [ ] `?sort=<anything else>` falls back to newest (no SQL error)
- [ ] Product page: thumbnails change the main image, click opens a lightbox
- [ ] Sold-out sizes/colours are disabled; "Only N left" appears at 5 or fewer

## 3. Cart
- [ ] Logged out: "Add to bag" sends you to login, then back
- [ ] Add an item -> drawer opens, badge updates, toast says "Added to your bag."
- [ ] Add the same size and colour again -> one line, quantity increased
- [ ] Try to exceed stock or 10 per item -> error toast, nothing changes
- [ ] Update quantity, remove an item, clear the bag, empty-cart state
- [ ] Refresh / log out and in -> the cart is still there (stored in MySQL)
- [ ] (DB) `cart_items` never has two rows with the same `cart_id` + `variant_id`

## 4. Wishlist
- [ ] Heart on a card toggles instantly and the header dot appears
- [ ] Wishlist page: "Move to bag" needs a size/colour, then removes it from the wishlist
- [ ] Logged out, the heart sends you to login

## 5. Addresses and profile
- [ ] Add / edit / delete / set default; first address becomes default automatically
- [ ] Validation errors for blank fields and a bad postal code
- [ ] Change the `id` in `/account/addresses/edit?id=` to another user's -> 404
- [ ] Update profile; change password (wrong current password is rejected)

## 6. Checkout and orders
- [ ] Empty bag -> `/checkout` redirects with a message
- [ ] No saved address -> "Place order" is disabled and you are offered "Add a delivery address"
- [ ] Place an order -> confirmation page; cart is empty; stock in `product_variants` dropped by the ordered quantity
- [ ] Free shipping at or above the threshold, flat fee below it (check `orders.shipping_fee`)
- [ ] Press browser Back and submit the old checkout again -> no second order is created
- [ ] In MySQL, lower a variant's stock below what is in the bag, then place the order -> message, **no** order row, stock and cart unchanged (rollback)
- [ ] Cancel a placed order -> status `CANCELLED`, stock returned once; cancel again -> no second restock
- [ ] A different user opening `/order-details?id=<your order>` -> 404

## 7. Admin
- [ ] Logged out, `/admin` -> login; customer -> 403
- [ ] Create a product (validation errors for blank name / negative price / discount over 90)
- [ ] Upload an image (JPG/PNG/WebP); a renamed `.php` or text file is rejected
- [ ] Add variants; duplicate size+colour rejected; negative stock rejected; stock edits save
- [ ] The new product appears in the shop only after it has a variant with stock
- [ ] Hide/show a product; create and delete an empty category (a non-empty one cannot be deleted)
- [ ] Order workflow: Placed -> Confirmed -> Shipped -> Delivered; skipping steps is refused; cancelling restocks
- [ ] Delivered COD orders become `PAID`
- [ ] The customer who received the product can now review it (once)
- [ ] Disable a customer -> they can no longer log in

## 8. UI and accessibility
- [ ] Widths 1440, 1024, 768, 390, 360: no sideways scrolling
- [ ] Mobile: menu drawer opens/closes (Esc, scrim), search overlay, bag and filters usable
- [ ] Keyboard only: Tab reaches every control with a visible focus ring; drawers trap focus and Esc closes them
- [ ] Hover effects do not shift the layout; on touch screens the wishlist heart and "View details" link are always reachable
- [ ] OS setting "reduce motion" -> animations are effectively disabled
- [ ] A broken product image falls back to the placeholder

---

# Record of what was run during development

Environment: Ubuntu sandbox, OpenJDK 21, **Tomcat 10.1.55**, **MariaDB 10.11**, MariaDB Connector/J 2.7.6, JSTL taglib converted to Jakarta namespaces. Headless Chromium for UI checks.

| Area | Result |
|---|---|
| `javac` compile of all 110 source files | Passed, no warnings treated as errors |
| JSP compilation (every view rendered by Tomcat's Jasper) | Passed after fixing one EL reserved-word issue (`.empty`) |
| Scripted integration suite (HTTP, cookie sessions, CSRF tokens, direct SQL assertions) | **104 / 104 checks passed** on a freshly seeded database |
| Responsive overflow check at 1440 / 1024 / 768 / 390 / 360 px on 11 pages | No horizontal overflow |
| Browser console errors on those pages | None |
| Cart drawer, wishlist heart, mobile menu, Escape-to-close | Worked in headless Chromium |

Not run: MySQL 8 + Connector/J, `mvn package`, real Google Fonts, Safari/Firefox, screen-reader testing, load testing. The integration script is not shipped with the project.

Bugs found and fixed by this testing: a MariaDB/MySQL-portability issue with an unsigned column in the generated `sale_price` expression, JSP use of the EL reserved word `empty`, a mis-located TLD file, and an invalid `SameSite` element in `web.xml` (the cookie now carries `SameSite=Lax`).
