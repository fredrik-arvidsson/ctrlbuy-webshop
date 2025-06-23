# 🛒 CtrlBuy Webshop 🚀 - Professional E-commerce Platform

<div align="center">
<img src="docs/images/ctrlbuy-logo.png" alt="CtrlBuy Logo" width="300"/>

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/)
[![Maven](https://img.shields.io/badge/Maven-3.9.5-blue.svg)](https://maven.apache.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)
[![Docker](https://img.shields.io/badge/Docker-Ready-2496ED.svg)](https://www.docker.com/)

<!-- LIVE DEMO BADGES -->
[![Railway Deployment](https://img.shields.io/badge/🚄_Railway-LIVE-brightgreen?style=flat-square)](https://ctrlbuy-webshop-production.up.railway.app)
[![AWS Deployment](https://img.shields.io/badge/☁️_AWS-LIVE-orange?style=flat-square)](http://webshop-final.eba-yzy7qfze.eu-north-1.elasticbeanstalk.com)
[![Coverage Reports](https://img.shields.io/badge/📊_Coverage-LIVE-blue?style=flat-square)](https://fredrik-arvidsson.github.io/ctrlbuy-webshop)

<!-- LIVE BADGES från din GitHub Pages! -->
[![Coverage](https://fredrik-arvidsson.github.io/ctrlbuy-webshop/badges/jacoco.svg)](https://fredrik-arvidsson.github.io/ctrlbuy-webshop/coverage/)
[![Branches](https://fredrik-arvidsson.github.io/ctrlbuy-webshop/badges/branches.svg)](https://fredrik-arvidsson.github.io/ctrlbuy-webshop/coverage/)
[![Build Status](https://github.com/fredrik-arvidsson/ctrlbuy-webshop/workflows/CI/badge.svg)](https://github.com/fredrik-arvidsson/ctrlbuy-webshop/actions)

<!-- Specifika service badges - länkar till live rapporter -->
[![Service Coverage](https://img.shields.io/badge/Service_Layer-Coverage-green)](https://fredrik-arvidsson.github.io/ctrlbuy-webshop/coverage/com.ctrlbuy.webshop.service/)
[![Controller Coverage](https://img.shields.io/badge/Controller_Layer-Coverage-green)](https://fredrik-arvidsson.github.io/ctrlbuy-webshop/coverage/com.ctrlbuy.webshop.controller/)
[![Security Coverage](https://img.shields.io/badge/Security_Layer-Coverage-green)](https://fredrik-arvidsson.github.io/ctrlbuy-webshop/coverage/com.ctrlbuy.webshop.security/)
</div>

En modern, skalbar och fullständigt testad e-handelsplattform byggd med Spring Boot. Professionell kvalitet med automatiserade tester och live coverage-rapporter.

## 🌐 LIVE DEMO - Testa Nu!

<div align="center">

### 🎯 VÄLJ DIN PLATTFORM

### 🚄 Railway Demo (Snabbast startup)
[![Railway Demo](https://img.shields.io/badge/🚄_Railway-LIVE_DEMO-brightgreen?style=for-the-badge&logo=railway)](https://ctrlbuy-webshop-production.up.railway.app)

### ☁️ AWS Demo (Enterprise-ready)
[![AWS Demo](https://img.shields.io/badge/☁️_AWS-LIVE_DEMO-orange?style=for-the-badge&logo=amazonaws)](http://webshop-final.eba-yzy7qfze.eu-north-1.elasticbeanstalk.com)

**✨ Live Features Du Kan Testa:**
* 🛒 Fullständig e-handelsfunktionalitet med varukorg
* 📱 58+ produkter (iPhone, Samsung, MacBook, etc.)
* 🇸🇪 Svensk gränssnitt med lokaliserade routes
* 👤 Användarregistrering och inloggning
* 💳 Komplett checkout-process
* 📱 Responsiv design - fungerar på mobil/tablet/desktop

🔑 **Test-användare för live demo:**
* Admin: `backup.admin` / `AdminPass123!`
* User: `test.user` / `TestPass123!`

</div>

## 🚀 Multi-Platform Deployment

| Platform | Status | URL | Features |
|----------|--------|-----|----------|
| 🚄 Railway | ✅ LIVE | [ctrlbuy-webshop-production.up.railway.app](https://ctrlbuy-webshop-production.up.railway.app) | Full MySQL, Auto-deploy |
| ☁️ AWS | ✅ LIVE | [webshop-final.eba-yzy7qfze.eu-north-1.elasticbeanstalk.com](http://webshop-final.eba-yzy7qfze.eu-north-1.elasticbeanstalk.com) | Elastic Beanstalk + Docker |
| 📊 GitHub Pages | ✅ LIVE | [fredrik-arvidsson.github.io/ctrlbuy-webshop](https://fredrik-arvidsson.github.io/ctrlbuy-webshop) | Coverage Reports |
| 🐳 Docker | ✅ Ready | `docker run -p 8080:8080 ctrlbuy/webshop` | Container ready |

## 🌍 Multi-Cloud Architecture

### 🚄 Railway Deployment (Development & Staging)
- **URL**: https://ctrlbuy-webshop-production.up.railway.app
- **Platform**: Railway Cloud
- **Database**: MySQL (Railway managed)
- **Deployment**: Git-based auto-deploy
- **SSL**: Automatic HTTPS
- **Best for**: Rapid development och prototyping

### ☁️ AWS Deployment (Production)
- **URL**: http://webshop-final.eba-yzy7qfze.eu-north-1.elasticbeanstalk.com
- **Platform**: Elastic Beanstalk (Docker)
- **Region**: Stockholm (eu-north-1)
- **Scaling**: Auto-scaling enabled
- **Load Balancer**: Application Load Balancer
- **Best for**: Production och enterprise use

### 📊 Development Transparency
- **Coverage Reports**: https://fredrik-arvidsson.github.io/ctrlbuy-webshop
- **CI/CD Pipeline**: GitHub Actions automatisk testing
- **Code Quality**: Live JaCoCo coverage tracking

## ⚡ Quick Start (8 sekunder)

Vill du bara testa applikationen snabbt? Här är det snabbaste sättet:

```bash
# 1. Klona och gå in i projektet
git clone https://github.com/fredrik-arvidsson/ctrlbuy-webshop.git
cd ctrlbuy-webshop

# 2. Starta applikationen (använder H2 in-memory databas)
./mvnw clean spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

🎉 **Klart!** Applikationen kör nu på: http://localhost:8080 (startar på ~8 sekunder)

## 🔍 Verify Everything Works

1. **Main Application:**  
   http://localhost:8080

2. **H2 Database Console (development):**  
   http://localhost:8080/h2-console
    * JDBC URL: `jdbc:h2:mem:ctrlbuydb`
    * Username: `sa`
    * Password: (leave empty)

3. **Test Login:**
    * Admin: `backup.admin` / `AdminPass123!`
    * Developer: `developer.admin` / `DevPass123!`
    * User: `test.user` / `TestPass123!`

## 🗄️ Database Configuration

Välj den databas som passar dig bäst:

### 🟢 Option 1: H2 Database (Rekommenderat för testing)
✅ Inget setup krävs - fungerar direkt!

```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

**Fördelar:**
* ✅ Inget installation
* ✅ Fungerar direkt
* ✅ Perfekt för demo och utveckling
* ✅ Inkluderar test-data

### 🔵 Option 2: MySQL med Docker (Enklast för produktion-liknande setup)
✅ Rekommenderat om du vill testa med riktig databas

```bash
# 1. Starta MySQL container
docker run --name ctrlbuy-mysql \
  -e MYSQL_ROOT_PASSWORD=password123 \
  -e MYSQL_DATABASE=ctrlbuy_webshop \
  -p 3306:3306 -d mysql:8.0

# 2. Vänta 30 sekunder för MySQL att starta, sedan:
./mvnw spring-boot:run -Dspring.profiles.active=prod \
  -Dspring.datasource.password=password123
```

Stoppa MySQL senare:
```bash
docker stop ctrlbuy-mysql
docker rm ctrlbuy-mysql
```

### 🟠 Option 3: Lokal MySQL Installation
För de som vill ha permanent MySQL setup

**macOS (med Homebrew):**
```bash
# 1. Installera MySQL
brew install mysql
brew services start mysql

# 2. Skapa databas
mysql -u root -p
CREATE DATABASE ctrlbuy_webshop;
EXIT;

# 3. Starta applikationen
./mvnw spring-boot:run -Dspring.profiles.active=prod
```

**Ubuntu/Debian:**
```bash
# 1. Installera MySQL
sudo apt update
sudo apt install mysql-server
sudo systemctl start mysql

# 2. Säkra installationen
sudo mysql_secure_installation

# 3. Skapa databas
sudo mysql -u root -p
CREATE DATABASE ctrlbuy_webshop;
EXIT;

# 4. Starta applikationen
./mvnw spring-boot:run -Dspring.profiles.active=prod
```

**Windows:**
1. Ladda ner MySQL från https://dev.mysql.com/downloads/mysql/
2. Installera och följ setup-wizarden
3. Skapa databas `ctrlbuy_webshop`
4. Kör: `mvnw.cmd spring-boot:run -Dspring.profiles.active=prod`

## 🚀 Test & Development

### Kör alla tester
```bash
./mvnw clean test
```

### Generera coverage rapport
```bash
./mvnw clean test jacoco:report
open target/site/jacoco/index.html  # macOS
xdg-open target/site/jacoco/index.html  # Linux
```

### Olika utvecklingsmiljöer
```bash
# Development med H2
./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"

# Production med MySQL
./mvnw spring-boot:run -Dspring.profiles.active=prod

# Test miljö
./mvnw spring-boot:run -Dspring.profiles.active=test
```

## 🏆 Live Testing Dashboard

🔴 **LIVE COVERAGE RAPPORTER** - Uppdateras automatiskt vid varje commit:

<div align="center">

[![Coverage Dashboard](https://img.shields.io/badge/📊_LIVE_COVERAGE-DASHBOARD-blue?style=for-the-badge)](https://fredrik-arvidsson.github.io/ctrlbuy-webshop)

</div>

### 📈 Realtime Coverage Status

| Service | Live Coverage | Live Report Link |
|---------|---------------|------------------|
| 🛒 All Services | 📊 Live | [📊 Detaljerad Rapport](https://fredrik-arvidsson.github.io/ctrlbuy-webshop/coverage/) |
| 💰 Payment Processing | 📊 Live | [Klicka på com.ctrlbuy.webshop.service](https://fredrik-arvidsson.github.io/ctrlbuy-webshop/coverage/com.ctrlbuy.webshop.service/) |
| 📦 Order Management | 📊 Live | [Klicka på com.ctrlbuy.webshop.controller](https://fredrik-arvidsson.github.io/ctrlbuy-webshop/coverage/com.ctrlbuy.webshop.controller/) |
| 🔐 Security Layer | 📊 Live | [Klicka på com.ctrlbuy.webshop.security](https://fredrik-arvidsson.github.io/ctrlbuy-webshop/coverage/com.ctrlbuy.webshop.security/) |

## 🎯 Development Status & Roadmap

🔄 **Active Development Project** - Detta är en fullt fungerande e-handelsplattform med ambitiös utvecklingsplan:

### ✅ Nuvarande Achievements (Production Ready)
* 🛒 **Fullständig e-handelsfunktionalitet** - Komplett webshop som fungerar
* 📦 **58+ produkter** med kategorisering och riktiga produktbilder
* 💳 **Komplett beställningsprocess** med orderbekräftelse och email
* 🎨 **Responsiv design** med Bootstrap och svensk lokalisering
* 📋 **Spring Boot MVC arkitektur** med professionella best practices
* 🗄️ **MySQL-integration** med JPA/Hibernate
* 🇸🇪 **Svensk e-handelsupplevelse** med lokaliserade routes
* 📊 **Live CI/CD Pipeline** med automatisk testing och deployment
* 🚄 **Railway Production Deployment** - Live demo tillgänglig 24/7
* ☁️ **AWS Production Deployment** - Elastic Beanstalk med Docker

### 🧪 Live Testing Excellence
* ✅ **Automatisk testning** vid varje commit
* ✅ **Live coverage rapporter** som uppdateras kontinuerligt
* ✅ **GitHub Pages integration** för transparent utveckling
* ✅ **JaCoCo detailed reporting** med klickbara rapporter
* ✅ **CI/CD pipeline** med Maven och GitHub Actions

### 🔄 Upcoming: Enhanced Multi-Cloud
* 🔄 **GitHub Actions → Multi-cloud pipeline**
* 🔒 **HTTPS för AWS** via Certificate Manager
* 📊 **CloudWatch monitoring** och alerting
* 🌍 **CDN integration** för global performance

## 🛠️ Troubleshooting

### Problem: MySQL Connection Error
```
Access denied for user 'root'@'localhost'
```
**Lösning:** Använd H2 istället:
```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

### Problem: Port redan används
```
Port 8080 was already in use
```
**Lösning:** Ändra port:
```bash
./mvnw spring-boot:run -Dserver.port=8081 -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

### Problem: Java version
```
Unsupported class file major version
```
**Lösning:** Se till att du har Java 21:
```bash
java -version  # Ska visa version 21
```

## 📊 Live Coverage Integration

### 🔴 Realtime Coverage Monitoring
Alla coverage-siffror uppdateras automatiskt:

```bash
# GitHub Actions genererar automatiskt:
📊 JaCoCo HTML-rapport → GitHub Pages
🏷️ Coverage badges → README
📈 Branch coverage → Live dashboard
🔄 Kontinuerlig uppdatering → Vid varje push
```

### 📋 Åtkomst till Live Rapporter
1. 🏠 **Huvuddashboard**: https://fredrik-arvidsson.github.io/ctrlbuy-webshop/
2. 📊 **Coverage Detaljer**: https://fredrik-arvidsson.github.io/ctrlbuy-webshop/coverage/
3. 🔧 **GitHub Actions**: [CI/CD Pipeline Status](https://github.com/fredrik-arvidsson/ctrlbuy-webshop/actions)

## 🔗 Live Links (Alltid Uppdaterade)

* 🛒 **LIVE WEBSHOP (Railway)**: https://ctrlbuy-webshop-production.up.railway.app
* 🛒 **LIVE WEBSHOP (AWS)**: http://webshop-final.eba-yzy7qfze.eu-north-1.elasticbeanstalk.com
* 🏠 **Live Coverage Dashboard**: https://fredrik-arvidsson.github.io/ctrlbuy-webshop/
* 📊 **Detailed Coverage Report**: https://fredrik-arvidsson.github.io/ctrlbuy-webshop/coverage/
* ⚙️ **GitHub Actions**: https://github.com/fredrik-arvidsson/ctrlbuy-webshop/actions
* 📁 **Repository**: https://github.com/fredrik-arvidsson/ctrlbuy-webshop

## 🏆 Live Development Transparency

<div align="center">

🎯 **TRANSPARENT DEVELOPMENT WITH LIVE METRICS**  
**Real Coverage | Real Tests | Real Progress | Real Time**

[![Live Transparency](https://img.shields.io/badge/🔴_LIVE-DEVELOPMENT_METRICS-red?style=for-the-badge)](https://fredrik-arvidsson.github.io/ctrlbuy-webshop)

Se utvecklingen i realtid med live coverage-rapporter och transparent CI/CD pipeline!

</div>

## 🌍 Deployment Options

### 🚄 Railway (Current Production)
```bash
# Already deployed and running!
# Visit: https://ctrlbuy-webshop-production.up.railway.app
```

### ☁️ AWS Deployment (Current Production)
```bash
# Already deployed and running!
# Visit: http://webshop-final.eba-yzy7qfze.eu-north-1.elasticbeanstalk.com
```

### 🐳 Docker Deployment
```bash
# Build and run locally
docker build -t ctrlbuy-webshop .
docker run -p 8080:8080 ctrlbuy-webshop
```

## 🎯 LinkedIn Ready

Detta projekt är redo för LinkedIn-delning med:

* ✅ **Live demo som faktiskt fungerar** - Två olika cloud platforms
* ✅ **Professional README** med badges och metrics
* ✅ **Live coverage rapporter** som visar kodkvalitet
* ✅ **Multi-platform deployment** (Railway + AWS)
* ✅ **Real e-commerce functionality** - inte bara en hello world
* ✅ **Enterprise-ready architecture** med Docker och auto-scaling

**Dela med stolthet!** 🚀

---

<div align="center">

**🔗 Quick Links**  
[🚄 Railway Demo](https://ctrlbuy-webshop-production.up.railway.app) |
[☁️ AWS Demo](http://webshop-final.eba-yzy7qfze.eu-north-1.elasticbeanstalk.com) |
[📊 Coverage Reports](https://fredrik-arvidsson.github.io/ctrlbuy-webshop) |
[📁 GitHub Repo](https://github.com/fredrik-arvidsson/ctrlbuy-webshop)

**Made with ❤️ in Sweden**

</div>