package com.example.admin.stocked;

public class Category {
    private String uuid;
    private String name;
    private String description;
    private String imagelocation = "";
    public Category() {

    }

    public Category(String name) {
        this.name = name;
    }

    public Category(String name,String uuid){
        this.name = name;
        this.uuid = uuid;
    }


    public Category(String uuid, String name, String description, String imagelocation) {
        this.uuid = uuid;
        this.name = name;
        this.description = description;
        this.imagelocation = imagelocation;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    public String getImagelocation() {
        return imagelocation;
    }

    public void setImagelocation(String imagelocation) {
        this.imagelocation = imagelocation;
    }

    @Override //metodo para el spinner ya que este carga la clase y no carga
    public String toString() {
        return name;
    }
}
