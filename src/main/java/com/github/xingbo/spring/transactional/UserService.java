package com.github.xingbo.spring.transactional;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * desc:
 *
 * @author: bobo
 * createDate: 20-10-23
 */
@Service
@Slf4j
public class UserService {

	@Autowired
	private UserRepository userRepository;

	public int createUserWrong1(String name) {
		try{
			this.createUserPrivate(new UserEntity(name));
		}
		catch(Exception ex) {
			log.error("create user failed because {}", ex.getMessage());
		}
		return userRepository.findByName(name).size();
	}

	//必须通过代理过的类从外部调用目标方法才能生效
	@Transactional
	public void createUserPrivate(UserEntity entity) {
		userRepository.save(entity);
		if(entity.getName().contains("test"))
			throw new RuntimeException("invalid username!");
	}

	//根据用户名查询用户数
	public int getUserCount(String name) {
		return userRepository.findByName(name).size();
	}

}
