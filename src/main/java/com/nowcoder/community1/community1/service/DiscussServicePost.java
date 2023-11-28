package com.nowcoder.community1.community1.service;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.nowcoder.community1.community1.dao.DiscussPostMapper;
import com.nowcoder.community1.community1.entity.DiscussPost;
import com.nowcoder.community1.community1.util.SensitiveFilter;
import org.attoparser.dom.INestableNode;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.omg.PortableServer.POA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class DiscussServicePost {
    private static final Logger logger = LoggerFactory.getLogger(DiscussServicePost.class);
    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Value("${caffeine.posts.max-size}")
    private int maxSize;
    @Value("${caffeine.posts.expire-second}")
    private int expireSeconds;

    //Caffeine核心接口：Cache,LoadingCache(同步),AsyncloadCache(异步，支持并发)
    //帖子列表的缓存
    private LoadingCache<String,List<DiscussPost>> postListCache;
    //帖子总数缓存
    private LoadingCache<Integer,Integer> postRowsCache;

    //初始化缓存
    @PostConstruct
    public void init(){
        //初始化帖子列表缓存
        postListCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<String, List<DiscussPost>>() {
                    @Override
                    public @Nullable List<DiscussPost> load(@NonNull String key) throws Exception {
                       //缓存数据的来源
                        if(key==null || key.length() == 0){
                            throw new IllegalArgumentException("参数错误！");
                        }
                        String[] params = key.split(":");
                        if(params == null || params.length!=2){
                            throw new IllegalArgumentException(params[0]);
                        }
                        int offset = Integer.valueOf(params[0]);
                        int limit = Integer.valueOf(params[1]);

                        //二级缓存：Redis->mysql
                        logger.debug("load post list from DB.");

                        return discussPostMapper.selectDiscussPosts(0,offset,limit,1);
                    }
                });
        //初始化帖子总数缓存
        postRowsCache=Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireSeconds,TimeUnit.SECONDS)
                .build(new CacheLoader<Integer, Integer>() {
                    @Override
                    public @Nullable Integer load(@NonNull Integer key) throws Exception {
                        logger.debug("load post list from DB.");
                        return discussPostMapper.selectDiscussPostRows(key);
                    }
                });
    }



    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit,int orderMode){
//        if(userId ==0 && orderMode==1){
//            return postListCache.get(offset+":"+limit);
//        }
        logger.debug("load post list from DB.");
        return discussPostMapper.selectDiscussPosts(userId,offset,limit,orderMode);
    }

    public int findDiscussPostRows(int userId){
//        if(userId==0){
//            return postRowsCache.get(userId);
//        }
        logger.debug("load post rows from DB.");
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
    public int updateType(int id,int type){
        return discussPostMapper.updateType(id,type);
    }
    public int updateStatus(int id,int status){
        return discussPostMapper.updateStatus(id,status);

    }
    public int updateScore(int id,double score){
        return discussPostMapper.updateScore(id,score);
    }










}
