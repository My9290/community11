package com.nowcoder.community1.community1.dao;

import com.nowcoder.community1.community1.entity.Comment;
import com.nowcoder.community1.community1.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {
    //声明查询方法
    List<Comment> selectCommentsByEntity(int entityType,int entityId,int offset,int limit);
    //返回帖子的条数
    int selectCountByEntity(int entityType,int entityId);
    //增加评论的方法
    int insertComment(Comment comment);
    //根据评论的id查询评论实体
    Comment selectCommentById(int id);




}
