#!/bin/bash

print_ok() {
  echo -e "\e[32m[OK]\e[0m $1"
}

print_error() {
  echo -e "\e[31m[ERRO]\e[0m $1"
}

print_info() {
  echo -e "\e[34m[INFO]\e[0m $1"
}

if ! command -v docker &> /dev/null; then
  print_error "Docker não está instalado. Instale o Docker antes de continuar."
  exit 1
else
  print_ok "Docker está instalado."
fi

if ! command -v java &> /dev/null; then
  print_error "Java não está instalado. Instale o Java (JDK) antes de continuar."
  exit 1
else
  JAVA_VERSION=$(java -version 2>&1 | head -n 1)
  print_ok "Java encontrado: $JAVA_VERSION"
fi

print_info "Gerando o JAR com Maven..."

./mvnw clean package -DskipTests

JAR_FILE=$(find target -name "*.jar" | head -n 1)

if [ -z "$JAR_FILE" ]; then
  print_error "Erro ao gerar o JAR. Verifique se o Maven está configurado corretamente."
  exit 1
else
  print_ok "JAR gerado com sucesso: $JAR_FILE"
fi


print_info "Iniciando os containers com Docker Compose..."
docker compose up --build