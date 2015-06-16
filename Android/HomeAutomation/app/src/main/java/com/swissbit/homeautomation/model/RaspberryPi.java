package com.swissbit.homeautomation.model;

/**
 * Created by manit on 09/06/15.
 */
public final class RaspberryPi {

    private String id;
    private boolean status;

    public RaspberryPi(String id, String description, String name, boolean status) {
        this.id = id;
        this.description = description;
        this.name = name;
        this.status = status;
    }

    private String description;
    private String name;



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public static RaspberryPi createRaspberrPi(final String id, final String desc, final String name, final boolean status){
        return new RaspberryPi(id, desc, name, status);
    }

}
