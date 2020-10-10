package com.github.wxisme.bloomfilter.typeclass;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 上界<? extends T>不能往里存，只能往外取
 */
public class test {
    public static void main(String[] args) {
        List<? extends Father> list = new LinkedList<>();
        //list.add(new Son());

        List<? extends Father> list1 = new LinkedList<Son>();
        //list1.add(new Son());

        List<? extends Father> list2 = new ArrayList<>();
        Father father = list2.get(0);//读取出来的东西只能存放在Father或它的基类里。
        Object object = list2.get(0);//读取出来的东西只能存放在Father或它的基类里。
        Human human = list2.get(0);//读取出来的东西只能存放在Father或它的基类里。
        Son son = (Son)list2.get(0);

        //super只能添加Father和Father的子类，不能添加Father的父类,读取出来的东西只能存放在Object类里
        List<? super Father> list3 = new ArrayList<>();
        list3.add(new Father());
        //list3.add(new Human());//compile error
        list3.add(new Son());
        //Father person1 = list3.get(0);//compile error
        //Son son = list3.get(0);//compile error
        Object object1 = list3.get(0);
    }

    public <T> List<T> fill(T t){
        List<T> list = new ArrayList<>();
        list.add(t);
        return list;
    }
}
class Human{
}
class Father extends Human{
}
class Son extends Father{
}
class LeiFeng extends Father {

}