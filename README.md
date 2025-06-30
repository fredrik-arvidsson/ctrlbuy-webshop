# CtrlBuy E-commerce Platform

> **Enterprise-ready Spring Boot application with 100% test coverage and clean architecture**

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Test Coverage](https://img.shields.io/badge/Coverage-100%25-brightgreen.svg)](https://github.com/fredrik-arvidsson/ctrlbuy-webshop)
[![Tests](https://img.shields.io/badge/Tests-352%20Passing-success.svg)](https://github.com/fredrik-arvidsson/ctrlbuy-webshop/actions)

## 🎯 What is CtrlBuy?

Professional e-commerce platform showcasing modern Spring Boot development practices. Built with enterprise patterns, comprehensive testing, and clean architecture.

**Key Features:**
- 🛒 Complete shopping cart functionality
- 👥 User authentication & authorization
- 📧 Email integration with MailHog testing
- 🔧 Admin dashboard for management
- 🏗️ Clean architecture with service layers
- 🧪 352 tests with 100% coverage

## 🚀 Quick Start

### Prerequisites
- Java 17 or higher
- Docker & Docker Compose
- Git

### Run Locally
```bash
# Clone the repository
git clone https://github.com/fredrik-arvidsson/ctrlbuy-webshop.git
cd ctrlbuy-webshop

# Start database and email services
docker-compose up -d

# Run the application
./mvnw spring-boot:run
```

### Access the Application
After starting locally:

| Component | Access | Description |
|-----------|--------|-------------|
| **Main Application** | http://localhost:8080 | E-commerce storefront |
| **Admin Dashboard** | http://localhost:8080/admin/dashboard | Management interface |
| **Email Testing** | http://localhost:8025 | MailHog email viewer |
| **Database** | localhost:3306 | MySQL (Docker) |

## 🧪 Testing & Quality

```bash
# Run all tests
./mvnw test

# Generate coverage report
./mvnw jacoco:report

# View coverage report
open target/site/jacoco/index.html
```

**Test Statistics:**
- ✅ **352 tests** passing
- ✅ **100% line coverage**
- ✅ **Unit & Integration tests**
- ✅ **Automated CI/CD pipeline**

## 🏗️ Architecture

```
src/
├── main/java/com/ctrlbuy/
│   ├── controller/     # REST endpoints
│   ├── service/        # Business logic
│   ├── repository/     # Data access
│   ├── model/          # Entity classes
│   └── config/         # Configuration
├── test/               # Comprehensive tests
└── resources/          # Configuration files
```

## 🛠️ Tech Stack

- **Backend:** Spring Boot 3.2, Spring Security, Spring Data JPA
- **Database:** MySQL 8.0
- **Testing:** JUnit 5, Mockito, TestContainers
- **Email:** JavaMailSender with MailHog
- **Build:** Maven
- **CI/CD:** GitHub Actions

## 📋 Development

### Database Setup
```bash
# Start MySQL container
docker-compose up -d mysql

# Application will auto-create tables
# Check logs: ./mvnw spring-boot:run
```

### Email Testing
```bash
# MailHog captures all emails
# View at: http://localhost:8025
# No real emails sent during development
```

### Admin Access
```bash
# Default admin credentials (development only):
# Username: admin
# Password: admin123
# Access: http://localhost:8080/admin/dashboard
```

## 🔧 Configuration

Key configuration files:
- `application.yml` - Main application settings
- `docker-compose.yml` - Local development services
- `pom.xml` - Dependencies and build configuration

## 📈 Status

**Current State:** ✅ Fully functional for local development
- All core features implemented
- Comprehensive test suite
- Production-ready code quality
- Clean, maintainable architecture

**Future Plans:**
- Cloud deployment setup
- Performance optimizations
- Additional payment methods

## 🤝 Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open Pull Request

## 📜 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

**Made with ❤️ in Sweden** | *Showcasing enterprise Spring Boot development*