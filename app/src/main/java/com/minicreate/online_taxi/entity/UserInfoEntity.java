package com.minicreate.online_taxi.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 登录历史实体类
 */
@Entity
public class UserInfoEntity {
    private String id;
    private String name;
    private String operation;
    private String signInTime;

    @Generated(hash = 427174320)
    public UserInfoEntity(String id, String name, String operation,
            String signInTime) {
        this.id = id;
        this.name = name;
        this.operation = operation;
        this.signInTime = signInTime;
    }

    @Generated(hash = 2042969639)
    public UserInfoEntity() {
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOperation() {
        return this.operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSignInTime() {
        return this.signInTime;
    }

    public void setSignInTime(String signInTime) {
        this.signInTime = signInTime;
    }
}
