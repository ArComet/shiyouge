package com.example.shiyouge.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.shiyouge.bean.Comment;
import com.example.shiyouge.bean.Post;
import com.example.shiyouge.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/treeHole")
public class TreeHoleController {
    @Autowired
    PostService postService;
    @Autowired
    UserService userService;
    @Autowired
    CollectService collectService;
    @Autowired
    CommentService commentService;
    @Autowired
    ReportService reportService;

    /**
     * 得到某分区的所有帖子
     * @return 状态：succeed 或 wrong + json数组：编号，内容，时间，评论数，收藏数（按时间排序，近期发布在前）
     */
    @RequestMapping(value = "/getPostsOfPartition", method = RequestMethod.POST)
    public String getPostsOfPartition(@RequestBody Map<String, Object> params) {
        int partition = Integer.parseInt(params.get("partition").toString());
        JSONObject json = new JSONObject();
        try {
            JSONArray jsonArray = new JSONArray();
            List<Post> posts = postService.getAllPostOfPartition(partition);
            for (Post post: posts) {
                JSONObject jo = new JSONObject();
                jo.put("postId", post.getPostId());
                jo.put("postContent", post.getPostContent());
                jo.put("publishTime", post.getPublishTime());
                jo.put("commentedTimes", post.getCommentedTimes());
                jo.put("collectedTimes", post.getCollectedTimes());
                jsonArray.add(jo);
            }
            json.put("posts", jsonArray);
            json.put("status", "succeed");
        } catch (Exception e){
            json.put("status", "wrong");
        }
        return json.toString();
    }

    /**
     * 查询包含某内容的全部帖子
     * @return 状态：succeed 或 wrong + json数组：编号，内容，时间，评论数，收藏数（按时间排序，近期发布在前）
     */
    @RequestMapping(value = "/getPostsByContent", method = RequestMethod.POST)
    public String getPostsByContent(@RequestBody Map<String, Object> params) {
        String content = params.get("content").toString();
        JSONObject json = new JSONObject();
        try {
            JSONArray jsonArray = new JSONArray();
            List<Post> posts = postService.getPostsByContent(content);
            for (Post post: posts) {
                JSONObject jo = new JSONObject();
                jo.put("postId", post.getPostId());
                jo.put("postContent", post.getPostContent());
                jo.put("publishTime", post.getPublishTime());
                jo.put("commentedTimes", post.getCommentedTimes());
                jo.put("collectedTimes", post.getCollectedTimes());
                jsonArray.add(jo);
            }
            json.put("posts", jsonArray);
            json.put("status", "succeed");
        } catch (Exception e){
            json.put("status", "wrong");
        }
        return json.toString();
    }

    /**
     * 查看帖子详细内容及评论
     * @return 状态：succeed 或 wrong + json对象：作者昵称，用户对该帖子是否收藏，评论的json数组
     */
    @RequestMapping(value = "/postDetail", method = RequestMethod.POST)
    public String postDetail(@RequestBody Map<String, Object> params) {
        String userId = params.get("userId").toString();
        int postId = Integer.parseInt(params.get("postId").toString());
        JSONObject json = new JSONObject();
        try {
            //得到对应的Post对象
            Post post = postService.getPostByPostId(postId);
            json.put("nicknameOfAuthor", userService.getNickNameByUserId(post.getUserIdOfPost()));
            //是否收藏，是则 1，否则 0
            json.put("ifCollect", collectService.ifUserCollectPost(userId, postId));
            //评论的json数组
            JSONArray jsonArray = new JSONArray();
            List<Comment> commments = commentService.getAllCommentsByPostId(postId);
            for (Comment comment: commments) {
                JSONObject jo = new JSONObject();
                jo.put("commentTime", comment.getCommentTime());
                jo.put("commentContent", comment.getCommentContent());
                jsonArray.add(jo);
            }
            json.put("comments", jsonArray);
            json.put("status", "succeed");
        } catch (Exception e){
            json.put("status", "wrong");
        }
        return json.toString();
    }

    /**
     * 用户写评论
     * @param params 用户ID + 帖子ID + 评论内容
     * @return 状态：created 或 wrong 或 silented
     */
    @RequestMapping(value = "/createComment", method = RequestMethod.POST)
    public String createComment(@RequestBody Map<String, Object> params) {
        String userId = params.get("userId").toString();
        int postId = Integer.parseInt(params.get("postId").toString());
        String content = params.get("content").toString();
        Date date = new Date();
        Timestamp createTime =  new Timestamp(date.getTime());
        Timestamp endSilentTime = userService.getTheEndSilentTime(userId);
        JSONObject json = new JSONObject();
        //如果用户禁言时间还未结束
        if (createTime.before(endSilentTime)){
            json.put("status", "silented");
            return json.toString();
        }
        try {
            //在数据库创建评论信息
            commentService.createTheNewComment(userId, postId, content, createTime);
            json.put("status", "created");
        } catch (Exception e){
            json.put("status", "wrong");
        }
        return json.toString();
    }

    /**
     * 用户举报帖子
     * @param params 对应的帖子ID + 色情低俗 + 政治敏感 + 违法 + 广告 + 病毒木马 + 其他
     * @return 状态：reported 或 wrong
     */
    @RequestMapping(value = "/reportPost", method = RequestMethod.POST)
    public String reportPost(@RequestBody Map<String, Object> params) {
        int postId = Integer.parseInt(params.get("postId").toString());
        int vulgar = Integer.parseInt(params.get("vulgar").toString());
        int sensitivity = Integer.parseInt(params.get("sensitivity").toString());
        int illegal = Integer.parseInt(params.get("illegal").toString());
        int advertisement = Integer.parseInt(params.get("advertisement").toString());
        int virus = Integer.parseInt(params.get("virus").toString());
        int others = Integer.parseInt(params.get("others").toString());
        JSONObject json = new JSONObject();
        try {
            //判断，如果是第一次被举报
            if (!reportService.findTheReportByPostId(postId)){
                //创建举报信息记录
                reportService.creatTheReportOfPost(postId);
                //设置状态为举报
                postService.setThePostIfReported(postId, 1);
            }
            //统计对对应数值加1
            reportService.updateTheValueOfReport(postId, vulgar, sensitivity, illegal, advertisement, virus, others);
            json.put("status", "reported");
        } catch (Exception e){
            json.put("status", "wrong");
        }
        return json.toString();
    }

    /**
     * 用户写帖子
     * @param params 用户ID + 评论内容 + 分区
     * @return 状态：created 或 wrong 或 silented
     */
    @RequestMapping(value = "/createPost", method = RequestMethod.POST)
    public String createPost(@RequestBody Map<String, Object> params) {
        String userId = params.get("userId").toString();
        String content = params.get("content").toString();
        int partition = Integer.parseInt(params.get("partition").toString());
        Date date = new Date();
        Timestamp createTime =  new Timestamp(date.getTime());
        Timestamp endSilentTime = userService.getTheEndSilentTime(userId);
        JSONObject json = new JSONObject();
        //如果用户禁言时间还未结束
        if (createTime.before(endSilentTime)){
            json.put("status", "silented");
            return json.toString();
        }
        try {
            //在数据库创建评论
            postService.createThePost(userId, content, partition, createTime);
            json.put("status", "created");
        } catch (Exception e){
            json.put("status", "wrong");
        }
        return json.toString();
    }
}
