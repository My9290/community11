package com.nowcoder.community1.community1.service;

import com.nowcoder.community1.community1.util.RedisKeyUtil;
import org.omg.CORBA.PUBLIC_MEMBER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

@Service
public class LikeService {

    @Autowired
    private RedisTemplate redisTemplate;
    //点赞
    public void like(int userId,int entityType,int entityId,int entityUserId){
//        //获取某个实体赞
//        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
//        //首先判断该用户有没有点过赞
//        Boolean isMember = redisTemplate.opsForSet().isMember(entityLikeKey, userId);
//        if(isMember){
//            //已经点过赞了，再点就是取消点赞操作
//            redisTemplate.opsForSet().remove(entityLikeKey,userId);
//        }else{
//            //没有点过赞，首次进入就是点赞
//            redisTemplate.opsForSet().add(entityLikeKey,userId);
//        }
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType,entityId);
                String userLikeKey = RedisKeyUtil.getUserLikeKey(entityUserId);

                //判断用户是否点过赞
                boolean isMember = operations.opsForSet().isMember(entityLikeKey,userId);
                //启动事务
                operations.multi();
                if(isMember){
                    //已经点过赞了
                    operations.opsForSet().remove(entityLikeKey,userId);
                    //总的点赞数目减一
                    operations.opsForValue().decrement(userLikeKey);
                }else{
                    //首次点赞，表示点赞操作
                    operations.opsForSet().add(entityLikeKey,userId);
                    //增加点赞数目
                    operations.opsForValue().increment(userLikeKey);
                }
                //提交事务
                return operations.exec();
            }
        });
    }

    //查询某实体点赞的数量
    public long findEntityLikeCount(int entityType,int entityId){
        //获取某个实体赞
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().size(entityLikeKey);
    }

    //查询某人对某实体的点赞状态
    public int findEntityLikeStatus(int userId,int entityType,int entityId){
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType,entityId);
        return redisTemplate.opsForSet().isMember(entityLikeKey,userId)? 1:0;
    }

    //查询某个用户获得的赞的总数
    public int findUserLikeCount(int userId){
        String userLikeKey = RedisKeyUtil.getUserLikeKey(userId);
        Integer count = (Integer) redisTemplate.opsForValue().get(userLikeKey);
        //如果count为空，表明该用户没有收到赞，否则返回赞的总数
        return count==null? 0:count.intValue();

    }
}
