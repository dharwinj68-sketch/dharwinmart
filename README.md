# DHARWINMART 🛒

A clean, functional, student-friendly Capstone E-Commerce MVP application engineered from scratch using modern **Java 17+**, **Spring Boot 3**, **Spring Data JPA**, **Hibernate**, **H2 Database (file-based)**, and **Thymeleaf**.

---

## 📌 Project Overview

**DHARWINMART** is designed as a complete, monolithic, student-approachable e-commerce platform. It provides a real-world shopping experience for customers (product catalog browsing, multi-attribute keyword search, category filtering, product details, session-based shopping cart, and transactional checkout with stock reduction) along with an intuitive **Demo Admin Portal** for managing store inventory and inspecting incoming customer orders.

All application data is persisted locally in an embedded file-based H2 database, requiring **zero external database installations** (no MySQL, MongoDB, or Firebase needed).

---

## ✨ Features

### Customer Storefront
- **Modern Responsive Storefront**: Clean, mobile-friendly interface styled with modern typography, subtle shadows, and responsive navigation.
- **Hero & Featured Showcase**: Landing page highlighting active offers, featured essentials, and category quick-filters.
- **Product Catalog & Category Filtering**: Browse all products or filter by:
  - `Electronics`
  - `Fashion`
  - `Home`
  - `Books`
  - `Accessories`
- **Keyword Search**: Search across product names and descriptions (e.g. `/search?keyword=laptop`) with clear search result status and empty states.
- **Product Details & Stock Validation**:
  - High-resolution product images, pricing in Indian Rupees (₹), category badges, and descriptions.
  - Live stock indicator (`In Stock` / `Out of Stock`).
  - Interactive quantity selector.
  - Automatic button disablement and "Out of Stock" notification when stock is 0.
- **Session-Based Shopping Cart**:
  - Add items directly from catalog or detail pages.
  - Live cart badge in navigation bar updating dynamically.
  - Increase / decrease quantities with instant subtotal re-calculation.
  - Remove single items or clear the entire cart.
  - Subtotal, free demo shipping, and grand total calculations.
- **Checkout & Transactional Order Placement**:
  - Collects customer Name, Email, Phone number, and Shipping Address with server-side validation.
  - Payment method: **Cash on Delivery / Demo Order** (no third-party credentials required).
  - Atomic stock reduction: Product inventory is deducted immediately in the database upon order placement.
  - Protection against race conditions and out-of-stock items during checkout.
- **Order Confirmation & Invoice**:
  - Displays unique Order Reference ID (`#DM-ORD-X`).
  - Formatted order timestamp, customer contact details, and shipping destination.
  - Itemized table with unit price snapshot, quantity, and total amount.
  - One-click "Continue Shopping" button.

### Demo Admin Portal
> **Note for Evaluators / Viva Examiners**: The Admin portal is configured in **Demo/MVP Mode** without complex multi-role authentication. This allows examiners and students to immediately test and demonstrate administrative workflows without managing passwords or session locks.

- **Admin Dashboard**:
  - Key Performance Indicators (KPIs): Total Products, Total Orders Placed, Active Categories, and Database status.
  - Quick action links: Add New Product, Inventory Table, Orders View.
  - Recent orders preview table.
- **Product Management (CRUD)**:
  - **Create**: Add new products with validation (Name required, Price > 0, Stock >= 0, Category required).
  - **Read**: View all inventory items with image thumbnails, categories, and stock alerts.
  - **Update**: Edit existing product details, update prices, and restock quantities.
  - **Delete**: Safely remove products with client-side JavaScript confirmation dialogs.
- **Order Management**:
  - View all historical customer orders sorted chronologically (newest first).
  - Inspect individual order details with complete itemized breakdown and delivery address.

---

## 🛠️ Technology Stack

| Layer | Technology |
| :--- | :--- |
| **Language** | Java 17+ |
| **Framework** | Spring Boot 3.2.5 |
| **Web MVC** | Spring MVC, Thymeleaf Template Engine |
| **Persistence** | Spring Data JPA, Hibernate ORM |
| **Database** | H2 Database Engine (File-Based Persistence) |
| **Validation** | Jakarta Bean Validation (Hibernate Validator) |
| **Build Tool** | Apache Maven & Maven Wrapper (`mvnw` / `mvnw.cmd`) |
| **Frontend** | HTML5, CSS3 (Custom Responsive Design), JavaScript (Vanilla) |
| **Testing** | JUnit 5, Spring Boot Test |

---

## 📋 System Requirements

- **Java Development Kit (JDK)**: Java 17 or higher
- **Operating System**: Windows, macOS, or Linux
- **Web Browser**: Chrome, Edge, Firefox, Safari

---

## 🚀 How to Run the Application

The project includes Maven scripts for seamless startup on Windows, macOS, and Linux.

### 1. Open Terminal / PowerShell
Navigate to the root directory where the project is located:
```powershell
cd c:\Users\dharw\OneDrive\Desktop\dharwinj
```

### 2. Run Using Maven Wrapper
On **Windows (Command Prompt / PowerShell)**:
```cmd
mvnw.cmd spring-boot:run
```
*(Or if you have Apache Maven installed globally)*:
```cmd
mvn spring-boot:run
```

On **macOS / Linux**:
```bash
./mvnw spring-boot:run
```

### 3. Access the Application
Once the console displays `Started DharwinMartApplication in ... seconds`:
- **Storefront Website**: [http://localhost:8080](http://localhost:8080)
- **Demo Admin Portal**: [http://localhost:8080/admin](http://localhost:8080/admin)
- **Products Catalog**: [http://localhost:8080/products](http://localhost:8080/products)
- **Shopping Cart**: [http://localhost:8080/cart](http://localhost:8080/cart)

---

## 🗄️ H2 Database Configuration

The application uses an **embedded file-based H2 database** located at `./data/dharwinmartdb.mv.db`. Your data (products, orders, order items) persists across server restarts!

### H2 Web Console Access
- **URL**: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
- **Driver Class**: `org.h2.Driver`
- **JDBC URL**: `jdbc:h2:file:./data/dharwinmartdb;AUTO_SERVER=TRUE;DB_CLOSE_DELAY=-1`
- **User Name**: `sa`
- **Password**: *(leave blank)*

> **Automatic Data Seeding**: On first run, `DataInitializer` automatically inserts 14 realistic sample products across all 5 categories when `productRepository.count() == 0`.

---

## 📐 Database Schema & Entities

### 1. `Product`
Represents an item available in the catalog.
- `id` (Long, Primary Key, Auto-increment)
- `name` (String, Not Blank)
- `description` (String, Max 1000 chars)
- `price` (BigDecimal, Scale 2, > 0)
- `imageUrl` (String)
- `category` (String: Electronics, Fashion, Home, Books, Accessories)
- `stock` (Integer, >= 0)

### 2. `Order`
Represents a customer purchase order.
- `id` (Long, Primary Key, Auto-increment)
- `customerName` (String, Not Blank)
- `email` (String, Valid Email)
- `phone` (String, Valid Phone)
- `address` (String, Delivery Address)
- `totalAmount` (BigDecimal, Total order cost)
- `orderDate` (LocalDateTime, Placement timestamp)
- `status` (String, Default: "PLACED")
- `items` (One-to-Many relationship with `OrderItem`, CascadeType.ALL)

### 3. `OrderItem`
Itemized line-item within an order preserving a snapshot of price.
- `id` (Long, Primary Key, Auto-increment)
- `order` (Many-to-One relationship with `Order`)
- `product` (Many-to-One relationship with `Product`)
- `quantity` (Integer)
- `price` (BigDecimal, Snapshot of product price at purchase time)

---

## 📂 Project Structure

```
dharwinmart/
├── src/
│   ├── main/
│   │   ├── java/com/dharwinmart/
│   │   │   ├── DharwinMartApplication.java       # Spring Boot main runner
│   │   │   ├── config/
│   │   │   │   └── DataInitializer.java          # Automatic DB seeder (14 items)
│   │   │   ├── controller/
│   │   │   │   ├── HomeController.java           # Storefront, search, category, details
│   │   │   │   ├── CartController.java           # Session cart actions
│   │   │   │   ├── CheckoutController.java       # Form validation & order placement
│   │   │   │   ├── AdminController.java          # Dashboard, product CRUD, orders
│   │   │   │   └── GlobalControllerAdvice.java   # Global cart badge & category data
│   │   │   ├── dto/
│   │   │   │   ├── Cart.java                     # Session shopping cart model
│   │   │   │   ├── CartItem.java                 # Individual cart item DTO
│   │   │   │   ├── CheckoutForm.java             # Checkout form with validations
│   │   │   │   └── ProductForm.java              # Admin product create/edit DTO
│   │   │   ├── entity/
│   │   │   │   ├── Product.java                  # JPA entity for items
│   │   │   │   ├── Order.java                    # JPA entity for orders
│   │   │   │   └── OrderItem.java                # JPA entity for line items
│   │   │   ├── exception/
│   │   │   │   ├── ResourceNotFoundException.java
│   │   │   │   ├── InsufficientStockException.java
│   │   │   │   └── GlobalExceptionHandler.java   # User-friendly error handlers
│   │   │   ├── repository/
│   │   │   │   ├── ProductRepository.java        # Spring Data JPA product queries
│   │   │   │   ├── OrderRepository.java          # Order queries & recent order sorting
│   │   │   │   └── OrderItemRepository.java      # Order item queries
│   │   │   └── service/
│   │   │       ├── ProductService.java           # Product business logic
│   │   │       ├── CartService.java              # Cart session management
│   │   │       └── OrderService.java             # Order placement & stock reduction
│   │   └── resources/
│   │       ├── application.properties            # App & H2 configuration
│   │       ├── static/
│   │       │   ├── css/
│   │       │   │   └── style.css                 # Clean modern responsive stylesheet
│   │       │   └── js/
│   │       │       └── main.js                   # Quantity controls & delete alerts
│   │       └── templates/
│   │           ├── fragments/
│   │           │   ├── header.html               # Navbar fragment
│   │           │   └── footer.html               # Footer fragment
│   │           ├── index.html                    # Storefront landing page
│   │           ├── products.html                 # Catalog and search results view
│   │           ├── product-details.html          # Product detail & quantity selector
│   │           ├── cart.html                     # Shopping cart table & summary
│   │           ├── checkout.html                 # Delivery form & demo order
│   │           ├── order-confirmation.html       # Success invoice view
│   │           ├── error.html                    # Friendly student error page
│   │           └── admin/
│   │               ├── dashboard.html            # Store stats & recent orders
│   │               ├── products.html             # Product management table
│   │               ├── product-form.html         # Add/edit product form
│   │               ├── orders.html               # Customer orders list
│   │               └── order-details.html        # Detailed order view
│   └── test/
│       └── java/com/dharwinmart/
│           ├── DharwinMartApplicationTests.java  # Spring context test
│           ├── ProductServiceTest.java           # Product CRUD & search test
│           ├── OrderServiceTest.java             # Order placement & stock deduction test
│           └── CartCalculationTest.java          # Cart math & quantity tests
├── Dockerfile                                    # Multi-stage production container build
├── .dockerignore                                 # Optimized Docker build context
├── render.yaml                                   # Render Blueprint configuration
├── pom.xml                                       # Maven build configuration
└── README.md                                     # Project documentation
```

---

## 🐳 Docker & Cloud Deployment (Render)

### Deploying to Render via Docker

1. **Push your code to GitHub**:
   ```bash
   git add .
   git commit -m "Add Docker and Render configuration"
   git push origin main
   ```

2. **Deploy on [Render.com](https://render.com/)**:
   - Go to your Render Dashboard and click **New +** -> **Web Service**.
   - Select **Build and deploy from a Git repository** and connect your `dharwinmart` repository.
   - Set the runtime environment to **Docker**.
   - Select the **Free** instance type.
   - Click **Create Web Service**.
   - *(Alternative)*: Select **Blueprints** and Render will automatically detect `render.yaml`.

3. **Environment Settings**:
   - Render automatically injects the `PORT` variable (default `10000`).
   - The application dynamically binds to `PORT` and initializes the database.

---

## 🧪 Testing

Run all automated unit and integration tests using:
```cmd
mvn test
```
*(or `./mvnw test` / `mvnw.cmd test`)*

The test suite covers:
1. `DharwinMartApplicationTests`: Confirms the Spring Boot `ApplicationContext` loads without issues.
2. `ProductServiceTest`: Verifies product creation, retrieval by ID, keyword search, category filtering, and deletion.
3. `OrderServiceTest`: Verifies order placement, calculation of total order cost, and atomic inventory stock deduction.
4. `CartCalculationTest`: Verifies unit price math, quantity increments/decrements, item removal, and grand total calculations.

---

## 🔮 Future Enhancements

For a production deployment beyond this Capstone MVP:
- **User Authentication & Role-Based Access Control**: Spring Security with JWT or OAuth2 for separating customer and admin logins.
- **Online Payment Gateways**: Integration with Razorpay, Stripe, or PayPal.
- **Customer Reviews & Ratings**: User-generated reviews with star ratings for each product.
- **Wishlist**: Save favorite items for future purchases.
- **Order Tracking & Email Dispatch**: Live order progress tracking (PLACED -> PACKED -> SHIPPED -> DELIVERED) with automated email notifications via Spring Mail.
- **Coupon / Discount Engine**: Promo codes and voucher discounts at checkout.

---

## 🎓 Viva / Academic Presentation Tips

- **Question**: *Why is `BigDecimal` used for price instead of `double` or `float`?*
  - **Answer**: `float` and `double` are binary floating-point numbers subject to rounding and precision errors (e.g. `0.1 + 0.2 = 0.30000000000000004`). `BigDecimal` provides exact decimal representation essential for currency calculations.
- **Question**: *How is cart persistence implemented?*
  - **Answer**: The cart is stored in the user's `HttpSession`. This ensures each visitor gets an isolated cart without requiring premature user login or database bloat.
- **Question**: *How is stock handled during checkout?*
  - **Answer**: In `OrderService.placeOrder()`, the method is `@Transactional`. It validates that all requested quantities are `<= product.stock`. If available, it creates the order items and immediately decrements each product's stock in the same database transaction.
- **Question**: *Why H2 file mode instead of in-memory?*
  - **Answer**: H2 in file mode (`jdbc:h2:file:./data/dharwinmartdb`) ensures that newly created products and placed orders persist even if the server is stopped or restarted, while still requiring zero external database installations.
