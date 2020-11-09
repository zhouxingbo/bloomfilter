package com.github.xingbo.spring.transactional;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * desc:
 *
 * @author: bobo
 * createDate: 20-10-23
 */
public interface UserRepository extends JpaRepository<UserEntity, Long> {

		List<UserEntity> findByName(String name);
}
