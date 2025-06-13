#!/bin/bash

echo "🧹 Städar och fixar ALLA back-knappar professionellt..."

# Backup alla filer först
echo "📋 Säkerhetskopierar alla HTML-filer..."
find docs/ -name "*.html" -exec cp {} {}.clean-backup \;

# Funktion för att rensa och lägga till back-knapp
clean_and_add_button() {
    local file="$1"
    local depth="$2"
    
    echo "🎯 Behandlar: $file"
    
    # Bestäm rätt sökväg baserat på djup
    local back_path=""
    case $depth in
        1) back_path="../index.html" ;;
        2) back_path="../../index.html" ;;
        3) back_path="../../../index.html" ;;
        *) back_path="../index.html" ;;
    esac
    
    # Ta bort alla befintliga back-knappar först
    sed -i '' '/Tillbaka till CtrlBuy Dashboard/d' "$file"
    sed -i '' '/<div style="background: linear-gradient.*#667eea.*#764ba2/d' "$file"
    
    # Lägg till en enda, enhetlig back-knapp direkt efter <body>
    sed -i '' "s|<body[^>]*>|&\
<div style=\"background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 15px 25px; margin-bottom: 20px; border-radius: 0; box-shadow: 0 4px 8px rgba(0,0,0,0.15); border-bottom: 3px solid rgba(255,255,255,0.2);\">\
    <a href=\"$back_path\" style=\"color: white; text-decoration: none; font-weight: 700; font-size: 16px; display: inline-flex; align-items: center; gap: 10px; text-shadow: 0 1px 2px rgba(0,0,0,0.3);\">\
        ← Tillbaka till CtrlBuy Dashboard\
    </a>\
</div>|" "$file"
}

echo ""
echo "🎯 Fixar alla rapportsidor..."

# Test reports (1 nivå)
if [ -f "docs/test-reports/index.html" ]; then
    clean_and_add_button "docs/test-reports/index.html" 1
fi

# Coverage reports huvudsida (1 nivå)
if [ -f "docs/coverage-reports/index.html" ]; then
    clean_and_add_button "docs/coverage-reports/index.html" 1
fi

# Alla andra coverage-filer (2 nivåer)
find docs/coverage-reports -name "*.html" -not -name "index.html" -not -name "*backup*" | while read file; do
    # Kolla om det är en djupare nivå (service-filer etc)
    levels=$(echo "$file" | tr '/' '\n' | wc -l)
    if [ $levels -ge 4 ]; then
        clean_and_add_button "$file" 2
    else
        clean_and_add_button "$file" 2
    fi
done

echo ""
echo "✅ Klart! Alla rapportsidor har nu:"
echo "  • En enda, enhetlig back-knapp"
echo "  • Professionell gradient-design"
echo "  • Korrekt sökväg tillbaka till dashboard"
echo "  • Konsekvent styling på alla sidor"
echo ""
echo "📊 Antal behandlade filer:"
find docs/ -name "*.html" -not -name "*backup*" | wc -l
echo ""
echo "🔄 Nästa steg: Committa och pusha ändringarna!"
