package com.dingmouren.dingdingmap.bean;


import java.io.Serializable;

/**
 * Created by dingmouren on 2016/12/8.
 */
public class GankResultWelfare implements Serializable {

    /**
     * _id : 5848c92e421aa963efd90da4
     * createdAt : 2016-12-08T10:45:02.271Z
     * desc : 12-8
     * publishedAt : 2016-12-08T11:42:08.186Z
     * source : chrome
     * type : 福利
     * url : http://ww1.sinaimg.cn/large/610dc034jw1faj6sozkluj20u00nt75p.jpg
     * used : true
     * who : 代码家
     */
    private String _id;
    private String createdAt;
    private String desc;
    private String publishedAt;
    private String source;
    private String type;
    private String url;
    private boolean used;
    private String who;

    public GankResultWelfare(String _id, String createdAt, String desc, String publishedAt, String source, String type, String url, boolean used, String who) {
        this._id = _id;
        this.createdAt = createdAt;
        this.desc = desc;
        this.publishedAt = publishedAt;
        this.source = source;
        this.type = type;
        this.url = url;
        this.used = used;
        this.who = who;
    }

    public GankResultWelfare() {
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public String getWho() {
        return who;
    }

    public void setWho(String who) {
        this.who = who;
    }

    public boolean getUsed() {
        return this.used;
    }
}
