package com.example.shiyouge.mapper;

import com.example.shiyouge.bean.Report;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReportMapper {

    /**
     * 创建某帖子的举报记录
     * @param postId 帖子ID
     */
    void creatTheReportOfPost(int postId);

    /**
     * 得到色情低俗次数
     * @param postId 帖子ID
     * @return 次数
     */
    int getTheVulgarNum(int postId);

    /**
     * 设置色情低俗次数
     * @param postId 帖子ID
     * @param num 次数
     */
    void setTheVulgarNum(@Param("postId") int postId, @Param("num") int num);

    /**
     * 得到政治敏感次数
     * @param postId 帖子ID
     * @return 次数
     */
    int getTheSensitivityNum(int postId);

    /**
     * 设置政治敏感次数
     * @param postId 帖子ID
     * @param num 次数
     */
    void setTheSensitivityNum(@Param("postId") int postId, @Param("num") int num);

    /**
     * 得到违法次数
     * @param postId 帖子ID
     * @return 次数
     */
    int getTheIllegalNum(int postId);

    /**
     * 设置违法次数
     * @param postId 帖子ID
     * @param num 次数
     */
    void setTheIllegalNum(@Param("postId") int postId, @Param("num") int num);

    /**
     * 得到广告次数
     * @param postId 帖子ID
     * @return 次数
     */
    int getTheAdvertisementNum(int postId);

    /**
     * 设置广告次数
     * @param postId 帖子ID
     * @param num 次数
     */
    void setTheAdvertisementNum(@Param("postId") int postId, @Param("num") int num);

    /**
     * 得到病毒木马次数
     * @param postId 帖子ID
     * @return 次数
     */
    int getTheVirusNum(int postId);

    /**
     * 设置病毒木马次数
     * @param postId 帖子ID
     * @param num 次数
     */
    void setTheVirusNum(@Param("postId") int postId, @Param("num") int num);

    /**
     * 得到病毒木马次数
     * @param postId 帖子ID
     * @return 次数
     */
    int getTheOthersNum(int postId);

    /**
     * 设置病毒木马次数
     * @param postId 帖子ID
     * @param num 次数
     */
    void setTheOthersNum(@Param("postId") int postId, @Param("num") int num);

    /**
     * 获得被举报帖子的ID
     * @return 被举报帖子的ID
     */
    List<Integer> getPostIdOfReport();

    /**
     * 获得被举报帖子的被举报类型
     * @param postIdOfReport 被举报帖子的ID
     * @return 被举报帖子的举报类型
     */
    String getReportTypeByPostId(int postIdOfReport);

    /**
     * 得到通过ID得到 Report 对象
     * @param postIdOfReport ID
     * @return Report 对象
     */
    Report getTheReportByPostId(int postIdOfReport);

    /**
     * 删除记录
     * @param postId 帖子ID
     */
    void deleteTheReportByPostId(int postId);
}
