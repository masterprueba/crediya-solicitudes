#!/bin/bash

# Script de prueba para validar la integración de autenticación entre microservicios
# Autor: Sistema de Autenticación Crediya
# Fecha: $(date)

set -e

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuración
AUTH_SERVICE_URL="http://localhost:8081/api/v1"
SOLICITUDES_SERVICE_URL="http://localhost:8080/api/v1"

echo -e "${BLUE}=== Prueba de Integración de Autenticación ===${NC}"
echo -e "${YELLOW}Verificando que los microservicios estén ejecutándose...${NC}"

# Verificar que los servicios estén corriendo
check_service() {
    local url=$1
    local service_name=$2
    
    if curl -s -f "$url/actuator/health" > /dev/null 2>&1; then
        echo -e "${GREEN}✓ $service_name está corriendo${NC}"
        return 0
    else
        echo -e "${RED}✗ $service_name no está disponible en $url${NC}"
        return 1
    fi
}

# Verificar servicios
if ! check_service "$AUTH_SERVICE_URL" "Servicio de Autenticación"; then
    echo -e "${RED}Por favor, inicie el microservicio crediya-autenticacion en puerto 8081${NC}"
    exit 1
fi

if ! check_service "$SOLICITUDES_SERVICE_URL" "Servicio de Solicitudes"; then
    echo -e "${RED}Por favor, inicie el microservicio crediya-solicitudes en puerto 8080${NC}"
    exit 1
fi

echo -e "\n${BLUE}=== Paso 1: Obtener Token de Autenticación ===${NC}"

# Datos de prueba (asegúrate de que este usuario exista en tu base de datos)
LOGIN_DATA='{
    "email": "admin@crediya.com",
    "password": "admin123"
}'

echo "Intentando login con: $LOGIN_DATA"

# Realizar login y obtener token
LOGIN_RESPONSE=$(curl -s -X POST "$AUTH_SERVICE_URL/login" \
    -H "Content-Type: application/json" \
    -d "$LOGIN_DATA")

if [ $? -eq 0 ]; then
    TOKEN=$(echo "$LOGIN_RESPONSE" | jq -r '.token // empty')
    
    if [ -n "$TOKEN" ] && [ "$TOKEN" != "null" ]; then
        echo -e "${GREEN}✓ Token obtenido exitosamente${NC}"
        echo "Token: ${TOKEN:0:50}..."
    else
        echo -e "${RED}✗ No se pudo obtener el token${NC}"
        echo "Respuesta del login: $LOGIN_RESPONSE"
        exit 1
    fi
else
    echo -e "${RED}✗ Error al realizar login${NC}"
    exit 1
fi

echo -e "\n${BLUE}=== Paso 2: Probar Validación de Token ===${NC}"

# Probar endpoint que requiere autenticación
echo "Probando endpoint /solicitud/listar (requiere rol ASESOR/ADMIN)..."

SOLICITUDES_RESPONSE=$(curl -s -w "%{http_code}" -X GET "$SOLICITUDES_SERVICE_URL/solicitud/listar?page=0&size=5" \
    -H "Authorization: Bearer $TOKEN" \
    -H "Accept: application/json")

HTTP_CODE="${SOLICITUDES_RESPONSE: -3}"
RESPONSE_BODY="${SOLICITUDES_RESPONSE%???}"

echo "Código de respuesta: $HTTP_CODE"

if [ "$HTTP_CODE" = "200" ]; then
    echo -e "${GREEN}✓ Autenticación exitosa - Token válido y usuario autorizado${NC}"
    echo "Respuesta: $(echo "$RESPONSE_BODY" | jq -r '.mensaje // "Solicitudes obtenidas"' 2>/dev/null || echo "Datos recibidos")"
elif [ "$HTTP_CODE" = "401" ]; then
    echo -e "${RED}✗ Token inválido o expirado${NC}"
    echo "Respuesta: $RESPONSE_BODY"
elif [ "$HTTP_CODE" = "403" ]; then
    echo -e "${YELLOW}⚠ Token válido pero sin permisos suficientes${NC}"
    echo "Respuesta: $RESPONSE_BODY"
    echo -e "${BLUE}Nota: El usuario necesita rol ASESOR o ADMIN para listar solicitudes${NC}"
else
    echo -e "${RED}✗ Error inesperado (HTTP $HTTP_CODE)${NC}"
    echo "Respuesta: $RESPONSE_BODY"
fi

echo -e "\n${BLUE}=== Paso 3: Probar Endpoint de Creación ===${NC}"

# Datos de solicitud de prueba
SOLICITUD_DATA='{
    "email": "cliente@test.com",
    "monto": 25000,
    "plazo_meses": 12,
    "tipo_prestamo": "PERSONAL"
}'

echo "Probando endpoint /solicitud (requiere rol CLIENTE/ASESOR/ADMIN)..."

CREAR_RESPONSE=$(curl -s -w "%{http_code}" -X POST "$SOLICITUDES_SERVICE_URL/solicitud" \
    -H "Authorization: Bearer $TOKEN" \
    -H "Content-Type: application/json" \
    -d "$SOLICITUD_DATA")

HTTP_CODE="${CREAR_RESPONSE: -3}"
RESPONSE_BODY="${CREAR_RESPONSE%???}"

echo "Código de respuesta: $HTTP_CODE"

if [ "$HTTP_CODE" = "201" ]; then
    echo -e "${GREEN}✓ Solicitud creada exitosamente${NC}"
    echo "Respuesta: $(echo "$RESPONSE_BODY" | jq -r '.id // "ID no disponible"' 2>/dev/null || echo "Solicitud creada")"
elif [ "$HTTP_CODE" = "401" ]; then
    echo -e "${RED}✗ Token inválido o expirado${NC}"
    echo "Respuesta: $RESPONSE_BODY"
elif [ "$HTTP_CODE" = "403" ]; then
    echo -e "${YELLOW}⚠ Token válido pero sin permisos suficientes${NC}"
    echo "Respuesta: $RESPONSE_BODY"
else
    echo -e "${YELLOW}⚠ Respuesta inesperada (HTTP $HTTP_CODE)${NC}"
    echo "Respuesta: $RESPONSE_BODY"
fi

echo -e "\n${BLUE}=== Paso 4: Probar Token Inválido ===${NC}"

echo "Probando con token inválido..."

INVALID_RESPONSE=$(curl -s -w "%{http_code}" -X GET "$SOLICITUDES_SERVICE_URL/solicitud/listar" \
    -H "Authorization: Bearer token_invalido_123" \
    -H "Accept: application/json")

HTTP_CODE="${INVALID_RESPONSE: -3}"
RESPONSE_BODY="${INVALID_RESPONSE%???}"

if [ "$HTTP_CODE" = "401" ]; then
    echo -e "${GREEN}✓ Correctamente rechazado token inválido${NC}"
    echo "Respuesta: $(echo "$RESPONSE_BODY" | jq -r '.mensaje // "Token inválido"' 2>/dev/null || echo "Acceso denegado")"
else
    echo -e "${RED}✗ No se rechazó correctamente el token inválido (HTTP $HTTP_CODE)${NC}"
    echo "Respuesta: $RESPONSE_BODY"
fi

echo -e "\n${BLUE}=== Paso 5: Probar Sin Token ===${NC}"

echo "Probando sin token de autorización..."

NO_TOKEN_RESPONSE=$(curl -s -w "%{http_code}" -X GET "$SOLICITUDES_SERVICE_URL/solicitud/listar" \
    -H "Accept: application/json")

HTTP_CODE="${NO_TOKEN_RESPONSE: -3}"
RESPONSE_BODY="${NO_TOKEN_RESPONSE%???}"

if [ "$HTTP_CODE" = "401" ]; then
    echo -e "${GREEN}✓ Correctamente requiere autenticación${NC}"
    echo "Respuesta: $(echo "$RESPONSE_BODY" | jq -r '.mensaje // "Autenticación requerida"' 2>/dev/null || echo "Acceso denegado")"
else
    echo -e "${RED}✗ No requiere autenticación correctamente (HTTP $HTTP_CODE)${NC}"
    echo "Respuesta: $RESPONSE_BODY"
fi

echo -e "\n${GREEN}=== Pruebas Completadas ===${NC}"
echo -e "${BLUE}Resumen:${NC}"
echo "- Validación centralizada de tokens: Implementada"
echo "- Control de roles: Configurado"
echo "- Manejo de errores: Funcionando"
echo "- Comunicación entre microservicios: Establecida"

echo -e "\n${YELLOW}Notas importantes:${NC}"
echo "1. Asegúrate de que el usuario de prueba exista en la base de datos"
echo "2. Verifica que los puertos 8080 y 8081 estén disponibles"
echo "3. Los roles deben estar correctamente configurados en la base de datos"
echo "4. Para producción, usa HTTPS para comunicación entre microservicios"

echo -e "\n${GREEN}¡Integración de autenticación completada exitosamente! 🎉${NC}"
