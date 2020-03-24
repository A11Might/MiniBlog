package com.xidian.miniblog.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * @author qhhu
 * @date 2020/3/22 - 16:47
 */
public class Event {

    private int type;
    private int actorId;
    private int entityType;
    private int entityId;
    private int entityOwnerId;
    private Map<String, String> datas = new HashMap<>();

    public int getType() {
        return type;
    }

    public Event setType(int type) {
        this.type = type;
        return this;
    }

    public int getActorId() {
        return actorId;
    }

    public Event setActorId(int actorId) {
        this.actorId = actorId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public Event setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public Event setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityOwnerId() {
        return entityOwnerId;
    }

    public Event setEntityOwnerId(int entityOwnerId) {
        this.entityOwnerId = entityOwnerId;
        return this;
    }

    public Map<String, String> getDatas() {
        return datas;
    }

    public Event setDatas(Map<String, String> datas) {
        this.datas = datas;
        return this;
    }

    public String getDate(String key) {
        return datas.get(key);
    }

    public Event setDate(String key, String value) {
        this.datas.put(key, value);
        return this;
    }

    @Override
    public String toString() {
        return "Event{" +
                "type=" + type +
                ", actorId=" + actorId +
                ", entityType=" + entityType +
                ", entityId=" + entityId +
                ", entityOwnerId=" + entityOwnerId +
                ", datas=" + datas +
                '}';
    }
}
