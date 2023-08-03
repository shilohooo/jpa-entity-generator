package org.shiloh.common.constant;

/**
 * XPath 语法常量
 *
 * @author shiloh
 * @date 2022/7/14 17:10
 */
public final class XPathConstants {
    private XPathConstants() {
    }

    /**
     * 查找根元素下的 Tables 元素
     */
    public static final String TABLES_PATH = "//c:Tables";

    /**
     * 查找表主键元素
     */
    public static final String PRIMARY_KEY_PATH = "./c:PrimaryKey//o:Key";

    /**
     * 查找主键列关联元素
     */
    public static final String PRIMARY_KEY_REF_COL_PATH = "./c:Keys/o:Key[@Id='%s']/c:Key.Columns/o:Column[1]";
}
