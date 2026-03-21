# 001-医生列表页面测试

## 测试用例名称
医生列表页面数据加载验证测试

## 测试概要说明
本测试用例验证 QA Live Healthcare 平台的医生列表页面（/doctors）能够正确加载并显示所有医生数据。测试将访问首页，通过导航菜单进入医生列表页面，并验证页面中展示的医生信息与数据源（doctor-user-list.json）中的数据一致。

## 测试包执行前置条件

1. **系统要求**：
   - Node.js >= 18.0.0
   - npm >= 9.0.0

2. **目标应用**：
   - web/qa-web 应用需要能够被访问（测试脚本会自动启动）
   - 应用运行在 http://localhost:5173

3. **安装依赖**：
   ```bash
   npm install
   ```

## 测试步骤

1. **启动目标应用并执行测试**
   - 使用 webapp-testing skill 的 with_server.py 启动 web/qa-web 应用
   - 在应用启动后执行 Playwright 测试脚本

2. **访问首页并截图**
   - 打开 http://localhost:5173（首页）
   - 等待页面完全加载（networkidle 状态）
   - 截取首页全屏截图保存到报告目录

3. **导航到医生列表页面并截图**
   - 点击顶部导航栏的"医生列表"菜单
   - 等待页面路由跳转到 /doctors
   - 等待医生列表数据加载完成
   - 截取医生列表页面全屏截图保存到报告目录

4. **验证医生数据**
   - 获取页面中显示的所有医生卡片信息
   - 对比数据源中的医生数据（doctor-user-list.json）
   - 验证医生姓名、职称、科室、在线状态等信息是否正确显示
   - 验证所有医生（共5位）都已正确加载

## 测试工具包结构

```
test/e2e/001-doctor-list-page/
├── README.md                   # 测试工具包入口说明（本文件）
├── package.json                # 项目依赖和脚本配置
├── tsconfig.json               # TypeScript 配置文件
├── scripts/
│   └── test.ts                 # 测试脚本（TypeScript + Playwright）
└── reports/
    └── assets/                 # 测试报告资源目录
        └── (动态生成)
            ├── 001-doctor-list-page-result-YYYYMMDD-HHMMSS/  # 单次测试结果目录
            │   ├── README.md          # 本次测试报告文档
            │   ├── assets/
            │   │   ├── report.json    # 测试结果JSON数据
            │   │   ├── test.log       # 测试执行日志
            │   │   └── screenshots/   # 测试截图目录
            │   │       ├── step-01-homepage.png
            │   │       └── step-02-doctor-list.png
            │   └── ...
```

## 测试工具包使用说明

### 执行测试

```bash
# 进入测试工具包目录
cd test/e2e/001-doctor-list-page

# 安装依赖（首次执行）
npm install

# 运行测试
npm run test
```

### 查看测试结果

测试执行完成后，结果将保存在 `reports/` 目录下，按以下格式组织：
- 测试结果目录：`001-doctor-list-page-result-YYYYMMDD-HHMMSS/`
- 测试报告：`001-doctor-list-page-result-YYYYMMDD-HHMMSS/README.md`
- 测试数据：`001-doctor-list-page-result-YYYYMMDD-HHMMSS/assets/report.json`
- 测试日志：`001-doctor-list-page-result-YYYYMMDD-HHMMSS/assets/test.log`
- 测试截图：`001-doctor-list-page-result-YYYYMMDD-HHMMSS/assets/screenshots/`

### 测试数据说明

测试将验证以下医生数据是否正确显示：

| 医生姓名 | 职称 | 科室 | 在线状态 |
|---------|------|------|---------|
| 张伟医生 | 主任医师 | 心内科 | 在线 |
| 李娜医生 | 副主任医师 | 儿科 | 在线 |
| 王强医生 | 主治医师 | 骨科 | 在线 |
| 刘敏医生 | 主任医师 | 妇产科 | 离线 |
| 陈杰医生 | 副主任医师 | 消化内科 | 在线 |

### 注意事项

1. 所有时间戳使用 GMT+8（北京时间）
2. 测试过程中会自动启动目标应用，无需手动启动
3. 测试完成后会自动关闭浏览器和清理临时资源
4. 如测试失败，请检查 test.log 和截图以定位问题
