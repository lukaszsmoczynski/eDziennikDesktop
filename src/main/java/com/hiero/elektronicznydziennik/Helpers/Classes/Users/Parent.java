package com.hiero.elektronicznydziennik.Helpers.Classes.Users;

import javafx.beans.property.SimpleStringProperty;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class Parent extends User {
    private final SimpleStringProperty mCountry = new SimpleStringProperty();
    private final SimpleStringProperty mCity = new SimpleStringProperty();
    private final SimpleStringProperty mRegion = new SimpleStringProperty();
    private final SimpleStringProperty mStreet = new SimpleStringProperty();
    private final SimpleStringProperty mHouseNumber = new SimpleStringProperty();
    private final SimpleStringProperty mFlatNumber = new SimpleStringProperty();
    private final SimpleStringProperty mPostalCode = new SimpleStringProperty();
    private final SimpleStringProperty mEMail = new SimpleStringProperty();
    private ArrayList<String> mParentPhones;
    private final SimpleStringProperty mPhones = new SimpleStringProperty();
    private List<Pair<Integer, Integer>> mStudentsRelationsList;

    public Parent(Integer pId) {
        super(pId);
    }

    public Parent setLogin(String login) {
        super.setLogin(login);
        return this;
    }

    public Parent setFirstName(String firstName) {
        super.setFirstName(firstName);
        return this;
    }

    public Parent setLastName(String lastName) {
        super.setLastName(lastName);
        return this;
    }

    public Parent setPassword(String password) {
        super.setPassword(password);
        return this;
    }

    public Parent setParent(boolean parent) {
        super.setParent(parent);
        return this;
    }

    public Parent setTeacher(boolean teacher) {
        super.setTeacher(teacher);
        return this;
    }

    public Parent setStudent(boolean student) {
        super.setStudent(student);
        return this;
    }

    public Parent setAdmin(boolean admin) {
        super.setAdmin(admin);
        return this;
    }
    
    public String getCountry() {
        return mCountry.get();
    }

    public SimpleStringProperty CountryProperty() {
        return mCountry;
    }

    public Parent setCountry(String Country) {
        this.mCountry.set(Country);
        return this;
    }

    public String getCity() {
        return mCity.get();
    }

    public SimpleStringProperty CityProperty() {
        return mCity;
    }

    public Parent setCity(String City) {
        this.mCity.set(City);
        return this;
    }

    public String getRegion() {
        return mRegion.get();
    }

    public SimpleStringProperty RegionProperty() {
        return mRegion;
    }

    public Parent setRegion(String Region) {
        this.mRegion.set(Region);
        return this;
    }

    public String getStreet() {
        return mStreet.get();
    }

    public SimpleStringProperty StreetProperty() {
        return mStreet;
    }

    public Parent setStreet(String Street) {
        this.mStreet.set(Street);
        return this;
    }

    public String getHouseNumber() {
        return mHouseNumber.get();
    }

    public SimpleStringProperty HouseNumberProperty() {
        return mHouseNumber;
    }

    public Parent setHouseNumber(String HouseNumber) {
        this.mHouseNumber.set(HouseNumber);
        return this;
    }

    public String getFlatNumber() {
        return mFlatNumber.get();
    }

    public SimpleStringProperty FlatNumberProperty() {
        return mFlatNumber;
    }

    public Parent setFlatNumber(String FlatNumber) {
        this.mFlatNumber.set(FlatNumber);
        return this;
    }

    public String getPostalCode() {
        return mPostalCode.get();
    }

    public SimpleStringProperty PostalCodeProperty() {
        return mPostalCode;
    }

    public Parent setPostalCode(String PostalCode) {
        this.mPostalCode.set(PostalCode);
        return this;
    }

    public ArrayList<String> getParentPhones() {
        return mParentPhones;
    }

    public Parent setParentPhones(ArrayList<String> phones) {
        this.mParentPhones = phones;
        this.mPhones.set(String.join(", ", mParentPhones));
        return this;
    }
    public SimpleStringProperty PhonesProperty() {
        return mPhones;
    }

    public String getEMail() {
        return mEMail.get();
    }

    public SimpleStringProperty EMailProperty() {
        return mEMail;
    }

    public Parent setEMail(String eMail) {
        this.mEMail.set(eMail);
        return this;
    }

    public List<Pair<Integer, Integer>> getStudentsRelationsList() {
        return mStudentsRelationsList;
    }

    public Parent setStudentsRelationsList(List<Pair<Integer, Integer>> studentsRelationsList) {
        mStudentsRelationsList = studentsRelationsList;
        return this;
    }
}
