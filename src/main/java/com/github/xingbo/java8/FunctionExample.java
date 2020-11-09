package com.github.xingbo.java8;

import java.util.stream.Stream;

/**
 * desc:
 *
 * @author: bobo
 * createDate: 20-11-9
 */
public class FunctionExample {


	public static void main(String[] args){
		int result = Stream.of(1, 2, 3, 4)
				.reduce(1, (acc, element) -> acc * element);
		System.out.print("result:" + result);
	}
}
