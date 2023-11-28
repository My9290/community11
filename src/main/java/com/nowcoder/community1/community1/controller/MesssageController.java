package com.nowcoder.community1.community1.controller;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.community1.community1.dao.MessageMapper;
import com.nowcoder.community1.community1.entity.Message;
import com.nowcoder.community1.community1.entity.Page;
import com.nowcoder.community1.community1.entity.User;
import com.nowcoder.community1.community1.service.MessageService;
import com.nowcoder.community1.community1.service.UserService;
import com.nowcoder.community1.community1.util.CommunityConstant;
import com.nowcoder.community1.community1.util.CommunityUtil;
import com.nowcoder.community1.community1.util.HostHolder;
import org.omg.CORBA.OBJECT_NOT_EXIST;
import org.omg.CORBA.OBJ_ADAPTER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;
import sun.applet.AppletResourceLoader;

import java.lang.reflect.Array;
import java.util.*;

@Controller
public class MesssageController implements CommunityConstant {
    @Autowired
    private MessageService messageService;
    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    //获取私信列表信息
    @RequestMapping(path="/letter/list",method = RequestMethod.GET)
    /**
     * model：用于建立更新页面的模版，
     *  page：用于分页
     */
    public String getLetterList(Model model, Page page){

        //获取当前登录的用户信息
        User user = hostHolder.getUser();
        //建立分页信息
        page.setLimit(5);
        page.setPath("/letter/list");
        page.setRows(messageService.findConversationCount(user.getId()));

        //获取会话列表
        List<Message> conversationList = messageService.findConversations(
                user.getId(), page.getOffset(), page.getLimit());
        //根据会话列表信息获取每个会话的具体信息
        ArrayList<Map<String, Object>> conversations = new ArrayList<Map<String, Object>>();
        if(conversationList!=null){
            //获取每个会话信息
            for(Message message:conversationList){
                HashMap<String, Object> map = new HashMap<>();
                map.put("conversation",message);
                map.put("letterCount",messageService.findLetterCount(message.getConversationId()));
                map.put("unreadCount",messageService.findLetterUnreadCount(user.getId(),message.getConversationId()));
                //获取对方的信息
                int targetId = user.getId() == message.getFormId()? message.getToId():message.getFormId();
                map.put("target",userService.findUserById(targetId));

                conversations.add(map);
            }
        }
        model.addAttribute("conversations",conversations);

        //查询未读消息数量
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(),null);
        model.addAttribute("letterUnreadCount",letterUnreadCount);

        //查询未读通知数量
        int notiveUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("noticeUnreadCount",notiveUnreadCount);
        return "/site/letter";
    }

    /**
     * 显示信息详情，这里是查询操作，所以使用get
     * @return
     */
    @RequestMapping(path="/letter/detail/{conversationId}",method = RequestMethod.GET)
    public String getLetterDetail(@PathVariable("conversationId") String conversationId,Page page,Model model){
        //分页信息
        page.setLimit(5);
        page.setPath("/letter/detail/"+conversationId);
        page.setRows(messageService.findLetterCount(conversationId));

        //获取每个会话中的私信详情
        List<Message> letterList = messageService.findLetters(conversationId, page.getOffset(), page.getLimit());
        //将每一条私信封装在map集合中
        ArrayList<Map<String, Object>> letters = new ArrayList<>();
        if(letterList!=null){
            for(Message message:letterList){
                HashMap<String, Object> map = new HashMap<>();
                map.put("letter",message);
                map.put("fromUser",userService.findUserById(message.getFormId()));

                letters.add(map);
            }
        }
        //将每条会话的所有私信放进模版中
        model.addAttribute("letters",letters);
        //查询私信的对象
        model.addAttribute("target",getLetterTarget(conversationId));
        //设置消息已读
        //获取会话中未读的消息数
        List<Integer> ids = getLetterIds(letterList);
        if(!ids.isEmpty()){
            messageService.readMessage(ids);
        }

        return "/site/letter-detail";

    }

    //获取当前会话的聊天对象
    private User getLetterTarget(String conversationId){
        String[] ids = conversationId.split("_");
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);
        if(hostHolder.getUser().getId()==id0){
            return userService.findUserById(id1);
        }else{
            return userService.findUserById(id0);
        }

    }

    //得到未读的消息id
    private List<Integer> getLetterIds(List<Message> letterList){
        ArrayList<Integer> ids = new ArrayList<>();
        if(letterList!=null){
            for(Message message:letterList){
                //确定当前登陆用户是接收消息的一方
                if(hostHolder.getUser().getId() == message.getToId() && message.getStatus()==0){
                    ids.add(message.getId());
                }
            }
        }
        return ids;
    }

    //发送消息核心代码
    @RequestMapping(path="/letter/send",method = RequestMethod.POST)
    @ResponseBody
    /**
     * 发给谁。发送内容
     */
    public String sendLetter(String toName,String content){
                //判断用户是否存在
        User target = userService.findUserByName(toName);
        if(target==null){
            return CommunityUtil.getJSONString(1,"目标用户不存在");
        }

        /**
         *  找到目标用户，开始设计消息内容
         */
        Message message = new Message();
        //发送人为当前登陆用户
        message.setFormId(hostHolder.getUser().getId());
        message.setToId(target.getId());
        //创建发送接收者字符串
        if(message.getFormId()<message.getToId()){
            message.setConversationId(message.getFormId()+"_"+message.getToId());
        }else{
            message.setConversationId(message.getToId()+"_"+message.getFormId());
        }

        message.setContent(content);
        message.setCreateTime(new Date());

        //将该条消息插入
        messageService.addMessage(message);
        return CommunityUtil.getJSONString(0);
    }

    /**
     * 系统发送通知
     */
    @RequestMapping(path = "/notice/list",method = RequestMethod.GET)
    public String getNoticeList(Model model){
        User user = hostHolder.getUser();
        //查询评论类通知
        Message message = messageService.findLatestNotice(user.getId(),TOPIC_COMMENT);

        if(message!=null){
//            messageVO.put("message",message);
            Map<String, Object> messageVO = new HashMap<>();
            messageVO.put("message",message);
            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String,Object> data = JSONObject.parseObject(content, HashMap.class);

            messageVO.put("user",userService.findUserById((Integer) data.get("userId")));
            messageVO.put("entityType",data.get("entityType"));
            messageVO.put("entityId",data.get("entityId"));
            messageVO.put("postId",data.get("postId"));

            int count = messageService.findNoticeCount(user.getId(),TOPIC_COMMENT);
            messageVO.put("count",count);
            int unread = messageService.findLetterUnreadCount(user.getId(), TOPIC_COMMENT);
            messageVO.put("unread",unread);
            model.addAttribute("commentNotice",messageVO);
        }



        //查询点赞类通知
        message = messageService.findLatestNotice(user.getId(),TOPIC_LIKE);

        if(message!=null){
            Map<String, Object> messageVO = new HashMap<>();
            messageVO = new HashMap<>();
            messageVO.put("message",message);
            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String,Object> data = JSONObject.parseObject(content, HashMap.class);

            messageVO.put("user",userService.findUserById((Integer) data.get("userId")));
            messageVO.put("entityType",data.get("entityType"));
            messageVO.put("entityId",data.get("entityId"));
            messageVO.put("postId",data.get("postId"));

            int count = messageService.findNoticeCount(user.getId(),TOPIC_LIKE);
            messageVO.put("count",count);
            int unread = messageService.findLetterUnreadCount(user.getId(), TOPIC_LIKE);
            messageVO.put("unread",unread);

            model.addAttribute("likeNotice",messageVO);
        }



        //查询关注类通知
        message = messageService.findLatestNotice(user.getId(),TOPIC_FOLLOW);


        if(message!=null){
            Map<String, Object> messageVO = new HashMap<>();
            messageVO = new HashMap<>();
            messageVO.put("message",message);
            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String,Object> data = JSONObject.parseObject(content, HashMap.class);

            messageVO.put("user",userService.findUserById((Integer) data.get("userId")));
            messageVO.put("entityType",data.get("entityType"));
            messageVO.put("entityId",data.get("entityId"));

            int count = messageService.findNoticeCount(user.getId(),TOPIC_FOLLOW);
            messageVO.put("count",count);
            int unread = messageService.findLetterUnreadCount(user.getId(), TOPIC_FOLLOW);
            messageVO.put("unread",unread);
            model.addAttribute("followNotice",messageVO);

        }


        //查询未读消息数量
        int letterUnreadCount = messageService.findNoticeUnreadCount(user.getId(),null);
        model.addAttribute("letterUnreadCount",letterUnreadCount);
        //查询未读通知数量
        int noticeUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("noticeUnreadCount",noticeUnreadCount);
        return "/site/notice";
    }
    //具体类别通知列表
    @RequestMapping(path="/notice/detail/{topic}",method = RequestMethod.GET)
    public String getNoticeDetail(@PathVariable("topic") String topic,Page page,Model model){
        User user = hostHolder.getUser();
        page.setLimit(5);
        page.setPath("/notice/detail/"+topic);
        page.setRows(messageService.findNoticeCount(user.getId(),topic));

        //获取信息
        List<Message> noticeList = messageService.findNotices(user.getId(), topic, page.getOffset(), page.getLimit());
        ArrayList<Map<String, Object>> noticeVoList = new ArrayList<>();
        if(noticeList!=null){
            for(Message notice:noticeList){
                HashMap<String, Object> map = new HashMap<>();
                //记录每条通知和内容
                map.put("notice",notice);
                String content = HtmlUtils.htmlUnescape(notice.getContent());
                map.put("content",content);

                //获取通知具体类型
                HashMap<String,Object> data = JSONObject.parseObject(content, HashMap.class);
                map.put("user",userService.findUserById((Integer)data.get("userId")));
                map.put("entityTyoe",data.get("entityType"));
                map.put("entityId",data.get("entityId"));
                map.put("postId",data.get("postId"));
                //通知作者
                map.put("formUser",userService.findUserById(notice.getFormId()));
                noticeVoList.add(map);
            }
        }
        model.addAttribute("notices",noticeVoList);

        //设置已读
        List<Integer> ids = getLetterIds(noticeList);
        if(!ids.isEmpty()){
            messageService.readMessage(ids);
        }
        return "/site/notice-detail";
    }








}
