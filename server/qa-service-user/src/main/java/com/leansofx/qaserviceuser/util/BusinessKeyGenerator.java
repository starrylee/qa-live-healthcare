package com.leansofx.qaserviceuser.util;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 患者业务键生成器。
 * 业务键格式：PAT-{yyyyMMdd}-{4位大写Base36随机串}，例如 PAT-20260714-8F3K。
 * 采用日期 + 纳秒/随机串组合，碰撞概率极低；建档冲突由唯一索引回退查重兜底。
 */
@Component
public class BusinessKeyGenerator {

    private static final String PREFIX = "PAT-";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;

    public String generate() {
        String datePart = LocalDate.now().format(DATE_FORMATTER);
        String randomPart = generateRandomPart();
        return PREFIX + datePart + "-" + randomPart;
    }

    private String generateRandomPart() {
        long nanos = System.nanoTime();
        int rand = ThreadLocalRandom.current().nextInt(1296); // 36^2，补充熵
        String raw = Long.toString(nanos, 36).toUpperCase() + Integer.toString(rand, 36).toUpperCase();
        // 取末尾 4 位，不足左补 0，固定长度以保持键格式一致
        return raw.length() >= 4 ? raw.substring(raw.length() - 4) : String.format("%4s", raw).replace(' ', '0');
    }
}
