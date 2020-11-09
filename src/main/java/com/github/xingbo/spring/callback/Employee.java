package com.github.xingbo.spring.callback;

/**
 * desc:
 *
 * @author: bobo
 * createDate: 20-10-21
 */
public class Employee {

	public void callback(String msg,CallBack callBack){
		int msg1 = Integer.parseInt(msg);
		callBack.baoGao(msg1);
	}

	public static void main(String[] args) {
		Employee employee = new Employee();
		employee.callback("5", num -> {
			System.out.println(num);
		});
	}


}
