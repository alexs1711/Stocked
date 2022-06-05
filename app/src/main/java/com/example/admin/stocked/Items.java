package com.example.admin.stocked;

public class Items {
    private String uuid = "";
    private String itemname = "";
    private String itembarcode = "";
    private String imagelocation = "";
    private String description;//añadir esto
    private String itemunits;//que diga si las cocacolas son un paquete de 12 y ponga x12 o si es lechuga pues los kg
    private String Catuuid;


public Items() {

}

    public Items(String uuid, String itemname, String itembarcode, String imagelocation, String description, String itemunits, String catuuid) {
        this.uuid = uuid;
        this.itemname = itemname;
        this.itembarcode = itembarcode;
        this.imagelocation = imagelocation;
        this.description = description;
        this.itemunits = itemunits;
        this.Catuuid = catuuid;
    }

    /*public Items(String uuid, String itemname, String itembarcode, String imagelocation, String description, String itemunits) {
        this.uuid = uuid;
        this.itemname = itemname;
        this.itembarcode = itembarcode;
        this.imagelocation = imagelocation;
        this.description = description;
        this.itemunits = itemunits;
    }*/

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public String getItemunits() { return itemunits; }

    public void setItemunits(String itemunits) { this.itemunits = itemunits; }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setItemname(String itemname) {
        this.itemname = itemname;
    }

    public void setItembarcode(String itembarcode) {
        this.itembarcode = itembarcode;
    }

    public void setImagelocation(String imagelocation) {
        this.imagelocation = imagelocation;
    }

    public String getItemname() {
        return itemname;
    }

    public String getItembarcode() {
        return itembarcode;
    }

    public String getImagelocation() {
        return imagelocation;
    }

    public String getCatuuid() {
        return Catuuid;
    }

    public void setCatuuid(String catuuid) {
        Catuuid = catuuid;
    }

    @Override
    public String toString() {
        return itemname;
    }
}