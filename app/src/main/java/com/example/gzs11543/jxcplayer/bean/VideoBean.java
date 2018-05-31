package com.example.gzs11543.jxcplayer.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @author jiang
 * @date 2018/05/23
 *
 */
public class VideoBean implements Serializable{
    private  DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean implements Serializable{
        private int end;
        private List<ListBean> info_list;

        public int getEnd() {
            return end;
        }

        public void setEnd(int end) {
            this.end = end;
        }

        public List<ListBean> getInfoList() {
            return info_list;
        }

        public void setInfoList(List<ListBean> info_list) {
            this.info_list = info_list;
        }

        public static class ListBean implements Serializable{
            private String flv;
            private String duration;
            private String title;
            private String cover;
            private int width;
            private int height;

            public String getFlv() {
                return flv;
            }

            public void setFlv(String flv) {
                this.flv = flv;
            }

            public String getDuration() {
                return duration;
            }

            public void setDuration(String duration) {
                this.duration = duration;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getCover() {
                return cover;
            }

            public void setCover(String cover) {
                this.cover = cover;
            }

            public int getWidth() {
                return width;
            }

            public int getHeight() {
                return height;
            }
        }
    }
}
