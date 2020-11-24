package com.github.xingbo.java8;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.stream.Collectors;
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

		List<Integer> list = Lists.newArrayList(3, 5, 1, 10, 8);
		List<Integer> sortedList = list.stream()
				.sorted(Integer::compareTo)
				.collect(Collectors.toList());
	}
}
