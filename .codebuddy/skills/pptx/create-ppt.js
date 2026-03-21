const pptxgen = require('pptxgenjs');
const html2pptx = require('/Users/nebula/repo/ai-training/homework-320/qa-live-healthcare/.codebuddy/skills/pptx/scripts/html2pptx');
const fs = require('fs');
const path = require('path');

async function createPresentation() {
    const pptx = new pptxgen();
    pptx.layout = 'LAYOUT_16x9';
    pptx.author = 'QA Live Healthcare Test Team';
    pptx.title = '医生列表页面测试报告';
    pptx.subject = '001-Doctor-List-Page Test Report';

    const slidesDir = '/tmp/pptx-report/slides';
    
    // Slide 1: 封面
    console.log('Creating slide 1: Title');
    await html2pptx(path.join(slidesDir, 'slide1.html'), pptx);

    // Slide 2: 测试概述
    console.log('Creating slide 2: Overview');
    await html2pptx(path.join(slidesDir, 'slide2.html'), pptx);

    // Slide 3: 测试步骤
    console.log('Creating slide 3: Test Steps');
    await html2pptx(path.join(slidesDir, 'slide3.html'), pptx);

    // Slide 4: 医生数据表格
    console.log('Creating slide 4: Doctor Data Table');
    const { slide: slide4, placeholders: placeholders4 } = await html2pptx(
        path.join(slidesDir, 'slide4.html'), 
        pptx
    );
    
    // 添加医生数据表格
    const tableData = [
        [
            { text: "序号", options: { fill: { color: "5EA8A7" }, color: "FFFFFF", bold: true, align: "center" } },
            { text: "医生姓名", options: { fill: { color: "5EA8A7" }, color: "FFFFFF", bold: true, align: "center" } },
            { text: "职称", options: { fill: { color: "5EA8A7" }, color: "FFFFFF", bold: true, align: "center" } },
            { text: "科室", options: { fill: { color: "5EA8A7" }, color: "FFFFFF", bold: true, align: "center" } },
            { text: "在线状态", options: { fill: { color: "5EA8A7" }, color: "FFFFFF", bold: true, align: "center" } }
        ],
        ["1", "张伟医生", "主任医师", "心内科", "✅ 在线"],
        ["2", "李娜医生", "副主任医师", "儿科", "✅ 在线"],
        ["3", "王强医生", "主治医师", "骨科", "✅ 在线"],
        ["4", "刘敏医生", "主任医师", "妇产科", "⭕ 离线"],
        ["5", "陈杰医生", "副主任医师", "消化内科", "✅ 在线"]
    ];
    
    if (placeholders4.length > 0) {
        const ph = placeholders4[0];
        slide4.addTable(tableData, {
            x: ph.x, y: ph.y, w: ph.w, h: ph.h,
            colW: [1, 2.5, 2.5, 2.5, 2],
            border: { pt: 0.5, color: "CCCCCC" },
            fill: { color: "F9F9F9" },
            align: "center",
            valign: "middle",
            fontSize: 12
        });
    }

    // Slide 5: 测试截图
    console.log('Creating slide 5: Screenshots');
    const { slide: slide5, placeholders: placeholders5 } = await html2pptx(
        path.join(slidesDir, 'slide5.html'), 
        pptx
    );
    
    // 添加截图
    const screenshotDir = '/Users/nebula/repo/ai-training/homework-320/qa-live-healthcare/test/e2e/001-doctor-list-page/reports/001-doctor-list-page-result-20260321-115520/assets/screenshots';
    
    if (placeholders5.length >= 2) {
        const ph1 = placeholders5[0];
        const ph2 = placeholders5[1];
        
        // 第一张截图 - 首页
        if (fs.existsSync(path.join(screenshotDir, 'step-01-homepage.png'))) {
            slide5.addImage({
                path: path.join(screenshotDir, 'step-01-homepage.png'),
                x: ph1.x, y: ph1.y, w: ph1.w, h: ph1.h,
                sizing: 'contain'
            });
        }
        
        // 第二张截图 - 医生列表
        if (fs.existsSync(path.join(screenshotDir, 'step-02-doctor-list.png'))) {
            slide5.addImage({
                path: path.join(screenshotDir, 'step-02-doctor-list.png'),
                x: ph2.x, y: ph2.y, w: ph2.w, h: ph2.h,
                sizing: 'contain'
            });
        }
    }

    // Slide 6: 总结
    console.log('Creating slide 6: Summary');
    await html2pptx(path.join(slidesDir, 'slide6.html'), pptx);

    // 保存 PPT
    const outputPath = '/Users/nebula/repo/ai-training/homework-320/qa-live-healthcare/test/e2e/001-doctor-list-page/reports/001-Doctor-List-Page-Test-Report.pptx';
    await pptx.writeFile({ fileName: outputPath });
    console.log(`Presentation created successfully: ${outputPath}`);
}

createPresentation().catch(err => {
    console.error('Error creating presentation:', err);
    process.exit(1);
});
