package com.nashcat.serieamaniav2.vo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by nashc on 2016-02-02.
 */
public class BoardListVO extends DefaultVO implements Serializable {


    private static final long serialVersionUID = -355980350024679790L;
    /** 게시판이름 */
    private String boardNm;
    /** 게시물번호 */
    private String number;
    /** 게시물제목 */
    private String subject;
    /** 작성자 */
    private String user;
    /** 리플 */
    private String reply;
    /** 아이콘 */
    private String userIcon;
    /** URL */
    private String url;
    /** 게시물 */
    private ArrayList<BoardContentsVO> contentsList;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ArrayList<BoardContentsVO> getContentsList() {
        return contentsList;
    }

    public void setContentsList(ArrayList<BoardContentsVO> contentsList) {
        this.contentsList = contentsList;
    }

    public String getBoardNm() {
        return boardNm;
    }

    public void setBoardNm(String boardNm) {
        this.boardNm = boardNm;
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

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
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
}
