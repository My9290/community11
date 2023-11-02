package com.nowcoder.community1.community1.service;

import com.nowcoder.community1.community1.dao.DiscussPostMapper;
import com.nowcoder.community1.community1.entity.DiscussPost;
import com.nowcoder.community1.community1.util.SensitiveFilter;
import org.omg.PortableServer.POA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class DiscussServicePost {
    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private SensitiveFilter sensitiveFilter;



    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit){
        return discussPostMapper.selectDiscussPosts(userId,offset,limit);
    }

    public int findDiscussPostRows(int userId){
        return discussPostMapper.selectDiscussPostRows(userId);
    }

    //增加帖子的方法
    public int addDiscussPost(DiscussPost post){
        //首先判断，帖子不能为空
        if(post==null){
            throw new IllegalArgumentException("帖子参数不能为空！");
        }

        /**
         * 这里先对特殊字符进行转义，然后使用过滤器过滤掉特殊字符
         * 转义HTML标记，转义使用一个工具HtmlUtils
         * 分别对标题和内容进行处理
         */
        //转义HTML标记
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));

        //过滤敏感词
        post.setTitle(sensitiveFilter.filter(post.getTitle()));
        post.setContent(sensitiveFilter.filter(post.getContent()));

        //将最终处理好的结果放进添加帖子的功能函数中，实现增加帖子
        return discussPostMapper.insertDiscussPost(post);
    }

    //根据用户Id查询帖子的内容
    public DiscussPost findDiscussPostById(int id){
        DiscussPost post = discussPostMapper.selectDiscussPostById(id);
        System.out.println(post.getTitle());
        return discussPostMapper.selectDiscussPostById(id);

    }

    //添加评论的数量
    public int updateCommentCount(int id,int commentCount){
        return discussPostMapper.updateCommentCount(id,commentCount);
    }

}
