package com.nowcoder.community1.community1.service;

import com.nowcoder.community1.community1.dao.UserMapper;
import com.nowcoder.community1.community1.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public User findUserById(int id){
        return userMapper.selectById(id);


    }}
