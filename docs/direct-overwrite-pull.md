# 直接覆盖式拉取说明

如果你认为本次 Fabric 阶段一改动文件移动较多、普通 merge 容易冲突，可以不要做三方合并，直接把当前分支作为目标分支的新内容覆盖过去。

## 推荐方式：在目标分支上直接重置到本分支

```bash
# 1. 先保存你当前工作区改动
git status

# 2. 切到你要被覆盖的目标分支，例如 main
git checkout main

# 3. 拉取远端最新状态
git fetch origin

# 4. 直接把 main 重置为本 PR 分支对应提交
git reset --hard <PR_BRANCH_OR_COMMIT>

# 5. 强制推送到你的仓库分支
git push --force-with-lease origin main
```

这种方式不会尝试合并旧目录结构和新目录结构，适合“以本 PR 内容为准”的场景。

## GitHub 网页操作建议

如果使用 GitHub 页面合并，建议选择 **Squash and merge**，避免把阶段性拆分过程作为多个提交混入历史。

## 当前阶段的边界

本 PR 只完成 Fabric 阶段一：真实 Fabric Loom 工程、独立 Fabric/Forge 产物、common 共享层和 CI 分离构建。Fabric 客户端功能、服务端功能、网络同步、配置、Capability、事件和渲染迁移仍需后续阶段继续实现。
