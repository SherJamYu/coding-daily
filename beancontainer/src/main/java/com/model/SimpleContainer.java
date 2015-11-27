/**
 * 
 */
package com.model;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import com.test.TestContainer;
import com.util.TypeConverter;

/**
 * a simple implement of bean container
 * 
 * @author <a href="zhangyu.xzy@aliyun.com">薛羽</a>
 * @version 1.1 2015年11月27日
 * @since 1.1
 */
public class SimpleContainer {

    private Map<String, Object> container = new HashMap<>();

    /**
     * 通过xml初始化容器
     * 
     * @param xmlPath
     */
    public SimpleContainer(String xmlPath) {
        SAXBuilder builder = new SAXBuilder();
        Document doc = null;
        try {
            doc = builder.build(TestContainer.class.getClassLoader().getResource(xmlPath).getPath());
            Element rootEle = doc.getRootElement();
            List<Element> beans = rootEle.getChildren("bean");
            resolve1(beans);
            resolve2(beans);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    /**
     * resolve for the first time,inject final value( 第一次解析,将引用的常量注入)
     * 
     * @param beans
     * @throws Exception
     */
    private void resolve1(List<Element> beans) throws Exception {
        for (Element bean : beans) {
            String className = bean.getAttributeValue("class"); // get class name
            String objName = bean.getAttributeValue("name"); // get bean name
            Class cls = Class.forName(className);
            Object obj = cls.newInstance();
            List<Element> properties = bean.getChildren("property"); // get peroperty tags
            for (Element child : properties) {

                // inject final filed value(注入常量值)
                String name = child.getAttribute("name") == null ? null : child.getAttribute("name").getValue();
                // get the config value(得到配置时的String类型值)
                String value = child.getAttribute("value") == null ? null : child.getAttribute("value").getValue();

                if (name != null && value != null) {
                    // according to reflect ,get the filed type(通过反射得到真实的类型)
                    Class paramType = cls.getDeclaredField(name).getType();
                    Method setmethod = cls.getDeclaredMethod("set" + name.substring(0, 1).toUpperCase() + name.substring(1), paramType);
                    Object param = value;
                    // if filed type is not String ,convert it (如果filed 类型不是String,转换类型)
                    if (paramType != String.class) {

                        Class convertCls = TypeConverter.class;
                        Method convertMet = convertCls.getMethod(
                                "convertTo" + paramType.getName().substring(0, 1).toUpperCase() + paramType.getName().substring(1), String.class);
                        param = convertMet.invoke(null, param);
                    }
                    // inject field value(注入filed 值)
                    setmethod.invoke(obj, param);
                }
            }
            container.put(objName, obj);
        }
    }

    /**
     * resolve for the second time ,inject bean (第二次解析，将引用的对象注入)
     * 
     * @param beans
     * @throws Exception
     */
    private void resolve2(List<Element> beans) throws Exception {
        for (Element bean : beans) {
            String objName = bean.getAttributeValue("name");
            String objClass = bean.getAttributeValue("class");
            List<Element> properties = bean.getChildren("property");
            for (Element property : properties) {

                String name = property.getAttribute("name") == null ? null : property.getAttribute("name").getValue();
                // get ref bean name(得到引用的bean的名称)
                String ref = property.getAttribute("ref") == null ? null : property.getAttribute("ref").getValue();
                if (name != null & ref != null) {

                    Class cls = Class.forName(objClass);
                    // get bean(得到bean的名称)
                    Object obj = container.get(objName);
                    // get bean set method(得到bean的set方法)
                    Method setMethod = cls.getMethod("set" + name.substring(0, 1).toUpperCase() + name.substring(1), container.get(ref).getClass());
                    // inject ref bean into bean (将引用的bean注入bean)
                    setMethod.invoke(obj, container.get(ref));
                }
            }
        }
    }

    public Object get(String name) {
        return container.get(name);
    }
    
}
