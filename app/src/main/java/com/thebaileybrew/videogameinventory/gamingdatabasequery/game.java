package com.thebaileybrew.videogameinventory.gamingdatabasequery;

public class game {
    private String gameName;
    private String gameGenre;
    private String gameSystem;
    private String gameReleased;
    private String gameDeveloper;
    private String gameDevCountry;
    private String gamePhone;
    private String gameDevEstablished;

    public game (String gameName, String gameGenre, String gameSystem, String gameReleased,
                 String gameDeveloper, String gameDevCountry, String gameDevEstablished) {
        this.gameName = gameName;
        this.gameGenre = gameGenre;
        this.gameSystem = gameSystem;
        this.gameReleased = gameReleased;
        this.gameDeveloper = gameDeveloper;
        this.gameDevCountry = gameDevCountry;
        this.gamePhone = "1-888-280-4331";
        this.gameDevEstablished = gameDevEstablished;
    }

    public String getGameName() {
        return gameName;
    }

    public String getGameGenre() {
        return gameGenre;
    }

    public String getGameSystem() {
        return gameSystem;
    }

    public String getGameReleased() {
        return gameReleased;
    }

    public String getGameDeveloper() {
        return gameDeveloper;
    }

    public String getGameDevCountry() {
        return gameDevCountry;
    }

    public String getGamePhone() {
        return gamePhone;
    }

    public String getGameDevEstablished() {
        return gameDevEstablished;
    }
}
