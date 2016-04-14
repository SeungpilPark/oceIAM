package org.opencloudengine.garuda.web.management;

import java.io.Serializable;
import java.sql.Date;

/**
 * Created by cloudine on 2015. 6. 3..
 */
public class Management implements Serializable {

    private long id;
    private long userId;
    private String groupName;
    private String groupKey;
    private String groupSecret;
    private String groupJwtSecret;
    private String description;
    private Date regDate;
    private Date updDate;

    public Management() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupKey() {
        return groupKey;
    }

    public void setGroupKey(String groupKey) {
        this.groupKey = groupKey;
    }

    public String getGroupSecret() {
        return groupSecret;
    }

    public void setGroupSecret(String groupSecret) {
        this.groupSecret = groupSecret;
    }

    public String getGroupJwtSecret() {
        return groupJwtSecret;
    }

    public void setGroupJwtSecret(String groupJwtSecret) {
        this.groupJwtSecret = groupJwtSecret;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getRegDate() {
        return regDate;
    }

    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

    public Date getUpdDate() {
        return updDate;
    }

    public void setUpdDate(Date updDate) {
        this.updDate = updDate;
    }

    @Override
    public String toString() {
        return "Management{" +
                "id=" + id +
                ", userId=" + userId +
                ", groupName='" + groupName + '\'' +
                ", groupKey='" + groupKey + '\'' +
                ", groupSecret='" + groupSecret + '\'' +
                ", groupJwtSecret='" + groupJwtSecret + '\'' +
                ", description='" + description + '\'' +
                ", regDate=" + regDate +
                ", updDate=" + updDate +
                '}';
    }
}