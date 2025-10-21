#!/bin/bash

# Script para verificar cobertura de código mínima
# Autor: Sistema de verificación automática
# Fecha: $(date)

echo "🔍 Verificando cobertura de código..."
echo "=================================="
echo ""

# Listar los tests disponibles
echo "📋 Tests unitarios encontrados:"
find src/test/java -name "*Test.java" -type f | while read test_file; do
    test_class=$(basename "$test_file" .java)
    echo "   ✓ $test_class"
done
echo ""

# Ejecutar TODOS los tests unitarios
echo "📋 Ejecutando TODOS los tests unitarios..."
echo "   Comando: ./mvnw clean test jacoco:report"
echo ""

# Ejecutar tests con output más detallado para debugging
./mvnw clean test jacoco:report

# Capturar el resultado de los tests
TEST_RESULT=$?

echo ""
echo "=================================="

# Verificar si las pruebas pasaron
if [ $TEST_RESULT -ne 0 ]; then
    echo "❌ ERROR: Uno o más tests unitarios fallaron"
    echo ""
    echo "📋 Información de debugging:"
    echo "   • Código de salida: $TEST_RESULT"
    echo "   • Para ver detalles completos: ./mvnw test"
    echo "   • Para ejecutar un test específico: ./mvnw test -Dtest=NombreTest"
    echo ""

    # Verificar si existen reportes de Surefire
    if [ -d "target/surefire-reports" ]; then
        echo "📊 Reportes de tests disponibles:"
        ls -la target/surefire-reports/*.txt 2>/dev/null | while read report; do
            echo "   • $(basename "$report")"
        done
        echo ""
        echo "💡 Para ver errores específicos, revisa los archivos .txt en target/surefire-reports/"
    fi

    exit 1
fi

echo "✅ Todos los tests unitarios completados exitosamente"
echo ""

# Contar tests ejecutados
if [ -d "target/surefire-reports" ]; then
    TEST_COUNT=$(find target/surefire-reports -name "TEST-*.xml" | wc -l | tr -d ' ')
    echo "📊 Resumen de ejecución:"
    echo "   • Tests ejecutados: $TEST_COUNT clases de test"
    echo ""
fi

# Generar reporte y verificar cobertura
echo "📊 Generando reporte de cobertura..."
./mvnw jacoco:check -q

# Capturar el resultado del check de cobertura
COVERAGE_CHECK_RESULT=$?

echo ""
echo "📈 Resumen de Cobertura:"
echo "=================================="

if [ $COVERAGE_CHECK_RESULT -eq 0 ]; then
    echo "✅ COBERTURA MÍNIMA ALCANZADA"
    echo "   ✓ La cobertura de código cumple con los estándares mínimos (80%)"
    echo "   ✓ Todas las métricas de calidad se han cumplido"
    echo ""
    echo "🎉 ¡Excelente trabajo! El código tiene una cobertura adecuada."
else
    echo "❌ COBERTURA MÍNIMA NO ALCANZADA"
    echo "   ⚠️  La cobertura de código está por debajo del 80% requerido"
    echo "   ⚠️  Se necesitan más pruebas unitarias"
    echo ""
    echo "📝 Recomendaciones:"
    echo "   • Agregar más pruebas unitarias"
    echo "   • Revisar métodos sin cobertura"
    echo "   • Verificar casos edge no cubiertos"
fi

echo ""
echo "📋 Para ver el reporte detallado:"
echo "   open target/site/jacoco/index.html"
echo ""

# Extraer métricas específicas del reporte XML si existe
if [ -f "target/site/jacoco/jacoco.xml" ]; then
    echo "📊 Métricas detalladas:"
    echo "=================================="

    # Usar xmllint si está disponible para extraer datos específicos
    if command -v xmllint &> /dev/null; then
        INSTRUCTION_COVERED=$(xmllint --xpath "//counter[@type='INSTRUCTION']/@covered" target/site/jacoco/jacoco.xml 2>/dev/null | cut -d'"' -f2)
        INSTRUCTION_MISSED=$(xmllint --xpath "//counter[@type='INSTRUCTION']/@missed" target/site/jacoco/jacoco.xml 2>/dev/null | cut -d'"' -f2)

        if [ ! -z "$INSTRUCTION_COVERED" ] && [ ! -z "$INSTRUCTION_MISSED" ]; then
            TOTAL=$((INSTRUCTION_COVERED + INSTRUCTION_MISSED))
            PERCENTAGE=$((INSTRUCTION_COVERED * 100 / TOTAL))
            echo "   📈 Cobertura de instrucciones: ${PERCENTAGE}%"
            echo "   📋 Instrucciones cubiertas: ${INSTRUCTION_COVERED}"
            echo "   📋 Instrucciones no cubiertas: ${INSTRUCTION_MISSED}"
        fi
    fi
fi

echo ""
exit $COVERAGE_CHECK_RESULT
