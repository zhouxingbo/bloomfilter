package com.github.wxisme.bloomfilter.java8;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Lambda4 {

    static int outerStaticNum;
    int outerNum;

    void testScopes() {

        ConverterIN<String, Integer> stringConverter1 = (from) -> {
            outerNum = 23;
         return Integer.valueOf(from);

        };

        ConverterIN<String, Integer> stringConverter2 = (from) -> {
            outerStaticNum = 72;
            return Integer.valueOf(from);
        };
    }

    public static void main(String[] args){
        Predicate<String> predicate = (s) -> s.length() > 0;
        predicate.test("foo");              // true
        predicate.negate().test("foo");     // false

        Predicate<Boolean> nonNull = Objects::nonNull;
        Predicate<Boolean> isNull = Objects::isNull;

        Predicate<String> isEmpty = String::isEmpty;
        Predicate<String> isNotEmpty = isEmpty.negate();

        Function<String, Integer> toInteger = Integer::valueOf;
        Function<String, String> backToString = toInteger.andThen(String::valueOf);

        backToString.apply("123");     // "123"

        Supplier<Person> personSupplier = Person::new;
        personSupplier.get();   // new Person
    }


}