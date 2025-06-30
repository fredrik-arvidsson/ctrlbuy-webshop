# 🛒 CtrlBuy Webshop - Professional E-commerce Platform

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/)
[![Maven](https://img.shields.io/badge/Maven-3.9.5-blue.svg)](https://maven.apache.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)
[![Docker](https://img.shields.io/badge/Docker-Ready-2496ED.svg)](https://www.docker.com/)

🔴 **LIVE DEMO:** [Railway Production](https://ctrlbuy-webshop-production.up.railway.app) | [AWS Production](http://webshop-final.eba-yzy7qfze.eu-north-1.elasticbeanstalk.com) | [Coverage Reports](https://fredrik-arvidsson.github.io/ctrlbuy-webshop)

> En modern, skalbar och fullständigt testad e-handelsplattform byggd med Spring Boot. Professionell kvalitet med automatiserade tester, fullständig admin-panel och live coverage-rapporter.

## 🌐 Live Demo - Testa Nu!

### 🚄 Railway Demo (Primary Production)
**URL:** https://ctrlbuy-webshop-production.up.railway.app
- 🛒 Fullständig e-handelsfunktionalitet med varukorg och checkout
- 📱 58+ produkter (iPhone, Samsung, MacBook, Gaming, VR, Audio)
- 🎛️ Komplett Admin-panel med produkthantering
- 🇸🇪 Svensk gränssnitt med lokaliserade routes

### ☁️ AWS Demo (Production Ready)
**URL:** http://webshop-final.eba-yzy7qfze.eu-north-1.elasticbeanstalk.com
- ☁️ Elastic Beanstalk deployment med auto-scaling
- 🌍 Stockholm region (eu-north-1)
- 🔧 Production-ready konfiguration

### 🔑 Test-användare för live demo:
- **Admin:** `backup.admin` / `AdminPass123!`
- **User:** `test.user` / `TestPass123!`

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

## 🚀 Multi-Platform Deployment

| Platform | Status | URL | Features |
|----------|--------|-----|----------|
| 🚄 Railway | ✅ LIVE | [ctrlbuy-webshop-production.up.railway.app](https://ctrlbuy-webshop-production.up.railway.app) | MySQL, Auto-deploy, Admin Panel |
| ☁️ AWS | ✅ LIVE | [webshop-final.eba-yzy7qfze.eu-north-1.elasticbeanstalk.com](http://webshop-final.eba-yzy7qfze.eu-north-1.elasticbeanstalk.com) | Elastic Beanstalk + Docker |
| 📊 GitHub Pages | ✅ LIVE | [fredrik-arvidsson.github.io/ctrlbuy-webshop](https://fredrik-arvidsson.github.io/ctrlbuy-webshop) | Coverage Reports |
| 🐳 Docker | ✅ Ready | `docker-compose up` | Complete stack |

## 🧪 Live Testing & Coverage

🔴 **LIVE COVERAGE RAPPORTER** - Uppdateras automatiskt vid varje commit:

**Coverage Dashboard:** https://fredrik-arvidsson.github.io/ctrlbuy-webshop

### Test Results (Senaste körning):
- **352 tester** genomförda
- **0 Failures** ❌
- **0 Errors** ❌
- **3 Skipped** ⏭️ (normalt)
- **BUILD SUCCESS** ✅

### Testsuiter som passerar:
- ✅ CustomErrorControllerTest (8 tester)
- ✅ ProductControllerTest (14 tester)
- ✅ UserServiceTest (30 tester)
- ✅ ProductServiceTest (41 tester)
- ✅ OrderServiceTest (32 tester)
- ✅ PaymentServiceTest (50 tester)

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

## 🎯 Development Status & Roadmap

### ✅ Current Features (Production Ready)
- 🛒 **Fullständig e-handelsfunktionalitet** - Komplett webshop
- 🎛️ **Professionell Admin-panel** - CRUD för produkter, användare, order
- 📦 **58+ produkter** med kategorisering
- 💳 **Komplett beställningsprocess** med email-bekräftelse
- 📧 **Email-system** med MailHog
- 📊 **Avancerade rapporter** - Försäljning, produkter, användare
- 🎨 **Responsiv design** med Bootstrap
- 🇸🇪 **Svensk lokalisering**
- 🚄 **Multi-platform deployment** (Railway + AWS)

### 🔄 Upcoming Enhancements
- 🔄 **Payment Integration** - Stripe/Klarna
- 🔄 **Advanced Search** - Elasticsearch
- 🔄 **Email Templates** - Professionella HTML-templates
- 🔄 **Analytics Dashboard** - Google Analytics integration
- 🔄 **Kubernetes** - Container orchestration

## 🌍 Live Links

- 🛒 **Railway Production:** https://ctrlbuy-webshop-production.up.railway.app
- ☁️ **AWS Production:** http://webshop-final.eba-yzy7qfze.eu-north-1.elasticbeanstalk.com
- 🎛️ **Admin Panel:** `/admin/dashboard` (använd test-användare ovan)
- 📊 **Coverage Dashboard:** https://fredrik-arvidsson.github.io/ctrlbuy-webshop
- 📁 **GitHub Repository:** https://github.com/fredrik-arvidsson/ctrlbuy-webshop

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
[🚄 Railway](https://ctrlbuy-webshop-production.up.railway.app) |
[☁️ AWS](http://webshop-final.eba-yzy7qfze.eu-north-1.elasticbeanstalk.com) |
[🎛️ Admin](https://ctrlbuy-webshop-production.up.railway.app/admin/dashboard) |
[📊 Coverage](https://fredrik-arvidsson.github.io/ctrlbuy-webshop)

**Made with ❤️ in Sweden**