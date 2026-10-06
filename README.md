# 宝宝喂养台 · 安卓 App（WebView 壳 + 桌面小组件）

这是一个原生安卓工程：主体是一个 WebView，加载你的在线站点
`https://nuehfrek.qwenwork.host/`；并附带一个**桌面小组件**，在手机主屏常驻显示
**今日喂奶次数 / 换尿布次数**（每 15 分钟自动从 `/api/today` 拉取，打开 App 时也会刷新）。

> 说明：数据、Gitee 同步、AI 日报、推送都跑在你的在线服务器上，App 只是壳 + 小组件。
> 因此 **App 内不含任何密钥**，可以放心放公开仓库构建。

## 目录结构
```
baby-android/
├─ .github/workflows/build-apk.yml   # GitHub Actions 云端打包
├─ settings.gradle / build.gradle / gradle.properties
└─ app/
   ├─ build.gradle
   └─ src/main/
      ├─ AndroidManifest.xml
      ├─ java/com/qwen/babytracker/  (MainActivity / FeedingWidget / WidgetRepository / WidgetUpdateWorker / Config)
      └─ res/ (布局、小组件配置、图标、颜色/字符串/主题)
```

## 方式一：用 GitHub Actions 云端打包（推荐，本机无需装安卓 SDK）
1. 在 GitHub 新建一个仓库（公开或私有均可）。
2. 把本 `baby-android/` 目录里的**全部内容**上传到该仓库根目录
   （注意 `.github/` 和 `.gitignore` 这些以点开头的目录也要一起传）。
   - 命令行示例：
     ```
     cd baby-android
     git init && git add -A && git commit -m "baby tracker android"
     git branch -M main
     git remote add origin <你的仓库地址>
     git push -u origin main
     ```
3. 打开仓库的 **Actions** 页 → 选 “Build APK” → **Run workflow**（或直接 push 触发）。
4. 构建完成后，进入这次运行记录，在 **Artifacts** 里下载
   `baby-tracker-debug-apk`，解压得到 `app-debug.apk`。
5. 把 apk 传到手机，点击安装（需允许“安装未知来源应用”）。
   - debug 版已用调试证书签名，可直接安装使用。

## 方式二：本机用 Android Studio 打包
1. 安装 Android Studio（自带 JDK + Android SDK）。
2. File → Open → 选择本 `baby-android` 目录，等待 Gradle 同步。
3. Build → Build Bundle(s)/APK(s) → Build APK(s)，产物在 `app/build/outputs/apk/debug/`。

## 安装后：添加桌面小组件
1. 先打开一次 App（授予网络即可，无需登录）。
2. 回到桌面 → 长按空白处 → “小组件/Widgets” → 找到 **“宝宝喂养台”** → 拖到桌面。
3. 小组件会显示喂奶、换尿布两个数字与更新时间；默认每 15 分钟刷新，打开 App 会立即刷新。

## 自定义
- 站点地址在 `app/src/main/java/com/qwen/babytracker/Config.kt`
  （`APP_URL` 与 `TODAY_API`），换域名改这里。
- 应用包名 `com.qwen.babytracker`（`app/build.gradle` 的 `applicationId`）。
- 想换正式签名：在 GitHub 仓库 Settings→Secrets 配置 keystore，并在 `app/build.gradle`
  的 `signingConfigs` 里引用（debug 版可直接用，无需这步）。

## 常见问题
- **Actions 构建失败**：把 Actions 日志最后几十行发我，通常是 SDK 版本/网络问题，按提示调 `build.gradle` 版本即可。
- **小组件数字不动**：确认手机未限制该 App 后台联网/省电；打开 App 可强制刷新一次。
- **WebView 打不开站点**：确认手机能访问 `https://nuehfrek.qwenwork.host/`。
