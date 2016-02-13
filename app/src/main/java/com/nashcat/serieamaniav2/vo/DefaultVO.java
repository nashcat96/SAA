package com.nashcat.serieamaniav2.vo;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by nashc on 2016-02-02.
 */
public class DefaultVO implements Serializable, Cloneable {


    private static final long serialVersionUID = -8211694323621165921L;
    /** user ID */
    private String userId;
    private Map<String, String> loginCookies;
    private String loginYn;
    private String userNick;
    private String mid;

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getUserNick() {
        return userNick;
    }

    public void setUserNick(String userNick) {
        this.userNick = userNick;
    }

    public DefaultVO(){

    }

    public String getLoginYn() {
        return loginYn;
    }

    public void setLoginYn(String loginYn) {
        this.loginYn = loginYn;
    }


    public Map<String, String> getLoginCookies() {
        return loginCookies;
    }

    public void setLoginCookies(Map<String, String> loginCookies) {
        this.loginCookies = loginCookies;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        // TODO Auto-generated method stub
        return super.clone();
    }
}
