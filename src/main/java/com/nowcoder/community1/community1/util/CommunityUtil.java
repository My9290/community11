package com.nowcoder.community1.community1.util;
import com.alibaba.fastjson.JSONObject;
import org.springframework.util.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CommunityUtil {
    //生成随机字符串，公有的静态方法，用到UUID工具来生成
    public static String generateUUID(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }
    //MD5算法加密
    //hello -> abc123def456，每次加密的结果都是同一个值，如果秘密简单，加密的结果也会简单，容易用密码本破解
    //因此需要给原密码之后加上随机字符串之后在进行加密，即 hello +3edrf ->abc3434jdsk
    public static String md5(String key){
        //Spring使用一个工具加密
        //先判断值是否为空
        if(StringUtils.isBlank(key)){
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    /**
     * getJSONString有三种实现方法，根据传入参数的不同设置不同的方法
     * 定义JSON用于实现前后端的交互
     * @param code
     * @param msg
     * @param map
     * @return
     */
    public static String getJSONString(int code, String msg, Map<String,Object> map){
        JSONObject json = new JSONObject();
        json.put("code",code);
        json.put("msg",msg);
        if(map!=null){
            for(String key:map.keySet()){
                json.put(key,map.get(key));
            }
        }
        return json.toJSONString();
    }
    public static String getJSONString(int code,String msg){
        return getJSONString(code,msg,null);
    }

    public static String getJSONString(int code){
        return getJSONString(code,null,null);
    }
    public static void main(String[] args){
        HashMap<String, Object> map = new HashMap<>();
        map.put("name","zhangsan");
        map.put("age",25);
        System.out.println(getJSONString(0,"ok",map));
    }



}
