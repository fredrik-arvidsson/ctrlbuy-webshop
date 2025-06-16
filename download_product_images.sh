#!/bin/bash

# Script för att ladda ner produktbilder som saknas
# Kör från webshop root-mappen

echo "🖼️  Laddar ner saknade produktbilder..."

# Skapa backup av befintliga filer först
echo "📋 Skapar backup av befintliga bilder..."
mkdir -p backup_images_$(date +%Y%m%d_%H%M%S)
cp -r src/main/resources/static/images/products/* backup_images_$(date +%Y%m%d_%H%M%S)/ 2>/dev/null

# Navigera till bildmappen
cd src/main/resources/static/images/products/

echo "🔍 Laddar ner Dell XPS 13 bild..."
curl -L "https://images.unsplash.com/photo-1588872657578-7efd1f1555ed?w=500&h=400&fit=crop&crop=center" \
  -H "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36" \
  -o "dellxps13_new.jpg" 2>/dev/null

if [ -f "dellxps13_new.jpg" ] && [ -s "dellxps13_new.jpg" ]; then
    mv dellxps13_new.jpg dellxps13.jpg
    echo "✅ Dell XPS 13 bild nedladdad"
else
    echo "❌ Kunde inte ladda ner Dell XPS 13 bild"
fi

echo "🔍 Laddar ner MacBook Pro 14\" bild..."
curl -L "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=500&h=400&fit=crop&crop=center" \
  -H "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36" \
  -o "macbookpro14_new.jpg" 2>/dev/null

if [ -f "macbookpro14_new.jpg" ] && [ -s "macbookpro14_new.jpg" ]; then
    mv macbookpro14_new.jpg macbookpro14.jpg
    echo "✅ MacBook Pro 14\" bild nedladdad"
else
    echo "❌ Kunde inte ladda ner MacBook Pro 14\" bild"
fi

echo "🔍 Laddar ner ThinkPad X1 bild..."
curl -L "https://images.unsplash.com/photo-1541807084-5c52b6b3adef?w=500&h=400&fit=crop&crop=center" \
  -H "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36" \
  -o "thinkpadx1_new.jpg" 2>/dev/null

if [ -f "thinkpadx1_new.jpg" ] && [ -s "thinkpadx1_new.jpg" ]; then
    mv thinkpadx1_new.jpg thinkpadx1.jpg
    echo "✅ ThinkPad X1 bild nedladdad"
else
    echo "❌ Kunde inte ladda ner ThinkPad X1 bild"
fi

# Kopiera till target-mappen också
echo "📂 Kopierar bilder till target-mappen..."
cd ../../../../../../../../
cp src/main/resources/static/images/products/*.jpg target/classes/static/images/products/ 2>/dev/null

echo ""
echo "🎉 Klar! Alla produktbilder har fixats."
echo "🌐 Gå till http://localhost:8080/products för att se resultatet!"
