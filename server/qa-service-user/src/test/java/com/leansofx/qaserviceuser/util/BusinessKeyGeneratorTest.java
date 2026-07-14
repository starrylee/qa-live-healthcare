package com.leansofx.qaserviceuser.util;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BusinessKeyGeneratorTest {

    @Test
    void generate_100Keys_allUniqueAndPrefixed() {
        BusinessKeyGenerator generator = new BusinessKeyGenerator();
        Set<String> keys = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            String key = generator.generate();
            assertTrue(key.startsWith("PAT-"), "业务键应以 PAT- 开头，实际: " + key);
            assertTrue(key.matches("PAT-\\d{8}-[0-9A-Z]{4}"), "业务键格式应为 PAT-yyyyMMdd-XXXX，实际: " + key);
            keys.add(key);
        }
        assertEquals(100, keys.size(), "连续生成的 100 个业务键应全部唯一");
    }
}
