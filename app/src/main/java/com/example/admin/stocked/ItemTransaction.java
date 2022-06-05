package com.example.admin.stocked;



public class ItemTransaction {
    Long timeStamp;
    String uuid;
    String itemuuid;
    float quantity;
    float pricePerUnity;
    boolean ItemEntering;

    public ItemTransaction() {
    }

    public ItemTransaction(Long timeStamp, String uuid, String itemuuid, float quantity, float pricePerUnity, boolean itemEntering) {
        this.timeStamp = timeStamp;
        this.uuid = uuid;
        this.itemuuid = itemuuid;
        this.quantity = quantity;
        this.pricePerUnity = pricePerUnity;
        ItemEntering = itemEntering;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getItemuuid() {
        return itemuuid;
    }

    public void setItemuuid(String itemuuid) {
        this.itemuuid = itemuuid;
    }

    public float getQuantity() {
        return quantity;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }

    public float getPricePerUnity() {
        return pricePerUnity;
    }

    public void setPricePerUnity(float pricePerUnity) {
        this.pricePerUnity = pricePerUnity;
    }

    public boolean isItemEntering() {
        return ItemEntering;
    }

    public void setItemEntering(boolean itemEntering) {
        ItemEntering = itemEntering;
    }
}

