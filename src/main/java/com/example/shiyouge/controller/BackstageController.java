package com.example.shiyouge.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.shiyouge.bean.Post;
import com.example.shiyouge.bean.Report;
import com.example.shiyouge.service.AdminService;
import com.example.shiyouge.service.PostService;
import com.example.shiyouge.service.ReportService;
import com.example.shiyouge.service.UserService;
import com.example.shiyouge.utils.DateUtil;
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
@RequestMapping(value = "/backStage")
public class BackstageController {
    @Autowired
    UserService userService;
    @Autowired
    PostService postService;
    @Autowired
    ReportService reportService;
    @Autowired
    AdminService adminService;

    /**
     * 获得所有用户ID，昵称，真实姓名，学号，
     * @return json数组： 用户ID + 昵称 + 真实姓名 + 学号 + 是否禁言
     */
    @RequestMapping(value = "/getUserStatus", method = RequestMethod.POST)
    public String getUserStatus() {
        JSONArray jsonArray = new JSONArray();
        List<String> userIds = userService.getAllUserId();
        for (String userId : userIds) {
            JSONObject jo = new JSONObject();
            jo.put("userId",userId);
            String userNickname = userService.getNickNameByUserId(userId);
            jo.put("userNickname", userNickname);
            String userRealname = userService.getRealNameByUserId(userId);
            jo.put("userRealname", userRealname);
            String userStudentNumber = userService.getStudentNumberByUserId(userId);
            jo.put("userStudentNumber", userStudentNumber);
            Integer ifSilent = userService.getIfSilentByUserId(userId);
            jo.put("ifSilent", ifSilent);
            jsonArray.add(jo);
        }
        return  jsonArray.toString();
    }

    /**
     * 修改真实姓名和学号
     * @param params 用户真实姓名 + 学号
     * @return 状态：succeed 或 wrong
     */
    @RequestMapping(value = "/updateUserInfo",method = RequestMethod.POST)
    public String updateUserInfo(@RequestBody Map<String, Object> params) {
        String userId = params.get("userId").toString();
        String userRealName = params.get("userRealName").toString();
        int userStudentNumber = Integer.parseInt(params.get("userStudentNumber").toString());
        JSONObject json = new JSONObject();
        try {
            userService.updateUserInfo(userRealName, userStudentNumber, userId);
            json.put("status", "succeed");
        } catch (Exception e){
            json.put("status","wrong");
        }
        return json.toString();
    }

    /**
     * 得到所有被举报的帖子
     * @return json数组：帖子ID + 帖子内容 + 举报次数 + 色情低俗 + 政治敏感 + 违法 + 广告 + 病毒木马 + 其他
     */
    @RequestMapping(value = "/getAllReported",method = RequestMethod.POST)
    public String getAllReported() {
        JSONObject json1 = new JSONObject();
        int numberOfReported = userService.getNumberOfReported();
        if (numberOfReported == 0){
            json1.put("status", "wrong");
        } else {
            JSONArray jsonArray = new JSONArray();
            List<Integer> postIdsOfReport = reportService.getPostIdOfReport();
            for (int postIdOfReport : postIdsOfReport) {
                Post thePost = postService.getPostByPostId(postIdOfReport);
                JSONObject json2 = new JSONObject();
                json2.put("postIdOfReport", postIdOfReport);
                String postContent = thePost.getPostContent();
                json2.put("postContent", postContent);
                int reportTimes = thePost.getReportTimes();
                json2.put("reportTimes", reportTimes);
                JSONObject json3 = new JSONObject();
                Report report = reportService.getTheReportByPostId(postIdOfReport);
                json3.put("vulgar",report.getVulgar());
                json3.put("sensitivity",report.getSensitivity());
                json3.put("illegal",report.getIllegal());
                json3.put("advertisement",report.getAdvertisement());
                json3.put("virus",report.getVirus());
                json3.put("others",report.getOthers());
                json2.put("type", json3);
                jsonArray.add(json2);
            }
            json1.put("posts",jsonArray);
            json1.put("status", "succeed");
        }
        return  json1.toString();
    }

    /**
     * 删帖
     * @param params 帖子ID
     * @return 状态：succeed
     */
    @RequestMapping(value = "/deletePost", method = RequestMethod.POST)
    public String deletePost(@RequestBody Map<String, Object> params){
        int postId = Integer.parseInt(params.get("postId").toString());
        JSONObject json = new JSONObject();
        if(postService.deletePostByPostId(postId) == 1) {
            //删除举报记录
            reportService.deleteReportByPostId(postId);
            json.put("status", "succeed");
        }
        else {
            json.put("status", "wrong");
        }
        return json.toString();
    }

    /**
     * 取消举报状态
     * @param params 帖子ID
     * @return 状态：succeed
     */
     @RequestMapping(value = "/reportedCancel", method = RequestMethod.POST)
    public String reportedCancel(@RequestBody Map<String, Object> params) {
         int postId = Integer.parseInt(params.get("postId").toString());
         JSONObject json = new JSONObject();
         if (postService.reportedCancel(postId) >= 1) {
             //删除举报记录
             reportService.deleteReportByPostId(postId);
             json.put("status", "succeed");
         }
         else {
             json.put("status", "wrong");
         }
         return  json.toString();
     }

    /**
     * 禁言用户
     * @param params 用户ID
     * @return 状态：succeed
     */
    @RequestMapping(value = "/silent", method = RequestMethod.POST)
    public String silentUser(@RequestBody Map<String, Object> params) {
        String userId = params.get("userId").toString();
        Date date = new Date();
        date = DateUtil.goToDayChange(1, date);
        Timestamp endSilentTime =  new Timestamp(date.getTime());
        JSONObject json = new JSONObject();
        if (userService.silent(userId) >= 1) {
            //用户结束禁言时间的设置
            userService.setTheEndSilentTime(userId, endSilentTime);
            json.put("status", "succeed");
        }
        else {
            json.put("status", "wrong");
        }
        return json.toString();
    }

    /**
     * 管理员登录
     * @param params 管理员ID 密码
     * @return 状态：succeed
     */
    @RequestMapping(value = "/signIn", method = RequestMethod.POST)
    public String adminSignIn(@RequestBody Map<String, Object> params) {
        String userId = params.get("userId").toString();
        String password = params.get("password").toString();
        JSONObject json = new JSONObject();
        if (adminService.signIn(userId, password) == 1) {
            json.put("status", "succeed");
        }
        else {
            json.put("status", "wrong");
        }
        return json.toString();
    }
}
