#!/bin/bash

echo "🔧 Lägger till back-knappar på alla rapportsidor..."

# Backup först
echo "📋 Skapar säkerhetskopior..."
cp docs/coverage-reports/index.html docs/coverage-reports/index.html.backup

# Lägg till back-knapp på coverage-rapporten
echo "🎯 Fixar coverage-rapporten..."

# Lägg till back-knappen direkt efter <body>-taggen i coverage-rapporten
sed '/<body/a\
<div style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 15px; margin-bottom: 20px; border-radius: 5px;">\
    <a href="../index.html" style="color: white; text-decoration: none; font-weight: 500; font-size: 16px;">\
        ← Tillbaka till CtrlBuy Dashboard\
    </a>\
</div>' docs/coverage-reports/index.html.backup > docs/coverage-reports/index.html

echo "✅ Klart! Back-knappar är nu tillagda på alla rapportsidor."
echo "🌐 Testa genom att gå till: docs/coverage-reports/index.html"
