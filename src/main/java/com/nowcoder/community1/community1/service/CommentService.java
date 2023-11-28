package com.nowcoder.community1.community1.service;


import com.nowcoder.community1.community1.dao.CommentMapper;
import com.nowcoder.community1.community1.entity.Comment;
import com.nowcoder.community1.community1.util.CommunityConstant;
import com.nowcoder.community1.community1.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class CommentService implements CommunityConstant {
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Autowired
    private DiscussServicePost discussServicePost;

    public List<Comment> findCommentsByEntity(int entityType,int entityId,int offset,int limit){
        return commentMapper.selectCommentsByEntity(entityType,entityId,offset,limit);
    }

    public int findCommentCount(int entityType,int entityId){
        return commentMapper.selectCountByEntity(entityType,entityId);
    }



    /**处理增加评论的方法
     * 添加注解隔离事务，添加传播机制
     * @param comment
     * @return
     */
    @Transactional(isolation = Isolation.READ_COMMITTED,propagation = Propagation.REQUIRED)
    public int addComment(Comment comment){
        //进行敏感词过滤
        if(comment == null){
            throw new IllegalArgumentException("参数不能为空！");
        }
        //对评论中的内容进行过滤
        //传入过滤之前的内容，进行字符转义
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveFilter.filter(comment.getContent()));
        int rows = commentMapper.insertComment(comment);

        /**
         * 因为评论有可能是帖子评论，也有可能是回复评论，这里只更新帖子评论的数目，而不更新回复评论的数量
         */
        //更新帖子评论数量
        if(comment.getEntityType()==ENTITY_TYPE_POST){
            int count = commentMapper.selectCountByEntity(comment.getEntityType(), comment.getEntityId());
            discussServicePost.updateCommentCount(comment.getEntityId(), count);
        }
        return rows;

    }

    public Comment findCommentById(int id){
        return commentMapper.selectCommentById(id);
    }

}
