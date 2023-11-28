package com.nowcoder.community1.community1.util;


import com.nowcoder.community1.community1.entity.User;
import org.apache.kafka.common.protocol.types.Field;

public class RedisKeyUtil {
    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    private static final String PREFIX_USER_LIKE = "like:user";
    //记录两份数据，一份用于被关注者的页面变化，另一份用于关注者的页面变化
    private static final String PREFIX_FOLLOWER = "follower";
    private static final String PREFIX_FOLLOWEE = "followee";
    //存储验证码
    private static final String PREFIX_KAPTCHA = "kaptcha";
    //存储凭证
    private static final String PREFIX_TICKET = "ticket";
    private static final String PREFIX_USER = "user";

    private static final String PREFIX_UV = "uv";
    private static final String PREFIX_DAU = "dau";
    private static final String PREFIX_POST= "post";



    //某个实体的赞
    //like:entity:entityType:entityId -> set(userId)
    public static String getEntityLikeKey(int entityType,int entityId){
        return PREFIX_ENTITY_LIKE+SPLIT+entityType+SPLIT+entityId;
    }

    //某个用户的赞
    //like:user:userId -> int
    public static String getUserLikeKey(int userId){
        return PREFIX_USER_LIKE + SPLIT + userId;
    }

    //某个用户关注的实体
    //userId：谁关注的，指的是当前用户；entityType：关注的是哪个类型
    //followee:userId:entityType ->zset(entityId,now)
    public static String getFolloweeKey(int userId,int entityType){
        return PREFIX_FOLLOWEE+SPLIT+userId+SPLIT+entityType;
    }

    //某个实体拥有的粉丝
    //follower:entityType:entityId -> zset(entityId,now)
    public static String getFollowerKey(int entityType,int entityId){
        return PREFIX_FOLLOWER+SPLIT+entityType+SPLIT+entityId;
    }

    //登陆验证码

    /**
     * 每个用户有各自对应的验证码，在用户访问登录页面的时候，给他发一个凭证（随机生成的字符串），并把该凭证存到cookie里，
     * 这个字符串来标识这个用户，然后很快让字符串过期就行
     * 验证码是在登录时用的
     * @return
     */
    public static String getKaptchaKey(String owner){
        return PREFIX_KAPTCHA+SPLIT+owner;
    }
    //登录的凭证
    public static String getTicketKey(String ticket){
        return PREFIX_TICKET+SPLIT+ticket;

    }

    //用户
    public static String getUserKey(int userId){
        return PREFIX_USER+SPLIT+userId;
    }

    //单日uv
    public static String getUVKey(String date){
        return PREFIX_UV+SPLIT+date;
    }
    //区间uv
    public static String getUVkey(String startDate,String endDate){
        return PREFIX_UV+SPLIT+startDate+SPLIT+endDate;
    }
    //单日活跃用户
    public static String getDAUKey(String date){
        return PREFIX_DAU+SPLIT+date;
    }
    //区间活跃用户
    public static String getDAUKey(String startDate,String endDate){
        return PREFIX_DAU+SPLIT+startDate+SPLIT+endDate;
    }

    //帖子分数
    public static String getPostScoreKey(){
        return PREFIX_POST+SPLIT+"score";
    }



}
























