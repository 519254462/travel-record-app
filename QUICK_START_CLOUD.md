# ⚡ 快速开始 - 云端编译APK

## 🎯 目标
通过GitHub Actions自动编译Android APK，无需本地Android开发环境。

## 📋 准备工作
- GitHub账号
- Git工具（可选，也可以直接上传文件）

## 🚀 步骤1: 创建GitHub仓库

1. 访问 [GitHub](https://github.com/new)
2. 仓库名称: `travel-record-app`
3. 描述: `Android旅游记录应用`
4. 设置为 **Public** (免费使用Actions)
5. 点击 "Create repository"

## 📤 步骤2: 上传项目代码

### 方法A: 使用Git命令行
```bash
# 在项目目录执行
git init
git add .
git commit -m "Initial commit: Android Travel Record App"
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/travel-record-app.git
git push -u origin main
```

### 方法B: 直接上传文件
1. 在GitHub仓库页面点击 "uploading an existing file"
2. 将所有项目文件拖拽到页面上
3. 填写提交信息: "Android旅游记录应用 - 完整项目"
4. 点击 "Commit changes"

## ⚙️ 步骤3: 触发自动编译

代码上传后，GitHub Actions会自动开始编译：

1. 访问仓库的 **Actions** 标签页
2. 查看 "Build Android APK" 工作流
3. 等待编译完成（通常需要5-10分钟）

## 📱 步骤4: 下载APK

### 方法A: 从Artifacts下载
1. 在Actions页面点击最新的成功构建
2. 滚动到底部找到 "Artifacts" 部分
3. 下载 `travel-record-app-debug.zip`
4. 解压获得 `app-debug.apk`

### 方法B: 从Releases下载
1. 访问仓库的 **Releases** 标签页
2. 下载最新版本的 `app-debug.apk`

## 📲 步骤5: 安装APK

1. 将APK文件传输到Android设备
2. 在设备上启用"未知来源"安装：
   - 设置 → 安全 → 未知来源 (Android 7及以下)
   - 设置 → 应用和通知 → 特殊应用访问 → 安装未知应用 (Android 8+)
3. 点击APK文件进行安装

## 🔄 重新编译

如需重新编译：
1. 修改代码并推送到GitHub
2. 或在Actions页面手动触发 "Run workflow"

## 📊 编译状态说明

- 🟢 **绿色对勾**: 编译成功，可以下载APK
- 🔴 **红色叉号**: 编译失败，查看日志排查问题
- 🟡 **黄色圆点**: 正在编译中，请等待

## 🛠️ 故障排除

### 编译失败怎么办？
1. 点击失败的工作流查看详细日志
2. 常见问题：
   - Gradle版本不兼容
   - 依赖下载失败
   - 代码语法错误

### APK无法安装？
1. 确认Android版本 ≥ 5.0
2. 检查是否启用了未知来源安装
3. 尝试重新下载APK文件

## 🎉 成功标志

当您看到以下内容时，说明成功了：
- ✅ GitHub Actions显示绿色对勾
- ✅ Releases页面有新的APK文件
- ✅ APK可以在Android设备上正常安装和运行

## 📞 需要帮助？

如果遇到问题：
1. 查看 `CLOUD_BUILD_GUIDE.md` 详细指南
2. 检查GitHub Actions的构建日志
3. 确认所有文件都已正确上传

---

**预计总时间**: 15-20分钟（包括编译时间）  
**生成文件**: `app-debug.apk` (~10MB)  
**支持系统**: Android 5.0+