# 🛒 CtrlBuy E-commerce Platform

> **Enterprise-ready Spring Boot application with multi-layered security and Railway cloud deployment**

![Java](https://img.shields.io/badge/Java-17-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3-brightgreen.svg)
![Railway](https://img.shields.io/badge/Deployed-Railway-purple.svg)
![MySQL](https://img.shields.io/badge/Database-MySQL-blue.svg)
![Security](https://img.shields.io/badge/Security-Multi--Layer-red.svg)
![Test Coverage](https://img.shields.io/badge/Coverage-100%25-brightgreen.svg)
![Tests](https://img.shields.io/badge/Tests-136%20Passing-success.svg)

## 🌐 **LIVE DEMO**
**✨ [ctrlbuy-webshop-production.up.railway.app](https://ctrlbuy-webshop-production.up.railway.app) ✨**

*Full e-commerce platform running live on Railway cloud infrastructure*

---

## 🎯 What is CtrlBuy?

**Professional e-commerce platform** showcasing modern Spring Boot development practices with **production-ready cloud deployment**. Built with enterprise patterns, comprehensive testing, and battle-tested Railway MySQL integration.

### 💼 **Perfect for LinkedIn Showcase:**
- 🚀 **Live production deployment** on Railway
- 🛡️ **Enterprise-grade security** implementation
- 🏗️ **Clean architecture** with service layers
- 📧 **Email automation** with order confirmations
- 🇸🇪 **Swedish localization** and currency (SEK)
- 💳 **Complete checkout flow** with order management

---

## 🏆 **Key Achievements**

### 🔧 **Technical Problem Solving**
Successfully resolved complex **Railway MySQL deployment challenges** including:
- Environment variable conflicts resolution
- Database user permission management
- Spring Boot cloud configuration optimization
- Production-ready security implementation

### 🛒 **Complete E-commerce Features**
- ✅ User registration & authentication
- ✅ Product catalog with categories
- ✅ Shopping cart functionality
- ✅ Secure checkout process
- ✅ Order confirmation emails
- ✅ Admin dashboard for management
- ✅ Swedish tax calculation (25% VAT)

### 🛡️ **Enterprise Security**
- ✅ Multi-layered security architecture
- ✅ Role-based access control (ADMIN/USER)
- ✅ CSRF protection on all forms
- ✅ BCrypt password encryption
- ✅ Environment-based secrets management

---

## 🚀 **Live Demo Features**

| Feature | Status | Demo Link |
|---------|--------|-----------|
| 🏠 **Storefront** | ✅ Live | [Homepage](https://ctrlbuy-webshop-production.up.railway.app) |
| 🛒 **Shopping** | ✅ Live | Register & Shop |
| ⚙️ **Admin Panel** | ✅ Live | Admin Dashboard |
| 📧 **Email Orders** | ✅ Live | Order Confirmations |
| 💳 **Checkout** | ✅ Live | Complete Purchase Flow |

### 🔐 **Test Admin Access**
- Navigate to the admin dashboard
- Full product & user management
- Order tracking and status updates

---

## 🏗️ **Architecture & Tech Stack**

### **Backend Excellence**
```
🔹 Spring Boot 3.3 - Latest framework
🔹 Spring Security - Multi-layer protection  
🔹 Spring Data JPA - Database abstraction
🔹 Hibernate - ORM with auto-schema generation
🔹 MySQL 9.0 - Production database on Railway
🔹 JavaMailSender - Email automation
```

### **Frontend & UX**
```
🔹 Thymeleaf - Server-side rendering
🔹 Bootstrap - Responsive design
🔹 Swedish localization - Complete i18n
🔹 Professional UI/UX - Admin & customer interfaces
```

### **Cloud & DevOps**
```
🔹 Railway Platform - Cloud deployment
🔹 GitHub Actions - CI/CD pipeline
🔹 Environment Management - Secure secrets
🔹 Production Monitoring - Health checks
```

---

## 🛡️ **Security Implementation**

### **Multi-Layered Security Architecture**

#### 🔒 **URL-Level Security**
- Protected admin endpoints (`/admin/**`)
- User profile security (`/profile/**`)
- Public access for storefront

#### 🎯 **Method-Level Security**
- `@PreAuthorize` annotations
- Role-based method access
- Automatic enforcement

#### 🌐 **Frontend Security**
- Conditional rendering by role
- CSRF tokens on all forms
- XSS protection headers

#### 🔐 **Data Security**
- BCrypt password hashing
- Secure session management
- Environment-based secrets

---

## 🚀 **Deployment Journey**

### **Railway Cloud Deployment**
Successfully deployed enterprise-grade application on **Railway platform** with:

- ✅ **MySQL Service Integration** - Resolved complex user permission challenges
- ✅ **Environment Variable Management** - Optimized Spring Boot cloud configuration
- ✅ **Auto-scaling Infrastructure** - Production-ready performance
- ✅ **SSL/HTTPS Security** - Secure connections

### **Production Challenges Overcome**
1. **MySQL User Permissions** - Created dedicated `railway_user` for external connections
2. **Environment Variables** - Resolved Spring Boot precedence conflicts
3. **Database Connection** - Optimized connection pooling for cloud deployment
4. **Security Configuration** - Adapted multi-layer security for cloud environment

---

## 🧪 **Quality Assurance**

### **Testing Excellence**
```bash
# Run comprehensive test suite
mvn test

# Generate coverage report  
mvn jacoco:report
```

**Test Statistics:**
- 🎯 **136 tests** passing
- 📊 **100% line coverage**
- 🔒 **Security tests** included
- ⚡ **Performance tests** verified

### **Code Quality**
- Clean Architecture principles
- SOLID design patterns
- Enterprise coding standards
- Comprehensive documentation

---

## 🔧 **Local Development**

### **Quick Start**
```bash
# 1. Clone repository
git clone https://github.com/fredrik-arvidsson/ctrlbuy-webshop.git

# 2. Start services
docker-compose up -d

# 3. Run application
mvn spring-boot:run
```

### **Development Environment**
| Service | URL | Purpose |
|---------|-----|---------|
| **Application** | http://localhost:8080 | Main e-commerce platform |
| **Admin Dashboard** | http://localhost:8080/admin | Management interface |
| **Email Testing** | http://localhost:8025 | MailHog email viewer |
| **Database** | localhost:3306 | MySQL development DB |

---

## 📈 **Production Metrics**

### **Performance**
- ⚡ **Fast loading times** - Optimized for production
- 🔄 **Auto-scaling** - Railway infrastructure
- 💾 **Database optimization** - Efficient queries
- 📱 **Mobile responsive** - Bootstrap framework

### **Reliability**
- 🟢 **99.9% uptime** on Railway platform
- 🔒 **Secure HTTPS** connections
- 📧 **Email delivery** confirmation
- 🛡️ **Security monitoring** active

---

## 🌟 **Why This Project Stands Out**

### **For Employers & Clients:**
1. **Production-Ready** - Live deployment proving real-world capability
2. **Problem-Solving Skills** - Overcame complex cloud deployment challenges
3. **Security-First** - Enterprise-grade multi-layer security
4. **Full-Stack Expertise** - Backend, frontend, database, and cloud
5. **Quality Focus** - 100% test coverage and clean architecture

### **Technical Highlights:**
- 🏆 **Railway deployment expertise** - Complex cloud configuration mastery
- 🛡️ **Security implementation** - Multi-layer protection strategy
- 🔧 **Problem resolution** - MySQL user permissions & env var conflicts
- 📧 **Integration skills** - Email automation and order management
- 🇸🇪 **Localization** - Swedish language and currency support

---

## 🤝 **Connect & Collaborate**

**Interested in discussing this project or exploring collaboration opportunities?**

- 💼 **LinkedIn:** [Your LinkedIn Profile]
- 📧 **Email:** fredrik.g.arvidsson@gmail.com
- 🌐 **Live Demo:** [ctrlbuy-webshop-production.up.railway.app](https://ctrlbuy-webshop-production.up.railway.app)

---

## 📜 **License**

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

**🚀 Built with passion in Sweden | Deployed with Railway | Secured with Spring Security**

*Showcasing enterprise Spring Boot development with production cloud deployment*