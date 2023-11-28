package com.nowcoder.community1.community1.dao;

import com.nowcoder.community1.community1.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

@Mapper
@Deprecated
public interface LoginTicketMapper {
    @Insert({"insert into login_ticket(user_id,ticket,status,expired) ",
            "values(#{userId},#{ticket},#{status},#{expired})"})
    //下面这个注解用于自动生成主键，并声明注入给id
    @Options(useGeneratedKeys = true,keyProperty = "id")
    int insertLoginTicket(LoginTicket loginTicket);
    @Select({
            "select id,user_id,ticket,status,expired ",
            "from login_ticket where ticket=#{ticket}"
    })
    LoginTicket selectByTicket(String ticket);

    //下面是生成了动态sql语句，有if标签的话，外面必须套上<script>标签
    @Update({
            "<script>",
            "update login_ticket set status=#{status} where ticket=#{ticket }",
            "<if test=\"ticket!=null\">",
            "and 1=1 ",
            "</if>",
            "</script>"
    })
    int updateStatus(String ticket,int status);


}
