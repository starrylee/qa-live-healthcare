# -*- coding: utf-8 -*-
"""
Playwright 测试脚本 - 医生列表页面验证
由 TypeScript 测试运行器自动生成
"""

import sys
import json
import os
from playwright.sync_api import sync_playwright, expect

# 截图保存目录
SCREENSHOT_DIR = sys.argv[1] if len(sys.argv) > 1 else "./screenshots"

# 预期医生数据
EXPECTED_DOCTORS = [
    {"name": "张伟医生", "title": "主任医师", "department": "心内科", "isActive": True},
    {"name": "李娜医生", "title": "副主任医师", "department": "儿科", "isActive": True},
    {"name": "王强医生", "title": "主治医师", "department": "骨科", "isActive": True},
    {"name": "刘敏医生", "title": "主任医师", "department": "妇产科", "isActive": False},
    {"name": "陈杰医生", "title": "副主任医师", "department": "消化内科", "isActive": True},
]

def log_result(step_num, step_name, status, message="", duration=0, screenshot=None, error=None):
    """输出测试结果 JSON"""
    result = {
        "step": step_num,
        "name": step_name,
        "status": status,
        "duration": duration,
        "message": message,
        "screenshot": screenshot,
        "error": error
    }
    print(f"TEST_RESULT: {json.dumps(result, ensure_ascii=False)}")
    return result

def main():
    results = []
    
    with sync_playwright() as p:
        browser = p.chromium.launch(headless=True)
        page = browser.new_page(viewport={"width": 1920, "height": 1080})
        
        try:
            # ===== 步骤 1: 访问首页并截图 =====
            step_start = os.times().elapsed
            print("[STEP 1] 访问首页...")
            
            page.goto('http://localhost:5173')
            page.wait_for_load_state('networkidle')
            page.wait_for_timeout(1000)  # 额外等待确保渲染完成
            
            # 截图
            homepage_screenshot = os.path.join(SCREENSHOT_DIR, 'step-01-homepage.png')
            page.screenshot(path=homepage_screenshot, full_page=True)
            print(f"[STEP 1] 首页截图已保存: {homepage_screenshot}")
            
            step_duration = int((os.times().elapsed - step_start) * 1000)
            results.append(log_result(
                1, "访问首页并截图", "passed",
                "成功访问首页并保存截图", step_duration,
                'step-01-homepage.png'
            ))
            
            # ===== 步骤 2: 导航到医生列表页面并截图 =====
            step_start = os.times().elapsed
            print("[STEP 2] 导航到医生列表页面...")
            
            # 点击顶部导航的"医生列表"
            doctor_menu = page.locator('a[href="/doctors"], .ant-menu-item:has-text("医生列表"), text=医生列表').first
            
            # 尝试多种方式定位导航链接
            try:
                # 先尝试通过文本定位
                page.get_by_text("医生列表").first.click()
            except:
                try:
                    # 再尝试通过 role 定位
                    page.get_by_role("link", name="医生列表").click()
                except:
                    # 最后直接访问 URL
                    page.goto('http://localhost:5173/doctors')
            
            page.wait_for_load_state('networkidle')
            page.wait_for_timeout(1500)  # 等待医生数据加载
            
            # 验证页面 URL
            expect(page).to_have_url(lambda url: '/doctors' in url)
            print("[STEP 2] 成功导航到医生列表页面")
            
            # 截图
            doctorlist_screenshot = os.path.join(SCREENSHOT_DIR, 'step-02-doctor-list.png')
            page.screenshot(path=doctorlist_screenshot, full_page=True)
            print(f"[STEP 2] 医生列表截图已保存: {doctorlist_screenshot}")
            
            step_duration = int((os.times().elapsed - step_start) * 1000)
            results.append(log_result(
                2, "导航到医生列表页面并截图", "passed",
                "成功导航到医生列表页面并保存截图", step_duration,
                'step-02-doctor-list.png'
            ))
            
            # ===== 步骤 3: 验证医生数据 =====
            step_start = os.times().elapsed
            print("[STEP 3] 验证医生数据...")
            
            # 获取页面上显示的所有医生卡片
            doctor_cards = page.locator('.ant-card, .doctor-card, [class*="doctor"]').all()
            print(f"[STEP 3] 找到 {len(doctor_cards)} 个医生卡片")
            
            # 获取页面文本内容用于验证
            page_content = page.content()
            page_text = page.inner_text('body')
            
            verification_errors = []
            found_doctors = []
            
            for expected in EXPECTED_DOCTORS:
                name = expected['name']
                title = expected['title']
                department = expected['department']
                
                # 验证医生姓名是否存在
                if name in page_text:
                    found_doctors.append(name)
                    print(f"[STEP 3] ✓ 找到医生: {name}")
                else:
                    verification_errors.append(f"未找到医生: {name}")
                    print(f"[STEP 3] ✗ 未找到医生: {name}")
                    continue
                
                # 验证职称
                if title not in page_text:
                    verification_errors.append(f"医生 {name} 的职称 '{title}' 未找到")
                
                # 验证科室
                if department not in page_text:
                    verification_errors.append(f"医生 {name} 的科室 '{department}' 未找到")
            
            # 验证医生总数
            if len(found_doctors) != len(EXPECTED_DOCTORS):
                verification_errors.append(
                    f"医生数量不匹配: 期望 {len(EXPECTED_DOCTORS)} 位，实际找到 {len(found_doctors)} 位"
                )
            
            step_duration = int((os.times().elapsed - step_start) * 1000)
            
            if len(verification_errors) == 0:
                results.append(log_result(
                    3, "验证医生数据", "passed",
                    f"成功验证所有 {len(EXPECTED_DOCTORS)} 位医生的数据", step_duration
                ))
            else:
                error_msg = "; ".join(verification_errors)
                results.append(log_result(
                    3, "验证医生数据", "failed",
                    f"验证失败: {error_msg}", step_duration,
                    error=error_msg
                ))
            
            # 输出所有结果
            print("\n[Test Complete]")
            for r in results:
                print(f"  Step {r['step']}: {r['name']} - {r['status']}")
            
        except Exception as e:
            print(f"[ERROR] 测试执行出错: {str(e)}")
            # 尝试保存错误截图
            try:
                error_screenshot = os.path.join(SCREENSHOT_DIR, 'error-screenshot.png')
                page.screenshot(path=error_screenshot, full_page=True)
                print(f"[ERROR] 错误截图已保存: {error_screenshot}")
            except:
                pass
            raise
        finally:
            browser.close()

if __name__ == '__main__':
    main()
