# Portfolio text

## GitHub repository description (one line)
Premium fashion e-commerce web app built with Java Servlets, JSP, JDBC and MySQL - MVC + DAO, transactional checkout, secure auth and an admin dashboard. No frameworks.

**Suggested topics:** `java` `servlets` `jsp` `jdbc` `mysql` `tomcat` `maven` `mvc` `dao-pattern` `ecommerce` `jakarta-ee` `full-stack`

## GitHub "About" / pinned-repo blurb
Glamoraa is a complete fashion e-commerce site built the traditional way: Jakarta Servlets and JSP for the web layer, a DAO + service architecture over plain JDBC, and a normalised MySQL schema with per-variant stock. It covers catalogue search and filtering, a database-backed cart and wishlist, address management, a transactional Cash-on-Delivery checkout that cannot oversell stock, order cancellation, verified-purchase reviews and a role-protected admin back-office. Security includes PBKDF2 password hashing, CSRF tokens, ownership checks, a strict Content-Security-Policy and validated uploads. The front end is hand-written HTML, CSS and vanilla JavaScript with a custom design system and reduced-motion support.

## Resume entry

**Glamoraa - Full-Stack E-Commerce Web Application** | Java 17, Servlets, JSP/JSTL, JDBC, MySQL, Tomcat 10.1, Maven, HTML/CSS/JavaScript | *GitHub: <your link>*
- Designed and built a complete fashion e-commerce platform using the MVC and DAO patterns with a service layer, 14 JavaBeans, 11 DAO interfaces with JDBC implementations and 25+ servlets, without any framework.
- Modelled a 16-table MySQL schema (foreign keys, unique constraints, indexes, generated price column) with stock tracked per size/colour variant and order lines stored as snapshots.
- Implemented a transactional checkout using row locking and conditional stock updates, a one-time submission token and a unique idempotency key to prevent overselling and duplicate orders; cancellations restock exactly once.
- Secured the application with PBKDF2 password hashing, session-fixation protection, CSRF tokens on every POST, server-side role checks, per-user ownership queries, prepared statements, a strict CSP and signature-checked image uploads.
- Built a responsive, accessible storefront (custom CSS design system, vanilla-JS cart drawer, wishlist and variant picker, Intersection Observer animations, `prefers-reduced-motion` support) and an admin dashboard for products, inventory, orders and customers.
- Verified the build with a scripted 104-check HTTP/SQL integration suite on Tomcat 10.1 and headless-browser checks at five screen widths.

*(Do not add user counts, traffic or sales figures - this is a portfolio project with no real customers.)*

## 60-second interview explanation
"Glamoraa is a Servlets, JSP and JDBC e-commerce app. Requests go through filters for CSRF and authentication, then to a servlet controller that calls a service. Services own the business rules and the transaction; DAOs run prepared statements against MySQL; JSPs only render. The part I'm proudest of is checkout: it locks the variant rows in a fixed order, re-reads price and stock under the lock, decrements stock with a conditional update, writes the order and its snapshot lines, then clears the cart - all in one transaction, so a failure rolls everything back and two customers can't buy the last item."

## Likely questions to prepare
- Why PBKDF2 rather than BCrypt? (JDK built-in, NIST-approved, no dependency; BCrypt/Argon2 would also be fine - swap `PasswordUtil`.)
- How do you stop overselling? (`SELECT ... FOR UPDATE` plus `UPDATE ... WHERE stock_quantity >= ?` inside one transaction.)
- What does the CSRF filter do and why is logout a POST?
- Why is `sale_price` a generated column?
- What would you change for production? (connection pool, HTTPS + secure cookie, email, payment gateway, tests, CI.)
