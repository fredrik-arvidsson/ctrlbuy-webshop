# 🛒 CtrlBuy E-commerce Platform

> **Enterprise-ready Spring Boot application with multi-layered security and Railway cloud deployment**

![Java](https://img.shields.io/badge/Java-17-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3-brightgreen.svg)
![Railway](https://img.shields.io/badge/Deployed-Railway-purple.svg)
![MySQL](https://img.shields.io/badge/Database-MySQL-blue.svg)
![Security](https://img.shields.io/badge/Security-Multi--Layer-red.svg)
![Test Coverage](https://img.shields.io/badge/Coverage-Real--Time-brightgreen.svg)
![Tests](https://img.shields.io/badge/Tests-136%20Passing-success.svg)

## 🌐 **LIVE DEMO**
**✨ [ctrlbuy-webshop-production.up.railway.app](https://ctrlbuy-webshop-production.up.railway.app) ✨**

*Full e-commerce platform running live on Railway cloud infrastructure*

## 📊 **LIVE TEST COVERAGE DASHBOARD**
**🎯 [Real-time JaCoCo Coverage Report](https://fredrik-arvidsson.github.io/ctrlbuy-webshop/) 🎯**

*Live test coverage metrics updated automatically with every commit*

---

## 🎯 What is CtrlBuy?

**Professional e-commerce platform** showcasing modern Spring Boot development practices with **production-ready cloud deployment**. Built with enterprise patterns, comprehensive testing, and battle-tested Railway MySQL integration.

### 💼 **Perfect for LinkedIn Showcase:**
- 🚀 **Live production deployment** on Railway
- 🛡️ **Enterprise-grade security** implementation
- 🏗️ **Clean architecture** with DTO patterns and service layers
- 📧 **Email automation** with order confirmations
- 🇸🇪 **Swedish localization** and currency (SEK)
- 💳 **Complete checkout flow** with order management
- 📊 **Real-time test coverage** dashboard

---

## 🏆 **Key Achievements**

### 🔧 **Technical Problem Solving**
Successfully resolved complex **Railway MySQL deployment challenges** including:
- Environment variable precedence conflicts resolution
- MySQL user permissions for external connections (`railway_user` creation)
- Spring Boot cloud configuration optimization
- Production-ready security implementation after **16+ hours debugging**

### 🛒 **Complete E-commerce Features**
- ✅ User registration & authentication
- ✅ Product catalog with categories
- ✅ Shopping cart functionality
- ✅ Secure checkout process
- ✅ Order confirmation emails via JavaMailSender
- ✅ Admin dashboard for management
- ✅ Swedish tax calculation (25% VAT)
- ✅ Order number generation with sequence management

### 🛡️ **Enterprise Security**
- ✅ Multi-layered security architecture
- ✅ Role-based access control (ADMIN/USER)
- ✅ CSRF protection on all forms
- ✅ BCrypt password encryption
- ✅ Environment-based secrets management
- ✅ Method-level security with @PreAuthorize

---

## 🚀 **Live Demo Features**

| Feature | Status | Demo Link |
|---------|--------|-----------|
| 🏠 **Storefront** | ✅ Live | [Homepage](https://ctrlbuy-webshop-production.up.railway.app) |
| 🛒 **Shopping** | ✅ Live | Register & Shop |
| ⚙️ **Admin Panel** | ✅ Live | Admin Dashboard |
| 📧 **Email Orders** | ✅ Live | Order Confirmations |
| 💳 **Checkout** | ✅ Live | Complete Purchase Flow |
| 📊 **Test Coverage** | ✅ Live | [Coverage Dashboard](https://fredrik-arvidsson.github.io/ctrlbuy-webshop/) |

### 🔐 **Test Admin Access**
- Navigate to the admin dashboard
- Full product & user management
- Order tracking and status updates

---

## 🏗️ **Architecture & Tech Stack**

### **Backend Excellence**
```
🔹 Spring Boot 3.3 - Latest framework with auto-configuration
🔹 Spring Security - Multi-layer protection with method security
🔹 Spring Data JPA - Database abstraction with Repository pattern
🔹 Hibernate - ORM with MySQL schema generation
🔹 MySQL 9.0 - Production database on Railway cloud
🔹 JavaMailSender - Email automation with HTML templates
🔹 DTO Pattern - Clean data transfer between layers
```

### **Frontend & UX**
```
🔹 Thymeleaf - Server-side rendering with fragments
🔹 Bootstrap - Responsive design framework
🔹 Swedish localization - Complete i18n implementation
🔹 Professional UI/UX - Admin & customer interfaces
🔹 HTMX integration - Enhanced user interactions
```

### **Cloud & DevOps**
```
🔹 Railway Platform - Cloud deployment with auto-scaling
🔹 GitHub Actions - CI/CD with automated testing
🔹 JaCoCo - Real-time test coverage reporting
🔹 Environment Management - Secure secrets handling
🔹 Production Monitoring - Health checks and logging
```

---

## 🛡️ **Security Implementation**

### **Multi-Layered Security Architecture**

#### 🔒 **URL-Level Security**
- Protected admin endpoints (`/admin/**`)
- User profile security (`/profile/**`)
- Public access for storefront and static resources

#### 🎯 **Method-Level Security**
- `@PreAuthorize("hasRole('ADMIN')")` annotations
- Role-based method access control
- Automatic Spring Security enforcement

#### 🌐 **Frontend Security**
- Conditional rendering by user role
- CSRF tokens on all forms
- XSS protection headers
- Secure session management

#### 🔐 **Data Security**
- BCrypt password hashing with salt
- Environment-based database credentials
- Secure Railway MySQL connections

---

## 🚀 **Deployment Journey**

### **Railway Cloud Deployment Success**
Successfully deployed enterprise-grade application on **Railway platform** after overcoming:

#### **Technical Challenges Solved:**
1. **MySQL User Permissions Crisis**
    - Problem: Default Railway MySQL user lacked external connection rights
    - Solution: Created dedicated `railway_user` with `host='%'` permissions
    - Result: Successful external database connections

2. **Environment Variable Precedence Conflicts**
    - Problem: Spring Boot configuration priority issues
    - Solution: Optimized application.yml vs environment variable hierarchy
    - Result: Clean cloud-native configuration

3. **Database Connection Optimization**
    - Problem: Connection pooling for cloud deployment
    - Solution: HikariCP configuration tuning for Railway
    - Result: Stable production database performance

### **Production Infrastructure**
- ✅ **Auto-scaling Railway infrastructure**
- ✅ **SSL/HTTPS security** with automatic certificates
- ✅ **MySQL 9.0** managed database service
- ✅ **Environment isolation** with secure secrets

---

## 🧪 **Quality Assurance & Testing**

### **Comprehensive Test Suite**
```bash
# Run all 136 tests with coverage
mvn test

# Generate JaCoCo coverage report
mvn jacoco:report

# View coverage report locally
open target/site/jacoco/index.html
```

### **Test Statistics (Latest Run)**
- 🎯 **136 tests passing** (0 failures, 0 errors, 0 skipped)
- 📊 **Real-time coverage tracking** via GitHub Actions
- 🔒 **Security tests** for authentication & authorization
- ⚡ **Performance tests** for database operations
- 🧪 **Unit tests** for all service layers
- 🔄 **Integration tests** for full workflows

### **Test Coverage Dashboard**
- **Live Coverage Reports:** [GitHub Pages Dashboard](https://fredrik-arvidsson.github.io/ctrlbuy-webshop/)
- **Automated Updates:** Coverage updated on every commit
- **Detailed Metrics:** Line, branch, and method coverage
- **Visual Reports:** Interactive HTML coverage explorer

### **Code Quality Standards**
- Clean Architecture principles
- SOLID design patterns
- Enterprise coding standards
- Comprehensive JavaDoc documentation

---

## 🔧 **Local Development**

### **Quick Start**
```bash
# 1. Clone repository
git clone https://github.com/fredrik-arvidsson/ctrlbuy-webshop.git
cd ctrlbuy-webshop

# 2. Start MySQL with Docker
docker run --name ctrlbuy-mysql -e MYSQL_ROOT_PASSWORD=password -e MYSQL_DATABASE=ctrlbuy -p 3306:3306 -d mysql:9.0

# 3. Run application
mvn spring-boot:run

# 4. Generate test coverage
mvn test jacoco:report
```

### **Development Environment**
| Service | URL | Purpose |
|---------|-----|---------|
| **Application** | http://localhost:8080 | Main e-commerce platform |
| **Admin Dashboard** | http://localhost:8080/admin | Management interface |
| **Test Coverage** | target/site/jacoco/index.html | Local coverage report |
| **Database** | localhost:3306 | MySQL development DB |

### **Environment Configuration**
```properties
# application-local.properties example
spring.datasource.url=jdbc:mysql://localhost:3306/ctrlbuy
spring.datasource.username=root
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=update
```

---

## 📈 **Production Metrics & Performance**

### **Live Performance**
- ⚡ **Sub-second response times** - Optimized database queries
- 🔄 **Auto-scaling** - Railway infrastructure adaptation
- 💾 **Database optimization** - Hibernate query tuning
- 📱 **Mobile responsive** - Bootstrap framework

### **Reliability & Monitoring**
- 🟢 **Production uptime** on Railway platform
- 🔒 **Secure HTTPS** connections with auto-renewal
- 📧 **Email delivery** confirmation tracking
- 🛡️ **Security monitoring** with Spring Security
- 📊 **Real-time test coverage** monitoring

---

## 🌟 **Why This Project Stands Out**

### **For Technical Recruiters:**
1. **Production-Ready Deployment** - Live Railway cloud platform
2. **Problem-Solving Excellence** - 16+ hours debugging Railway MySQL
3. **Enterprise Architecture** - Multi-layer security, DTO patterns, service layers
4. **Quality Engineering** - 136 passing tests with real-time coverage
5. **Full-Stack Competency** - Backend, frontend, database, cloud, DevOps

### **Technical Excellence Demonstrated:**
- 🏆 **Cloud deployment mastery** - Railway platform expertise
- 🛡️ **Security implementation** - Multi-layer protection strategy
- 🔧 **Critical problem resolution** - MySQL permissions & environment conflicts
- 📧 **System integration** - Email automation and order workflows
- 🇸🇪 **Internationalization** - Swedish localization implementation
- 📊 **DevOps practices** - CI/CD with automated testing and coverage

### **Business Value Delivery:**
- 💰 **Complete e-commerce solution** - Ready for production use
- 🎯 **Professional user experience** - Admin and customer interfaces
- 📈 **Scalable architecture** - Enterprise patterns for growth
- 🔐 **Security compliance** - Multi-layer protection implementation

---

## 🛠️ **Technical Deep Dive**

### **Spring Boot Configuration Excellence**
```java
// Multi-environment configuration
@Profile("railway")
@Configuration
public class RailwayConfig {
    // Cloud-optimized settings
}

// Security configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    // Multi-layer security setup
}
```

### **Database Architecture**
- **Entities:** User, Product, Order, OrderItem
- **Repositories:** Spring Data JPA with custom queries
- **DTOs:** Clean data transfer between layers
- **Services:** Business logic encapsulation

### **Testing Strategy**
- **Unit Tests:** All service methods
- **Integration Tests:** Complete workflows
- **Security Tests:** Authentication & authorization
- **Coverage Reports:** JaCoCo with GitHub Pages

---

## 🤝 **Connect & Collaborate**

**Interested in discussing this project, the technical challenges overcome, or exploring collaboration opportunities?**

- 💼 **LinkedIn:** [Fredrik Arvidsson](https://www.linkedin.com/in/fredrik-arvidsson-57587b11a)
- 📧 **Email:** fredrik.g.arvidsson@gmail.com
- 🌐 **Live Demo:** [ctrlbuy-webshop-production.up.railway.app](https://ctrlbuy-webshop-production.up.railway.app)
- 📊 **Coverage Dashboard:** [JaCoCo Reports](https://fredrik-arvidsson.github.io/ctrlbuy-webshop/)
- 💻 **Source Code:** [GitHub Repository](https://github.com/fredrik-arvidsson/ctrlbuy-webshop)

---

## 📜 **License**

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

**🚀 Built with passion in Sweden | Deployed with Railway | Secured with Spring Security | Tested with JaCoCo**

*Showcasing enterprise Spring Boot development with production cloud deployment and real-time quality metrics*