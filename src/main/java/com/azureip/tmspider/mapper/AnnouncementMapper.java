package com.azureip.tmspider.mapper;

import com.azureip.tmspider.model.Announcement;

public interface AnnouncementMapper {
    int deleteByPrimaryKey(String id);

    int insert(Announcement record);

    int insertSelective(Announcement record);

    Announcement selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Announcement record);

    int updateByPrimaryKey(Announcement record);
}