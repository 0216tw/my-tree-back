package com.example.mytree.repository;

import java.util.List;

import com.example.mytree.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserRepository {

	int countByUserId(@Param("userId") String userId);

	int insertUser(User user);

	User findByUserId(@Param("userId") String userId);

	List<User> findAllUsers();

	int updateUser(User user);

	int deleteByUserId(@Param("userId") String userId);
}
