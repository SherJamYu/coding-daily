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
public class Address {
    private String detail;

    public Address() {
        detail = "cq";
    }

    /**
     * @param detail
     */
    public Address(String detail) {
        super();
        this.detail = detail;
    }

    /**
     * @return the detail
     */
    public String getDetail() {
        return detail;
    }

    /**
     * @param detail the detail to set
     */
    public void setDetail(String detail) {
        this.detail = detail;
    }

}
