# FT-001 患者身份验证与自动建档 — User Story 列表

> 本文档记录 Feature 拆分后的所有 User Story 及其状态。

**Feature ID**: FT-001
**Feature 名称**: 患者身份验证与自动建档
**创建日期**：2026-07-14
**User Story 总数**: 5
**拆分模式**：Workflow Steps（主）+ Operations / CRUD (Read)（档案查询、历史问诊）

---

## User Story 清单

| User Story ID | User Story 名称 | 用户角色 | 优先级 | 依赖 | 状态 | 文档链接 |
| -------- | ---------- | -------- | ------ | ---- | ---- | -------- |
| FT-001-US-01 | 患者身份验证与自动建档 | 患者 | P0 | 无 | 草稿 | [查看](./user_stories/FT-001-US-01.md) |
| FT-001-US-02 | 患者档案查询 | 已验证患者 | P1 | FT-001-US-01 | 草稿 | [查看](./user_stories/FT-001-US-02.md) |
| FT-001-US-03 | 历史问诊查询 | 已验证患者 | P1 | 无 | 草稿 | [查看](./user_stories/FT-001-US-03.md) |
| FT-001-US-04 | 前端对接真实接口与门户展示 | 患者 | P1 | FT-001-US-01、FT-001-US-02、FT-001-US-03 | 草稿 | [查看](./user_stories/FT-001-US-04.md) |
| FT-001-US-05 | 切换用户 | 共用设备的患者 | P2 | FT-001-US-04、FT-001-US-01 | 草稿 | [查看](./user_stories/FT-001-US-05.md) |

## 拆分模式说明

本 Feature 描述了一个完整的"验证建档 → 门户展示 → 切换用户"患者旅程，因此**主模式采用 Workflow Steps**，将旅程中的关键步骤拆为可独立交付的纵向切片：

- **FT-001-US-01 患者身份验证与自动建档**：旅程核心入口，包含业务键生成与姓名归一化去重，是一个端到端纵向切片（后端 verify 接口 + 建档）。
- **FT-001-US-02 患者档案查询**、**FT-001-US-03 历史问诊查询**：两者都是后端 Read 能力，单独以 **Operations / CRUD (Read)** 切片，保证可独立开发与测试，且分别为门户展示与历史展示提供数据。
- **FT-001-US-04 前端对接真实接口与门户展示**：将已就绪的后端能力整合为面向患者的门户体验（Workflow Steps 的整合层）。
- **FT-001-US-05 切换用户**：旅程的收尾步骤，复用建档能力并保持档案持久（Workflow Steps）。

选择理由：Feature 自然呈现"先验证建档、再查询展示、最后切换"的流程，且后端查询能力边界清晰，适合在 Workflow Steps 之下辅以 Operations/CRUD 单独切片，兼顾独立交付与可测试性。

## 关联文档

- [Feature 准备文档](./FT-001-patient-identity-verification-FeaturePrep.md) — Feature 的 who/what/why 分析
- [关联 PRD 设计文档](../../qahc-harness/feat/FT-001-patient-identity-verification/FT-001-patient-identity-verification-PRD.md) — 技术依据摘录自 PRD v1.1.0
- [评估报告](./FT-001-patient-identity-verification-UserStoriesEvaluationReport.md) — INVEST 评估结果（待生成）

---

**文档版本**：0.1
**创建日期**：2026-07-14
**最后更新**：2026-07-14
**维护者**：AI Agent (asdm-feature-split)
