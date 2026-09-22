@echo off
title DHARWINMART - Capstone E-Commerce Application
color 0A
echo ====================================================================
echo                 DHARWINMART - Spring Boot E-Commerce
echo ====================================================================
echo.

:: Detect Java environment
if exist "C:\Users\dharw\.jdks\jdk-17.0.12+7" (
    set "JAVA_HOME=C:\Users\dharw\.jdks\jdk-17.0.12+7"
    set "PATH=C:\Users\dharw\.jdks\jdk-17.0.12+7\bin;C:\Users\dharw\.maven\apache-maven-3.9.6\bin;%PATH%"
)

echo [1/2] Checking Java Version...
java -version
if %errorlevel% neq 0 (
    echo [ERROR] Java 17+ is required but not found in PATH or JAVA_HOME.
    pause
    exit /b 1
)

echo.
echo [2/2] Launching DHARWINMART Application...
echo.
echo ====================================================================
echo   * Web Storefront:   http://localhost:8080
echo   * Demo Admin:       http://localhost:8080/admin
echo   * Product Catalog:  http://localhost:8080/products
echo   * Shopping Cart:    http://localhost:8080/cart
echo   * H2 Database:      http://localhost:8080/h2-console
echo     JDBC URL:         jdbc:h2:file:./data/dharwinmartdb;AUTO_SERVER=TRUE;DB_CLOSE_DELAY=-1
echo     Username:         sa (password empty)
echo ====================================================================
echo.

if exist "target\dharwinmart-1.0.0.jar" (
    echo Starting pre-built JAR package...
    java -jar target\dharwinmart-1.0.0.jar
) else (
    echo Starting via Maven Wrapper...
    call mvnw.cmd spring-boot:run
)

pause
