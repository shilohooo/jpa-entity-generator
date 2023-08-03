package org.shiloh.config;

import lombok.experimental.UtilityClass;

/**
 * 实体生成器配置
 *
 * @author shiloh
 * @date 2023/8/3 15:50
 */
@UtilityClass
public class EntityGeneratorConfig {
    /**
     * 实体模板存放路径
     */
    public static final String TEMPLATE_PATH = "src/main/resources/templates";

    /**
     * 实体模板文件名
     */
    public static final String TEMPLATE_NAME = "entity.ftl";

    /**
     * 源码存放路径
     */
    public static final String SOURCE_CODE_PATH = "src/main/java/";

    /**
     * 实体类包名
     */
    public static final String ENTITY_PACKAGE_NAME = "org.shiloh.entity";

    /**
     * 表名称统一前缀，为空则不生效
     */
    public static final String TABLE_NAME_PREFIX = "t_";
}
