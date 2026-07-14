# FT-002 代码调研报告 · 前端（qa-web）

> 调研对象：`web/qa-web/`（Vue3 + TS + Ant Design Vue 4 + Vite）
> 调研目的：为 FT-002「患者问诊记录查看」详细设计提供前端实现基线
> 调研方式：只读代码搜索与阅读，未修改任何文件

---

## 1. 代码库概述

`web/qa-web/src` 关键目录：

```
src/
├── api/
│   ├── request.ts          # axios 实例（baseURL=/api，响应拦截器直接返回 response.data）
│   ├── types.ts            # ApiResponse<T> 等
│   └── modules/doctor.ts   # 仅 doctorApi，无 questionApi
├── components/             # AppHeader / AppFooter / HelloWorld（无问诊相关组件）
├── data/
│   ├── question-list.json   # ★ 问诊记录 mock（关键）
│   ├── patient-user.json
│   └── doctor-user-list.json
├── locales/                # index.ts + zh-cn.json + en-us.json（vue-i18n，legacy:false）
├── router/index.ts          # 含 /consultation 与 /consultation/:doctorUsername
├── store/index.ts           # 单例 reactive store（非 Pinia），定义 Question/Doctor/Patient 接口
└── views/
    ├── Consultation.vue     # ★ 患者门户（重点：历史问诊区块）
    ├── DoctorRoom.vue        # 含 a-collapse accordion 用法参考
    └── ...（Home/Doctors/DoctorLogin/About）
```

---

## 2. 现有实现分析（患者门户 Consultation.vue）

- 「历史问诊」区块标题为「我的问题」，由 `myQuestions` 驱动。
- **数据来源为本地 mock**：`myQuestions` = `store.getQuestionsByPatient(currentPatient.value.id)`，store 数据来自 `data/question-list.json`，**无任何 axios 接口调用**。
- **当前未实现的能力**：按提交时间倒序（mock 原序）、内容摘要截断、状态筛选标签页、客户端分页、内联手风琴。
- 已使用的 Ant Design 组件：`a-card` / `a-tag`(:color green/orange) / `a-empty` / `a-divider` / `a-form` 等。
- 已用 `dayjs` 格式化时间（`formatTime`，`YYYY-MM-DD HH:mm`）。

### 可复用 UI 原子能力
| 能力 | 现状 | 复用位置 |
|------|------|----------|
| 状态标签 | ✅ `a-tag` + color | Consultation.vue |
| 空状态 | ✅ `a-empty` | Consultation.vue |
| 手风琴展开 | ✅ `a-collapse accordion` | DoctorRoom.vue（患者门户需接入） |
| 时间格式化 | ✅ dayjs | Consultation.vue / DoctorRoom.vue |
| 卡片列表 | ✅ `a-card` v-for | Consultation.vue（改造基础） |

---

## 3. 关键发现与缺失项（对照 FT-002 功能）

| FT-002 功能 | 可复用 | 需新建 | 依赖 FT-001 接口 |
|------|------|------|------|
| 倒序列表 | ❌（mock 原序） | 排序逻辑/接口返回顺序确认 | 接口返回顺序需确认 |
| 状态标签（待回答/已回答） | ✅ `a-tag` | 文案改 i18n | 否 |
| 摘要截断约 50 字 | ❌（DoctorRoom 有 `substring(0,50)+'...'` 参考） | 截断函数 | 否 |
| 内联手风琴展开 | ✅ `a-collapse` 参考 | 在门户接入 | 否 |
| 状态筛选标签页 | ❌ 全局无 `a-tabs` | 新建 a-tabs + computed 过滤 | 否 |
| 空状态 | ✅ `a-empty` | 改 i18n「暂无问诊记录」 | 否 |
| 客户端分页「加载更多」 | ❌ 全局无分页 | 新建 slice + 加载更多 | 否 |
| 数据来源 GET /api/questions?patientId= | ❌ 无 questionApi | 新建 `api/modules/question.ts` | ✅ 强依赖 FT-001 |

**核心结论**：UI 原子能力基本具备，可直接复用；倒序、摘要截断、状态标签页、客户端分页 4 项需新建；**唯一硬阻塞是 question 接口尚未实现**，真实取数链路须等 FT-001 完成。

---

## 4. 待确认问题（编码前）

1. **数据来源切换策略**：先用 mock 跑通 UI、FT-001 就绪后切接口？或接口就绪才开工？store 的 `getQuestionsByPatient` 是否保留作兜底？
2. **倒序由前端还是后端负责**：接口是否保证倒序返回？还是需要前端 `sort`（涉及时区）？
3. **状态枚举语义对齐**：mock/接口 status 用 `pending`/`answered`，PRD 文案为「待回答/已回答」，是否引入第三种状态？映射需与后端对齐。
4. **分页参数**：「加载更多」纯前端 slice 还是后端分页（`page`/`pageSize`）？若后端分页需确认返回结构。
5. **i18n 改造范围**：是否把 Consultation 现有写死中文（「我的问题」「已解答/待解答」「您还没有提交过问题」等）一并迁移到 `consultation.*` 命名空间？建议新建统一 `consultation` 节点。
6. **组件拆分粒度**：直接在 Consultation.vue 内联，还是抽出 `QuestionList.vue` / `QuestionItem.vue`？
7. **patientId 来源**：门户当前靠 `store.state.currentPatient.id` 定位患者，是否符合 FT-001 接口的 `patientId` 入参？
8. **摘要截断「约50字」**：50 字符还是 50 汉字？中英文混排如何处理？建议明确 `slice` 长度与省略号规则。

---

## 5. 调研来源文件

- `web/qa-web/src/views/Consultation.vue`
- `web/qa-web/src/views/DoctorRoom.vue`
- `web/qa-web/src/api/request.ts` / `api/modules/doctor.ts` / `api/types.ts`
- `web/qa-web/src/data/question-list.json`
- `web/qa-web/src/locales/index.ts` / `zh-cn.json` / `en-us.json`
- `web/qa-web/src/store/index.ts`
- `web/qa-web/src/router/index.ts`

---

*由 QA Healthcare 驾驭工程工具集（qahc-harness）Code Research 产出。*
