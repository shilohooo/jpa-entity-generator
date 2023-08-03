package org.shiloh.generator;

import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.shiloh.common.constant.SymbolConstants;
import org.shiloh.config.EntityGeneratorConfig;
import org.shiloh.pojo.Entity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 实体生成器
 *
 * @author shiloh
 * @date 2023/8/2 16:38
 */
@UtilityClass
@Slf4j
public class EntityGenerator {
    /**
     * Java源文件后缀
     */
    private static final String JAVA_FILE_SUFFIX = ".java";

    /**
     * 根据表信息生成实体类
     *
     * @param entities 表信息集合
     * @throws RuntimeException 生成失败抛出的异常
     * @author shiloh
     * @date 2023/8/2 16:38
     */
    public static void generate(List<Entity> entities) {
        final String packagePath = String.format(
                "%s%s%s",
                EntityGeneratorConfig.SOURCE_CODE_PATH,
                EntityGeneratorConfig.ENTITY_PACKAGE_NAME.replaceAll("\\.", SymbolConstants.SLASH),
                SymbolConstants.SLASH
        );
        try {
            // 如果指定的包不存在，则新建一个
            createPackageIfNotExists(packagePath);
            // 创建 freemarker 实例，指定 freemarker 版本
            final Configuration configuration = new Configuration(Configuration.VERSION_2_3_31);
            // 获取模板文件
            configuration.setDirectoryForTemplateLoading(new File(EntityGeneratorConfig.TEMPLATE_PATH));
            for (Entity entity : entities) {
                // 指定实体类文件输出文件
                try (final BufferedWriter bufferedWriter = new BufferedWriter(
                        new OutputStreamWriter(
                                Files.newOutputStream(Paths.get(packagePath + entity.getName() + JAVA_FILE_SUFFIX)),
                                StandardCharsets.UTF_8
                        )
                )) {
                    // 创建数据模型
                    final Map<String, Object> dataMap = new HashMap<>(16);
                    // 插入模板变量与对应的值
                    dataMap.put("package", EntityGeneratorConfig.ENTITY_PACKAGE_NAME);
                    dataMap.put("entity", entity);
                    dataMap.put("dependencies", entity.getDependencies());
                    // 加载模板文件
                    final Template template = configuration.getTemplate(EntityGeneratorConfig.TEMPLATE_NAME);
                    // 生成源代码文件
                    template.process(dataMap, bufferedWriter);
                    log.info("实体 [{}] 生成成功", entity.getName());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("实体类生成失败", e);
        }
    }

    /**
     * 如果指定的包不存在，则新建一个
     *
     * @param packagePath 包路径
     * @author shiloh
     * @date 2023/8/2 16:59
     */
    private static void createPackageIfNotExists(String packagePath) throws IOException {
        final Path path = Paths.get(packagePath);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
    }
}
