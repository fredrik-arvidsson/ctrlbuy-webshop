# 🛒 CtrlBuy Webshop - Professional E-commerce Platform

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/)
[![Maven](https://img.shields.io/badge/Maven-3.9.5-blue.svg)](https://maven.apache.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)
[![Docker](https://img.shields.io/badge/Docker-Ready-2496ED.svg)](https://www.docker.com/)
[![Tests](https://img.shields.io/badge/Tests-352%20Passing-brightgreen.svg)](https://github.com/fredrik-arvidsson/ctrlbuy-webshop)

📊 **LIVE COVERAGE:** [Test Reports & Coverage](https://fredrik-arvidsson.github.io/ctrlbuy-webshop)

> En modern, skalbar och fullständigt testad e-handelsplattform byggd med Spring Boot. Professionell kvalitet med 352 automatiserade tester, fullständig admin-panel och live coverage-rapporter.

## 🌍 Live Applications

### ✅ Available Now
- 📊 **Coverage Dashboard:** https://fredrik-arvidsson.github.io/ctrlbuy-webshop
- 🐳 **Local Development:** `docker-compose up` (full e-commerce experience)
- 🎛️ **Local Admin Panel:** http://localhost:8080/admin/dashboard

### 🔄 Deployment Status
- 🚄 **Railway Demo** - Under development
- ☁️ **AWS Demo** - Deployment in progress
- 💯 **Full functionality available locally** via Docker setup

## 🎯 Project Highlights

- ✅ **352 Tests Passing** - 100% green build med omfattande testning
- 🛒 **Complete E-commerce** - Fullständig webshop med varukorg och checkout
- 🎛️ **Professional Admin Panel** - CRUD för produkter, användare, order
- 📊 **Live Coverage Reports** - Automatisk testrapportering via GitHub Pages
- 🐳 **Docker Ready** - Komplett containeriserad utvecklingsmiljö
- 🇸🇪 **Swedish Localization** - Lokaliserat gränssnitt och routes

## ⚡ Quick Start (30 sekunder)

```bash
# 1. Klona projektet
git clone https://github.com/fredrik-arvidsson/ctrlbuy-webshop.git
cd ctrlbuy-webshop

# 2. Starta med Docker Compose (rekommenderat)
docker-compose up -d

# 3. Starta applikationen
./mvnw spring-boot:run

# 4. Öppna i webbläsare
open http://localhost:8080
```

🎉 **Klart!** Hela stacken (MySQL + MailHog + Webshop) körs nu lokalt.

## 🔑 Test-användare

- **Admin:** `backup.admin` / `AdminPass123!`
- **User:** `test.user` / `TestPass123!`

## 🎛️ Avancerad Admin-Panel

Komplett administrationsgränssnitt med professionell CRUD-funktionalitet:

### 📦 Produkthantering
- ✅ **Fullständig produktadministration** - Lägg till, redigera, ta bort produkter
- ✅ **Kategorisystem** med organiserade produktkategorier
- ✅ **REA-hantering** med datumintervall
- ✅ **Lagerhantering** och lågt lager-varningar
- ✅ **Produktbilder** med Unsplash-integration

### 👥 Användarhantering
- ✅ **Komplett användaradministration** - Hantera alla användarkonton
- ✅ **Rollbaserade behörigheter** - Admin, Moderator, User
- ✅ **Email-verifiering** med automatisk MailHog-integration
- ✅ **Kontostatus** - Aktivera/inaktivera användare

### 📊 Avancerade Rapporter
- ✅ **Försäljningsrapporter** med detaljerad analys
- ✅ **Produktrapporter** per kategori och prestanda
- ✅ **Användarrapporter** med registreringar och aktivitet
- ✅ **REA-analys** för kampanjframgång

## 🧪 Live Testing & Coverage

🔴 **LIVE COVERAGE RAPPORTER** - Uppdateras automatiskt vid varje commit:

**📊 Coverage Dashboard:** https://fredrik-arvidsson.github.io/ctrlbuy-webshop

### Test Results (Senaste körning):
- **352 tester** genomförda ✅
- **0 Failures** ❌
- **0 Errors** ❌
- **3 Skipped** ⏭️ (normalt)
- **BUILD SUCCESS** ✅

### Testsuiter som passerar:
- ✅ **CustomErrorControllerTest** (8 tester)
- ✅ **ProductControllerTest** (14 tester)
- ✅ **UserServiceTest** (30 tester)
- ✅ **ProductServiceTest** (41 tester)
- ✅ **OrderServiceTest** (32 tester)
- ✅ **PaymentServiceTest** (50 tester)
- ✅ **CartControllerTest** (10 tester)
- ✅ **CheckoutControllerTest** (8 tester)

## 🏗️ Development Stack

| Technology | Version | Purpose |
|------------|---------|---------|
| **Spring Boot** | 3.3.5 | Backend Framework |
| **Java** | 21 | Programming Language |
| **MySQL** | 8.0 | Database |
| **Maven** | 3.9.5 | Build Tool |
| **Docker** | Latest | Containerization |
| **JaCoCo** | 0.8.12 | Coverage Analysis |
| **JUnit** | 5.x | Testing Framework |
| **Bootstrap** | 5.3 | Frontend Framework |

## 🗄️ Database Configuration

### 🚀 Docker Compose (Rekommenderat)
```bash
# Starta hela stacken
docker-compose up -d

# Verifiera services
docker-compose ps

# Visa logs
docker-compose logs -f webshop-app
```

**Fördelar:**
- ✅ Enhetlig miljö för alla utvecklare
- ✅ Isolerad databas som inte krockar
- ✅ MailHog inkluderat för email-testning
- ✅ Inga konfigurationsproblem

### 🔧 Manuell MySQL (Alternativ)
```bash
# macOS
brew install mysql
brew services start mysql

# Ubuntu/Debian
sudo apt install mysql-server
sudo systemctl start mysql

# Skapa databas
mysql -u root -p
CREATE DATABASE ctrlbuy_webshop;
```

## 🔍 Verify Everything Works

1. **Main Application:** http://localhost:8080
2. **Admin Dashboard:** http://localhost:8080/admin/dashboard
3. **MailHog Email Testing:** http://localhost:8025
4. **MySQL Database:** Port 3306 (via Docker)

## 🧪 Development & Testing

```bash
# Kör alla tester
./mvnw clean test

# Generera coverage rapport
./mvnw clean test jacoco:report
open target/site/jacoco/index.html

# Olika miljöer
./mvnw spring-boot:run -Dspring.profiles.active=dev    # Development
./mvnw spring-boot:run -Dspring.profiles.active=prod   # Production
./mvnw spring-boot:run -Dspring.profiles.active=test   # Testing
```

## 🛠️ Troubleshooting

### MySQL Connection Error
```
Access denied for user 'root'@'localhost'
```
**Lösning:** Använd Docker Compose:
```bash
docker-compose down
docker-compose up -d
```

### Port redan används
```
Port 8080 was already in use
```
**Lösning:**
```bash
./mvnw spring-boot:run -Dserver.port=8081
```

### Admin-panel laddar inte
**Lösning:** Kontrollera att applikationen startat helt:
```bash
docker-compose logs -f webshop-app
```

## 🎯 Project Features & Status

### ✅ Current Features (Production Ready)
- 🛒 **Fullständig e-handelsfunktionalitet** - Komplett webshop
- 🎛️ **Professionell Admin-panel** - CRUD för produkter, användare, order
- 📦 **58+ produkter** med kategorisering
- 💳 **Komplett beställningsprocess** med email-bekräftelse
- 📧 **Email-system** med MailHog
- 📊 **Avancerade rapporter** - Försäljning, produkter, användare
- 🎨 **Responsiv design** med Bootstrap
- 🇸🇪 **Svensk lokalisering**
- 🧪 **352 automatiserade tester** med live coverage
- 🐳 **Docker containerization**

### 🔄 Future Enhancements
- 🔄 **Payment Integration** - Stripe/Klarna
- 🔄 **Advanced Search** - Elasticsearch
- 🔄 **Email Templates** - Professionella HTML-templates
- 🔄 **Analytics Dashboard** - Google Analytics integration
- 🔄 **Cloud Deployment** - AWS/Railway production

## 📊 Code Quality & Metrics

- **Test Coverage:** Live reports på GitHub Pages
- **Code Quality:** Spring Boot best practices
- **Security:** CSRF protection, SQL injection prevention
- **Performance:** Optimized queries och caching
- **Documentation:** Comprehensive README och comments

## 🌍 Links & Resources

- 📁 **GitHub Repository:** https://github.com/fredrik-arvidsson/ctrlbuy-webshop
- 📊 **Coverage Dashboard:** https://fredrik-arvidsson.github.io/ctrlbuy-webshop
- 🎛️ **Local Admin Panel:** http://localhost:8080/admin/dashboard
- 📧 **Local MailHog:** http://localhost:8025

## 💻 Local Demo Experience

**Start the complete e-commerce platform locally:**
```bash
docker-compose up -d
./mvnw spring-boot:run
```

**Then explore:**
- 🛒 **Webshop:** http://localhost:8080
- 🎛️ **Admin Panel:** http://localhost:8080/admin/dashboard
- 📧 **Email Testing:** http://localhost:8025
- 🗄️ **Database:** MySQL on port 3306

## 📄 Licens

**CtrlBuy Webshop** är ett proprietärt projekt utvecklat av Fredrik Arvidsson.

### Användning
- ✅ **Tillåtet:** Personligt lärande, utbildning, portfolioreferenser
- ❌ **Ej tillåtet:** Kommersiell användning utan skriftligt tillstånd

### Attribution
Vid delning inkludera: "Ursprungligen skapat av Fredrik Arvidsson – https://github.com/fredrik-arvidsson/ctrlbuy-webshop"

### Kommersiell användning
För kommersiella licenser: fredrik.arvidsson.dev@gmail.com

---

**🔗 Quick Links:**  
[📁 Repository](https://github.com/fredrik-arvidsson/ctrlbuy-webshop) |
[📊 Coverage](https://fredrik-arvidsson.github.io/ctrlbuy-webshop) |
[🐳 Local Setup](#quick-start-30-sekunder) |
[📧 MailHog](http://localhost:8025)

**Made with ❤️ in Sweden**