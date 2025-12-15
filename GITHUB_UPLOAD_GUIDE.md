# GitHub上传指南

## 已完成的工作

✅ Git仓库已初始化
✅ 所有代码文件已提交
✅ APK文件已添加到 `releases/app-debug.apk`
✅ README.md已创建

## 下一步：上传到GitHub

### 方法一：使用GitHub网页界面（推荐新手）

1. **创建GitHub仓库**
   - 访问 https://github.com
   - 登录您的账户
   - 点击右上角的 "+" 按钮，选择 "New repository"
   - 填写仓库名称（例如：`Grocerystore`）
   - 选择 Public 或 Private
   - **不要**勾选 "Initialize this repository with a README"（因为我们已经有了）
   - 点击 "Create repository"

2. **连接本地仓库到GitHub**
   在项目目录下运行以下命令（将 `YOUR_USERNAME` 替换为您的GitHub用户名，`REPOSITORY_NAME` 替换为仓库名）：

```bash
cd C:\Users\Administrator\AndroidStudioProjects\Grocerystore
git remote add origin https://github.com/YOUR_USERNAME/REPOSITORY_NAME.git
git branch -M main
git push -u origin main
```

### 方法二：使用GitHub CLI（如果已安装）

```bash
cd C:\Users\Administrator\AndroidStudioProjects\Grocerystore
gh repo create Grocerystore --public --source=. --remote=origin --push
```

### 方法三：使用SSH（如果已配置SSH密钥）

```bash
cd C:\Users\Administrator\AndroidStudioProjects\Grocerystore
git remote add origin git@github.com:YOUR_USERNAME/REPOSITORY_NAME.git
git branch -M main
git push -u origin main
```

## 验证上传

上传成功后，访问您的GitHub仓库页面，您应该能看到：
- ✅ 所有源代码文件
- ✅ `releases/app-debug.apk` APK文件
- ✅ `README.md` 项目说明文档

## 更新Git用户信息（可选）

如果您想使用自己的GitHub信息，可以运行：

```bash
git config user.name "您的GitHub用户名"
git config user.email "您的GitHub邮箱"
```

## 后续更新

当您修改代码后，可以使用以下命令更新GitHub：

```bash
git add .
git commit -m "描述您的更改"
git push
```

## 注意事项

- APK文件较大，首次上传可能需要一些时间
- 如果遇到认证问题，GitHub现在推荐使用Personal Access Token而不是密码
- 如果仓库是私有的，只有您和您授权的用户可以访问APK文件

## 获取APK下载链接

上传成功后，APK文件的直接下载链接格式为：
```
https://github.com/YOUR_USERNAME/REPOSITORY_NAME/raw/main/releases/app-debug.apk
```

或者访问：
```
https://github.com/YOUR_USERNAME/REPOSITORY_NAME/blob/main/releases/app-debug.apk
```
然后点击 "Download" 按钮。

