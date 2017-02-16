package com.gkzxhn.gkprison.bean;

import java.util.List;

/**
 * Created by wrf on 2016/10/26.
 */

public class FaceDetect {


    /**
     * image_id : mJJw31PO2heNyWkSRhngXA==
     * request_id : 1477471369,c7f0e393-22be-4623-9d38-0dd990458849
     * time_used : 605
     * faces : [{"face_rectangle":{"width":506,"top":916,"left":279,"height":507},"face_token":"f957e8e85072f116f0f8d9c3476d93c2"}]
     */

    private String image_id;
    private String request_id;
    private int time_used;
    /**
     * face_rectangle : {"width":506,"top":916,"left":279,"height":507}
     * face_token : f957e8e85072f116f0f8d9c3476d93c2
     */

    private List<FacesBean> faces;

    public String getImage_id() {
        return image_id;
    }

    public void setImage_id(String image_id) {
        this.image_id = image_id;
    }

    public String getRequest_id() {
        return request_id;
    }

    public void setRequest_id(String request_id) {
        this.request_id = request_id;
    }

    public int getTime_used() {
        return time_used;
    }

    public void setTime_used(int time_used) {
        this.time_used = time_used;
    }

    public List<FacesBean> getFaces() {
        return faces;
    }

    public void setFaces(List<FacesBean> faces) {
        this.faces = faces;
    }

    @Override
    public String toString() {
        return "FaceDetect{" +
                "image_id='" + image_id + '\'' +
                ", request_id='" + request_id + '\'' +
                ", time_used=" + time_used +
                ", faces=" + faces +
                '}';
    }
}
