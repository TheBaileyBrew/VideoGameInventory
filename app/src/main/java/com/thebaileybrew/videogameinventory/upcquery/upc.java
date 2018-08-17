package com.thebaileybrew.videogameinventory.upcquery;

public class upc {
    private String upcCode;
    private String upcGameName;
    private String upcGameImage;
    private String upcGameLowPrice;

    public upc (String upcCode, String upcGameName, String upcGameImage, String upcGameLowPrice) {
        this.upcCode = upcCode;
        this.upcGameName = upcGameName;
        this.upcGameImage = upcGameImage;
        this.upcGameLowPrice = upcGameLowPrice;
    }

    public String getUpcCode() {
        return upcCode;
    }

    public String getUpcGameName() {
        return upcGameName;
    }

    public String getUpcGameImage() {
        return upcGameImage;
    }

    public String getUpcGameLowPrice() {
        return upcGameLowPrice;
    }
}
