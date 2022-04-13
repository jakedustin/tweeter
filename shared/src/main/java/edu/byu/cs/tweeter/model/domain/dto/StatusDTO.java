package edu.byu.cs.tweeter.model.domain.dto;

import java.util.List;

public class StatusDTO {
    private String post;
    private String datetime;
    private String userAlias;
    private List<String> urls;
    private List<String> mentions;

    public StatusDTO() {

    }

    public StatusDTO(String post, String datetime, String userAlias, List<String> urls, List<String> mentions) {
        this.post = post;
        this.datetime = datetime;
        this.userAlias = userAlias;
        this.urls = urls;
        this.mentions = mentions;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getUserAlias() {
        return userAlias;
    }

    public void setUserAlias(String userAlias) {
        this.userAlias = userAlias;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    public List<String> getMentions() {
        return mentions;
    }

    public void setMentions(List<String> mentions) {
        this.mentions = mentions;
    }

    @Override
    public String toString() {
        return "StatusDTO{" +
                "post='" + post + '\'' +
                ", datetime='" + datetime + '\'' +
                ", userAlias='" + userAlias + '\'' +
                ", urls=" + urls +
                ", mentions=" + mentions +
                '}';
    }
}
