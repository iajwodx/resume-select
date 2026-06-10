@echo off
chcp 65001 >nul
echo ================================
echo   简历筛选平台 - 一键启动
echo ================================
echo.

:: Check Docker
docker --version >nul 2>&1
if errorlevel 1 (
    echo [错误] 未检测到 Docker，请先安装 Docker Desktop
    echo 下载地址: https://www.docker.com/products/docker-desktop
    pause
    exit /b 1
)

:: Check Docker Compose
docker compose version >nul 2>&1
if errorlevel 1 (
    echo [错误] 未检测到 Docker Compose
    pause
    exit /b 1
)

:: Check DEEPSEEK_API_KEY
if "%DEEPSEEK_API_KEY%"=="" (
    echo [警告] 未设置 DEEPSEEK_API_KEY 环境变量
    echo 请输入你的 DeepSeek API Key:
    set /p DEEPSEEK_API_KEY="DEEPSEEK_API_KEY: "
)

echo.
echo [1/3] 构建并启动服务...
docker compose up --build -d

if errorlevel 1 (
    echo [错误] 启动失败，请检查日志
    docker compose logs
    pause
    exit /b 1
)

echo.
echo [2/3] 等待服务就绪...
timeout /t 10 /nobreak >nul

echo.
echo [3/3] 检查服务状态...
docker compose ps

echo.
echo ================================
echo   启动完成！
echo   前端地址: http://localhost
echo   后端地址: http://localhost:8080
echo ================================
echo.
echo 停止服务: docker compose down
echo 查看日志: docker compose logs -f
echo.
pause
