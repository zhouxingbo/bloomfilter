package com.github.xingbo.spring.transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * desc:
 *
 * @author: bobo
 * createDate: 20-10-23
 */
public class UserController {

	@Autowired
	private UserService userService;
​
	@GetMapping("wrong1")
	public int wrong1(@RequestParam("name") String name){
		return userService.createUserWrong1(name);
	}

}
