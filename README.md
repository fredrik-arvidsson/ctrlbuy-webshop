# CtrlBuy E-commerce Platform

> **Enterprise-ready Spring Boot application with multi-layered security and clean architecture**

![Java](https://img.shields.io/badge/Java-17-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-brightgreen.svg)
![Security](https://img.shields.io/badge/Security-Multi--Layer-red.svg)
![Test Coverage](https://img.shields.io/badge/Coverage-100%25-brightgreen.svg)
![Tests](https://img.shields.io/badge/Tests-352%20Passing-success.svg)

## 🎯 What is CtrlBuy?

Professional e-commerce platform showcasing modern Spring Boot development practices. Built with enterprise patterns, comprehensive testing, multi-layered security, and clean architecture.

**Key Features:**
- 🛒 Complete shopping cart functionality
- 👥 User authentication & authorization
- 🛡️ Multi-layered security architecture
- 📧 Email integration with MailHog testing
- 🔧 Admin dashboard for management
- 🏗️ Clean architecture with service layers
- 🧪 352 tests with 100% coverage

## 🛡️ Security Architecture

**Multi-Layered Security Approach:**

### URL-Level Security
Protected endpoints with role-based access control. Admin endpoints require ADMIN role while user profiles require authentication. Public endpoints are accessible to all users.

### Method-Level Security
Controller methods are protected with PreAuthorize annotations. Admin dashboard is restricted to ROLE_ADMIN and user profiles are restricted to authenticated users. Automatic security enforcement happens at method level.

### Frontend Security
Conditional rendering based on user roles. Admin controls are only visible to administrators and user-specific content is based on authentication status. Clean separation exists between public and private content.

### CSRF Protection
- CSRF tokens for all state-changing operations
- Cookie-based token repository with HttpOnly flags
- XOR token request handling for enhanced security

### Password Security
- BCrypt password encoding with salt
- Secure password validation
- Environment-based admin credentials

### Session Management
- Secure session handling
- Remember-me functionality with secure tokens
- Automatic session invalidation on logout

**Security Features:**
- ✅ Role-based access control (ADMIN, USER)
- ✅ CSRF protection on all forms
- ✅ Secure password hashing (BCrypt)
- ✅ Method-level security annotations
- ✅ Environment-based secrets (no hardcoded passwords)
- ✅ Session security with timeout handling

## 🚀 Quick Start

### Prerequisites
- Java 17 or higher
- Docker & Docker Compose
- Git

### Run Locally
1. Clone the repository
2. Start database and email services with Docker Compose
3. Run the application with Maven

### Access the Application

| Component | Access | Description |
|-----------|--------|-------------|
| Main Application | http://localhost:8080 | E-commerce storefront |
| Admin Dashboard | http://localhost:8080/admin/dashboard | Management interface |
| Email Testing | http://localhost:8025 | MailHog email viewer |
| Database | localhost:3306 | MySQL (Docker) |

## 🔐 Security Configuration

### Admin Access
For development environment:
- Username: admin
- Password: Set via ADMIN_PASSWORD environment variable
- Access: http://localhost:8080/admin/dashboard

### Environment Variables
Required environment variables for security:
- ADMIN_USERNAME: Administrator username
- ADMIN_PASSWORD: Secure administrator password
- ADMIN_EMAIL: Administrator email address

Optional security settings:
- JWT_SECRET: Secret key for JWT tokens
- REMEMBER_ME_KEY: Key for remember-me functionality

## 🧪 Testing & Quality

Run all tests with Maven test command. Generate coverage report with Jacoco plugin. View coverage report in target/site/jacoco/index.html.

**Test Statistics:**
- ✅ 352 tests passing
- ✅ 100% line coverage
- ✅ Security tests included
- ✅ Unit & Integration tests
- ✅ Automated CI/CD pipeline

## 🏗️ Architecture

```
src/
├── main/java/com/ctrlbuy/
│   ├── controller/     # REST endpoints
│   ├── service/        # Business logic
│   ├── repository/     # Data access
│   ├── model/          # Entity classes
│   ├── security/       # Security configuration
│   └── config/         # Application configuration
├── test/               # Comprehensive tests
└── resources/          # Configuration files
```

## 🛠️ Tech Stack

- **Backend:** Spring Boot 3.2, Spring Security, Spring Data JPA
- **Security:** Method-level security, CSRF protection, BCrypt
- **Database:** MySQL 8.0
- **Testing:** JUnit 5, Mockito, TestContainers, Security tests
- **Email:** JavaMailSender with MailHog
- **Build:** Maven
- **CI/CD:** GitHub Actions

## 📋 Development

### Database Setup
Start MySQL container with Docker Compose. Application will auto-create tables when running. Check logs for startup confirmation.

### Email Testing
MailHog captures all emails during development. View emails at http://localhost:8025. No real emails are sent during development.

### Security Development
Enable security debugging for troubleshooting. Test security configurations with curl commands. Unauthenticated users should be redirected to login.

## 🔧 Configuration

**Key configuration files:**
- SecurityConfig.java - Multi-layered security setup
- application.yml - Main application settings
- docker-compose.yml - Local development services
- pom.xml - Dependencies and build configuration

## 📈 Status

**Current State:** ✅ Fully functional for local development
- All core features implemented
- Enterprise-grade security implementation
- Comprehensive test suite
- Production-ready code quality
- Clean, maintainable architecture

**Security Compliance:**
- ✅ OWASP security best practices
- ✅ Role-based access control
- ✅ CSRF protection
- ✅ Secure session management
- ✅ Environment-based secrets

**Future Plans:**
- Cloud deployment setup
- OAuth2 integration
- Additional security monitoring
- Performance optimizations

## 🤝 Contributing

1. Fork the repository
2. Create feature branch
3. Commit changes
4. Push to branch
5. Open Pull Request

**Security Guidelines:**
- All admin functionality must use PreAuthorize annotations with ADMIN role
- Never hardcode passwords or secrets in code
- Include security tests for new features
- Follow OWASP security guidelines

## 📜 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

**Made with ❤️ in Sweden** | *Showcasing enterprise Spring Boot development with security focus*# Railway deployment
