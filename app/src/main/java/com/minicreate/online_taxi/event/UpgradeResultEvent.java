package com.minicreate.online_taxi.event;

/**
 * 升级错误事件
 */
public class UpgradeResultEvent {
    public int currentPackage;//缺失包号
    public int state;//状态

    @Override
    public String toString() {
        return "currentPackage = " + currentPackage + " ,state = " + state;
    }
}
