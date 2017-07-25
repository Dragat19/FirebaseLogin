package com.example.firebaselogin.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by fede on 7/6/17.
 */

public class Register {

    @SerializedName("first_name")
    @Expose
    private String name;

    @SerializedName("last_name")
    @Expose
    private String last_name;

    @SerializedName("username")
    @Expose
    private String username;

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("password")
    @Expose
    private String password;

    public Register(String name, String last_name, String username, String email, String password) {
        this.name = name;
        this.last_name = last_name;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public Register(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
