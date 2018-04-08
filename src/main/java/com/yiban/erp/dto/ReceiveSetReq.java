package com.yiban.erp.dto;

import com.yiban.erp.entities.RepositoryOrderDetail;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class ReceiveSetReq {

    private Long orderId;
    private Long detailId;
    private String updateBy;
    private Date updateTime;

    //设置验收温度
    private BigDecimal checkTemp;

    //设置详情的抽样数据
    private Date surveyDate;
    private Integer surveyQuality;
    private String surveyUser;
    private String surveyAddress;
    private String surveyResult;
    private String surveyTarget;

    //设置验收结果
    private Integer inCount;
    private Integer rightCount;
    private Integer errorCount;
    private String errorPlan;
    private String saleCert;
    private String errorReason;
    private Long checkTempMethod;
    private String checkUser;
    private Date checkTime;
    private String checkResult;

    //保存详情信息
    List<RepositoryOrderDetail> detailList;

    //档案编号
    private String fileNo;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public BigDecimal getCheckTemp() {
        return checkTemp;
    }

    public void setCheckTemp(BigDecimal checkTemp) {
        this.checkTemp = checkTemp;
    }

    public Long getDetailId() {
        return detailId;
    }

    public void setDetailId(Long detailId) {
        this.detailId = detailId;
    }

    public Date getSurveyDate() {
        return surveyDate;
    }

    public void setSurveyDate(Date surveyDate) {
        this.surveyDate = surveyDate;
    }

    public Integer getSurveyQuality() {
        return surveyQuality;
    }

    public void setSurveyQuality(Integer surveyQuality) {
        this.surveyQuality = surveyQuality;
    }

    public String getSurveyUser() {
        return surveyUser;
    }

    public void setSurveyUser(String surveyUser) {
        this.surveyUser = surveyUser;
    }

    public String getSurveyAddress() {
        return surveyAddress;
    }

    public void setSurveyAddress(String surveyAddress) {
        this.surveyAddress = surveyAddress;
    }

    public String getSurveyResult() {
        return surveyResult;
    }

    public void setSurveyResult(String surveyResult) {
        this.surveyResult = surveyResult;
    }

    public String getSurveyTarget() {
        return surveyTarget;
    }

    public void setSurveyTarget(String surveyTarget) {
        this.surveyTarget = surveyTarget;
    }

    public Integer getInCount() {
        return inCount;
    }

    public void setInCount(Integer inCount) {
        this.inCount = inCount;
    }

    public Integer getRightCount() {
        return rightCount;
    }

    public void setRightCount(Integer rightCount) {
        this.rightCount = rightCount;
    }

    public Integer getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(Integer errorCount) {
        this.errorCount = errorCount;
    }

    public String getErrorPlan() {
        return errorPlan;
    }

    public void setErrorPlan(String errorPlan) {
        this.errorPlan = errorPlan;
    }

    public String getSaleCert() {
        return saleCert;
    }

    public void setSaleCert(String saleCert) {
        this.saleCert = saleCert;
    }

    public String getErrorReason() {
        return errorReason;
    }

    public void setErrorReason(String errorReason) {
        this.errorReason = errorReason;
    }

    public Long getCheckTempMethod() {
        return checkTempMethod;
    }

    public void setCheckTempMethod(Long checkTempMethod) {
        this.checkTempMethod = checkTempMethod;
    }

    public String getCheckUser() {
        return checkUser;
    }

    public void setCheckUser(String checkUser) {
        this.checkUser = checkUser;
    }

    public Date getCheckTime() {
        return checkTime;
    }

    public void setCheckTime(Date checkTime) {
        this.checkTime = checkTime;
    }

    public String getCheckResult() {
        return checkResult;
    }

    public void setCheckResult(String checkResult) {
        this.checkResult = checkResult;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public List<RepositoryOrderDetail> getDetailList() {
        return detailList;
    }

    public void setDetailList(List<RepositoryOrderDetail> detailList) {
        this.detailList = detailList;
    }

    public String getFileNo() {
        return fileNo;
    }

    public void setFileNo(String fileNo) {
        this.fileNo = fileNo;
    }
}