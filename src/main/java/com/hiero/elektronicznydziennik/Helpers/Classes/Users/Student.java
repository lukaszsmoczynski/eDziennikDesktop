package com.hiero.elektronicznydziennik.Helpers.Classes.Users;

import com.hiero.elektronicznydziennik.Helpers.Classes.DocumentType;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.Date;

public class Student extends User {
    private final SimpleObjectProperty<Date> mDateOfBirth = new SimpleObjectProperty<>();
    private final SimpleStringProperty mPlaceOfBirth = new SimpleStringProperty();
    private DocumentType documentType;
    private final SimpleStringProperty mTypeOfDocument = new SimpleStringProperty();
    private final SimpleStringProperty mDocumentNumber = new SimpleStringProperty();
    private final SimpleStringProperty mPESEL = new SimpleStringProperty();
    private final SimpleStringProperty mCountry = new SimpleStringProperty();
    private final SimpleStringProperty mCity = new SimpleStringProperty();
    private final SimpleStringProperty mRegion = new SimpleStringProperty();
    private final SimpleStringProperty mStreet = new SimpleStringProperty();
    private final SimpleStringProperty mHouseNumber = new SimpleStringProperty();
    private final SimpleStringProperty mFlatNumber = new SimpleStringProperty();
    private final SimpleStringProperty mPostalCode = new SimpleStringProperty();
    private final SimpleStringProperty mReasonForLeave = new SimpleStringProperty();

    private Boolean present;

    public Student(Integer pId) {
        super(pId);

        present = null;
    }

    public Student setLogin(String login) {
        super.setLogin(login);
        return this;
    }

    public Student setFirstName(String firstName) {
        super.setFirstName(firstName);
        return this;
    }

    public Student setLastName(String lastName) {
        super.setLastName(lastName);
        return this;
    }

    public Student setPassword(String password) {
        super.setPassword(password);
        return this;
    }

    public Student setParent(boolean parent) {
        super.setParent(parent);
        return this;
    }

    public Student setTeacher(boolean teacher) {
        super.setTeacher(teacher);
        return this;
    }

    public Student setStudent(boolean student) {
        super.setStudent(student);
        return this;
    }

    public Student setAdmin(boolean admin) {
        super.setAdmin(admin);
        return this;
    }

    public Date getDateOfBirth() {
        return mDateOfBirth.get();
    }

    public SimpleObjectProperty<Date> dateOfBirthProperty() {
        return mDateOfBirth;
    }

    public Student setDateOfBirth(Date dateOfBirth) {
        this.mDateOfBirth.set(dateOfBirth);
        return this;
    }

    public String getPlaceOfBirth() {
        return mPlaceOfBirth.get();
    }

    public SimpleStringProperty PlaceOfBirthProperty() {
        return mPlaceOfBirth;
    }

    public Student setPlaceOfBirth(String PlaceOfBirth) {
        this.mPlaceOfBirth.set(PlaceOfBirth);
        return this;
    }

    public String getTypeOfDocument() {
        return mTypeOfDocument.get();
    }

    public SimpleStringProperty TypeOfDocumentProperty() {
        return mTypeOfDocument;
    }

    public String getDocumentNumber() {
        return mDocumentNumber.get();
    }

    public SimpleStringProperty DocumentNumberProperty() {
        return mDocumentNumber;
    }

    public Student setDocumentNumber(String DocumentNumber) {
        this.mDocumentNumber.set(DocumentNumber);
        return this;
    }

    public String getPESEL() {
        return mPESEL.get();
    }

    public SimpleStringProperty PESELProperty() {
        return mPESEL;
    }

    public Student setPESEL(String PESEL) {
        this.mPESEL.set(PESEL);
        return this;
    }

    public String getCountry() {
        return mCountry.get();
    }

    public SimpleStringProperty CountryProperty() {
        return mCountry;
    }

    public Student setCountry(String Country) {
        this.mCountry.set(Country);
        return this;
    }

    public String getCity() {
        return mCity.get();
    }

    public SimpleStringProperty CityProperty() {
        return mCity;
    }

    public Student setCity(String City) {
        this.mCity.set(City);
        return this;
    }

    public String getRegion() {
        return mRegion.get();
    }

    public SimpleStringProperty RegionProperty() {
        return mRegion;
    }

    public Student setRegion(String Region) {
        this.mRegion.set(Region);
        return this;
    }

    public String getStreet() {
        return mStreet.get();
    }

    public SimpleStringProperty StreetProperty() {
        return mStreet;
    }

    public Student setStreet(String Street) {
        this.mStreet.set(Street);
        return this;
    }

    public String getHouseNumber() {
        return mHouseNumber.get();
    }

    public SimpleStringProperty HouseNumberProperty() {
        return mHouseNumber;
    }

    public Student setHouseNumber(String HouseNumber) {
        this.mHouseNumber.set(HouseNumber);
        return this;
    }

    public String getFlatNumber() {
        return mFlatNumber.get();
    }

    public SimpleStringProperty FlatNumberProperty() {
        return mFlatNumber;
    }

    public Student setFlatNumber(String FlatNumber) {
        this.mFlatNumber.set(FlatNumber);
        return this;
    }

    public String getPostalCode() {
        return mPostalCode.get();
    }

    public SimpleStringProperty PostalCodeProperty() {
        return mPostalCode;
    }

    public Student setPostalCode(String PostalCode) {
        this.mPostalCode.set(PostalCode);
        return this;
    }

    public String getReasonForLeave() {
        return mReasonForLeave.get();
    }

    public SimpleStringProperty ReasonForLeaveProperty() {
        return mReasonForLeave;
    }

    public Student setReasonForLeave(String ReasonForLeave) {
        this.mReasonForLeave.set(ReasonForLeave);
        return this;
    }

    public Boolean getPresent() {
        return present;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public Student setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
        this.mTypeOfDocument.set(documentType.getName());
        return this;
    }
}
