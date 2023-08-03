package org.shiloh.test;

import org.junit.Test;
import org.shiloh.config.EntityGeneratorConfig;
import org.shiloh.generator.EntityGenerator;
import org.shiloh.parser.PdmParser;

import java.net.URL;

/**
 * 实体生成测试
 *
 * @author shiloh
 * @date 2023/8/3 10:46
 */
public class EntityGenerateTests {
    /**
     * PDM 文件名
     */
    private static final String PDM_FILE_NAME = "test2.xml";

    /**
     * 测试实体生成
     *
     * @author shiloh
     * @date 2023/8/3 11:49
     */
    @Test
    public void testGenerateEntities() throws Exception {
        final URL url = EntityGenerateTests.class
                .getClassLoader()
                .getResource(PDM_FILE_NAME);
        EntityGenerator.generate(EntityGeneratorConfig.ENTITY_PACKAGE_NAME, PdmParser.parse(url));
    }
}
