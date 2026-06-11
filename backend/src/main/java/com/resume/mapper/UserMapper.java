package com.resume.mapper;

import com.resume.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * Mapper for user authentication.
 */
@Mapper
public interface UserMapper {

    User findByUsername(@Param("username") String username);

    int insert(User user);
}
