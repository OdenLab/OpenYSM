# Fabric 阶段二客户端迁移清单（对照 Forge 原版）

本清单用于阶段二逐项迁移客户端功能。状态含义：

- 已完成：Fabric 端已有等价实现。
- 部分完成：Fabric 端已有启动/目录/配置准备，但业务逻辑尚未真正接入。
- 未迁移：仍完全依赖 Forge API 或 Forge 侧源码。

| 功能 | Forge 相关文件 | 当前 Fabric 状态 | 下一步方案 |
| --- | --- | --- | --- |
| Fabric/客户端入口 | `client/event/ClientSetupEvent.java` | 部分完成：已有 `YesSteveModelFabricClient`，但只记录日志 | 用 Fabric client entrypoint 注册 key binding、资源 reload、HUD/GUI/mixin 初始化 |
| 配置加载 | `config/GeneralConfig.java`, `ExtraPlayerRenderConfig.java`, `LoadingStateConfig.java` | 部分完成：Fabric 已生成同默认值 properties，但业务逻辑未读取 | 抽象 `YsmConfigAccess`，Forge 包装 `ForgeConfigSpec`，Fabric 包装 properties |
| 模型目录初始化 | `model/ServerModelManager.java` | 部分完成：Fabric bootstrap 已创建 `config/yes_steve_model` 目录和 cache 子目录 | 将目录常量抽到 common，客户端/服务端逻辑统一使用平台路径 |
| 内置模型提取 | `ServerModelManager#extractBuiltinModels` | 部分完成：Fabric 已用 Fabric Loader root 解压 builtin 到 built | 后续接入真正 `loadModels` 流程，并验证 blacklist 与 Forge 行为一致 |
| 默认/自定义模型加载 | `ServerModelManager`, `ClientModelManager`, `resource/*` | 未迁移 | 先迁移 platform-neutral parser/cache 到 common，再做 Fabric 端调用入口 |
| GUI / 模型切换 | `client/gui/*`, `command/RootClientCommand.java` | 未迁移 | 替换 Forge screen/key/client-command 注册为 Fabric API 或 Mixin |
| 渲染注入 | `mixin/client/*`, `client/renderer/*` | 未迁移：Fabric mixin 配置当前为空 | 逐个迁移 mixin 到 Yarn 命名并补 access widener |
| 动画更新 | `client/animation/*`, `client/event/ClientTickEvent.java` | 未迁移 | 用 Fabric client tick event 或 mixin 调度动画 tick |
| 贴图上传/释放 | `client/texture/*`, `client/event/PlayerSkinTextureManager.java` | 未迁移 | 先确认 MC/Yarn 映射访问点，再迁移 resource reload 和 close hooks |
| 世界进入/退出 | `ClientPlayerJoinNotification.java`, `EnterServerEvent.java`, `PlayerLogoutEvent.java` | 未迁移 | 用 Fabric networking connection events 或 mixin 替代 Forge events |
| 兼容层 | `client/compat/*` | 未迁移 | Forge-only compat 保留在 Forge；Fabric 逐个寻找 Fabric 等价 mod API |

## 阶段二优先顺序

1. 配置访问抽象：先让现有业务逻辑能在 Forge/Fabric 两侧读取同一语义配置。
2. Mixin 迁移：只迁移不依赖 Forge capability/network 的纯客户端渲染 mixin。
3. 客户端 tick / key mapping / GUI：恢复本地模型选择与预览的最小闭环。
4. 模型加载和贴图缓存：复用 parser/cache/render 数据结构，逐步剥离 Forge-only capability。
