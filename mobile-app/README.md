# 手机监控 APP（GI-Monitor，代码节选）

本仓库对应论文 *Development of an automatic monitoring system for cattle rumination behavior based on solid conduction and deep learning* 中手机端监控界面部分。

## 说明

本仓库仅提供 APP 的**工程结构与界面骨架**，用于展示手机端监控界面的整体框架。

完整代码暂未公开。项目仍处于持续开发阶段、代码尚未定型，为避免误导使用者，这里仅提供代码节选。核心功能（BLE 蓝牙扫描、多设备连接、状态解析等）未公开；如需完整代码，可联系通讯作者。

## 工程结构

```
APP_GIRecorder/
├── app/
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/example/girecorder/MainActivity.kt
│       └── res/values/strings.xml
├── build.gradle.kts
├── settings.gradle.kts
└── gradle.properties
```

## 技术栈

- Kotlin + Jetpack Compose
- Android Gradle Plugin 8.9.2 / Kotlin 2.1.0
- 最低支持 Android 8.0（API 26）
