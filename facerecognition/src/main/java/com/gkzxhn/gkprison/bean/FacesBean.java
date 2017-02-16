package com.gkzxhn.gkprison.bean;

/**
 * Created by wrf on 2016/10/26.
 */

public class FacesBean {

    /**
     * width : 506
     * top : 916
     * left : 279
     * height : 507
     */

    private FaceRectangleBean face_rectangle;
    private String face_token;

    public FaceRectangleBean getFace_rectangle() {
        return face_rectangle;
    }

    public void setFace_rectangle(FaceRectangleBean face_rectangle) {
        this.face_rectangle = face_rectangle;
    }

    public String getFace_token() {
        return face_token;
    }

    public void setFace_token(String face_token) {
        this.face_token = face_token;
    }

    public static class FaceRectangleBean {
        private int width;
        private int top;
        private int left;
        private int height;

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getTop() {
            return top;
        }

        public void setTop(int top) {
            this.top = top;
        }

        public int getLeft() {
            return left;
        }

        public void setLeft(int left) {
            this.left = left;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }
    }

    @Override
    public String toString() {
        return "FacesBean{" +
                "face_rectangle=" + face_rectangle +
                ", face_token='" + face_token + '\'' +
                '}';
    }
}
