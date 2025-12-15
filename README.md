# Grocery Store Android App

一个功能完整的杂货商店Android应用，支持多语言（中文、英文、俄文）。

## 功能特性

- 🔐 **用户认证**：登录和注册功能
- 🏠 **主页**：商品展示、广告轮播、热门分类
- 🔍 **搜索**：商品搜索功能
- 📦 **分类浏览**：按类别浏览商品
- 🛒 **购物车**：添加商品、数量调整、价格明细
- 📋 **订单管理**：订单预览、订单详情、订单列表
- 👤 **个人中心**：个人信息管理、订单历史、退出登录
- 🌍 **多语言支持**：中文、英文、俄文切换

## 应用截图

应用包含以下主要页面：
- 登录/注册页面
- 主页（商品展示）
- 分类页面
- 商品详情页面
- 购物车页面
- 订单页面
- 个人中心页面

## 技术栈

- **开发语言**：Kotlin
- **UI框架**：Android Jetpack (Material Design)
- **架构**：MVVM模式
- **数据存储**：SharedPreferences
- **最低SDK版本**：24 (Android 7.0)
- **目标SDK版本**：34 (Android 14)

## 安装说明

### 方式一：直接安装APK

1. 下载 `releases/app-debug.apk` 文件
2. 在Android设备上启用"未知来源"安装权限
3. 将APK文件传输到设备并安装

### 方式二：从源码构建

1. 克隆仓库：
```bash
git clone https://github.com/your-username/Grocerystore.git
cd Grocerystore
```

2. 使用Android Studio打开项目

3. 同步Gradle依赖

4. 运行项目

## 项目结构

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/example/grocerystore/
│   │   │   ├── MainActivity.kt          # 登录页面
│   │   │   ├── RegisterActivity.kt     # 注册页面
│   │   │   ├── HomeActivity.kt         # 主页
│   │   │   ├── CategoriesActivity.kt   # 分类页面
│   │   │   ├── CartActivity.kt         # 购物车页面
│   │   │   ├── OrderActivity.kt        # 订单详情页面
│   │   │   ├── OrdersActivity.kt       # 订单列表页面
│   │   │   ├── ProfileActivity.kt      # 个人中心页面
│   │   │   └── ...                     # 其他辅助类
│   │   └── res/                        # 资源文件
│   └── ...
releases/
└── app-debug.apk                       # 调试版APK文件
```

## 主要功能说明

### 登录/注册
- 支持邮箱登录
- 用户注册功能
- "记住我"功能
- 多语言界面

### 主页
- 商品搜索
- 广告轮播（ViewPager2）
- 热门商品分类
- 商品网格展示
- 底部导航栏

### 购物车
- 商品数量调整
- 商品删除
- 价格明细（原价、折扣、减免、总价）
- 继续购物功能
- 结算功能

### 订单管理
- 订单预览
- 订单确认对话框
- 订单详情查看
- 订单列表
- 订单状态显示

### 个人中心
- 个人信息展示
- 编辑个人信息
- 已完成订单列表
- 退出登录

## 开发环境

- Android Studio Hedgehog | 2023.1.1 或更高版本
- Gradle 8.0+
- JDK 17+

## 许可证

本项目仅供学习和参考使用。

## 联系方式

如有问题或建议，请提交Issue。

