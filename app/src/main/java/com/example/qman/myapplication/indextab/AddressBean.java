package com.example.qman.myapplication.indextab;

/**
 * Created by Qman on 2017/1/25.
 */

public class AddressBean {
    public String name;

    public AddressBean(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPickerViewText() {
        //这里是固定写法,依赖库会根据集合内容使用反射显示文本到省份栏的文本框中
        return this.name;
    }

    @Override
    public String toString() {
        return "AddressBean{" +
                "name='" + name + '\'' +
                '}';
    }
}
