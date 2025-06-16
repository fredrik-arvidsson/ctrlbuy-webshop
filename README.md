# 🛒 CtrlBuy Webshop - Professional E-commerce Platform

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

### 🎯 Development Status & Roadmap

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

## 💾 Installation & Testing

### 🚀 Lokal Development Setup

```bash
# 1. Klona repositoryt
git clone https://github.com/fredrik-arvidsson/ctrlbuy-webshop.git
cd ctrlbuy-webshop

# 2. Kör tester och generera live coverage
./mvnw clean test jacoco:report

# 3. Öppna lokal coverage rapport
open target/site/jacoco/index.html

# 4. Starta applikationen
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

### 🔗 Live Links (Alltid Uppdaterade)

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