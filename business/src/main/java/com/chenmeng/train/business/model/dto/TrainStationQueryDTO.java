package com.chenmeng.train.business.model.dto;

import com.chenmeng.train.common.req.PageReq;

/**
 * @author 沉梦听雨
 **/
public class TrainStationQueryDTO extends PageReq {

    /**
     * 车次编号
     */
    private String trainCode;

    public String getTrainCode() {
        return trainCode;
    }

    public void setTrainCode(String trainCode) {
        this.trainCode = trainCode;
    }

    @Override
    public String toString() {
        return "TrainStationQueryDTO{" +
                "trainCode='" + trainCode + '\'' +
                "} " + super.toString();
    }
}

