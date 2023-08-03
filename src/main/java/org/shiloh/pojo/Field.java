package org.shiloh.pojo;

import lombok.Data;

/**
 * 实体字段
 *
 * @author shiloh
 * @date 2023/8/3 10:39
 */
@Data
public class Field {
    /**
     * 所属实体
     */
    private String entityName;

    /**
     * 列标签的 ID 属性的值
     * <p>
     * 用于设置关联时方便查找
     */
    private String colId;

    /**
     * 列名（下划线命名风格）
     */
    private String colName;

    /**
     * 实体字段名（驼峰命名风格，首字母小写）
     */
    private String name;

    /**
     * 实体字段类型
     */
    private String type;

    /**
     * 实体字段类型的全限定名
     * <p>
     * 数据类型属于自动导入的包时，该值为 {@code null}
     */
    private String typeQualifiedName;

    /**
     * 实体字段 / 列的注释
     */
    private String comment;

    /**
     * 列的数据类型
     */
    private String colType;

    /**
     * 列的字段长度
     */
    private Integer colLength;

    /**
     * 列的值是否必填
     */
    private Boolean mandatory;

    /**
     * 该列是否为主键
     */
    private Boolean isPrimaryKey;
}
