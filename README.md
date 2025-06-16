# 🛒 CtrlBuy Webshop 🚀 - Professional E-commerce Platform

<div align="center">
  <img src="docs/images/ctrlbuy-logo.png" alt="CtrlBuy Logo" width="300"/>

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)
[![Maven](https://img.shields.io/badge/Maven-3.9+-red.svg)](https://maven.apache.org/)

<!-- LIVE BADGES från din GitHub Pages! -->
[![Tests](https://img.shields.io/badge/Tests-164%20passed-success.svg)](https://fredrik-arvidsson.github.io/ctrlbuy-webshop/)
[![Live Coverage](https://img.shields.io/endpoint?url=https://fredrik-arvidsson.github.io/ctrlbuy-webshop/badges/jacoco.json)](https://fredrik-arvidsson.github.io/ctrlbuy-webshop/coverage/)
[![Branch Coverage](https://img.shields.io/endpoint?url=https://fredrik-arvidsson.github.io/ctrlbuy-webshop/badges/branches.json)](https://fredrik-arvidsson.github.io/ctrlbuy-webshop/coverage/)

<!-- Specifika service badges - länkar till live rapporter -->
[![Coverage Report](https://img.shields.io/badge/Coverage%20Report-Live-brightgreen.svg)](https://fredrik-arvidsson.github.io/ctrlbuy-webshop/coverage/)
[![Test Dashboard](https://img.shields.io/badge/Test%20Dashboard-Live-blue.svg)](https://fredrik-arvidsson.github.io/ctrlbuy-webshop/)

[![Build Status](https://github.com/fredrik-arvidsson/ctrlbuy-webshop/workflows/CtrlBuy%20CI%2FCD%20Pipeline/badge.svg)](https://github.com/fredrik-arvidsson/ctrlbuy-webshop/actions)
[![AWS Ready](https://img.shields.io/badge/AWS-Ready-ff9900.svg)](https://aws.amazon.com/)
[![Security](https://img.shields.io/badge/Security-Enterprise%20Grade-red.svg)](https://fredrik-arvidsson.github.io/ctrlbuy-webshop/coverage/)
[![Production](https://img.shields.io/badge/Production-Ready-success.svg)](#-aws-deployment)
</div>

> **En modern, skalbar och fullständigt testad e-handelsplattform byggd med Spring Boot. Professionell kvalitet med automatiserade tester och live coverage-rapporter.**

## ⚡ Quick Start (30 sekunder)

**Vill du bara testa applikationen snabbt? Här är det snabbaste sättet:**

```bash
# 1. Klona och gå in i projektet
git clone https://github.com/fredrik-arvidsson/ctrlbuy-webshop.git
cd ctrlbuy-webshop

# 2. Starta applikationen (använder H2 in-memory databas)
./mvnw spring-boot:run -Dspring.profiles.active=dev
```

**🎉 Klart!** Applikationen kör nu på: **http://localhost:8080**

**💡 H2 Databas Console:** http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:ctrlbuydb`
- Username: `sa`
- Password: *(lämna tomt)*

---

## 🗄️ Database Configuration

**Välj den databas som passar dig bäst:**

### 🟢 Option 1: H2 Database (Rekommenderat för testing)
**✅ Inget setup krävs - fungerar direkt!**

```bash
./mvnw spring-boot:run -Dspring.profiles.active=dev
```

**Fördelar:**
- ✅ Inget installation
- ✅ Fungerar direkt
- ✅ Perfekt för demo och utveckling
- ✅ Inkluderar test-data

---

### 🔵 Option 2: MySQL med Docker (Enklast för produktion-liknande setup)
**✅ Rekommenderat om du vill testa med riktig databas**

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

**Stoppa MySQL senare:**
```bash
docker stop ctrlbuy-mysql
docker rm ctrlbuy-mysql
```

---

### 🟠 Option 3: Lokal MySQL Installation
**För de som vill ha permanent MySQL setup**

#### macOS (med Homebrew):
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

#### Ubuntu/Debian:
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

#### Windows:
1. Ladda ner MySQL från https://dev.mysql.com/downloads/mysql/
2. Installera och följ setup-wizarden
3. Skapa databas `ctrlbuy_webshop`
4. Kör: `mvnw.cmd spring-boot:run -Dspring.profiles.active=prod`

---

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
./mvnw spring-boot:run -Dspring.profiles.active=dev

# Production med MySQL
./mvnw spring-boot:run -Dspring.profiles.active=prod

# Test miljö
./mvnw spring-boot:run -Dspring.profiles.active=test
```

---

## 🏆 Live Testing Dashboard

**🔴 LIVE COVERAGE RAPPORTER** - Uppdateras automatiskt vid varje commit:

<div align="center">

### 📊 [**KLICKA HÄR FÖR LIVE COVERAGE DASHBOARD**](https://fredrik-arvidsson.github.io/ctrlbuy-webshop/)

[![Live Coverage Dashboard](https://img.shields.io/badge/🔴%20LIVE-Coverage%20Dashboard-red.svg?style=for-the-badge)](https://fredrik-arvidsson.github.io/ctrlbuy-webshop/)

</div>

### 📈 Realtime Coverage Status

| Service | Live Coverage | Live Report Link |
|---------|---------------|------------------|
| 🛒 **All Services** | [📊 Live](https://fredrik-arvidsson.github.io/ctrlbuy-webshop/coverage/) | [📊 Detaljerad Rapport](https://fredrik-arvidsson.github.io/ctrlbuy-webshop/coverage/) |
| 💰 **Payment Processing** | [📊 Live](https://fredrik-arvidsson.github.io/ctrlbuy-webshop/coverage/) | Klicka på `com.ctrlbuy.webshop.service` |
| 📦 **Order Management** | [📊 Live](https://fredrik-arvidsson.github.io/ctrlbuy-webshop/coverage/) | Klicka på `com.ctrlbuy.webshop.controller` |
| 🔐 **Security Layer** | [📊 Live](https://fredrik-arvidsson.github.io/ctrlbuy-webshop/coverage/) | Klicka på `com.ctrlbuy.webshop.security` |

---

## 🎯 Development Status & Roadmap

**🔄 Active Development Project** - Detta är en fullt fungerande e-handelsplattform med ambitiös utvecklingsplan:

### ✅ **Nuvarande Achievements (Production Ready)**
- 🛒 **Fullständig e-handelsfunktionalitet** - Komplett webshop som fungerar
- 📦 **58+ produkter** med kategorisering och riktiga produktbilder
- 💳 **Komplett beställningsprocess** med orderbekräftelse och email
- 🎨 **Responsiv design** med Bootstrap och svensk lokalisering
- 📋 **Spring Boot MVC arkitektur** med professionella best practices
- 🗄️ **MySQL-integration** med JPA/Hibernate
- 🇸🇪 **Svensk e-handelsupplevelse** med lokaliserade routes
- 📊 **Live CI/CD Pipeline** med automatisk testing och deployment

### 🧪 **Live Testing Excellence**
- ✅ **Automatisk testning** vid varje commit
- ✅ **Live coverage rapporter** som uppdateras kontinuerligt
- ✅ **GitHub Pages integration** för transparent utveckling
- ✅ **JaCoCo detailed reporting** med klickbara rapporter
- ✅ **CI/CD pipeline** med Maven och GitHub Actions

---

## 🛠️ Troubleshooting

### Problem: MySQL Connection Error
```
Access denied for user 'root'@'localhost'
```
**Lösning:** Använd H2 istället:
```bash
./mvnw spring-boot:run -Dspring.profiles.active=dev
```

### Problem: Port redan används
```
Port 8080 was already in use
```
**Lösning:** Ändra port:
```bash
./mvnw spring-boot:run -Dserver.port=8081 -Dspring.profiles.active=dev
```

### Problem: Java version
```
Unsupported class file major version
```
**Lösning:** Se till att du har Java 21:
```bash
java -version  # Ska visa version 21
```

---

## 📊 Live Coverage Integration

### 🔴 Realtime Coverage Monitoring

**Alla coverage-siffror uppdateras automatiskt:**

```yaml
# GitHub Actions genererar automatiskt:
📊 JaCoCo HTML-rapport → GitHub Pages
🏷️ Coverage badges → README
📈 Branch coverage → Live dashboard
🔄 Kontinuerlig uppdatering → Vid varje push
```

### 📋 Åtkomst till Live Rapporter

1. **🏠 Huvuddashboard**: https://fredrik-arvidsson.github.io/ctrlbuy-webshop/
2. **📊 Coverage Detaljer**: https://fredrik-arvidsson.github.io/ctrlbuy-webshop/coverage/
3. **🔧 GitHub Actions**: [CI/CD Pipeline Status](https://github.com/fredrik-arvidsson/ctrlbuy-webshop/actions)

---

## 🔗 Live Links (Alltid Uppdaterade)

- **🏠 Live Coverage Dashboard**: https://fredrik-arvidsson.github.io/ctrlbuy-webshop/
- **📊 Detailed Coverage Report**: https://fredrik-arvidsson.github.io/ctrlbuy-webshop/coverage/
- **⚙️ GitHub Actions**: https://github.com/fredrik-arvidsson/ctrlbuy-webshop/actions
- **📁 Repository**: https://github.com/fredrik-arvidsson/ctrlbuy-webshop

---

## 🏆 Live Development Transparency

<div align="center">

### 🎯 **TRANSPARENT DEVELOPMENT WITH LIVE METRICS**
**Real Coverage | Real Tests | Real Progress | Real Time**

[![Live Pipeline](https://img.shields.io/badge/🔴%20LIVE-CI%2FCD%20Pipeline-success.svg?style=for-the-badge)](https://github.com/fredrik-arvidsson/ctrlbuy-webshop/actions)

**Se utvecklingen i realtid med live coverage-rapporter och transparent CI/CD pipeline!**

</div>
