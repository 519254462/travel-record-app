@echo off
echo 正在编译Android旅游记录应用...

REM 设置环境变量
set ANDROID_HOME=D:\Android\SDK
set JAVA_HOME=C:\Program Files\Android\jdk

REM 检查环境
if not exist "%ANDROID_HOME%" (
    echo 错误: Android SDK未找到
    pause
    exit /b 1
)

if not exist "%JAVA_HOME%" (
    echo 错误: Java JDK未找到
    pause
    exit /b 1
)

echo Android SDK: %ANDROID_HOME%
echo Java JDK: %JAVA_HOME%

REM 尝试使用gradlew编译
echo 正在尝试编译APK...
if exist gradlew.bat (
    call gradlew.bat assembleDebug
) else (
    echo 错误: Gradle Wrapper未找到
    echo 请在Android Studio中打开项目进行编译
)

echo.
echo 编译完成！
echo 如果成功，APK文件位于: app\build\outputs\apk\debug\app-debug.apk
echo.
pause