# PaySnap - QR-Based Payment Link Generator

![PaySnap](https://img.shields.io/badge/PaySnap-Payment%2520Solution-blue)
![Spring Boot](https://img.shields.io/badge/Spring%2520Boot-3.2-green)
![Stripe](https://img.shields.io/badge/Stripe-Payment%2520Processing-635bff)

PaySnap is a secure and modular payment system that allows users to generate Stripe-powered payment links and QR codes for fast and contactless transactions. Built with modern Java technologies, it provides a robust backend API for managing payments, orders, and receipts.

## Features
- QR Code Payments: Generate scannable QR codes for instant payment processing
- Stripe Integration: Secure payment processing with Stripe Checkout
- JWT Authentication: Secure token-based authentication with Redis blacklisting
- Real-time Webhooks: Instant payment status updates via Stripe webhooks
- PDF Receipts: Automatic receipt generation for completed payments
- Order Management: Complete order history and tracking
- Multi-currency Support: Support for USD, EUR, GBP, and more
- RESTful API: Clean, well-documented API endpoints

## Technology Stack
Backend:
- Java 17
- Spring Boot 3.2
- Spring Security
- Spring Data JPA
- Spring Data Redis

Database & Caching:
- PostgreSQL
- Redis

Payment & Utilities:
- Stripe SDK
- ZXing (Zebra Crossing) for QR code generation
- JJWT for JWT authentication
- Lombok

Development Tools:
- Docker & Docker Compose
- Maven
- Postman

## Prerequisites
- Java 17 or higher
- Maven 3.6+
- Docker and Docker Compose
- Stripe account with API keys

## Quick Start

1. Clone the Repository
git clone https://github.com/yourusername/paysnap.git
cd paysnap

2. Environment Setup
Create application.properties:
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/paysnap
spring.datasource.username=postgres
spring.datasource.password=password

# Redis
spring.data.redis.host=localhost
spring.data.redis.port=6379

# JWT
jwt.secret=your-64-character-secret-key-here-must-be-very-long-for-hs512
jwt.expiration=86400000

# Stripe
stripe.secret-key=sk_test_your_stripe_secret_key
stripe.publishable-key=pk_test_your_stripe_publishable_key
stripe.webhook-secret=whsec_your_webhook_secret
stripe.success-url=http://localhost:8080/api/payment/success
stripe.cancel-url=http://localhost:8080/api/payment/cancel

# Application
app.payment.session-timeout-minutes=15
app.payment.base-url=http://localhost:8080

3. Start Infrastructure
docker-compose up -d

4. Build and Run
mvn clean install
mvn spring-boot:run
The application will start on http://localhost:8080

## API Documentation

Authentication Endpoints
POST /api/auth/register - Register new user
POST /api/auth/login - User login
POST /api/auth/logout - User logout

Payment Endpoints
POST /api/payment/create - Create payment session
GET /api/payment/orders - Get user orders
GET /api/payment/orders/{id} - Get specific order

Webhook Endpoints
POST /api/webhook/stripe - Stripe webhook handler
GET /api/webhook/health - Webhook health check

## Usage Examples

1. User Registration
curl -X POST http://localhost:8080/api/auth/register \
-H "Content-Type: application/json" \
-d '{"username": "testuser","password": "password123"}'

2. User Login
curl -X POST http://localhost:8080/api/auth/login \
-H "Content-Type: application/json" \
-d '{"username": "testuser","password": "password123"}'

3. Create Payment Session
curl -X POST http://localhost:8080/api/payment/create \
-H "Content-Type: application/json" \
-H "Authorization: Bearer YOUR_JWT_TOKEN" \
-d '{"amount": 2000,"currency": "USD","description": "Test payment","customerEmail": "customer@example.com"}'

4. Get User Orders
curl -X GET http://localhost:8080/api/payment/orders \
-H "Authorization: Bearer YOUR_JWT_TOKEN"

## Stripe Setup
- Create a Stripe account at stripe.com
- Get your API keys from Stripe Dashboard
- Set up webhooks for local development using Stripe CLI:
stripe listen --forward-to localhost:8080/api/webhook/stripe

## Database Schema
- users - User accounts and authentication
- orders - Payment orders and status tracking
- receipts - Generated payment receipts

## Security Features
- JWT-based authentication
- Redis token blacklisting
- Password encryption with BCrypt
- CORS configuration
- Input validation and sanitization

## Testing
- Import Postman collection: /docs/postman/PaySnap-Collection.json
- Set environment variables: base_url=http://localhost:8080, token=(auto-populated after login)
- Test flow: register → login → create payment → scan QR → check order → logout

## Project Structure
PaySnap/
├── src/main/java/com/vahabvahabov/PaySnap/
│   ├── config/
│   ├── controller/
│   ├── service/
│   ├── repository/
│   ├── model/
│   ├── dto/
│   ├── exception/
│   └── security/
├── src/main/resources/
│   ├── application.properties
│   └── templates/
├── docker-compose.yml
└── README.md

## Deployment
- Update application.properties with production values
- Use PostgreSQL and Redis production instances
- Configure SSL/TLS certificates
- Set proper firewall rules

Environment Variables for Production
export SPRING_DATASOURCE_URL=jdbc:postgresql://production-db:5432/paysnap
export SPRING_DATASOURCE_USERNAME=prod_user
export SPRING_DATASOURCE_PASSWORD=prod_password
export STRIPE_SECRET_KEY=sk_live_...
export JWT_SECRET=your-production-secret

## Contributing
- Fork the repository
- Create a feature branch: git checkout -b feature/amazing-feature
- Commit changes: git commit -m 'Add some amazing feature'
- Push to branch: git push origin feature/amazing-feature
- Open a Pull Request

## License
MIT License - see LICENSE file

## Support
- Check the Issues page
- Create a new issue with logs and steps to reproduce

## Acknowledgments
- Stripe for payment processing
- Spring Boot team
- ZXing for QR code generation
