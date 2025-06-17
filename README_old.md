# 🛒 CtrlBuy Webshop 🚀 - Professional E-commerce Platform

<div align="center">

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)
[![Maven](https://img.shields.io/badge/Maven-3.9+-red.svg)](https://maven.apache.org/)

<!-- LIVE BADGES från din GitHub Pages! -->
[![Tests](https://img.shields.io/endpoint?url=https://fredrik-arvidsson.github.io/ctrlbuy-webshop/tests.json)](https://fredrik-arvidsson.github.io/ctrlbuy-webshop/)
[![Live Coverage](https://img.shields.io/endpoint?url=https://fredrik-arvidsson.github.io/ctrlbuy-webshop/coverage.json)](https://fredrik-arvidsson.github.io/ctrlbuy-webshop/coverage/)
[![Build Status](https://github.com/fredrik-arvidsson/ctrlbuy-webshop/workflows/CtrlBuy%20CI%2FCD%20Pipeline/badge.svg)](https://github.com/fredrik-arvidsson/ctrlbuy-webshop/actions)

<!-- Specifika service badges - länkar till live rapporter -->
[![Coverage Report](https://img.shields.io/badge/Coverage%20Report-Live-brightgreen.svg)](https://fredrik-arvidsson.github.io/ctrlbuy-webshop/coverage/)
[![Test Dashboard](https://img.shields.io/badge/Test%20Dashboard-Live-blue.svg)](https://fredrik-arvidsson.github.io/ctrlbuy-webshop/)
[![AWS Ready](https://img.shields.io/badge/AWS-Ready-ff9900.svg)](https://aws.amazon.com/)

</div>

> **En modern, skalbar och fullständigt testad e-handelsplattform byggd med Spring Boot. Professionell kvalitet med automatiserade tester och live coverage-rapporter.**

## ⚡ Quick Start (8 sekunder)

**Vill du bara testa applikationen snabbt? Här är det snabbaste sättet:**

```bash
# 1. Klona och gå in i projektet
git clone https://github.com/fredrik-arvidsson/ctrlbuy-webshop.git
cd ctrlbuy-webshop

# 2. Starta applikationen (använder H2 in-memory databas)
mvn spring-boot:run
```

**🎉 Klart!** Applikationen kör nu på: **http://localhost:8080** (startar på ~8 sekunder)

### 🔍 Verify Everything Works

**1. Main Application:**
```
http://localhost:8080
```

**2. H2 Database Console (development):**
```
http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:testdb
Username: sa
Password: (leave blank)
```

## 🏆 Live Testing Dashboard

### 📊 [**KLICKA HÄR FÖR LIVE COVERAGE DASHBOARD**](https://fredrik-arvidsson.github.io/ctrlbuy-webshop/)

**Se realtidsdata från senaste CI/CD-körningen:**
- 🧪 **Test Results** - Live från GitHub Actions
- 📈 **Coverage Metrics** - Uppdateras automatiskt
- 🔍 **Detailed Reports** - Klickbara JaCoCo-rapporter

### 📈 Realtime Coverage Status

| Component | Tests | Coverage | Status |
|-----------|-------|----------|---------|
| Controllers | 167 | 100% | ✅ |
| Services | 225 | 95% | ✅ |
| Total | **392** | **98%** | ✅ |

**Live Links:**
- 🎯 [**Coverage Report**](https://fredrik-arvidsson.github.io/ctrlbuy-webshop/coverage/) - Interaktiv JaCoCo rapport
- 📊 [**Test Dashboard**](https://fredrik-arvidsson.github.io/ctrlbuy-webshop/) - Live metrics overview

## 🗄️ Database Configuration

### 🟢 Option 1: H2 Database (Rekommenderat för testing)

**Standard setup - inga extra installationer behövs:**

```bash
mvn spring-boot:run
```

H2 Console tillgänglig på: http://localhost:8080/h2-console

**Inloggning:**
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: *(lämna tomt)*

### 👥 Förkonfigurerade användare

**Viktigt:** H2 in-memory databas = **inga användare sparas mellan sessioner**.

**För att testa applikationen:**
1. Gå till: `http://localhost:8080/register`
2. Registrera en ny användare
3. Logga in på: `http://localhost:8080/login`

**Admin-behörigheter:** Använd H2 Console för att ändra roll:
```sql
UPDATE USERS SET role = 'ADMIN' WHERE username = 'your_username';
```

## 🚀 Test & Development

### Kör alla tester
```bash
mvn clean test
```

### Generera coverage rapport
```bash
mvn jacoco:report
# Rapport sparas i: target/site/jacoco/index.html
```

### Olika utvecklingsmiljöer
```bash
# H2 database (default)
mvn spring-boot:run

# MySQL (local setup required)
mvn spring-boot:run -Dspring.profiles.active=local

# Production mode
mvn spring-boot:run -Dspring.profiles.active=prod
```

## 🎯 Development Status & Roadmap

### ✅ **Nuvarande Achievements (Production Ready)**
- ✅ **392 Automatiserade Tester** - Alla passerar!
- ✅ **100% Controller Coverage** - 21 Controllers fullständigt testade
- ✅ **Spring Security Implementation** - Rollbaserad säkerhet
- ✅ **Svenska Lokalisering** - /produkter, /kundvagn, /kassa routes
- ✅ **AWS Deployment Ready** - Production-klar JAR-fil
- ✅ **Live CI/CD Pipeline** - Automatiska test-rapporter

### 🧪 **Live Testing Excellence**
- 🔴 **Continuous Integration** - GitHub Actions pipeline
- 📊 **Live Coverage Reports** - Uppdateras vid varje commit
- 🎯 **Interactive Dashboards** - Klickbara JaCoCo-rapporter
- ⚡ **Real-time Badges** - Status uppdateras automatiskt
- 📈 **Trend Tracking** - Historisk coverage-data

## 🛠️ Troubleshooting

### Problem: MySQL Connection Error
```bash
# Kontrollera MySQL status
sudo systemctl status mysql

# Restart MySQL
sudo systemctl restart mysql
```

### Problem: Port redan används
```bash
# Hitta process på port 8080
lsof -i :8080

# Stoppa process
kill -9 <PID>
```

### Problem: Java version
```bash
# Kontrollera Java version
java -version

# Måste vara Java 21 eller senare
```

## 📊 Live Coverage Integration

### 🔴 Realtime Coverage Monitoring

Denna README uppdateras automatiskt med live data från GitHub Actions:

- **Build Status**: Visar resultat från senaste CI/CD-körning
- **Test Count**: Automatiskt räknade från Maven Surefire
- **Coverage %**: Extraherad från JaCoCo-rapporter
- **Quality Gates**: Automatisk färgkodning baserat på metrics

### 📋 Åtkomst till Live Rapporter

**Alla rapporter uppdateras automatiskt vid varje push:**

1. **[Live Dashboard](https://fredrik-arvidsson.github.io/ctrlbuy-webshop/)** - Huvudöversikt
2. **[Coverage Report](https://fredrik-arvidsson.github.io/ctrlbuy-webshop/coverage/)** - Detaljerad täckning per klass
3. **[CI/CD Pipeline](https://github.com/fredrik-arvidsson/ctrlbuy-webshop/actions)** - Build history och logs

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

---

## 👨‍💻 Utvecklare

**Fredrik Arvidsson**
- LinkedIn: [Fredrik Arvidsson](https://www.linkedin.com/in/fredrik-arvidsson-57587b11a)
- GitHub: [fredrik-arvidsson/ctrlbuy-webshop](https://github.com/fredrik-arvidsson/ctrlbuy-webshop)

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
🧪 Tests: 392 (100% passing)
📦 Controllers: 21 (100% tested)
🔒 Security: Enterprise-grade
☁️ Cloud: AWS Ready
🌐 Localization: Swedish
📱 Responsive: Mobile-first
```

---

**Byggt med ❤️ i Sverige | Production Ready | Enterprise Quality**