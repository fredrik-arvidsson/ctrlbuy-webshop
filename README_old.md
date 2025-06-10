# 🛒 CtrlBuy Webshop - Professional E-commerce Platform

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)
[![Maven](https://img.shields.io/badge/Maven-3.9+-red.svg)](https://maven.apache.org/)
[![Tests](https://img.shields.io/badge/Tests-189%20passed-success.svg)](/)
[![Coverage](https://img.shields.io/badge/Controller%20Coverage-100%25-success.svg)](/)
[![Build Status](https://img.shields.io/badge/Build-SUCCESS-brightgreen.svg)](/)
[![AWS Ready](https://img.shields.io/badge/AWS-Ready-ff9900.svg)](https://aws.amazon.com/)

> **En modern, skalbar och fullständigt testad e-handelsplattform byggd med Spring Boot, designad för professionell deployment och produktion.**

## 📋 Innehållsförteckning

- [Översikt](#-översikt)
- [Teknisk Stack](#-teknisk-stack)
- [Arkitektur](#-arkitektur)
- [Funktioner](#-funktioner)
- [Installation](#-installation)
- [Konfiguration](#-konfiguration)
- [Testing](#-testing)
- [AWS Deployment](#-aws-deployment)
- [API Dokumentation](#-api-dokumentation)
- [Säkerhet](#-säkerhet)
- [Prestanda](#-prestanda)
- [Bidrag](#-bidrag)

## 🎯 Översikt

CtrlBuy Webshop är en professionell e-handelsplattform utvecklad med moderna Java-teknologier och bästa praxis inom mjukvaruutveckling. Plattformen erbjuder en komplett lösning för online-försäljning med robust säkerhet, skalbar arkitektur och omfattande testning.

### ✨ Nyckelframgångar
- **189 automatiserade tester** med 100% controller coverage
- **21 fullständigt testade controllers** med professionell error handling
- **Production-ready** kod med AWS-deployment support
- **Svenska språkstöd** med lokaliserade routes och innehåll
- **Modern säkerhetsimplementation** med Spring Security
- **Responsiv design** med Bootstrap och svenska UX-patterns

## 🛠 Teknisk Stack

### Backend Technologies
- **Spring Boot 3.3.5** - Modern Java framework för enterprise-applikationer
- **Spring Security 6** - Avancerad autentisering och auktorisering
- **Spring Data JPA** - ORM och databasabstraktion
- **MySQL 8.0** - Relationsdatabas för produktionsdata
- **Maven** - Dependency management och build automation
- **Java 21** - Senaste LTS-versionen med moderna språkfunktioner

### Frontend & UI
- **Thymeleaf** - Server-side template engine
- **Bootstrap 5** - Responsiv CSS framework
- **JavaScript ES6+** - Modern frontend-logik
- **Svenska UI/UX** - Lokaliserat användargrängsnitt

### Testing & Quality Assurance
- **JUnit 5** - Unit testing framework
- **Mockito** - Mocking framework för isolerade tester
- **Spring Boot Test** - Integration testing
- **189 tester totalt** - Omfattande testning av alla komponenter
- **100% controller coverage** - Fullständig validering av alla endpoints

### DevOps & Deployment
- **AWS EC2/Elastic Beanstalk** - Cloud deployment platform
- **AWS RDS** - Managed database service
- **Maven profiles** - Environment-specifik konfiguration
- **Production-ready** - Optimerad för skalning och prestanda

## 🏗 Arkitektur

### MVC Arkitektur
```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   Presentation  │    │    Business      │    │      Data       │
│     Layer       │────│     Layer        │────│     Layer       │
│ (Controllers)   │    │   (Services)     │    │ (Repositories)  │
└─────────────────┘    └──────────────────┘    └─────────────────┘
```

### Huvudkomponenter
- **Controllers (21st)** - REST endpoints och view rendering
- **Services** - Affärslogik och databearbetning
- **Repositories** - Dataåtkomst och persistering
- **Security** - Autentisering och auktorisering
- **Configuration** - Miljö- och säkerhetskonfiguration

### Databasschema
```sql
-- Huvudtabeller
Users (id, username, email, password, roles)
Products (id, name, description, price, category)
Orders (id, user_id, total, status, created_at)
Reviews (id, product_id, user_id, rating, comment)
Categories (id, name, description)
```

## 🚀 Funktioner

### 👤 Användarhantering
- **Registrering och inloggning** med email-verifiering
- **Lösenordsåterställning** via säker token-baserad process
- **Användarroller** (USER, ADMIN) med rollbaserad åtkomst
- **Profilhantering** med personliga inställningar

### 🛍 E-handel Funktionalitet
- **Produktkatalog** med kategorisering och sökning
- **Kundvagn** med realtidsuppdateringar
- **Checkout-process** med orderhantering
- **Recensionssystem** med betyg och kommentarer
- **Orderhistorik** med detaljerad spårning

### 🔐 Säkerhet
- **Spring Security integration** med anpassad konfiguration
- **CSRF-skydd** och XSS-prevention
- **Lösenordskryptering** med BCrypt
- **Session management** med automatisk timeout
- **SQL injection skydd** via JPA/Hibernate

### 🌐 Lokalisering
- **Svenska routes** (`/produkter`, `/kundvagn`, `/kassa`)
- **Svensk innehåll** och felmeddelanden
- **Lokaliserade email-templates**
- **Svenska datum- och valutaformat**

### 📊 Administration
- **Admin dashboard** med försäljningsstatistik
- **Produkthantering** (CRUD operationer)
- **Användaradministration** med rollhantering
- **Orderhantering** och statusuppdateringar

## 💾 Installation

### Förutsättningar
- Java 21 eller senare
- Maven 3.9+
- MySQL 8.0
- Git

### Lokal Development Setup

1. **Klona repositoryt**
```bash
git clone https://github.com/your-username/ctrlbuy-webshop.git
cd ctrlbuy-webshop
```

2. **Databasinställning**
```sql
-- Skapa databas
CREATE DATABASE webshop_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Skapa användare
CREATE USER 'webshop_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON webshop_db.* TO 'webshop_user'@'localhost';
FLUSH PRIVILEGES;
```

3. **Konfiguration**
```properties
# application-dev.properties
spring.datasource.url=jdbc:mysql://localhost:3306/webshop_db
spring.datasource.username=webshop_user
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=create-drop
```

4. **Bygg och kör**
```bash
# Installera dependencies
mvn clean install

# Kör alla tester
mvn test

# Starta applikationen
mvn spring-boot:run
```

5. **Åtkomst**
- Applikation: http://localhost:8080
- H2 Console: http://localhost:8080/h2-console (endast dev)

## ⚙️ Konfiguration

### Miljöprofiler

#### Development Profile
```properties
# application-dev.properties
spring.profiles.active=dev
spring.jpa.show-sql=true
logging.level.com.ctrlbuy=DEBUG
spring.h2.console.enabled=true
```

#### Production Profile
```properties
# application-prod.properties
spring.profiles.active=prod
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
logging.level.com.ctrlbuy=INFO
server.error.whitelabel.enabled=false
```

### Säkerhetskonfiguration
```yaml
# Security settings
security:
  jwt:
    secret: ${JWT_SECRET:defaultSecret}
    expiration: 86400000
  cors:
    allowed-origins: ${ALLOWED_ORIGINS:http://localhost:3000}
```

### Email Konfiguration
```properties
# Email settings för lösenordsåterställning
spring.mail.host=${MAIL_HOST:smtp.gmail.com}
spring.mail.port=${MAIL_PORT:587}
spring.mail.username=${MAIL_USERNAME}
spring.mail.password=${MAIL_PASSWORD}
```

## 🧪 Testing

### Test Framework
Projektet använder en omfattande teststrategi med **189 automatiserade tester**:

```bash
# Kör alla tester
mvn test

# Kör tester med coverage
mvn test jacoco:report

# Kör endast unit tests
mvn test -Dtest="**/*Test"

# Kör endast integration tests
mvn test -Dtest="**/*IT"
```

### Test Kategorier

#### Controller Tests (21 Controllers - 100% Coverage)
- **Unit tests** med MockMVC för alla endpoints
- **Error handling** validering
- **Authentication** och auktorisering
- **Request/Response** validering

```java
@ExtendWith(MockitoExtension.class)
class ProductControllerTest {
    
    @Test
    void getProduct_WhenValidId_ShouldReturnProduct() {
        // Comprehensive test implementation
    }
}
```

#### Service Tests
- **Business logic** validering
- **Error scenarios** hantering
- **Data transformation** tester

#### Repository Tests
- **Database operations** validering
- **Query performance** tester
- **Data integrity** kontroller

### Test Coverage Statistik
```
╔═══════════════════╦══════════╦═══════════╗
║ Component         ║ Tests    ║ Coverage  ║
╠═══════════════════╬══════════╬═══════════╣
║ Controllers       ║ 167      ║ 100%      ║
║ Services          ║ 18       ║ 95%       ║
║ Repositories      ║ 4        ║ 100%      ║
║ Total             ║ 189      ║ 98%       ║
╚═══════════════════╩══════════╩═══════════╝
```

## ☁️ AWS Deployment

### Förutsättningar för AWS
- AWS CLI konfigurerat
- IAM användare med nödvändiga behörigheter
- RDS MySQL instans
- EC2 eller Elastic Beanstalk environment

### Production Build
```bash
# Bygg production JAR
mvn clean package -Pprod

# Verifiera build
java -jar target/webshop-1.0-SNAPSHOT.jar --spring.profiles.active=prod
```

### AWS RDS Setup
```sql
-- Production databas setup
ENDPOINT: your-db-instance.region.rds.amazonaws.com
PORT: 3306
DATABASE: webshop_prod
```

### Environment Variables
```bash
# AWS Environment Configuration
export DB_URL=jdbc:mysql://your-rds-endpoint:3306/webshop_prod
export DB_USER=webshop_user
export DB_PASSWORD=secure_password
export JWT_SECRET=production_jwt_secret
export MAIL_USERNAME=your_email@domain.com
export MAIL_PASSWORD=app_password
```

### Elastic Beanstalk Deployment
```bash
# Installera EB CLI
pip install awsebcli

# Initiera EB application
eb init ctrlbuy-webshop

# Skapa environment
eb create production

# Deploy applikation
eb deploy
```

### EC2 Manual Deployment
```bash
# Installera Java 21 på EC2
sudo yum update -y
sudo yum install -y java-21-amazon-corretto

# Transfer JAR file
scp target/webshop-1.0-SNAPSHOT.jar ec2-user@your-ec2-ip:~/

# Kör applikation
nohup java -jar webshop-1.0-SNAPSHOT.jar \
  --spring.profiles.active=prod \
  --server.port=8080 > app.log 2>&1 &
```

### LoadBalancer & SSL Setup
```yaml
# Application Load Balancer configuration
listeners:
  - port: 443
    protocol: HTTPS
    ssl_certificate: arn:aws:acm:region:account:certificate/cert-id
  - port: 80
    protocol: HTTP
    redirect_to: 443

health_check:
  path: /actuator/health
  interval: 30
  timeout: 5
```

## 📡 API Dokumentation

### Public Endpoints
```http
GET    /                          # Hemsida
GET    /produkter                 # Produktlista
GET    /produkter/{id}           # Produktdetaljer
GET    /kategorier               # Kategoriöversikt
POST   /registrera               # Användarregistrering
POST   /logga-in                 # Användarinloggning
```

### Authenticated Endpoints
```http
GET    /min-profil               # Användarprofil
GET    /kundvagn                 # Kundvagn
POST   /kundvagn/lagg-till       # Lägg till i kundvagn
GET    /kassa                    # Checkout
POST   /bestall                  # Genomför beställning
GET    /mina-bestallningar       # Orderhistorik
```

### Admin Endpoints
```http
GET    /admin/dashboard          # Admin översikt
GET    /admin/produkter          # Produkthantering
POST   /admin/produkter          # Skapa produkt
PUT    /admin/produkter/{id}     # Uppdatera produkt
DELETE /admin/produkter/{id}     # Ta bort produkt
GET    /admin/anvandare          # Användarhantering
```

### API Response Format
```json
{
  "status": "success|error",
  "message": "Beskrivande meddelande",
  "data": {
    "id": 1,
    "name": "Produktnamn",
    "price": 299.99,
    "category": "Kategori"
  },
  "timestamp": "2025-06-08T10:13:28+02:00"
}
```

## 🔒 Säkerhet

### Authentication & Authorization
- **JWT Token** baserad autentisering
- **Role-based access control** (RBAC)
- **Password encryption** med BCrypt
- **Session timeout** efter inaktivitet

### Data Protection
- **SQL Injection** skydd via parametriserade queries
- **XSS Protection** genom content security policy
- **CSRF Protection** med tokens
- **Input validation** på alla endpoints

### Security Headers
```java
// Säkerhetskonfiguration
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        return http
            .headers(headers -> headers
                .frameOptions().deny()
                .contentTypeOptions().and()
                .httpStrictTransportSecurity(hstsConfig -> hstsConfig
                    .maxAgeInSeconds(31536000)
                    .includeSubdomains(true)))
            .build();
    }
}
```

### Audit Logging
```properties
# Säkerhetsloggning
logging.level.org.springframework.security=DEBUG
logging.level.com.ctrlbuy.webshop.security=INFO
```

## ⚡ Prestanda

### Optimization Features
- **Connection Pooling** med HikariCP
- **Query Optimization** med JPA/Hibernate
- **Caching Strategy** för statisk data
- **Lazy Loading** för relationer
- **Database Indexing** för snabba sökningar

### Performance Metrics
```properties
# Prestanda monitoring
management.endpoints.web.exposure.include=health,metrics,prometheus
management.endpoint.health.show-details=always
management.metrics.export.prometheus.enabled=true
```

### Database Optimization
```sql
-- Index optimization för bästa prestanda
CREATE INDEX idx_products_category ON products(category);
CREATE INDEX idx_products_name ON products(name);
CREATE INDEX idx_orders_user_id ON orders(user_id);
CREATE INDEX idx_orders_created_at ON orders(created_at);
CREATE INDEX idx_reviews_product_id ON reviews(product_id);
```

### Caching Configuration
```java
@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("products", "categories");
    }
}
```

## 📈 Monitoring & Observability

### Health Checks
```http
GET /actuator/health          # Applikationshälsa
GET /actuator/metrics         # Prestanda metrics
GET /actuator/info           # Applikationsinformation
```

### Logging Configuration
```yaml
logging:
  level:
    com.ctrlbuy.webshop: INFO
    org.springframework.security: WARN
    org.hibernate.SQL: WARN
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
  file:
    name: logs/webshop.log
```

## 🤝 Bidrag

### Development Guidelines
1. **Fork** projektet
2. **Skapa feature branch** (`git checkout -b feature/AmazingFeature`)
3. **Commit changes** (`git commit -m 'Add some AmazingFeature'`)
4. **Push to branch** (`git push origin feature/AmazingFeature`)
5. **Öppna Pull Request**

### Code Style
- **Java Code Conventions** enligt Oracle standards
- **Spring Boot Best Practices**
- **SonarQube** kvalitetskontroller
- **Javadoc** för alla publika metoder

### Testing Requirements
- **Alla nya features** ska ha motsvarande tester
- **Minimum 90% code coverage** för nya komponenter
- **Integration tests** för kritisk funktionalitet
- **Performance tests** för nya endpoints

## 📄 Licens

Detta projekt är licensierat under MIT License - se [LICENSE](LICENSE) filen för detaljer.

## 👨‍💻 Utvecklare

**Fredrik Zarkow**
- LinkedIn: [Fredrik Zarkow](https://www.linkedin.com/in/fredrik-zarkow)
- Email: fredrik@example.com
- GitHub: [@zarkow](https://github.com/zarkow)

## 🙏 Erkännanden

- **Spring Boot Team** för det fantastiska ramverket
- **Bootstrap Team** för responsive UI komponenter
- **MySQL Team** för robust databashantering
- **AWS** för cloud infrastructure
- **Open Source Community** för kontinuerligt stöd

---

## 📊 Project Statistics

```
📁 Total Files: 150+
📝 Lines of Code: 15,000+
🧪 Tests: 189 (100% passing)
📦 Controllers: 21 (100% tested)
🔒 Security: Enterprise-grade
☁️ Cloud: AWS Ready
🌐 Localization: Swedish
📱 Responsive: Mobile-first
```

---

**Byggt med ❤️ i Sverige | Production Ready | Enterprise Quality**