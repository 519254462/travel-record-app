# 🧳 Android旅游记录应用

[![Build APK](https://github.com/YOUR_USERNAME/travel-record-app/actions/workflows/build-apk.yml/badge.svg)](https://github.com/YOUR_USERNAME/travel-record-app/actions/workflows/build-apk.yml)

一个功能完整的Android原生旅游记录应用，使用Kotlin开发，采用MVVM架构模式。

## 📱 功能特性

- ✅ **旅游记录管理** - 创建、查看、编辑、删除旅游记录
- ✅ **旅游过程记录** - 详细记录旅游过程和时间线
- ✅ **开销管理** - 记录各项开销并自动计算总额
- ✅ **搜索功能** - 快速查找旅游记录
- ✅ **统计显示** - 直观的数据概览
- ✅ **Material Design 3** - 现代化界面设计
- ✅ **本地存储** - 支持离线使用

## 🚀 快速开始

### 下载APK
1. 前往 [Releases](https://github.com/YOUR_USERNAME/travel-record-app/releases) 页面
2. 下载最新版本的 `app-debug.apk`
3. 在Android设备上安装

### 从源码编译
```bash
git clone https://github.com/YOUR_USERNAME/travel-record-app.git
cd travel-record-app
./gradlew assembleDebug
```

## 🏗️ 技术架构

- **语言**: Kotlin
- **架构**: MVVM + Repository Pattern
- **数据库**: Room (SQLite)
- **UI**: Material Design 3
- **导航**: Navigation Component
- **异步**: Kotlin Coroutines
- **测试**: Kotest + Property-Based Testing

## 📊 系统要求

- Android 5.0 (API 21) 或更高版本
- 存储空间: 约10MB

## 🛠️ 开发环境

- Android Studio Arctic Fox+
- JDK 17
- Android SDK 34
- Gradle 8.4

## 📸 应用截图

*截图将在编译完成后添加*

## 🤝 贡献

欢迎提交Issue和Pull Request！

## 📄 许可证

本项目仅供学习和个人使用。

---

**自动编译**: 每次推送代码都会自动编译生成APK，可在Actions页面下载。