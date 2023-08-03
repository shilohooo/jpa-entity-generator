package ${package};

import java.io.Serializable;
import javax.persistence.*;
import lombok.Setter;
import lombok.Getter;
import lombok.ToString;
<#list dependencies as dependence>
import ${dependence};
</#list>

/**
 * ${entity.comment}
 *
 * @author ${entity.creator}
 * @date ${.now?string("yyyy/MM/dd HH:mm")}
 */
@Entity
@Setter
@Getter
@ToString
@Table(name = "${entity.tableName}")
@org.hibernate.annotations.Table(appliesTo = "${entity.tableName}", comment = "${entity.comment}")
public class ${entity.name} implements Serializable {
    private static final long serialVersionUID = 1L;

<#list entity.fields as field>
    /*
     * ${field.comment}
     */
    <#if field.isPrimaryKey>
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    </#if>
    @Column(name = "${field.colName}", columnDefinition = "${field.colType} ${field.mandatory?string('not null', '')} comment '${field.comment}'")
    private ${field.type} ${field.name};
    <#if field_has_next>

    </#if>
</#list>
}
