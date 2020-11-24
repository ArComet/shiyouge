package com.example.shiyouge.service;

import com.example.shiyouge.bean.Report;
import com.example.shiyouge.mapper.ReportMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ReportService {
    @Autowired
    ReportMapper reportMapper;

    /**
     * 更新信息，有的对应 + 1
     * @param postId 帖子ID
     * @param vulgar 色情低俗
     * @param sensitivity 政治敏感
     * @param illegal 违法
     * @param advertisement 广告
     * @param virus 病毒木马
     * @param others 其他
     */
    public void updateTheValueOfReport(int postId, int vulgar, int sensitivity, int illegal, int advertisement, int virus, int others){
        if (vulgar != 0){
            reportMapper.setTheVulgarNum(postId, reportMapper.getTheVulgarNum(postId)+1);
        }
        if (sensitivity != 0){
            reportMapper.setTheSensitivityNum(postId, reportMapper.getTheSensitivityNum(postId)+1);
        }
        if (illegal != 0){
            reportMapper.setTheIllegalNum(postId, reportMapper.getTheIllegalNum(postId)+1);
        }
        if (advertisement != 0){
            reportMapper.setTheAdvertisementNum(postId, reportMapper.getTheAdvertisementNum(postId)+1);
        }
        if (virus != 0){
            reportMapper.setTheVirusNum(postId, reportMapper.getTheVirusNum(postId)+1);
        }
        if (others != 0){
            reportMapper.setTheOthersNum(postId, reportMapper.getTheOthersNum(postId)+1);
        }
    }

    /**
     * 判断是否第一次被举报
     * @param postId 帖子ID
     * @return 是为 true，否为 false
     */
    public boolean findTheReportByPostId(int postId){
        Report report = reportMapper.getTheReportByPostId(postId);
        if (report != null){
            return true;
        } else {
            return false;
        }
    }

    /**
     * 创建某帖子的举报记录
     * @param postId 帖子ID
     */
    public void creatTheReportOfPost(int postId){
        reportMapper.creatTheReportOfPost(postId);
    }

    /**
     * 获得被举报帖子的ID
     * @return 被举报帖子的ID
     */
    public List<Integer> getPostIdOfReport() { return reportMapper.getPostIdOfReport();}

    /**
     * 获得被举报帖子的被举报类型
     * @param postIdOfReport 被举报帖子的ID
     * @return 被举报帖子的举报类型
     */
    public String getReportTypeByPostId(int postIdOfReport) {return reportMapper.getReportTypeByPostId(postIdOfReport);}

    /**
     * 得到通过ID得到 Report 对象
     * @param postIdOfReport ID
     * @return Report 对象
     */
    public Report getTheReportByPostId(int postIdOfReport){
        return reportMapper.getTheReportByPostId(postIdOfReport);
    }

    /**
     * 删除记录
     * @param postId 帖子ID
     */
    public void deleteReportByPostId(int postId){
        reportMapper.deleteTheReportByPostId(postId);
    }
}
