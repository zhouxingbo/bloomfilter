package com.github.xingbo.spring.transactional;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import static javax.persistence.GenerationType.AUTO;

/**
 * desc:
 *
 * @author: bobo
 * createDate: 20-10-23
 */
@Entity
@Data
public class UserEntity {

	@Id
	@GeneratedValue(strategy=AUTO)
	private Long id;
	private String name;

	public UserEntity(){
	}
	public UserEntity(String name){
		this.name=name;
	}
}
