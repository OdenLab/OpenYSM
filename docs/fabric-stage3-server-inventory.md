# Fabric 阶段三服务端与网络迁移清单（对照 Forge 原版）

| 功能 | Forge 相关文件 | 当前 Fabric 状态 | 下一步方案 |
| --- | --- | --- | --- |
| 服务端入口/生命周期 | `event/ServerStartupEvent.java` | 部分完成：已有 Fabric dedicated server entrypoint，但未接入 server about-to-start 等生命周期 | 无 Fabric API 时先用 server entrypoint 准备目录；引入可兼容 Loom 的 Fabric API 后接 `ServerLifecycleEvents` |
| 模型扫描/缓存 | `model/ServerModelManager.java` | 部分完成：目录和 builtin 提取已准备；真实 `reloadPacks/loadModels` 未迁移 | 抽离 Forge `ModList`/`ServerLifecycleHooks`/capability 依赖后迁入 common |
| NetworkHandler | `network/NetworkHandler.java` | 未迁移 | 用 Fabric custom payload / channel 注册替代 Forge `SimpleChannel` |
| Packet 编解码 | `network/message/*` | 未迁移 | 保留 buffer encode/decode 语义，替换 `NetworkEvent.Context`、`NetworkDirection` |
| 玩家状态存储 | `capability/*`, `event/CapabilityEvent.java` | 未迁移 | 用 Fabric 可用的数据附加方案或自定义 `UUID -> state` manager 替代 Forge capability |
| 玩家加入/退出 | `EnterServerEvent.java`, `PlayerLogoutEvent.java` | 未迁移 | 用 Fabric connection events 或 mixin 触发同步状态创建/清理 |
| 服务端命令 | `command/RootCommand.java`, `event/CommandRegistry.java` | 未迁移 | 用 Fabric command registration callback 替换 Forge command event |
| 模型分块同步 | `S2CModelSyncPayload`, `ServerModelManager#send...` | 未迁移 | 迁移发送队列、限速、timeout，发送端改 Fabric networking |

## 阶段三风险

- Forge capability 是当前服务端状态的核心依赖，必须先定好 Fabric 状态存储方案。
- 网络包数量多，应先迁移共享 encode/decode，再替换发送/接收胶水层。
- Fabric API 版本需要与当前 Loom 版本兼容；若引入高版本 Fabric API，会触发旧 Loom 不支持的 mixin remap 配置。
