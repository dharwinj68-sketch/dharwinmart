# DHARWINMART - PowerShell Application Launcher
Write-Host "====================================================================" -ForegroundColor Cyan
Write-Host "                DHARWINMART - Spring Boot E-Commerce" -ForegroundColor Green
Write-Host "====================================================================" -ForegroundColor Cyan
Write-Host ""

$jdkPath = "C:\Users\dharw\.jdks\jdk-17.0.12+7"
$mvnPath = "C:\Users\dharw\.maven\apache-maven-3.9.6\bin"

if (Test-Path $jdkPath) {
    $env:JAVA_HOME = $jdkPath
    $env:PATH = "$jdkPath\bin;$mvnPath;" + $env:PATH
}

Write-Host "[1/2] Verifying Java environment..." -ForegroundColor Yellow
& java -version

Write-Host ""
Write-Host "[2/2] Launching Application..." -ForegroundColor Yellow
Write-Host "--------------------------------------------------------------------"
Write-Host "  * Web Storefront:   http://localhost:8080" -ForegroundColor Cyan
Write-Host "  * Demo Admin:       http://localhost:8080/admin" -ForegroundColor Cyan
Write-Host "  * Product Catalog:  http://localhost:8080/products" -ForegroundColor Cyan
Write-Host "  * Shopping Cart:    http://localhost:8080/cart" -ForegroundColor Cyan
Write-Host "  * H2 Database:      http://localhost:8080/h2-console" -ForegroundColor Cyan
Write-Host "    JDBC URL:         jdbc:h2:file:./data/dharwinmartdb;AUTO_SERVER=TRUE;DB_CLOSE_DELAY=-1"
Write-Host "    Username:         sa (password empty)"
Write-Host "--------------------------------------------------------------------"
Write-Host ""

if (Test-Path "target\dharwinmart-1.0.0.jar") {
    Write-Host "Starting pre-compiled JAR package..." -ForegroundColor Green
    & java -jar "target\dharwinmart-1.0.0.jar"
} else {
    Write-Host "Starting with Maven..." -ForegroundColor Green
    & .\mvnw.cmd spring-boot:run
}
