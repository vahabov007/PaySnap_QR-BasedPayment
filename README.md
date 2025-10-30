# PaySnap – Contactless QR Payment Platform

PaySnap is a secure and modular payment platform designed for businesses and individuals to generate Stripe-powered payment links and QR codes for fast, contactless transactions. With a clean REST API and real-time payment tracking, PaySnap enables seamless integration with modern payment workflows.

---

## Key Features

- **QR Code Payments:** Instantly generate QR codes for customer payments.  
- **Stripe Integration:** Secure checkout using Stripe’s API.  
- **JWT Authentication:** Token-based authentication with Redis token blacklisting.  
- **Real-time Webhooks:** Receive instant payment status updates via Stripe webhooks.  
- **PDF Receipts:** Automatically generate downloadable receipts.  
- **Order Management:** Full order history with search and filtering capabilities.  
- **Multi-Currency Support:** Accept payments in USD, EUR, GBP, and more.  
- **RESTful API:** Clean and well-documented API endpoints for backend integration.

---

## Technology Stack

**Backend & API:**  
- Java 17  
- Spring Boot 3.2  
- Spring Security (JWT + Redis blacklisting)  
- Spring Data JPA  

**Database & Caching:**  
- PostgreSQL  
- Redis  

**Payment & Utilities:**  
- Stripe SDK  
- ZXing (QR code generation)  
- JJWT (JWT implementation)  
- Lombok  

**Development Tools:**  
- Docker & Docker Compose  
- Maven  
- Postman  

---

## Getting Started

### Prerequisites
- Java 17 or higher  
- Maven 3.6+  
- Docker & Docker Compose  
- Stripe account with API keys  

### Installation

1. Clone the repository:
```bash
git clone https://github.com/yourusername/paysnap.git
cd paysnap
