package com.nashcat.serieamaniav2.vo;

import java.io.Serializable;

/**
 * Created by nashc on 2016-02-02.
 */
public class ReplyContentsVO extends DefaultVO implements Serializable {


    private static final long serialVersionUID = -1485422408752130601L;

    /** 리플넘버comment_srl */
    private String replyNumber;
    /** 리플작성자명 */
    private String replyUser;
    /** 리플일자 */
    private String replyDt;
    /** 리플본인작성여부 */
    private String replyMineYn;
    /** 리플내용 */
    private String replyContent;
    /** 아이콘 */
    private String userIcon;
    /** 대댓글 여부 마진*/
    private String rplyMargin;

    public String getRplyMargin() {
        return rplyMargin;
    }

    public void setRplyMargin(String rplyMargin) {
        this.rplyMargin = rplyMargin;
    }

    public String getReplyNumber() {
        return replyNumber;
    }

    public void setReplyNumber(String replyNumber) {
        this.replyNumber = replyNumber;
    }

    public String getReplyUser() {
        return replyUser;
    }

    public void setReplyUser(String replyUser) {
        this.replyUser = replyUser;
    }

    public String getReplyDt() {
        return replyDt;
    }

    public void setReplyDt(String replyDt) {
        this.replyDt = replyDt;
    }

    public String getReplyMineYn() {
        return replyMineYn;
    }

    public void setReplyMineYn(String replyMineYn) {
        this.replyMineYn = replyMineYn;
    }

    public String getReplyContent() {
        return replyContent;
    }

    public void setReplyContent(String replyContent) {
        this.replyContent = replyContent;
    }

    public String getUserIcon() {
        return userIcon;
    }

    public void setUserIcon(String userIcon) {
        this.userIcon = userIcon;
    }
}
