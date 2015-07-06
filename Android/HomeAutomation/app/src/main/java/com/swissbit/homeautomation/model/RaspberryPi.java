package com.swissbit.homeautomation.model;

/**
 * Created by manit on 09/06/15.
 */
public final class RaspberryPi {

    private String id;
    private String secureElementId;
    private boolean status;
    private String description;
    private String name;


    public RaspberryPi(String id, String secureElementId, String description, String name, boolean status) {
        this.id = id;
        this.secureElementId = secureElementId;
        this.description = description;
        this.name = name;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getSecureElementId() {
        return secureElementId;
    }

    public void setSecureElementId(String secureElementId) {
        this.secureElementId = secureElementId;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static RaspberryPi createRaspberryPi(final String id, final String secureElementId, final String name, final String desc, final boolean status){
        return new RaspberryPi(id, secureElementId, desc, name, status);
    }

}
