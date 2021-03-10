package com.cs65.homie.models;

import android.location.Location;

import java.util.ArrayList;
import java.util.List;

public class Profile {
    private String id;
    private String password;
    private String email;
    private String firstName;
    private String avatarImage;

    private String bio;
    private int gender;
    private List<String> keywords;
    private List<String> images;
    private double minPrice;
    private double maxPrice;
    private boolean privateBathroom;
    private List<String> likes;
    private Location location;
    private String address;
    private double radius;
    private boolean hasApartment;
    private boolean isPetFriendly;
    private boolean isSmoking;

    @Override
    public String toString() {
        return "Profile{" +
                "id='" + id + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", avatarImage='" + avatarImage + '\'' +
                ", bio='" + bio + '\'' +
                ", gender=" + gender +
                ", keywords=" + keywords +
                ", images=" + images +
                ", minPrice=" + minPrice +
                ", maxPrice=" + maxPrice +
                ", privateBathroom=" + privateBathroom +
                ", likes=" + likes +
                ", location=" + location +
                ", address='" + address + '\'' +
                ", radius=" + radius +
                ", hasApartment=" + hasApartment +
                ", isPetFriendly=" + isPetFriendly +
                ", isSmoking=" + isSmoking +
                '}';
    }

    public Profile() {
//        firstName = "Matt";
//        bio = "Diehard tennis player, love hula hooping more than anything.";
        gender = 1;
        minPrice = 600;
        maxPrice = 1000;
        privateBathroom = false;
        address = "Dartmouth College";
        radius = 5; // imperial by default
        hasApartment = false;
        isPetFriendly = false;
        isSmoking = false;

        likes = new ArrayList<>();
        keywords = new ArrayList<>();
        images = new ArrayList<>();
    }

    public Profile(boolean withApartment) {
        this();
        hasApartment = withApartment;
    }
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(double minPrice) {
        this.minPrice = minPrice;
    }

    public double getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(double maxPrice) {
        this.maxPrice = maxPrice;
    }

    public boolean isPrivateBathroom() {
        return privateBathroom;
    }

    public void setPrivateBathroom(boolean privateBathroom) {
        this.privateBathroom = privateBathroom;
    }

    public List<String> getLikes() {
        return likes;
    }

    public void setLikes(List<String> likes) {
        this.likes = likes;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public boolean isHasApartment() {
        return hasApartment;
    }

    public void setHasApartment(boolean hasApartment) {
        this.hasApartment = hasApartment;
    }

    public boolean isPetFriendly() {
        return isPetFriendly;
    }

    public void setPetFriendly(boolean petFriendly) {
        isPetFriendly = petFriendly;
    }

    public boolean isSmoking() {
        return isSmoking;
    }

    public void setSmoking(boolean smoking) {
        isSmoking = smoking;
    }

    public String getAvatarImage() {
        return avatarImage;
    }

    public void setAvatarImage(String avatarImage) {
        this.avatarImage = avatarImage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }
}
