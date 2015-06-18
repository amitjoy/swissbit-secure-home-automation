package com.swissbit.homeautomation.model;

/**
 * Created by manit on 17/06/15.
 */
public class Device {

    private String id;
    private String raspberryId;
    private String name;
    private String description;
    private boolean status;

    public Device(String id, String raspberryId, String name, String description, boolean status) {
        this.id = id;
        this.raspberryId = raspberryId;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRaspberryId() {
        return raspberryId;
    }

    public void setRaspberryId(String raspberryId) {
        this.raspberryId = raspberryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public static Device createDevice(final String id,final String raspberryId,final String name,final String description,final boolean status){
        return new Device(id, raspberryId, name, description, status);
    }
}
