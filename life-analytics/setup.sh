#!/bin/bash
# ═══════════════════════════════════════════════════════════════════════════
# Life Analytics 2.0 – Setup Script
# ═══════════════════════════════════════════════════════════════════════════
# This script installs Java 17 and Maven if not present, then builds the app.

set -e

echo "════════════════════════════════════════════════════════════════"
echo "  Life Analytics 2.0 – Environment Setup"
echo "════════════════════════════════════════════════════════════════"

# Check for Java
if ! command -v java &> /dev/null; then
    echo "☐ Java not found. Installing OpenJDK 17..."
    sudo apt update
    sudo apt install -y openjdk-17-jdk
else
    echo "☑ Java found: $(java -version 2>&1 | head -1)"
fi

# Check for Maven
if ! command -v mvn &> /dev/null; then
    echo "☐ Maven not found. Installing..."
    sudo apt install -y maven
else
    echo "☑ Maven found: $(mvn -v | head -1)"
fi

echo ""
echo "════════════════════════════════════════════════════════════════"
echo "  Building Life Analytics 2.0..."
echo "════════════════════════════════════════════════════════════════"

cd "$(dirname "$0")"
mvn clean compile

echo ""
echo "════════════════════════════════════════════════════════════════"
echo "  ✓ Setup complete!"
echo ""
echo "  To run the application:"
echo "    mvn spring-boot:run"
echo ""
echo "  Then test with:"
echo "    curl http://localhost:8080/ping"
echo "════════════════════════════════════════════════════════════════"
