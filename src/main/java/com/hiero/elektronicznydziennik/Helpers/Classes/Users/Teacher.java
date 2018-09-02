package com.hiero.elektronicznydziennik.Helpers.Classes.Users;

import com.hiero.elektronicznydziennik.Helpers.Classes.FormOfEmployment;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.Date;

public class Teacher extends User {
    private final SimpleObjectProperty<Date> mHireDate = new SimpleObjectProperty<>();
    private FormOfEmployment mFormOfEmployment;
    private final SimpleStringProperty mFormOfEmploymentName = new SimpleStringProperty();

    public Teacher(Integer pId) {
        super(pId);
    }

    public Teacher setLogin(String login) {
        super.setLogin(login);
        return this;
    }

    public Teacher setFirstName(String firstName) {
        super.setFirstName(firstName);
        return this;
    }

    public Teacher setLastName(String lastName) {
        super.setLastName(lastName);
        return this;
    }

    public Teacher setPassword(String password) {
        super.setPassword(password);
        return this;
    }

    public Teacher setParent(boolean parent) {
        super.setParent(parent);
        return this;
    }

    public Teacher setTeacher(boolean teacher) {
        super.setTeacher(teacher);
        return this;
    }

    public Teacher setStudent(boolean student) {
        super.setStudent(student);
        return this;
    }

    public Teacher setAdmin(boolean admin) {
        super.setAdmin(admin);
        return this;
    }

    public Date getHireDate() {
        return mHireDate.get();
    }

    public SimpleObjectProperty<Date> hireDateProperty() {
        return mHireDate;
    }

    public Teacher setHireDate(Date hireDate) {
        this.mHireDate.set(hireDate);
        return this;
    }

    public FormOfEmployment getFormOfEmployment() {
        return mFormOfEmployment;
    }

    public Teacher setFormOfEmployment(FormOfEmployment formOfEmployment) {
        this.mFormOfEmployment = formOfEmployment;
        this.mFormOfEmploymentName.set(mFormOfEmployment.getName());
        return this;
    }

    public SimpleStringProperty FormOfEmploymentProperty() {
        return mFormOfEmploymentName;
    }
}
