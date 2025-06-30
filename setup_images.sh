#!/bin/bash
echo "🚀 Startar bildhantering setup..."

mkdir -p src/main/resources/static/images/products
mkdir -p src/main/resources/static/images

cat > src/main/resources/static/images/placeholder-product.svg << 'SVGEOF'
<svg width="400" height="400" xmlns="http://www.w3.org/2000/svg">
  <rect width="400" height="400" fill="#f8f9fa"/>
  <rect x="50" y="150" width="300" height="100" fill="#e9ecef" rx="10"/>
  <text x="200" y="190" text-anchor="middle" fill="#6c757d" font-family="Arial, sans-serif" font-size="16">Produktbild</text>
  <text x="200" y="210" text-anchor="middle" fill="#6c757d" font-family="Arial, sans-serif" font-size="16">kommer snart</text>
</svg>
SVGEOF

cp src/main/resources/static/images/placeholder-product.svg src/main/resources/static/images/placeholder-product.jpg

echo "✅ Bildhantering klar!"
