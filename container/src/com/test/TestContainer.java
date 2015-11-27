/**
 * 
 */
package com.test;

import com.model.SimpleContainer;
import com.model.User;

/**
 * 
 * @author <a href="zhangyu.xzy@aliyun.com">薛羽</a>
 * @version 1.1 2015年11月27日
 * @since 1.1
 */
public class TestContainer {
    public static void main(String[] args) throws Exception {
        SimpleContainer beans = new SimpleContainer("beans.xml");
        User u = (User) beans.get("user");
        System.out.println("user name:" + u.getName());
        System.out.println("age:" + u.getAge());
        System.out.println("address:" + u.getAddr().getDetail());
    }
}
