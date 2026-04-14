/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author Usuario
 */
public class userModel {
    private int idUser;
    private String name;
    private String email;
    private String phone;
    private boolean activo;

    public userModel(int idUser, String name, String email, String phone, boolean activo) {
        this.idUser = idUser;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.activo = activo;
    }

    public userModel(int idUser, String name, String email, String phone) {
        this.idUser = idUser;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.activo = true; // Default verdadero si no se especifica
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
