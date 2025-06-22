#!/bin/bash

echo "🔧 Fixar navigation på ALLA rapportsidor..."

# Backup alla filer först
echo "📋 Skapar säkerhetskopior..."
find docs/ -name "*.html" -exec cp {} {}.backup \;

# Funktion för att lägga till snygg back-knapp
add_back_button() {
    local file="$1"
    local depth="$2"  # Hur många nivåer upp vi behöver gå (../ eller ../../)
    
    echo "🎯 Fixar: $file"
    
    # Skapa rätt sökväg baserat på djup
    local back_path=""
    for ((i=0; i<depth; i++)); do
        back_path="../$back_path"
    done
    back_path="${back_path}index.html"
    
    # Lägg till/uppdatera back-knapp direkt efter <body>-taggen
    sed "s|<body[^>]*>|&\
<div style=\"background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 12px 20px; margin-bottom: 15px; border-radius: 0; box-shadow: 0 2px 4px rgba(0,0,0,0.1);\">\
    <a href=\"$back_path\" style=\"color: white; text-decoration: none; font-weight: 600; font-size: 15px; display: inline-flex; align-items: center; gap: 8px;\">\
        ← Tillbaka till CtrlBuy Dashboard\
    </a>\
</div>|" "$file.backup" > "$file"
}

# Fixa test-reports huvudsida (1 nivå upp)
if [ -f "docs/test-reports/index.html" ]; then
    add_back_button "docs/test-reports/index.html" 1
fi

# Fixa coverage-reports huvudsida (1 nivå upp) 
if [ -f "docs/coverage-reports/index.html" ]; then
    add_back_button "docs/coverage-reports/index.html" 1
fi

# Fixa alla individuella coverage rapporter (2 nivåer upp)
find docs/coverage-reports -name "*.html" -not -name "index.html" -not -name "*.backup" | while read file; do
    add_back_button "$file" 2
done

# Fixa alla service-specifika rapporter (3 nivåer upp om de ligger djupare)
find docs/coverage-reports -path "*/com.ctrlbuy.webshop.service/*.html" -not -name "*.backup" | while read file; do
    add_back_button "$file" 3
done

echo ""
echo "✅ Klart! Alla rapportsidor har nu enhetliga back-knappar."
echo ""
echo "📊 Fixade filer:"
echo "  • Test reports huvudsida"
echo "  • Coverage reports huvudsida" 
echo "  • Alla individuella coverage rapporter"
echo "  • Alla service-specifika rapporter"
echo ""
echo "🌐 Nu kan du navigera smidigt mellan alla rapporter!"
