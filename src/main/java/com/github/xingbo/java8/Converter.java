package com.github.xingbo.java8;

interface ConverterIN<F, T> {
    T convert(F from);
}
public class Converter{
    public static void main(String args[]){
        ConverterIN<String, Integer> converter = (from) -> Integer.valueOf(from);
        Integer converted = converter.convert("123");
        System.out.println(converted);    // 123

        ConverterIN<String, Integer> converter1 = Integer::valueOf;
        Integer converted1 = converter1.convert("123");
        System.out.println(converted1);   // 123

        String something = "";
        converter = something::indexOf;
        Integer java = converter.convert("Java");
        System.out.println(java);
    }
}
