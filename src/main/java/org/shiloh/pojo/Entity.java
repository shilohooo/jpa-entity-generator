package org.shiloh.pojo;

import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 实体
 *
 * @author shiloh
 * @date 2023/8/3 10:37
 */
@Data
public class Entity {
    /**
     * 实体名称
     */
    private String name;

    /**
     * 表名称
     */
    private String tableName;

    /**
     * 实体 / 表注释
     */
    private String comment;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 创建时间
     */
    private Date createDate;

    /**
     * 主键列的 ID 属性
     */
    private String primaryKeyColId;

    /**
     * 实体字段列表
     */
    @ToString.Exclude
    private List<Field> fields;

    /**
     * 依赖列表（需要导入的包）
     */
    private Set<String> dependencies;
}
