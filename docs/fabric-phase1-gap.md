# Fabric 移植阶段一差距清单

阶段一目标是建立真实的 Fabric Loom 工程结构、独立 Fabric/Forge 产物和可继续迁移功能的启动链路。当前阶段完成的是工程与启动链路，不代表 Fabric 功能已经与 Forge 原版等价。

## 已替换或隔离的 Forge API

- 构建链路：Fabric 端使用 Fabric Loom，Forge 端继续使用 ForgeGradle。
- 加载器入口：Fabric 端使用 `ModInitializer` / `ClientModInitializer` / `DedicatedServerModInitializer`，Forge 端继续使用 `@Mod`。
- 元数据：Fabric 端使用 `fabric.mod.json`，Forge 端继续使用 `META-INF/mods.toml`。
- Native 初始化：`NativeLibLoader` 已放入 `common`，不直接引用 Forge 或 Minecraft 类；错误显示由各加载器侧负责。

## 已复用的 common 逻辑

- `NativeLibLoader` 已迁入 `common` 模块，并被 Forge 与 Fabric 两端共同使用。
- Fabric/Forge 产物都会打包 common 输出中的 `NativeLibLoader`。

## 当前 Fabric 可用功能

- Fabric Loader 能识别独立 Fabric jar。
- Fabric 端有独立 main/client/server entrypoint，并已接入 Fabric API 的 client/server lifecycle、client tick、玩家连接事件和 key binding 注册钩子。
- Fabric 端能执行 native 层初始化入口。
- Fabric jar 包含资源、`fabric.mod.json`、Fabric mixin 配置文件和 Fabric access widener 文件。
- Fabric bootstrap 会创建与 Forge 服务端模型目录语义一致的 `config/yes_steve_model/{built,custom,auth,export,cache}` 目录，并生成包含 Forge 默认键值的 Fabric properties 配置文件。
- Fabric bootstrap 会通过 Fabric Loader 定位本 mod jar/root，解压 `assets/yes_steve_model/builtin` 到 `config/yes_steve_model/built`，并支持与 Forge 同路径语义的 blacklist 规则。
- Fabric Loom 可生成 remapped Fabric jar，Fabric 模块显式依赖 Fabric API `0.83.1+1.20.1` 以承载后续 Events、Networking、Commands、Key Binding 迁移。
- 根项目提供 `verifyLoaderArtifacts` 验收任务，会在构建后检查 Fabric/Forge jar 的加载器元数据、入口 class 和 common `NativeLibLoader` 是否正确分离。

## 与 Forge 原版相比仍缺失的模块

阶段一完成后，Fabric 版仍未复刻 Forge 原版功能，至少缺失：

1. 配置系统：Fabric bootstrap 已生成 Forge 默认键值的 properties 配置和模型目录，但 Forge 的 `ForgeConfigSpec` 静态访问点尚未抽象，客户端/服务端业务逻辑尚未读取这些 Fabric 配置值。
2. 事件系统：Fabric API lifecycle/connection/client tick/key binding 钩子已建立，但 `client/event`、`event`、兼容模块中的 Forge 事件订阅尚未逐项迁移到 Fabric Events 或 Mixin。
3. Capability：Forge capability 数据存储尚未迁移到 Fabric 可用的数据附加/组件/自定义存储方案。
4. 网络同步：Forge `SimpleChannel`、`PacketDistributor` 和所有 packet 注册/发送尚未迁移到 Fabric Networking。
5. 命令注册：Forge 命令注册事件尚未迁移到 Fabric command callback。
6. 客户端按键行为：Fabric 已注册与 Forge 默认键位一致的 Controls 条目，但打开 GUI、动画轮盘、额外动画触发等具体行为仍需等 Capability/Networking/GUI 迁移后接通。
7. 客户端渲染：Forge 客户端事件、overlay、reload listener 等尚未迁移。
8. Fabric mixin：当前 Fabric mixin 配置为空，尚未迁移 Forge 原版 mixin 行为。
9. 服务端生命周期：Forge server lifecycle hooks 尚未迁移；目前只在 Fabric entrypoint 阶段完成目录/配置/内置模型准备，尚未接入 dedicated server reload/loadModels 流程。
10. 兼容模块：依赖 Forge-only 第三方 mod API 的兼容层尚未分离或替换为 Fabric 等价实现。
11. 客户端/服务端图形化运行验收：阶段一已提供独立构建、entrypoint、元数据和 jar 分离检查；后续阶段仍需在带图形界面的 Minecraft 环境中实际执行 `runClient` 到主菜单、进入单人世界和多人同步验证。

## 下一步补齐方案

- 阶段二客户端迁移清单见 `docs/fabric-stage2-client-inventory.md`，下一步按配置抽象、mixin、客户端 tick/key/gui、模型加载顺序推进。
- 阶段三服务端与网络迁移清单见 `docs/fabric-stage3-server-inventory.md`，下一步先定 Fabric 玩家状态存储和 networking 胶水层。
