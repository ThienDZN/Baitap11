#!/usr/bin/env bash
# Chay baitap11 tren Tomcat 11.0.25 voi MySQL hien co.
# Cach dung:
#   export APP_DB_PASSWORD='<mat-khau-MySQL-cua-user-thien>'
#   ./run-tomcat.sh
set -euo pipefail

TC=/home/thien/Lap-trinh-Web/apache-tomcat-11.0.25
export JAVA_HOME=${JAVA_HOME:-/usr/lib/jvm/java-21-openjdk-amd64}
export CATALINA_HOME=$TC CATALINA_BASE=$TC

# Mac dinh ket noi DB cu dang chay (co the override bang bien moi truong)
export APP_DB_URL=${APP_DB_URL:-"jdbc:mysql://localhost:3306/baitap11?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Ho_Chi_Minh&sslMode=PREFERRED"}
export APP_DB_USERNAME=${APP_DB_USERNAME:-thien}
export APP_DB_PASSWORD=${APP_DB_PASSWORD:-}
export APP_DB_DRIVER=${APP_DB_DRIVER:-com.mysql.cj.jdbc.Driver}

echo ">> Build WAR..."
( cd "$(dirname "$0")" && mvn -o -q clean package -DskipTests )

echo ">> Deploy WAR..."
rm -rf "$TC/webapps/baitap11" "$TC/webapps/baitap11.war"
cp "$(dirname "$0")/target/baitap11.war" "$TC/webapps/"

echo ">> Restart Tomcat..."
"$TC/bin/shutdown.sh" 2>/dev/null || true
sleep 4
"$TC/bin/startup.sh"

echo
echo ">> Cho 20s roi kiem tra log:"
echo "   tail -f $TC/logs/catalina.out"
echo ">> URL: http://localhost:8080/baitap11/home"
echo ">> Neu log con 'Access denied for user thien' => APP_DB_PASSWORD chua dung."
