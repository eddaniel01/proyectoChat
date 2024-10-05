package com.example.proyectochatmovil;

public class User {

    private String id;
    private String imageURL;
    private String username;
    private String fcmToken;

    public User(String id, String imageURL, String username) {
        this.id = id;
        this.imageURL = imageURL;
        this.username = username;
    }

    public User() {


    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImagenURL() {
        return imageURL;
    }

    public void setImagenURL(String imagenURL) {
        this.imageURL = imagenURL;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}
