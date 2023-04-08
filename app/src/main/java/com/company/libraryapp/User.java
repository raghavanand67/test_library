package com.company.libraryapp;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.PropertyName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
    public User() {
    }


    private String name, email;
    private List<Integer> book = new ArrayList<Integer>();
    private List<Integer> fine = new ArrayList<Integer>();
    private List<Integer> re = new ArrayList<Integer>();
    private List<Timestamp> date = new ArrayList<Timestamp>();
    private String enroll;
    private int card;
    private int type;

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    private String fcmToken;

    public int getLeft_fine() {
        return left_fine;
    }

    public void setLeft_fine(int left_fine) {
        this.left_fine = left_fine;
    }

    private int left_fine;


    @PropertyName("type")
    public int getType() {
        return type;
    }

    @PropertyName("type")
    public void setType(int type) {
        this.type = type;
    }



    public User(String name, String email, String enroll, int card, int type) {
        this.name = name;
        this.email = email;
        this.enroll = enroll;
        this.card = card;
        this.type = type;
    }
    @PropertyName("name")
    public String getName() {
        return name;
    }

    @PropertyName("name")
    public void setName(String name) {
        this.name = name;
    }

    @PropertyName("email")
    public String getEmail() {
        return email;
    }
    @PropertyName("email")
    public void setEmail(String email) {
        this.email = email;
    }


    @PropertyName("book")
    public List<Integer> getBook() {
        return book;
    }

    @PropertyName("book")

    public void setBook(List<Integer> book) {
        this.book = book;
    }

    public List<Integer> getFine() {
        return fine;
    }

    public void setFine(List<Integer> fine) {
        this.fine = fine;
    }

    public List<Integer> getRe() {
        return re;
    }

    public void setRe(List<Integer> re) {
        this.re = re;
    }

    public List<Timestamp> getDate() {
        return date;
    }

    public void setDate(List<Timestamp> date) {
        this.date = date;
    }

    public String getEnroll() {
        return enroll;
    }

    public void setEnroll(String enroll) {
        this.enroll = enroll;
    }

    @PropertyName("card")
    public int getCard() {
        return card;
    }

    @PropertyName("card")
    public void setCard(int card) {
        this.card = card;
    }

    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", book=" + book +
                '}';
    }
}





