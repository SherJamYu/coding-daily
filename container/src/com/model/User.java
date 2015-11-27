/**
 * 
 */
package com.model;

/**
 * 
 * @author <a href="zhangyu.xzy@aliyun.com">薛羽</a>
 * @version 1.1 2015年11月27日
 * @since 1.1
 */
public class User {
    private String  name; // user name
    private Address addr; // user address
    private int     age;

    public User() {
    }

    /**
     * @param name
     * @param addr
     */
    public User(String name, Address addr, int age) {
        super();
        this.name = name;
        this.addr = addr;
        this.age = age;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the addr
     */
    public Address getAddr() {
        return addr;
    }

    /**
     * @param addr the addr to set
     */
    public void setAddr(Address addr) {
        this.addr = addr;
    }

    /**
     * @return the age
     */
    public int getAge() {
        return age;
    }

    /**
     * @param age the age to set
     */
    public void setAge(int age) {
        this.age = age;
    }

}
