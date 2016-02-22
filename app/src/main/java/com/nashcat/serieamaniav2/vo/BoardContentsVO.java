package com.nashcat.serieamaniav2.vo;

import java.io.Serializable;

/**
 * Created by nashc on 2016-02-02.
 */
public class BoardContentsVO extends DefaultVO implements Serializable {


    private static final long serialVersionUID = -6268631231624093259L;
    /** 게시물번호 */
    private String number;
    /** 게시물제목 */
    private String subject;
    /** 작성자 */
    private String name;
    /** 리플 */
    private String reply;
    /** 아이콘 */
    private String userIcon;
    /** 내용 */
    private String contents;
    /** 텍스트 컬러 */
    private String textColor;
    /** URL */
    private String contentUrl;

    private String replyCnt;

    public String getReplyCnt() {
        return replyCnt;
    }

    public void setReplyCnt(String replyCnt) {
        this.replyCnt = replyCnt;
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public String getUserIcon() {
        return userIcon;
    }

    public void setUserIcon(String userIcon) {
        this.userIcon = userIcon;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }
}
