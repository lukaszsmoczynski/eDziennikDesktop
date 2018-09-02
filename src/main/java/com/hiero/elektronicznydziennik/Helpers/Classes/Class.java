package com.hiero.elektronicznydziennik.Helpers.Classes;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Class {
    private Integer mId;
    private String mName;
    private Integer mTeacherId;
    private Integer mSubjectId;
    private DayOfWeek mDayOfOccurence;
    private List<Integer> mGroupIds = new ArrayList<>();
    private Calendar mBeginDate;
    private Calendar mEndDate;
    private Calendar mBeginTime;
    private Calendar mEndTime;

    public Class setName(String mName) {
        this.mName = mName;
        return this;
    }

    public Class setTeacherId(Integer mTeacherId) {
        this.mTeacherId = mTeacherId;
        return this;
    }

    public Class setSubjectId(Integer mSubjectId) {
        this.mSubjectId = mSubjectId;
        return this;
    }

    public Class setDayOfOccurence(DayOfWeek mDayOfOccurence) {
        this.mDayOfOccurence = mDayOfOccurence;
        return this;
    }

    public Class setGroupIds(List<Integer> mGroupIds) {
        this.mGroupIds = mGroupIds;
        return this;
    }

    public Class setBeginDate(Calendar mBeginDate) {
        this.mBeginDate = mBeginDate;
        return this;
    }

    public Class setEndDate(Calendar mEndDate) {
        this.mEndDate = mEndDate;
        return this;
    }

    public Class setBeginTime(Calendar mBeginTime) {
        this.mBeginTime = mBeginTime;
        return this;
    }

    public Class setEndTime(Calendar mEndTime) {
        this.mEndTime = mEndTime;
        return this;
    }

    public Integer getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public Integer getTeacherId() {
        return mTeacherId;
    }

    public Integer getSubjectId() {
        return mSubjectId;
    }

    public DayOfWeek getDayOfOccurence() {
        return mDayOfOccurence;
    }

    public List<Integer> getGroupIds() {
        return mGroupIds;
    }

    public Calendar getBeginDate() {
        return mBeginDate;
    }

    public Calendar getEndDate() {
        return mEndDate;
    }

    public Calendar getBeginTime() {
        return mBeginTime;
    }

    public Calendar getEndTime() {
        return mEndTime;
    }

    public Class(Integer pId) {
        this.mId = mId;
    }
}
