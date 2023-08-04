package org.shiloh.parser;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.CaseUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.shiloh.common.constant.JdbcTypeMapping;
import org.shiloh.common.constant.SymbolConstants;
import org.shiloh.config.EntityGeneratorConfig;
import org.shiloh.pojo.Entity;
import org.shiloh.pojo.Field;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.shiloh.common.constant.AttributeNameConstants.ID;
import static org.shiloh.common.constant.AttributeNameConstants.REF;
import static org.shiloh.common.constant.ColumnConstants.MANDATORY;
import static org.shiloh.common.constant.ElementNameConstants.*;
import static org.shiloh.common.constant.XPathConstants.*;

/**
 * Pdm文档解析器
 *
 * @author shiloh
 * @date 2023/8/3 11:45
 */
@UtilityClass
@Slf4j
public class PdmParser {

    /**
     * 解析 pdm 文档，获取实体类列表
     *
     * @param url pdm 文件路径（xml 格式）
     * @return 实体类列表
     * @author shiloh
     * @date 2023/8/3 11:48
     */
    public static List<Entity> parse(URL url) throws DocumentException {
        final SAXReader saxReader = new SAXReader();
        // 读取 xml 文件，获取根元素
        final Document document = saxReader.read(url);
        return getEntities(document);
    }

    /**
     * 获取实体列表
     *
     * @param document 文档对象
     * @return 实体列表
     * @author shiloh
     * @date 2023/8/2 14:21
     */
    private static List<Entity> getEntities(Document document) {
        final Element rootElement = document.getRootElement();
        // 使用 XPATH 语法获取 Tables 元素
        final Element tablesEle = (Element) rootElement.selectSingleNode(TABLES_PATH);


        // 获取 Tables 元素下的所有 Table 元素
        final List<Element> tableElements = tablesEle.elements();
        final List<Entity> entities = new ArrayList<>(tableElements.size());
        // 获取所有表的信息
        final boolean tableNamePrefixIsNotBlank = StringUtils.isNotBlank(EntityGeneratorConfig.TABLE_NAME_PREFIX);
        tableElements.forEach(tableEle -> {
            final Entity entity = new Entity();
            // 获取表 ID
            entity.setTableId(tableEle.attributeValue(ID));
            // 获取表英文名称
            final String tableName = tableEle.elementText(CODE);
            entity.setTableName(
                    // 判断是否有表名前缀
                    tableNamePrefixIsNotBlank ? EntityGeneratorConfig.TABLE_NAME_PREFIX + tableName : tableName
            );
            entity.setName(CaseUtils.toCamelCase(
                    tableName, true, SymbolConstants.UNDERSCORE_CHAR
            ));
            // 获取注释
            entity.setComment(tableEle.elementText(COMMENT));
            // 获取创建人
            entity.setCreator(tableEle.elementText(CREATOR));
            // 获取创建日期
            final long creationDate = Long.parseLong(tableEle.elementText(CREATION_DATE));
            entity.setCreateDate(new Date(creationDate));
            // 设置主键列的 Id
            setPrimaryKeyColId(tableEle, entity);
            // 查找表下面的列
            final List<Field> fields = getFields(tableEle, entity.getPrimaryKeyColId());
            // 设置列所属的表名称
            fields.forEach(field -> field.setEntityName(tableName));

            entity.setFields(fields);

            // 获取要导入的包
            final Set<String> dependencies = fields.stream()
                    .map(Field::getTypeQualifiedName)
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.toSet());
            entity.setDependencies(dependencies);

            entities.add(entity);
        });
        return entities;
    }

    /**
     * 设置列主键 ID
     *
     * @param tableEle 表元素
     * @param entity   实体
     * @author shiloh
     * @date 2023/8/3 15:08
     */
    private static void setPrimaryKeyColId(Element tableEle, Entity entity) {
        final Element primaryKeyEle = (Element) tableEle.selectSingleNode(PRIMARY_KEY_PATH);
        if (primaryKeyEle != null) {
            final String primaryKeyRef = primaryKeyEle.attributeValue(REF);
            final Element primaryKeyColEle = (Element) tableEle.selectSingleNode(String.format(
                    PRIMARY_KEY_REF_COL_PATH, primaryKeyRef
            ));
            if (primaryKeyColEle != null) {
                final String primaryKeyColId = primaryKeyColEle.attributeValue(REF);
                entity.setPrimaryKeyColId(primaryKeyColId);
            }
        }
    }

    /**
     * 获取实体字段列表
     *
     * @param tableEle        指定表元素
     * @param primaryKeyColId 表主键的 ref Id
     * @return 字段列表
     * @author shiloh
     * @date 2022/7/14 16:26
     */
    private static List<Field> getFields(Element tableEle, String primaryKeyColId) {
        // 获取所有列元素
        final List<Element> colElements = tableEle.element(COLUMNS).elements();
        final List<Field> fields = new ArrayList<>(colElements.size());

        for (Element colEle : colElements) {
            final Field field = new Field();
            // 获取列的 ID
            final String colId = colEle.attributeValue(ID);
            field.setColId(colId);
            // 判断是否为主键
            field.setIsPrimaryKey(colId.equals(primaryKeyColId));
            // 获取列名称
            final String colName = colEle.elementText(CODE);
            field.setColName(colName);
            // 列名转驼峰，设置字段名称
            final String fieldName = CaseUtils.toCamelCase(
                    colName, false, SymbolConstants.UNDERSCORE_CHAR
            );
            field.setName(StringUtils.isBlank(fieldName) ? colName : fieldName);
            // 获取列注释
            field.setComment(colEle.elementText(COMMENT));
            // 获取列的数据类型
            final String colType = colEle.elementText(DATA_TYPE);
            field.setColType(colType);
            // 根据列数据类型设置Java字段类型和字段类型全限定名
            final JdbcTypeMapping jdbcTypeMapping = JdbcTypeMapping.getByType(colType);
            if (jdbcTypeMapping == null) {
                throw new RuntimeException(String.format("根据JDBC数据类型 [%s] 未找到对应的 Java 类型", colType));
            }
            field.setType(jdbcTypeMapping.getJavaType());
            field.setTypeQualifiedName(jdbcTypeMapping.getJavaTypeQualifiedName());
            // 获取列的字段长度
            final Element lengthEle = colEle.element(LENGTH);
            if (lengthEle != null) {
                field.setColLength(Integer.valueOf(lengthEle.getText()));
            }
            // 获取列是否必填的标识
            field.setMandatory(MANDATORY.equals(colEle.elementText(COLUMN_DOT_MANDATORY)));
            fields.add(field);
        }
        return fields;
    }
}
