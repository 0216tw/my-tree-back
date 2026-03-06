package com.example.mytree.repository;

import java.util.List;

import com.example.mytree.domain.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PostRepository {

	int insertPost(Post post);

	Post findByPostNo(@Param("postNo") Long postNo);

	List<Post> findAllPosts();
}
