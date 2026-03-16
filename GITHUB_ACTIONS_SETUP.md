# GitHub Actions 配置说明

## 🔧 权限配置

如果遇到 403 错误（无法创建Release），需要配置GitHub Actions权限：

### 方法1：在仓库设置中启用权限（推荐）

1. 进入你的GitHub仓库
2. 点击 **Settings** (设置)
3. 在左侧菜单找到 **Actions** → **General**
4. 滚动到 **Workflow permissions** 部分
5. 选择 **Read and write permissions**
6. 勾选 **Allow GitHub Actions to create and approve pull requests**
7. 点击 **Save** 保存

### 方法2：使用Personal Access Token

如果方法1不起作用，可以创建Personal Access Token：

1. 进入GitHub **Settings** → **Developer settings** → **Personal access tokens** → **Tokens (classic)**
2. 点击 **Generate new token (classic)**
3. 设置token名称，如 "Travel Record App Release"
4. 勾选以下权限：
   - `repo` (完整仓库访问权限)
5. 点击 **Generate token** 并复制token
6. 在仓库中添加Secret：
   - 进入仓库 **Settings** → **Secrets and variables** → **Actions**
   - 点击 **New repository secret**
   - Name: `RELEASE_TOKEN`
   - Value: 粘贴你的token
7. 修改工作流文件，将 `GITHUB_TOKEN` 改为 `RELEASE_TOKEN`

## ✅ 当前配置

工作流文件已经配置了：
- ✅ `permissions: contents: write` - 写权限
- ✅ `continue-on-error: true` - Release失败不影响APK构建
- ✅ 使用最新版本的 `softprops/action-gh-release@v2`

## 📦 获取APK文件

即使Release创建失败，APK文件仍然可以通过以下方式获取：

### 方式1：从Actions Artifacts下载
1. 进入仓库的 **Actions** 标签
2. 点击最新的工作流运行
3. 在 **Artifacts** 部分找到 `travel-record-app-debug-{编号}`
4. 点击下载

### 方式2：从Releases下载（如果创建成功）
1. 进入仓库的 **Releases** 标签
2. 找到最新版本
3. 下载 `app-debug.apk`

## 🚀 手动触发构建

你可以手动触发构建：
1. 进入仓库的 **Actions** 标签
2. 选择 **Build Android APK** 工作流
3. 点击 **Run workflow** 按钮
4. 选择分支并点击 **Run workflow**

## 📝 注意事项

- APK文件会在Actions Artifacts中保存30天
- 每次推送到main/master分支都会自动触发构建
- Pull Request也会触发构建，但不会创建Release
- 如果不需要自动创建Release，可以删除工作流中的"Create Release"步骤
