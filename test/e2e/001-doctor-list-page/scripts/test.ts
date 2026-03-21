/**
 * 001-医生列表页面测试脚本
 * 使用 Playwright 测试医生列表页面数据加载
 */

import { spawn, execSync } from 'child_process';
import * as fs from 'fs';
import * as path from 'path';
import dayjs from 'dayjs';
import timezone from 'dayjs/plugin/timezone';
import utc from 'dayjs/plugin/utc';
import { chromium, Browser, Page } from 'playwright';

// 配置 dayjs 使用时区
dayjs.extend(utc);
dayjs.extend(timezone);
dayjs.tz.setDefault('Asia/Shanghai');

// 预期医生数据
const EXPECTED_DOCTORS = [
  { name: '张伟医生', title: '主任医师', department: '心内科', isActive: true },
  { name: '李娜医生', title: '副主任医师', department: '儿科', isActive: true },
  { name: '王强医生', title: '主治医师', department: '骨科', isActive: true },
  { name: '刘敏医生', title: '主任医师', department: '妇产科', isActive: false },
  { name: '陈杰医生', title: '副主任医师', department: '消化内科', isActive: true },
];

// 颜色输出
const colors = {
  reset: '\x1b[0m',
  green: '\x1b[32m',
  red: '\x1b[31m',
  yellow: '\x1b[33m',
  blue: '\x1b[34m',
  cyan: '\x1b[36m',
};

interface TestResult {
  step: number;
  name: string;
  status: 'passed' | 'failed' | 'skipped';
  duration: number;
  message: string;
  screenshot?: string;
  error?: string;
}

interface TestReport {
  testName: string;
  timestamp: string;
  totalSteps: number;
  passedSteps: number;
  failedSteps: number;
  duration: number;
  results: TestResult[];
  summary: string;
}

class TestRunner {
  private reportDir: string;
  private assetsDir: string;
  private screenshotsDir: string;
  private logFile: string;
  private reportFile: string;
  private results: TestResult[] = [];
  private startTime: number;
  private testTimestamp: string;
  private logger: Logger;
  private browser: Browser | null = null;
  private page: Page | null = null;

  constructor() {
    // 生成时间戳 (GMT+8)
    this.testTimestamp = dayjs().tz('Asia/Shanghai').format('YYYYMMDD-HHmmss');
    
    // 设置报告目录
    const baseDir = path.resolve(__dirname, '..');
    this.reportDir = path.join(baseDir, 'reports', `001-doctor-list-page-result-${this.testTimestamp}`);
    this.assetsDir = path.join(this.reportDir, 'assets');
    this.screenshotsDir = path.join(this.assetsDir, 'screenshots');
    this.logFile = path.join(this.assetsDir, 'test.log');
    this.reportFile = path.join(this.assetsDir, 'report.json');
    
    // 创建目录
    fs.mkdirSync(this.screenshotsDir, { recursive: true });
    
    // 初始化日志
    this.logger = new Logger(this.logFile);
    this.startTime = Date.now();
  }

  async run(): Promise<void> {
    this.logger.info('========================================');
    this.logger.info('001-医生列表页面测试开始');
    this.logger.info(`测试时间: ${dayjs().tz('Asia/Shanghai').format('YYYY-MM-DD HH:mm:ss')} (GMT+8)`);
    this.logger.info('========================================\n');

    let serverStarted = false;
    
    try {
      // 首先检查应用是否已在运行
      const isAppRunning = await this.checkAppRunning();
      
      if (!isAppRunning) {
        this.logger.info('应用未运行，将自动启动...');
        await this.startServer();
        serverStarted = true;
        // 等待服务器完全启动
        await this.waitForServer(30000);
      } else {
        this.logger.info('检测到应用已在运行，将直接使用现有服务');
      }

      // 执行 Playwright 测试
      await this.runPlaywrightTest();
      
      // 生成报告
      await this.generateReport();
      
    } catch (error) {
      this.logger.error(`测试执行失败: ${error}`);
      throw error;
    } finally {
      // 关闭浏览器
      if (this.browser) {
        await this.browser.close();
        this.logger.info('浏览器已关闭');
      }
      
      // 如果测试启动的服务器，停止它
      if (serverStarted) {
        await this.stopServer();
      }
    }
  }

  private async checkAppRunning(): Promise<boolean> {
    return new Promise((resolve) => {
      const http = require('http');
      const req = http.get('http://localhost:5173', (res: any) => {
        resolve(res.statusCode === 200);
      });
      req.on('error', () => resolve(false));
      req.setTimeout(3000, () => {
        req.destroy();
        resolve(false);
      });
    });
  }

  private async startServer(): Promise<void> {
    const webappDir = path.resolve(__dirname, '../../../../web/qa-web');
    
    return new Promise((resolve, reject) => {
      this.logger.info(`启动应用服务器: ${webappDir}`);
      
      const child = spawn('npm', ['run', 'dev'], {
        cwd: webappDir,
        detached: true,
        stdio: ['ignore', 'pipe', 'pipe'],
        env: { ...process.env, PORT: '5173' }
      });

      // 记录服务器输出到日志
      child.stdout?.on('data', (data) => {
        const text = data.toString().trim();
        if (text.includes('Local:') || text.includes('ready') || text.includes('5173')) {
          this.logger.info(`[SERVER] ${text}`);
        }
      });

      child.stderr?.on('data', (data) => {
        const text = data.toString().trim();
        if (!text.includes('experimental') && !text.includes('deprecated')) {
          this.logger.warn(`[SERVER-ERR] ${text}`);
        }
      });

      child.on('error', (err) => {
        this.logger.error(`启动服务器失败: ${err.message}`);
        reject(err);
      });

      // 保存 PID 以便后续停止
      if (child.pid) {
        fs.writeFileSync(path.join(this.assetsDir, 'server.pid'), child.pid.toString());
        this.logger.info(`服务器已启动，PID: ${child.pid}`);
      }

      // 给服务器一些时间启动
      setTimeout(resolve, 3000);
    });
  }

  private async waitForServer(timeout: number): Promise<void> {
    const startTime = Date.now();
    
    while (Date.now() - startTime < timeout) {
      const isRunning = await this.checkAppRunning();
      if (isRunning) {
        this.logger.info('服务器已就绪');
        return;
      }
      await new Promise(r => setTimeout(r, 1000));
    }
    
    throw new Error('等待服务器启动超时');
  }

  private async stopServer(): Promise<void> {
    try {
      const pidFile = path.join(this.assetsDir, 'server.pid');
      if (fs.existsSync(pidFile)) {
        const pid = fs.readFileSync(pidFile, 'utf-8').trim();
        this.logger.info(`停止服务器进程: ${pid}`);
        
        try {
          process.kill(parseInt(pid), 'SIGTERM');
        } catch (e) {
          // 进程可能已经退出
        }
        
        fs.unlinkSync(pidFile);
      }
    } catch (e) {
      this.logger.warn(`停止服务器时出错: ${e}`);
    }
  }

  private async runPlaywrightTest(): Promise<void> {
    this.logger.info('启动 Playwright 浏览器...');
    
    // 启动浏览器
    this.browser = await chromium.launch({ headless: true });
    this.page = await this.browser.newPage({ viewport: { width: 1920, height: 1080 } });

    try {
      // ===== 步骤 1: 访问首页并截图 =====
      await this.step1_VisitHomepage();
      
      // ===== 步骤 2: 导航到医生列表页面并截图 =====
      await this.step2_NavigateToDoctorList();
      
      // ===== 步骤 3: 验证医生数据 =====
      await this.step3_VerifyDoctorData();
      
    } catch (error) {
      this.logger.error(`测试执行出错: ${error}`);
      // 保存错误截图
      if (this.page) {
        const errorScreenshot = path.join(this.screenshotsDir, 'error-screenshot.png');
        await this.page.screenshot({ path: errorScreenshot, fullPage: true });
        this.logger.info(`错误截图已保存: ${errorScreenshot}`);
      }
      throw error;
    }
  }

  private async step1_VisitHomepage(): Promise<void> {
    const stepStart = Date.now();
    this.logger.info('[步骤 1] 访问首页...');
    
    if (!this.page) throw new Error('页面未初始化');
    
    await this.page.goto('http://localhost:5173');
    await this.page.waitForLoadState('networkidle');
    await this.page.waitForTimeout(1000); // 额外等待确保渲染完成
    
    // 截图
    const screenshotPath = path.join(this.screenshotsDir, 'step-01-homepage.png');
    await this.page.screenshot({ path: screenshotPath, fullPage: true });
    this.logger.info(`首页截图已保存: ${screenshotPath}`);
    
    const duration = Date.now() - stepStart;
    this.results.push({
      step: 1,
      name: '访问首页并截图',
      status: 'passed',
      duration,
      message: '成功访问首页并保存截图',
      screenshot: 'step-01-homepage.png'
    });
  }

  private async step2_NavigateToDoctorList(): Promise<void> {
    const stepStart = Date.now();
    this.logger.info('[步骤 2] 导航到医生列表页面...');
    
    if (!this.page) throw new Error('页面未初始化');
    
    // 尝试点击顶部导航的"医生列表"
    try {
      // 先尝试通过文本定位
      const doctorMenu = this.page.getByText('医生列表').first();
      await doctorMenu.click();
      this.logger.info('通过文本定位点击"医生列表"成功');
    } catch (e) {
      try {
        // 再尝试通过 role 定位
        const doctorMenu = this.page.getByRole('link', { name: '医生列表' });
        await doctorMenu.click();
        this.logger.info('通过 role 定位点击"医生列表"成功');
      } catch (e2) {
        // 最后直接访问 URL
        this.logger.warn('无法通过点击导航，直接访问 URL');
        await this.page.goto('http://localhost:5173/doctors');
      }
    }
    
    await this.page.waitForLoadState('networkidle');
    await this.page.waitForTimeout(1500); // 等待医生数据加载
    
    // 验证页面 URL
    const currentUrl = this.page.url();
    if (!currentUrl.includes('/doctors')) {
      throw new Error(`页面导航失败，当前 URL: ${currentUrl}`);
    }
    this.logger.info('成功导航到医生列表页面');
    
    // 截图
    const screenshotPath = path.join(this.screenshotsDir, 'step-02-doctor-list.png');
    await this.page.screenshot({ path: screenshotPath, fullPage: true });
    this.logger.info(`医生列表截图已保存: ${screenshotPath}`);
    
    const duration = Date.now() - stepStart;
    this.results.push({
      step: 2,
      name: '导航到医生列表页面并截图',
      status: 'passed',
      duration,
      message: '成功导航到医生列表页面并保存截图',
      screenshot: 'step-02-doctor-list.png'
    });
  }

  private async step3_VerifyDoctorData(): Promise<void> {
    const stepStart = Date.now();
    this.logger.info('[步骤 3] 验证医生数据...');
    
    if (!this.page) throw new Error('页面未初始化');
    
    // 获取页面文本内容用于验证
    const pageText = await this.page.innerText('body');
    
    // 获取所有医生卡片
    const doctorCards = await this.page.locator('.ant-card, .doctor-card, [class*="doctor"]').all();
    this.logger.info(`找到 ${doctorCards.length} 个医生卡片`);
    
    const verificationErrors: string[] = [];
    const foundDoctors: string[] = [];
    
    for (const expected of EXPECTED_DOCTORS) {
      const { name, title, department } = expected;
      
      // 验证医生姓名是否存在
      if (pageText.includes(name)) {
        foundDoctors.push(name);
        this.logger.info(`✓ 找到医生: ${name}`);
      } else {
        verificationErrors.push(`未找到医生: ${name}`);
        this.logger.error(`✗ 未找到医生: ${name}`);
        continue;
      }
      
      // 验证职称
      if (!pageText.includes(title)) {
        verificationErrors.push(`医生 ${name} 的职称 '${title}' 未找到`);
        this.logger.warn(`医生 ${name} 的职称 '${title}' 未找到`);
      }
      
      // 验证科室
      if (!pageText.includes(department)) {
        verificationErrors.push(`医生 ${name} 的科室 '${department}' 未找到`);
        this.logger.warn(`医生 ${name} 的科室 '${department}' 未找到`);
      }
    }
    
    // 验证医生总数
    if (foundDoctors.length !== EXPECTED_DOCTORS.length) {
      verificationErrors.push(
        `医生数量不匹配: 期望 ${EXPECTED_DOCTORS.length} 位，实际找到 ${foundDoctors.length} 位`
      );
    }
    
    const duration = Date.now() - stepStart;
    
    if (verificationErrors.length === 0) {
      this.results.push({
        step: 3,
        name: '验证医生数据',
        status: 'passed',
        duration,
        message: `成功验证所有 ${EXPECTED_DOCTORS.length} 位医生的数据`
      });
      this.logger.info(`✓ 成功验证所有 ${EXPECTED_DOCTORS.length} 位医生的数据`);
    } else {
      const errorMsg = verificationErrors.join('; ');
      this.results.push({
        step: 3,
        name: '验证医生数据',
        status: 'failed',
        duration,
        message: `验证失败: ${errorMsg}`,
        error: errorMsg
      });
      this.logger.error(`✗ 验证失败: ${errorMsg}`);
    }
  }

  private async generateReport(): Promise<void> {
    const totalDuration = Date.now() - this.startTime;
    const passedSteps = this.results.filter(r => r.status === 'passed').length;
    const failedSteps = this.results.filter(r => r.status === 'failed').length;
    
    const report: TestReport = {
      testName: '001-医生列表页面测试',
      timestamp: dayjs().tz('Asia/Shanghai').format('YYYY-MM-DD HH:mm:ss'),
      totalSteps: this.results.length,
      passedSteps,
      failedSteps,
      duration: totalDuration,
      results: this.results,
      summary: failedSteps === 0 ? '所有测试步骤通过' : `${failedSteps} 个步骤失败`
    };

    // 保存 JSON 报告
    fs.writeFileSync(this.reportFile, JSON.stringify(report, null, 2));
    this.logger.info(`测试报告已保存: ${this.reportFile}`);

    // 生成 Markdown 报告
    await this.generateMarkdownReport(report);

    // 输出摘要
    this.logger.info('\n========================================');
    this.logger.info('测试执行完成');
    this.logger.info(`总步骤: ${report.totalSteps}, 通过: ${report.passedSteps}, 失败: ${report.failedSteps}`);
    this.logger.info(`总耗时: ${(totalDuration / 1000).toFixed(2)} 秒`);
    this.logger.info(`报告目录: ${this.reportDir}`);
    this.logger.info('========================================');
    
    // 如果有失败的步骤，抛出错误
    if (failedSteps > 0) {
      throw new Error(`${failedSteps} 个测试步骤失败`);
    }
  }

  private async generateMarkdownReport(report: TestReport): Promise<void> {
    const reportMdPath = path.join(this.reportDir, 'README.md');
    
    let md = `# 001-医生列表页面测试报告

## 测试概述

- **测试名称**: ${report.testName}
- **执行时间**: ${report.timestamp} (GMT+8)
- **测试状态**: ${report.failedSteps === 0 ? '✅ 通过' : '❌ 失败'}
- **总步骤数**: ${report.totalSteps}
- **通过步骤**: ${report.passedSteps}
- **失败步骤**: ${report.failedSteps}
- **总耗时**: ${(report.duration / 1000).toFixed(2)} 秒

## 测试场景说明

本测试验证了 QA Live Healthcare 平台的医生列表页面（/doctors）能够正确加载并显示所有医生数据。

测试场景链接: [../README.md](../README.md)

## 测试数据源

预期医生数据（来自 doctor-user-list.json）：

| 序号 | 医生姓名 | 职称 | 科室 | 在线状态 |
|-----|---------|------|------|---------|
| 1 | 张伟医生 | 主任医师 | 心内科 | 在线 |
| 2 | 李娜医生 | 副主任医师 | 儿科 | 在线 |
| 3 | 王强医生 | 主治医师 | 骨科 | 在线 |
| 4 | 刘敏医生 | 主任医师 | 妇产科 | 离线 |
| 5 | 陈杰医生 | 副主任医师 | 消化内科 | 在线 |

## 测试结果详情

`;

    // 添加每个步骤的结果
    for (const result of report.results) {
      const statusIcon = result.status === 'passed' ? '✅' : result.status === 'failed' ? '❌' : '⏭️';
      md += `### 步骤 ${result.step}: ${result.name} ${statusIcon}\n\n`;
      md += `- **状态**: ${result.status.toUpperCase()}\n`;
      md += `- **耗时**: ${result.duration} ms\n`;
      md += `- **说明**: ${result.message}\n`;
      
      if (result.error) {
        md += `- **错误**: ${result.error}\n`;
      }
      
      // 嵌入截图
      if (result.screenshot) {
        const screenshotPath = `assets/screenshots/${result.screenshot}`;
        md += `- **截图**: \n\n![步骤${result.step}截图](${screenshotPath})\n\n`;
      }
      
      md += '\n---\n\n';
    }

    // 添加截图汇总
    md += `## 测试截图汇总

### 首页截图
![首页截图](assets/screenshots/step-01-homepage.png)

### 医生列表页面截图
![医生列表截图](assets/screenshots/step-02-doctor-list.png)

## 测试日志

完整测试日志请查看: [assets/test.log](assets/test.log)

## 测试数据

完整测试数据请查看: [assets/report.json](assets/report.json)

---

*报告生成时间: ${report.timestamp} (GMT+8)*
`;

    fs.writeFileSync(reportMdPath, md);
    this.logger.info(`Markdown 报告已保存: ${reportMdPath}`);
  }
}

// 日志类
class Logger {
  private logFile: string;
  private logStream: fs.WriteStream;

  constructor(logFile: string) {
    this.logFile = logFile;
    this.logStream = fs.createWriteStream(logFile, { flags: 'a' });
  }

  private write(level: string, message: string): void {
    const timestamp = dayjs().tz('Asia/Shanghai').format('YYYY-MM-DD HH:mm:ss');
    const line = `[${timestamp}] [${level}] ${message}\n`;
    
    // 写入文件
    this.logStream.write(line);
    
    // 同时输出到控制台
    console.log(line.trim());
  }

  info(message: string): void {
    this.write('INFO', message);
  }

  warn(message: string): void {
    this.write('WARN', message);
  }

  error(message: string): void {
    this.write('ERROR', message);
  }
}

// 主入口
async function main() {
  const runner = new TestRunner();
  await runner.run();
}

main().catch(err => {
  console.error('测试执行失败:', err);
  process.exit(1);
});
