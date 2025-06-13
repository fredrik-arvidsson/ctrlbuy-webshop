#!/bin/bash
echo "🚀 CtrlBuy Webshop - Databas Fix"

# Absolut sökväg till properties-filen
PROPS_FILE="$(pwd)/src/main/resources/application-local.properties"

if [ ! -f "$PROPS_FILE" ]; then
    echo "❌ Kan inte hitta $PROPS_FILE"
    exit 1
fi

echo "✅ Hittade properties-fil: $PROPS_FILE"

mvn spring-boot:run \
  -Dspring.config.location="classpath:application.properties,file:$PROPS_FILE"
