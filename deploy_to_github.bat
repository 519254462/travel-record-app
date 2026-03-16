@echo off
echo 🚀 部署Android旅游记录应用到GitHub...

REM 检查是否安装了Git
git --version >nul 2>&1
if errorlevel 1 (
    echo ❌ 错误: 未安装Git，请先安装Git
    echo 下载地址: https://git-scm.com/download/win
    pause
    exit /b 1
)

echo ✅ Git已安装

REM 初始化Git仓库
if not exist .git (
    echo 📁 初始化Git仓库...
    git init
    git branch -M main
)

REM 添加所有文件
echo 📝 添加项目文件...
git add .

REM 提交代码
echo 💾 提交代码...
git commit -m "🧳 Android旅游记录应用 - 完整项目代码

✨ 功能特性:
- 旅游记录管理 (增删改查)
- 旅游过程记录
- 开销管理和统计
- Material Design 3界面
- 本地数据存储
- 搜索和筛选功能

🏗️ 技术架构:
- MVVM + Repository模式
- Room数据库
- Kotlin协程
- Navigation Component
- 属性测试覆盖

🚀 自动编译:
- GitHub Actions自动编译APK
- 支持手动触发编译
- 自动发布Release版本"

echo.
echo 📋 接下来的步骤:
echo 1. 在GitHub上创建新仓库 'travel-record-app'
echo 2. 复制仓库URL (例如: https://github.com/YOUR_USERNAME/travel-record-app.git)
echo 3. 运行以下命令连接远程仓库:
echo.
echo    git remote add origin https://github.com/YOUR_USERNAME/travel-record-app.git
echo    git push -u origin main
echo.
echo 4. 推送完成后，GitHub Actions会自动开始编译APK
echo 5. 在仓库的Actions页面查看编译进度
echo 6. 编译完成后在Releases页面下载APK
echo.
echo 🌐 GitHub仓库创建地址: https://github.com/new
echo.
pause