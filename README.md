# Computer Assembly Company Management System

This is a Java application with a JavaFX frontend and a backend designed to manage the production operations of a computer assembly company. The application provides various functionalities for managers, employees, and customers to interact with the system.

## Project Requirements

The system is designed to meet the following requirements:

### Services Offered
1. **Purchase Pre-Assembled Systems**: Customers can buy pre-assembled electronic systems.
2. **Custom Desktop PC Assembly**: Customers can have desktop PCs built from provided parts.
3. **Service Requests**: Customers can request service for their systems.

### User Roles
- **Manager**: Can add new employees and view all employees. 
  - **Employees**: Can be categorized as juniors or seniors.
    - **Junior Employees**: Can process orders and change order or service statuses.
    - **Senior Employees**: Can add new products and parts to the system, in addition to the capabilities of junior employees.
- **Customers**: 
  - Can view products without an account.
  - Must create an account and log in to place orders.
  - Can place two types of orders: purchase or service.
    - **Purchase Orders**: Can include only catalog items (parts or pre-assembled systems). An additional fee of 100 RON is charged for pre-assembled parts.
    - **Service Orders**: Include authentication details, a problem description, and a chosen date for service.

### Product Categories
- **Electronic Systems**: Desktop PCs, Laptop PCs, Printers, and Peripherals.
- **Components**: All parts are categorized under components.

### Product Details
- Each product has a price and a description.
  - The description includes the component type, a brief description (up to 100 words), and a rating from critics (1-5 stars).

### Promotions
- Managers can propose and delete promotions.
- Promotions are listed in a separate category and, when added to the cart, include the products in the promotion and a special item called promotion with a negative price of 10% of the total product value.

## Implemented Features

- **Authentication**: Users can create accounts and log in.
- **View Products**: All users can view products, regardless of their role.

## Technologies Used

- **Java**
- **JavaFX**: For the frontend.
- **MySQL**: For the backend database.
