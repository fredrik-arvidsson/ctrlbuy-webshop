#!/bin/bash
echo "🚀 CtrlBuy Webshop - Quick Start"
echo "================================="

if ! command -v mvn &> /dev/null; then
    echo "❌ Maven krävs"
    exit 1
fi

mkdir -p logs
echo "🔨 Bygger projektet..."
mvn clean compile -q

if [ $? -eq 0 ]; then
    echo "✅ Projektet byggt framgångsrikt"
else
    echo "❌ Byggfel"
    exit 1
fi

echo "🌟 Startar CtrlBuy Webshop..."
echo "📋 Tillgänglig på: http://localhost:8080"
echo "⏹️  Tryck Ctrl+C för att stoppa"
echo "================================="

mvn spring-boot:run -Dspring.config.additional-location=classpath:application-local.properties
