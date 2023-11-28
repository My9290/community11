package com.nowcoder.community1.community1.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
@Component
public class SensitiveFilter {
    //打印日志
    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    private static final String REPLACEMENT = "***";

    //根节点
    private TrieNode rootNode = new TrieNode();

    //添加初始化方法
    @PostConstruct
    public void init(){
        try (
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                //使用缓冲流读取数据
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                ) {
            //每次读取的值存放在这个变量中
            String keyword;
            while ((keyword = reader.readLine())!=null){
                //添加到前缀树
                this.addKeyword(keyword);

            }        }catch (IOException e){
            logger.error("加载敏感词文件失败："+e.getMessage());
        }
    }

    //将一个敏感词添加到前缀树当中去
    private void addKeyword(String keyword){
        //临时节点
        TrieNode tempNode = rootNode;
        for(int i= 0;i<keyword.length();i++){
            char c = keyword.charAt(i);
            //将该字符放到节点下面
            TrieNode subNode = tempNode.getSubNode(c);
            if(subNode == null){
                //初始化子节点，创建子节点
                subNode = new TrieNode();
                tempNode.addSubNode(c,subNode);
            }
            //子节点已经存在，指向子节点，进入下一轮循环
            tempNode = subNode;

            //设置结束标识
            if(i == keyword.length()-1){
                tempNode.setKeywordEnd(true);
            }
        }
    }

    /**

     * 过滤敏感词，算法实现过程
     * @param text 待过滤的文本
     * @return
     */
    public String filter(String text){
        if(StringUtils.isBlank(text)){
            //参数为空
            return null;
        }
        //声明三个指针
        //指针1
        TrieNode tempNode = rootNode;
        //指针2
        int begin = 0;
        //指针3
        int position = 0;
        //结果
        StringBuilder sb = new StringBuilder();
        //判断是否遍历到结尾
        while (position<text.length()){
            char c = text.charAt(position);
            //跳过符号
            if(isSymbol(c)){
                //若指针1处于根节点，将此符号计入结果，让指针2向下走一步
                if(tempNode ==rootNode){
                    sb.append(c);
                    begin++;
                }
                //无论符号在开头或中间，指针3都向下走
                /**
                 * 这里是只要遇到符号，就跳过进行一个字符继续判断
                 */
                position++;
                continue;
            }
            //该字符不是符号，继续检查它的下级节点
            tempNode = tempNode.getSubNode(c);
            /**
             * 有三种情况
             * 前两种是遍历到了叶子节点，要么是敏感词，要么不是
             * 第三种是遍历到中间字符
             */
            //如果遍历到叶子节点了，发现不是敏感词
            if(tempNode == null){
                //以begin开头的字符串不是敏感词，记录该字符，左指针右移一位，重新开始下一段字符串判断
                sb.append(text.charAt(begin));
                //进入下一个位置
                position = ++begin;
                //重新指向根节点
                tempNode = rootNode;
            }else if(tempNode.isKeywordEnd()){//true，说明遍历到敏感词了，敏感词是感觉最后一个字符确定的
                //发现敏感词，将begin-position字符串替换掉
                sb.append(REPLACEMENT);
                //进入下一个位置
                begin = ++position;
                tempNode = rootNode;
            }else{
                position++;
            }
        }
        //将最后一批字符计入结果
        sb.append(text.substring(begin));
        return sb.toString();

    }
    //判断字符是否为符号
    private boolean isSymbol(Character c){
        //0x2E80~0x9FFF 是东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c<0x2E80 || c>0x9FFF);


    }

    //构造前缀树
    private class TrieNode{
        //描述关键词结束的标识
        private boolean isKeywordEnd = false;
        //子节点(Key是下级字符，value是下级节点)
        private Map<Character,TrieNode> subNodes = new HashMap<>();

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

        //添加子节点的方法
        public void addSubNode(Character c,TrieNode node){
            subNodes.put(c,node);
        }

        //获取子节点
        public TrieNode getSubNode(Character c){
            return subNodes.get(c);
        }
    }


}
