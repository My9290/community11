package com.nowcoder.community1.community1.dao;

import com.nowcoder.community1.community1.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper
public interface DiscussPostMapper {
    
    //查询当前用户发布的帖子，并分页显示
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit, int orderMode);
    /**
     * @Param注解用于给参数起别名
     * 如果只有一个参数，并且在<if>里使用，则必须加别名
     */
    //查询帖子的总数
    int selectDiscussPostRows(@Param("userId") int userId);

    //增加帖子的方法
    int insertDiscussPost(DiscussPost discussPost);

    //根据用户的ID查询帖子的内容
    DiscussPost selectDiscussPostById(int id);

    int updateCommentCount(int id,int commentCount);

    int updateType(int id,int type);
    int updateStatus(int id,int status);

    int updateScore(int id,double score);






























}
