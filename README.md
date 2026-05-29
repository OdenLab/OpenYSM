<div align="center">
  <img src="images/brand.png" alt="logo" width="300"/>
  <h1>OpenYSM</h1>
  <p>YSM开源替代品，基于2.6.5，提供独立 Forge/Fabric 构建产物</p>
</div>

## 说明

本仓库包含了 YesSteveModel (YSM) 2.6.5（2026年4月）版本的完整源代码。

包含 1.20.1 Forge 版本的全部源码，并开始进行 Fabric 1.20.1 移植。当前仓库已拆分为 `common`、`forge`、`fabric` 三个 Gradle 模块，Forge 与 Fabric 会生成不同的安装 jar。

**请注意：项目并非 Production Ready，可能存在命名语义错误，渲染错误等问题，如果您在使用过程中遇到了任何问题请打开 Issue 反馈，最好附带截图和可能的报错日志。**

## 为什么开源？

我们决定将新版 YSM 源码开源，主要基于以下几个原因：

### 1. 新版本的完全重置

新版 YSM 已经经过完全重新设计和开发，采用了全新的架构和加密方式。

我们认为这个全新的架构和加密方式很酷，因此发布了源代码供大家学习研究和使用。

### 2. 新版加密的现状

此前社区已经出现了一个破解 YSM 2.6.5 及以下版本加密的工具和方法，新版的加密机制实际上已经失去了保护作用。

同时，目前社区中的大部分新模型都已经公布了源文件或者被解密，新版加密已经毫无实际意义。

### 3. 支持开放的游戏氛围

我们注意到社区中没有开发者制作了去除加密功能的最新 YSM 版本，这表明了这是一个蓝海市场。

OpenYSM 开发组一直非常支持开放、自由的游戏开发氛围，我们希望通过开源新版源码，为其他开发者的二次开发和学习提供便利。

## TODO

- [x] Ogg Opus音频解码播放
- [x] Webp、Avif等纹理的解码
- [x] 符合YSM标准的服务器客户端通讯握手流程
- [x] 模型的读取与渲染
- [ ] 子模型动画控制器
- [ ] SIMD加速渲染未完工
- [ ] 与服务器通讯握手时默认模型未正确处理
- [ ] 未测试低版本二进制模型/未加密模型的兼容性
- [ ] YSGPHeader生成

## 修改

我们相比已经发布的 YSM 版本做出了以下修改

- 使用 Java 重写了加载和渲染逻辑，现在可以脱离 Native 运行，例如在 MacOS，RISC-V 甚至手机上
- 支持现有的已加密的 YSM 模型
- 添加了`/openysm cache dump`命令帮助你调试模型传输，导出服务器中的所有模型

## 开源协议

### 源代码协议

本项目的源代码采用 MIT License 开放，您可以自由地使用、修改和分发代码，仅需要保留原始的版权声明。

详细的许可证条款请参见 LICENSE 文件。

### 模型资源协议

仓库中自带的模型文件采用不同的协议：

- 默认模型: 采用 CC0 (Creative Commons Zero) 协议，完全开放，无任何使用限制
- 酒狐 (Wine Fox) 模型: 采用 CC BY-NC-SA 4.0 协议，允许非商业使用，需要署名，并且衍生作品需要采用相同协议

请在使用相应模型时严格遵守对应的协议要求。

## 构建与安装产物

本仓库现在使用多模块结构：

- `common`：平台无关共享代码，仅作为编译/打包输入，不作为玩家安装 jar 发布。
- `forge`：ForgeGradle 构建的 Forge 1.20.1 产物。
- `fabric`：Fabric Loom 构建的 Fabric 1.20.1 产物。

构建命令：

```bash
# 构建 Forge 版
./gradlew :forge:build

# 构建 Fabric 版
./gradlew :fabric:build

# 生成 Fabric remapped jar
./gradlew :fabric:remapJar

# 同时构建并检查 Forge/Fabric 产物没有混装加载器元数据
./gradlew verifyLoaderArtifacts
```

产物位置与命名：

- Forge：`forge/build/libs/openysm-forge-1.20.1-<version>.jar`
- Fabric：`fabric/build/libs/openysm-fabric-1.20.1-<version>.jar`

请注意：Forge 用户必须下载 Forge jar，Fabric 用户必须下载 Fabric jar。Fabric 版还需要安装 Fabric API（`fabric-api >= 0.83.1+1.20.1`）。两者是不同产物，不能把 Forge jar 放进 Fabric 的 `mods` 文件夹，也不能把 Fabric jar 放进 Forge 的 `mods` 文件夹。

GitHub Actions 会分别运行 `./gradlew --no-daemon :forge:build`、`./gradlew --no-daemon :fabric:build` 和 `./gradlew --no-daemon verifyLoaderArtifacts`，并分别上传 `openysm-forge-jars` 和 `openysm-fabric-jars`。

### Fabric 移植状态

当前 Fabric 移植处于阶段一：已具备真实 Fabric Loom 工程、独立 Fabric jar、Fabric Loader 元数据、Fabric API 生命周期/key binding/resource reload/sound registry bootstrap、access widener 声明和构建后 jar 分离验收。它还没有完整复刻 Forge 原版的客户端、服务端、网络同步、配置、Capability、事件和渲染功能。阶段一差距清单见 `docs/fabric-phase1-gap.md`。 如果普通 merge 因文件移动过多产生冲突，可以参考 `docs/direct-overwrite-pull.md` 使用直接覆盖式拉取。

## 使用建议

我们鼓励开发者基于此源码进行二次开发，创造出更加开放、易用的模型加载工具。
