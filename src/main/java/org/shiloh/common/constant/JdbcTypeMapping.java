package org.shiloh.common.constant;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JDBC 字段类型映射枚举
 *
 * @author shiloh
 * @date 2023/8/3 11:00
 */
@Getter
public enum JdbcTypeMapping {
    /**
     * bit 类型
     */
    BIT("bit", Boolean.class.getSimpleName(), null),

    /**
     * 整数类型
     */
    TINYINT("tinyint", Integer.class.getSimpleName(), null),
    SMALLINT("smallint", Integer.class.getSimpleName(), null),
    INT("int", Integer.class.getSimpleName(), null),
    BIGINT("bigint", Long.class.getSimpleName(), null),

    /**
     * 浮点数类型
     */
    FLOAT("float", Float.class.getSimpleName(), null),
    DOUBLE("double", Double.class.getSimpleName(), null),
    DECIMAL("decimal", BigDecimal.class.getSimpleName(), BigDecimal.class.getName()),

    /**
     * 文本类型
     */
    CHAR("char", String.class.getSimpleName(), null),
    VARCHAR("varchar", String.class.getSimpleName(), null),
    TEXT("text", String.class.getSimpleName(), null),
    LONGTEXT("longtext", String.class.getSimpleName(), null),
    JSON("json", String.class.getSimpleName(), null),

    /**
     * 日期类型
     */
    TIME("time", Date.class.getSimpleName(), Date.class.getName()),
    DATE("date", Date.class.getSimpleName(), Date.class.getName()),
    DATETIME("datetime", Date.class.getSimpleName(), Date.class.getName()),
    TIMESTAMP("timestamp", Date.class.getSimpleName(), Date.class.getName());

    /**
     * JDBC 数据类型
     */
    private final String type;

    /**
     * Java 字段数据类型
     */
    private final String javaType;

    /**
     * Java 字段数据类型全限定名
     */
    private final String javaTypeQualifiedName;

    /**
     * 实例缓存：key = JDBC 数据类型，value = 字段类型映射枚举
     */
    private static final Map<String, JdbcTypeMapping> INSTANCE_CACHE = new HashMap<>(values().length);

    static {
        for (JdbcTypeMapping jdbcTypeMapping : values()) {
            INSTANCE_CACHE.put(jdbcTypeMapping.type, jdbcTypeMapping);
        }
    }

    JdbcTypeMapping(String type, String javaType, String javaTypeQualifiedName) {
        this.type = type;
        this.javaType = javaType;
        this.javaTypeQualifiedName = javaTypeQualifiedName;
    }

    /**
     * 根据 JDBC 数据类型获取字段类型映射枚举
     *
     * @param colType JDBC 数据类型
     * @return 字段类型映射枚举
     * @author shiloh
     * @date 2023/8/3 11:30
     */
    public static JdbcTypeMapping getByType(String colType) {
        // 如果是带有长度的类型，则先截取类型
        final String type = colType.contains(SymbolConstants.LEFT_BRACE)
                ? StringUtils.substringBefore(colType, SymbolConstants.LEFT_BRACE)
                : colType;
        return INSTANCE_CACHE.get(type);
    }
}
