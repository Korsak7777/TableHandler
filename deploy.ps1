param(
    [string]$ImageTag = "latest"
)

$ErrorActionPreference = "Stop"

# --- Auto-detect JAVA_HOME ---
if (-not $env:JAVA_HOME -or -not (Test-Path "$env:JAVA_HOME\bin\java.exe")) {
    $candidates = @(
        "$env:USERPROFILE\.jdks\liberica-23.0.2",
        "$env:USERPROFILE\.jdks\axiomjdk-25.0.1",
        "$env:USERPROFILE\.jdks\axiomjdk-17.0.17",
        "C:\Program Files\Eclipse Adoptium\jdk-*",
        "C:\Program Files\Java\jdk-*"
    )
    $found = $null
    foreach ($pattern in $candidates) {
        if ($pattern -like "*-*" -and -not (Test-Path $pattern)) {
            $resolved = Resolve-Path $pattern -ErrorAction SilentlyContinue | Select-Object -First 1
            if ($resolved) { $found = $resolved.Path; break }
        }
        elseif (Test-Path "$pattern\bin\java.exe") {
            $found = $pattern; break
        }
    }
    if (-not $found) {
        Write-Host "No JDK found! Install one or set JAVA_HOME." -ForegroundColor Red
        exit 1
    }
    $env:JAVA_HOME = $found
    Write-Host "JAVA_HOME auto-detected: $env:JAVA_HOME" -ForegroundColor Yellow
}

$RegistryUrl = "localhost:5001"
$ImageName   = "table_handler"
$FullImage   = "$RegistryUrl/${ImageName}:${ImageTag}"

Write-Host "=== Step 1/4: Unit tests ===" -ForegroundColor Cyan
.\gradlew.bat test
if ($LASTEXITCODE -ne 0) {
    Write-Host "Unit tests failed!" -ForegroundColor Red
    exit 1
}

Write-Host "`n=== Step 2/4: Integration tests ===" -ForegroundColor Cyan
.\gradlew.bat integrationTest
if ($LASTEXITCODE -ne 0) {
    Write-Host "Integration tests failed!" -ForegroundColor Red
    exit 1
}

Write-Host "`n=== Step 3/4: Docker build & push to $RegistryUrl ===" -ForegroundColor Cyan
docker build -t $FullImage .
if ($LASTEXITCODE -ne 0) {
    Write-Host "Docker build failed!" -ForegroundColor Red
    exit 1
}

docker push $FullImage
if ($LASTEXITCODE -ne 0) {
    Write-Host "Docker push failed! Is the registry running at $RegistryUrl?" -ForegroundColor Red
    exit 1
}

Write-Host "`n=== Step 4/4: Git push ===" -ForegroundColor Cyan
git push
if ($LASTEXITCODE -ne 0) {
    Write-Host "Git push failed!" -ForegroundColor Red
    exit 1
}

Write-Host "`nAll steps completed successfully!" -ForegroundColor Green
