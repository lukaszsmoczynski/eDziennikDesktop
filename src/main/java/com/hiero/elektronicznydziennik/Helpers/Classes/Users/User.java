package com.hiero.elektronicznydziennik.Helpers.Classes.Users;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class User {
    private final SimpleIntegerProperty mId = new SimpleIntegerProperty();
    private final SimpleStringProperty mLogin = new SimpleStringProperty();
    private final SimpleStringProperty mPassword = new SimpleStringProperty();
    private final SimpleStringProperty mFirstName = new SimpleStringProperty();
    private final SimpleStringProperty mLastName = new SimpleStringProperty();
    private final SimpleBooleanProperty mParent = new SimpleBooleanProperty();
    private final SimpleBooleanProperty mTeacher = new SimpleBooleanProperty();
    private final SimpleBooleanProperty mStudent = new SimpleBooleanProperty();
    private final SimpleBooleanProperty mAdmin = new SimpleBooleanProperty();

    public User(Integer id) {
        this.mId.set(id);
    }

    public Integer getId() {
        return mId.get();
    }

    public SimpleIntegerProperty IdProperty() {
        return mId;
    }

    public String getLogin() {
        return mLogin.get();
    }

    public SimpleStringProperty LoginProperty() {
        return mLogin;
    }

    public User setLogin(String login) {
        this.mLogin.set(login);
        return this;
    }

    public String getFirstName() {
        return mFirstName.get();
    }

    public SimpleStringProperty firstNameProperty() {
        return mFirstName;
    }

    public User setFirstName(String firstName) {
        this.mFirstName.set(firstName);
        return this;
    }

    public String getLastName() {
        return mLastName.get();
    }

    public SimpleStringProperty lastNameProperty() {
        return mLastName;
    }

    public User setLastName(String lastName) {
        this.mLastName.set(lastName);
        return this;
    }

    public String getPassword() {
        return mPassword.get();
    }

    public SimpleStringProperty passwordProperty() {
        return mPassword;
    }

    public User setPassword(String password) {
        this.mPassword.set(password);
        return this;
    }

    public boolean isParent() {
        return mParent.get();
    }

    public SimpleBooleanProperty parentProperty() {
        return mParent;
    }

    public User setParent(boolean parent) {
        this.mParent.set(parent);
        return this;
    }

    public boolean isTeacher() {
        return mTeacher.get();
    }

    public SimpleBooleanProperty teacherProperty() {
        return mTeacher;
    }

    public User setTeacher(boolean teacher) {
        this.mTeacher.set(teacher);
        return this;
    }

    public boolean isStudent() {
        return mStudent.get();
    }

    public SimpleBooleanProperty studentProperty() {
        return mStudent;
    }

    public User setStudent(boolean student) {
        this.mStudent.set(student);
        return this;
    }

    public boolean isAdmin() {
        return mAdmin.get();
    }

    public SimpleBooleanProperty adminProperty() {
        return mAdmin;
    }

    public User setAdmin(boolean admin) {
        this.mAdmin.set(admin);
        return this;
    }
}