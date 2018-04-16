package com.azureip.tmspider.service;

import com.azureip.tmspider.mapper.AnnouncementMapper;
import com.azureip.tmspider.model.Announcement;
import com.azureip.tmspider.pojo.AnnoucementPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnnService {

    @Autowired
    private AnnouncementMapper mapper;

    public int save (AnnoucementPojo pojo) {
        Announcement ann = new Announcement(
                pojo.getId(),
                pojo.getPage_no(),
                pojo.getAnn_type_code(),
                pojo.getAnn_type(),
                pojo.getAnn_num(),
                pojo.getAnn_date(),
                pojo.getReg_name(),
                pojo.getReg_num(),
                pojo.getTm_name()
        );
        return mapper.insert(ann);
    }
}
