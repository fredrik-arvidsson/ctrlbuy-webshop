#!/bin/bash
echo "🧪 CtrlBuy Development Reports"
echo "=============================="

mkdir -p docs/coverage-reports

echo "🧪 Kör alla 164 tester..."
mvn clean test

echo "📊 Genererar coverage-rapporter..."
mvn jacoco:report

echo "📁 Kopierar rapporter..."
cp -r target/site/jacoco/* docs/coverage-reports/ 2>/dev/null
echo "Senast uppdaterad: $(date)" > docs/coverage-reports/last-updated.txt

JACOCO_FILE="target/site/jacoco/index.html"
if [ -f "$JACOCO_FILE" ]; then
    echo "📊 Coverage-rapport: file://$(pwd)/$JACOCO_FILE"
    
    if [[ "$OSTYPE" == "darwin"* ]]; then
        open "$JACOCO_FILE"
    fi
    
    echo "✨ Klart! 164 tester körda, rapporter genererade."
else
    echo "❌ Coverage-rapport kunde inte hittas"
fi
