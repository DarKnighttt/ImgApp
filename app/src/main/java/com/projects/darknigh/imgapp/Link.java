package com.projects.darknigh.imgapp;

class Link {
    private int id;
    private String link;
    private int status;
    private double openTime;

    public Link(){}

    public Link(String link, int status, double openTime) {
        this.link = link;
        this.status = status;
        this.openTime = openTime;
    }

    public Link(int id, String link, int status, double openTime) {
        this.id = id;
        this.link = link;
        this.status = status;
        this.openTime = openTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public double getOpenTime() {
        return openTime;
    }

    public void setOpenTime(double openTime) {
        this.openTime = openTime;
    }

    @Override
    public String toString() {
        return "Link id: " + id + "; link: " + link + "; status: " + status + "; openTime: " + openTime;
    }
}
