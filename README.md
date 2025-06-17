# 🛒 Online Grocery Shop – Bot Picker System

This project simulates an online grocery shopping application with a smart warehouse picking bot. Users can browse products, place orders, and track bot movement in the warehouse as it picks up items.

## 💡 Project Idea

This automated fulfillment system models the warehouse as a 2D grid. Each coordinate `(X, Y)` holds a single product type with unlimited quantity. A single bot calculates an optimal route (starting and ending at `(0, 0)`) to pick items for a customer order.

**Important**: The bot can only move horizontally or vertically along the X and Y axes — diagonal movement is not allowed.

---

## 🔧 Technologies Used

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Security 6**
- **MySQL 8.0**
- **Spring Data JPA**
- **Thymeleaf**
- **Maven**
- **Bootstrap 5.3**
- **Bootstrap Icons**

---

## 🗂️ Project Structure

```
src/main/java/com/online/shop/
├── config/                     → Security configuration
├── controller/                 → Web and REST controllers
├── dto/                       → DTOs for requests/responses
├── entity/                    → JPA entity models (Product, Order, RouteStep, User)
├── repository/                → Spring Data JPA Repositories
├── service/                   → Business logic
├── util/                      → Bot route calculator
└── OnlineGroceryApplication.java
```

---

## 👤 Features for Users

### Authentication
- ✅ User registration (Name, Email, Password, Phone, Address)
- 🔐 Login/logout with session handling
- 🔒 Page access secured using Spring Security

### Product Management (CRUD)
- Add new product: `name`, `price`, `quantity`, and `location (X,Y)`
- List all available products
- Edit existing product
- Delete product

### Order Management
- Place a new order by selecting multiple products and quantities
- Check if stock is sufficient
- Display `Order SUCCESS` or `Order FAIL` message
- Print bot's path through warehouse (e.g. `[0,0], [0,1], [1,0], [0,0]`)
- Reduce product inventory quantities on successful order

### Route Calculation
- Bot always starts and ends at (0,0)
- Movement allowed: horizontal/vertical only (no diagonals)
- Uses greedy algorithm (Manhattan distance) to calculate a simple optimized path

### Route Logging
- Each order saves the full route steps into the `routes` table
- Bot visits are displayed after order completion

### Error Message Example
❌ **We cannot fulfill your order right now – not enough stock Missing items:**
* Coca-Cola: requested 5, available 2
* Apples: requested 10, available 0

---

## 🌐 API Endpoints

### Product Management
- `POST /api/products` – Create a product
- `GET /api/products` – List all products
- `GET /api/products/available` – List available products
- `PUT /api/products/{id}` – Update a product
- `DELETE /api/products/{id}` – Delete a product

### Order Management
- `POST /api/orders` – Create a new order
  - Input: List of product names + quantities
  - Output: Status SUCCESS or FAIL
- `GET /api/orders/{id}` – Check status of an existing order

### Routes
- `GET /api/orders/routes?orderId=123` – Returns the list of warehouse locations (X, Y) visited by the bot to fulfill the given order.

Example:
```json
{
  "orderId": "123e4567-e89b-12d3-a456-426614174000",
  "status": "SUCCESS",
  "visitedLocations": [[0,0], [0,1], [1,1], [1,0], [0,0]]
}
```

---

## 🗄️ Database Schema

### `users`
| Column | Type | Description |
|--------|------|-------------|
| id | UUID | Primary key |
| name | VARCHAR(100) | User's full name |
| email | VARCHAR(150) | Unique email address |
| password | VARCHAR | Encrypted password |
| phone | VARCHAR(20) | Phone number |
| address | VARCHAR(500) | Full address |
| created_at | TIMESTAMP | Account creation time |
| updated_at | TIMESTAMP | Last update time |

### `products`
| Column | Type | Description |
|--------|------|-------------|
| id | UUID | Primary key |
| name | VARCHAR(200) | Product name |
| price | DECIMAL(10,2) | Product price |
| quantity | INTEGER | Available stock |
| x | INTEGER | X coordinate in warehouse |
| y | INTEGER | Y coordinate in warehouse |
| created_at | TIMESTAMP | Product creation time |
| updated_at | TIMESTAMP | Last update time |

### `orders`
| Column | Type | Description |
|--------|------|-------------|
| id | UUID | Primary key |
| user_id | UUID | Foreign key to users |
| status | ENUM | SUCCESS or FAIL |
| created_at | TIMESTAMP | Order creation time |

### `order_items`
| Column | Type | Description |
|--------|------|-------------|
| id | UUID | Primary key |
| order_id | UUID | Foreign key to orders |
| product_id | UUID | Foreign key to products |
| quantity | INTEGER | Ordered quantity |

### `routes`
| Column | Type | Description |
|--------|------|-------------|
| id | UUID | Primary key |
| order_id | UUID | Foreign key to orders |
| step_index | INTEGER | Step sequence number |
| x | INTEGER | X coordinate |
| y | INTEGER | Y coordinate |

---

## 🌐 Frontend (Thymeleaf)

- **Login & Registration pages** with modern Bootstrap styling
- **Product management dashboard** with CRUD operations
- **Place new order** with multi-select product list and real-time order summary
- **Bot route visualization** as coordinate sequence
- **User-friendly success/fail messages** with detailed error information
- **Responsive design** that works on desktop and mobile devices

---

## 🚀 Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+

### Database Setup
1. Create a MySQL database named `grocery_shop`
2. Update `src/main/resources/application.properties` with your database credentials:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/grocery_shop
spring.datasource.username=root
spring.datasource.password=your_password
```

### Running the Application
1. Clone the repository
2. Navigate to the project directory
3. Run the application:

```bash
mvn spring-boot:run
```

4. Open your browser and go to `http://localhost:8080`
5. Register a new account or login
6. Start adding products and placing orders!

### Sample Data
You can add some sample products through the web interface:
- **Apples** - $2.50, Quantity: 100, Location: (1, 1)
- **Bananas** - $1.80, Quantity: 50, Location: (2, 1)
- **Milk** - $3.20, Quantity: 30, Location: (1, 2)
- **Bread** - $2.00, Quantity: 25, Location: (3, 1)

---

## 🧠 Bot Algorithm

The bot uses a **greedy nearest-neighbor algorithm** with Manhattan distance:

1. Start at (0, 0)
2. Find the nearest unvisited product location
3. Move horizontally first, then vertically to reach the location
4. Repeat until all products are collected
5. Return to (0, 0)

**Manhattan Distance Formula**: `|x1 - x2| + |y1 - y2|`

---

## 🛡️ Security Features

- **Password encryption** using BCrypt
- **Session management** with Spring Security
- **CSRF protection** enabled
- **Input validation** on all forms
- **SQL injection prevention** through JPA
- **XSS protection** via Thymeleaf escaping

---

## 🎯 Future Enhancements

- 📦 Pagination and filtering in product list
- 📜 Order history for logged-in users
- 📈 Export route data (CSV or PDF)
- 🛡️ Admin role with extra permissions
- 📊 Dashboard with analytics and insights
- 🗺️ Visual warehouse grid representation
- 📱 Mobile app integration
- 🔔 Real-time notifications

---

## 📝 License

This project is created for educational purposes and demonstrates modern Spring Boot development practices.

---

## 🤝 Contributing

Feel free to fork this project and submit pull requests for improvements!

---

**Built with ❤️ using Spring Boot and modern web technologies** 