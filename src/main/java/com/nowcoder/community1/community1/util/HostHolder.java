package com.nowcoder.community1.community1.util;

import com.nowcoder.community1.community1.entity.User;
import org.springframework.stereotype.Component;

/**
 * 持有用户信息，用于代替session对象
 * session对象是持有数据并且是线程隔离的
 */
@Component
public class HostHolder {
    private ThreadLocal<User> users = new ThreadLocal<>();
    public void setUser(User user){
        users.set(user);
    }
    public User getUser(){
        return users.get();
    }
    //清理资源
    public void clear(){
        users.remove();
    }



}
