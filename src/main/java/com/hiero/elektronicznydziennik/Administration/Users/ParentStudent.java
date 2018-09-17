package com.hiero.elektronicznydziennik.Administration.Users;

import com.hiero.elektronicznydziennik.Helpers.Classes.DocumentType;
import com.hiero.elektronicznydziennik.Helpers.Classes.RelationType;
import com.hiero.elektronicznydziennik.Helpers.Classes.Users.Parent;
import com.hiero.elektronicznydziennik.Helpers.Classes.Users.Student;
import com.hiero.elektronicznydziennik.Helpers.Controller;
import com.hiero.elektronicznydziennik.Helpers.Functions;
import com.hiero.elektronicznydziennik.WebService.MySQLWebService;
import com.hiero.elektronicznydziennik.WebService.WebServiceConstants;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.util.Pair;
import javafx.util.StringConverter;
import org.json.JSONObject;

import javax.swing.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

public class ParentStudent extends Controller implements Initializable {
    public ListView<Parent> lvParents;
    public ListView<Student> lvStudents;
    public Button btnSave;
    public Button btnCancel;
    public ComboBox<RelationType> cbRelationType;
    public Label lblRelationType;

    private LoadRelationTypesTask loadRelationTypesTask;
    private LoadParentsTask loadParentsTask;
    private LoadStudentsTask loadStudentsTask;
    private SaveParentChildrenTask saveParentChildrenTask;

    private ObservableList<Parent> parentsList = FXCollections.observableArrayList();
    private ObservableList<Student> studentsList = FXCollections.observableArrayList();
    private ObservableMap<Student, BooleanProperty> studentsCheckList = FXCollections.observableHashMap();
    private ObservableList<RelationType> relationsTypesList = FXCollections.observableArrayList();

    private boolean disableLvStudentsListeners;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cbRelationType.setVisible(false);
        lblRelationType.setVisible(false);

        lvParents.setItems(parentsList);

        lvParents.setCellFactory(param -> new ListCell<Parent>(){
            @Override
            protected void updateItem(Parent item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    setText(item.getLogin());
                }
            }
        });

        lvParents.getSelectionModel().selectedItemProperty().addListener((observable, oldParent, newParent) -> {
            LoadParentChildren(newParent);

            Student selectedStudent = lvStudents.getSelectionModel().getSelectedItem();

            cbRelationType.setVisible(loadRelationTypesTask == null && selectedStudent != null && studentsCheckList.get(selectedStudent).get() && newParent != null);
            lblRelationType.setVisible(cbRelationType.isVisible());
        });

        lvStudents.setItems(studentsList);

        lvStudents.setCellFactory(lvStudents -> new CheckBoxListCell<>(student -> {
            BooleanProperty checked = new SimpleBooleanProperty();
            studentsCheckList.put(student, checked);
            return checked;
        }, new StringConverter<Student>() {
            @Override
            public String toString(Student object) {
                return object.getLogin();
            }

            @Override
            public Student fromString(String string) {
                return null;
            }
        }));

        lvStudents.getSelectionModel().selectedItemProperty().addListener((observable, oldStudent, newStudent) -> {
            disableLvStudentsListeners = true;

            Parent selectedParent = lvParents.getSelectionModel().getSelectedItem();

            LoadRelationType(selectedParent, newStudent);

            cbRelationType.setVisible(loadRelationTypesTask == null && selectedParent != null && newStudent != null && studentsCheckList.get(newStudent).get());
            lblRelationType.setVisible(cbRelationType.isVisible());

            disableLvStudentsListeners = false;
        });

        cbRelationType.setItems(relationsTypesList);

        cbRelationType.setConverter(new StringConverter<RelationType>() {
            @Override
            public String toString(RelationType object) {
                return object.getName();
            }

            @Override
            public RelationType fromString(String string) {
                return null;
            }
        });

        cbRelationType.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (disableLvStudentsListeners) {
                return;
            }

            Parent selectedParent = lvParents.getSelectionModel().getSelectedItem();

            if (selectedParent == null) {
                return;
            }

            Student selectedStudent = lvStudents.getSelectionModel().getSelectedItem();

            if (selectedStudent == null) {
                return;
            }

            for (Pair<Integer, Integer> studentRelation : selectedParent.getStudentsRelationsList()) {
                if (studentRelation.getKey().equals(selectedStudent.getId())) {
                    selectedParent.getStudentsRelationsList().remove(studentRelation);
                    selectedParent.getStudentsRelationsList().add(new Pair<>(studentRelation.getKey(), newValue.getId()));

                    break;
                }
            }
        });

        if (loadParentsTask == null)
            (loadParentsTask = new LoadParentsTask()).execute();

        if (loadStudentsTask == null)
            (loadStudentsTask = new LoadStudentsTask()).execute();

        if (loadRelationTypesTask == null)
            (loadRelationTypesTask = new LoadRelationTypesTask()).execute();
    }

    private void LoadRelationType(Parent parent, Student student) {
        if (parent == null) {
            return;
        }

        if (student == null) {
            return;
        }

        for (Pair<Integer, Integer> studentRelation : parent.getStudentsRelationsList()) {
            if (student.getId().equals(studentRelation.getKey())) {
                for (RelationType relationType : relationsTypesList) {
                    if (studentRelation.getValue().equals(relationType.getId())) {
                        cbRelationType.setValue(relationType);

                        break;
                    }
                }

                break;
            }
        }
    }

    void SetButtonActive(final Boolean active) {
        Platform.runLater(() -> {
            btnSave.setText(!active ? "" : "Zapisz");
            btnSave.setGraphic(!active ? new ProgressIndicator() : null);
        });
    }

    public void onBtnSaveAction(ActionEvent actionEvent) {
        if (saveParentChildrenTask != null) {
            return;
        }

        Parent selectedParent = lvParents.getSelectionModel().getSelectedItem();

        if (selectedParent == null) {
            return;
        }

        Map<Student, RelationType> studentRelationTypeMap = new HashMap<>();
        for (Map.Entry<Student, BooleanProperty> entry : studentsCheckList.entrySet()) {
            if (entry.getValue().get()) {
                RelationType relationType = null;
                for (Pair<Integer, Integer> studentRelation : selectedParent.getStudentsRelationsList()) {
                    if (studentRelation.getKey().equals(entry.getKey().getId())) {
                        relationType = new RelationType(studentRelation.getValue());
                        break;
                    }
                }
                studentRelationTypeMap.put(new Student(entry.getKey().getId()), relationType);
            }
        }

        (saveParentChildrenTask = new SaveParentChildrenTask(new Parent(selectedParent.getId()), studentRelationTypeMap)).execute();
    }

    public void onBtnCancelAction(ActionEvent actionEvent) {
        LoadParentChildren(lvParents.getSelectionModel().getSelectedItem());
    }

    private void LoadParentChildren(Parent selectedParent) {
        if (selectedParent == null) {
            return;
        }

        for (Map.Entry<Student, BooleanProperty> entry : studentsCheckList.entrySet()) {
            entry.getValue().setValue(false);

            for (Pair studentRelation : selectedParent.getStudentsRelationsList()) {
                if (studentRelation.getKey().equals(entry.getKey().getId())) {
                    entry.getValue().setValue(true);
                    break;
                }
            }
        }
    }

    private class LoadParentsTask extends SwingWorker {
        private JSONObject mResult;

        @Override
        protected Object doInBackground() throws Exception {
            try {
                mResult = new MySQLWebService().GetParents(true);
            } catch (Exception e) {
                e.printStackTrace();
                Functions.SaveLog(e);
            }
            return null;
        }

        @Override
        protected void done() {
            try {
                if (mResult.getBoolean(WebServiceConstants.GetParents.Result.RESULT)) {
                    Platform.runLater(() -> {
                        try {
                            parentsList.clear();
                            for (int i = 0; i < mResult.getJSONArray(WebServiceConstants.GetParents.Result.PARENTS).length(); i++) {
                                JSONObject parent = mResult.getJSONArray(WebServiceConstants.GetParents.Result.PARENTS).getJSONObject(i);
                                ArrayList<String> phones = new ArrayList<>();

                                for (int j = 0; j < parent.getJSONArray(WebServiceConstants.GetParents.Result.Parents.PHONES).length(); j++) {
                                    phones.add(parent.getJSONArray(WebServiceConstants.GetParents.Result.Parents.PHONES).getString(j));
                                }

                                List<Pair<Integer, Integer>> studentsRelations = new ArrayList<>();

                                for (int j = 0; j < parent.getJSONArray(WebServiceConstants.GetParents.Result.Parents.STUDENTS_RELATIONS).length(); j++) {
                                    JSONObject studentRelation = parent.getJSONArray(WebServiceConstants.GetParents.Result.Parents.STUDENTS_RELATIONS).getJSONObject(j);
                                    if (!studentRelation.getString(WebServiceConstants.GetParents.Result.Parents.StudentsRelations.STUDENT_ID).isEmpty())
                                        studentsRelations.add(new Pair<>(studentRelation.getInt(WebServiceConstants.GetParents.Result.Parents.StudentsRelations.STUDENT_ID),
                                                studentRelation.getInt(WebServiceConstants.GetParents.Result.Parents.StudentsRelations.RELATION_TYPE_ID)));
                                }

                                parentsList.add(new Parent(parent.getInt(WebServiceConstants.GetParents.Result.Parents.ID))
                                        .setLogin(parent.getString(WebServiceConstants.GetParents.Result.Parents.LOGIN))
                                        .setFirstName(parent.getString(WebServiceConstants.GetParents.Result.Parents.NAME))
                                        .setLastName(parent.getString(WebServiceConstants.GetParents.Result.Parents.SURNAME))
                                        .setCity(parent.getString(WebServiceConstants.GetParents.Result.Parents.CITY))
                                        .setCountry(parent.getString(WebServiceConstants.GetParents.Result.Parents.COUNTRY))
                                        .setFlatNumber(!parent.isNull(WebServiceConstants.GetParents.Result.Parents.FLAT_NUMBER) ? parent.getString(WebServiceConstants.GetParents.Result.Parents.FLAT_NUMBER) : "")
                                        .setHouseNumber(parent.getString(WebServiceConstants.GetParents.Result.Parents.HOUSE_NUMBER))
                                        .setPostalCode(parent.getString(WebServiceConstants.GetParents.Result.Parents.POSTAL_CODE))
                                        .setStreet(parent.getString(WebServiceConstants.GetParents.Result.Parents.STREET))
                                        .setRegion(!parent.isNull(WebServiceConstants.GetParents.Result.Parents.REGION) ? parent.getString(WebServiceConstants.GetParents.Result.Parents.REGION) : "")
                                        .setEMail(!parent.isNull(WebServiceConstants.GetParents.Result.Parents.E_MAIL) ? parent.getString(WebServiceConstants.GetParents.Result.Parents.E_MAIL) : "")
                                        .setParentPhones(phones)
                                        .setStudentsRelationsList(studentsRelations)
                                );
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Functions.SaveLog(e);
                        }
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
                Functions.SaveLog(e);
            }
            finally {
                loadParentsTask = null;
            }
        }
    }

    public class LoadStudentsTask extends SwingWorker {
        private JSONObject mResult;

        @Override
        protected Object doInBackground() throws Exception {
            try {
                mResult = new MySQLWebService().GetStudents();
            } catch (Exception e) {
                e.printStackTrace();
                Functions.SaveLog(e);
            }
            return null;
        }

        @Override
        protected void done() {
            try {
                if (mResult.getBoolean(WebServiceConstants.GetStudents.Result.RESULT)) {
                    Platform.runLater(() -> {
                        try {
                            studentsList.clear();
                            for (int i = 0; i < mResult.getJSONArray(WebServiceConstants.GetStudents.Result.STUDENTS).length(); i++) {
                                JSONObject student = mResult.getJSONArray(WebServiceConstants.GetStudents.Result.STUDENTS).getJSONObject(i);
                                studentsList.add(new Student(student.getInt(WebServiceConstants.GetStudents.Result.Students.ID))
                                        .setLogin(student.getString(WebServiceConstants.GetStudents.Result.Students.LOGIN))
                                        .setFirstName(student.getString(WebServiceConstants.GetStudents.Result.Students.NAME))
                                        .setLastName(student.getString(WebServiceConstants.GetStudents.Result.Students.SURNAME))
                                        .setDateOfBirth(new SimpleDateFormat("yyyy-MM-dd").parse(student.getString(WebServiceConstants.GetStudents.Result.Students.DATE_OF_BIRTH)))
                                        .setPlaceOfBirth(student.getString(WebServiceConstants.GetStudents.Result.Students.PLACE_OF_BIRTH))
                                        .setCity(student.getString(WebServiceConstants.GetStudents.Result.Students.CITY))
                                        .setCountry(student.getString(WebServiceConstants.GetStudents.Result.Students.COUNTRY))
                                        .setDocumentNumber(student.getString(WebServiceConstants.GetStudents.Result.Students.DOCUMENT_NUMBER))
                                        .setFlatNumber(!student.isNull(WebServiceConstants.GetStudents.Result.Students.FLAT_NUMBER) ? student.getString(WebServiceConstants.GetStudents.Result.Students.FLAT_NUMBER) : "")
                                        .setHouseNumber(student.getString(WebServiceConstants.GetStudents.Result.Students.HOUSE_NUMBER))
                                        .setDocumentType(new DocumentType(student.getInt(WebServiceConstants.GetStudents.Result.Students.ID_DOCUMENT_TYPE))
                                                .setName(student.getString(WebServiceConstants.GetStudents.Result.Students.TYPE_OF_DOCUMENT)))
                                        .setPESEL(!student.isNull(WebServiceConstants.GetStudents.Result.Students.PESEL) ? student.getString(WebServiceConstants.GetStudents.Result.Students.PESEL) : "")
                                        .setPostalCode(student.getString(WebServiceConstants.GetStudents.Result.Students.POSTAL_CODE))
                                        .setReasonForLeave(!student.isNull(WebServiceConstants.GetStudents.Result.Students.REASON_FOR_LEAVE) ? student.getString(WebServiceConstants.GetStudents.Result.Students.REASON_FOR_LEAVE) : "")
                                        .setStreet(student.getString(WebServiceConstants.GetStudents.Result.Students.STREET))
                                        .setRegion(!student.isNull(WebServiceConstants.GetStudents.Result.Students.REGION) ? student.getString(WebServiceConstants.GetStudents.Result.Students.REGION) : "")
                                );
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Functions.SaveLog(e);
                        }
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
                Functions.SaveLog(e);
            }
            finally {
                loadStudentsTask = null;
            }
        }
    }

    private class SaveParentChildrenTask extends SwingWorker {
        private final Parent mParent;
        private final Map<Student, RelationType> mStudentRelationTypeMap;
        private JSONObject mResult;

        public SaveParentChildrenTask(Parent parent, Map<Student, RelationType> studentRelationTypeMap) {
            mParent = parent;
            mStudentRelationTypeMap = studentRelationTypeMap;
        }

        @Override
        protected Object doInBackground() throws Exception {
            try {
                SetButtonActive(false);
                mResult = new MySQLWebService().SaveParentChildren(mParent, mStudentRelationTypeMap);
            } catch (Exception e) {
                e.printStackTrace();
                Functions.SaveLog(e);
            }
            return null;
        }

        @Override
        protected void done() {
            try {
                SetButtonActive(true);

                if (!mResult.getBoolean(WebServiceConstants.SaveParentChildren.Result.RESULT)) {
                    return;
                }

                if (loadParentsTask == null)
                    (loadParentsTask = new LoadParentsTask()).execute();
                if (loadStudentsTask == null)
                    (loadStudentsTask = new LoadStudentsTask()).execute();
            } finally {
                saveParentChildrenTask = null;
            }
        }
    }

    public class LoadRelationTypesTask extends SwingWorker {
        private JSONObject mResult;

        @Override
        protected Object doInBackground() {
            try {
                cbRelationType.setVisible(false);
                lblRelationType.setVisible(false);

                relationsTypesList.clear();
                mResult = new MySQLWebService().GetRelationTypes();
            } catch (Exception e) {
                e.printStackTrace();
                Functions.SaveLog(e);
            }
            return null;
        }

        @Override
        protected void done() {
            try {
                if (mResult.getBoolean(WebServiceConstants.GetRelationTypes.Result.RESULT)) {
                    for (int i = 0; i < mResult.getJSONArray(WebServiceConstants.GetRelationTypes.Result.RELATION_TYPES).length(); i++) {
                        JSONObject documentType = mResult.getJSONArray(WebServiceConstants.GetRelationTypes.Result.RELATION_TYPES).getJSONObject(i);
                        relationsTypesList.add(new RelationType(documentType.getInt(WebServiceConstants.GetRelationTypes.Result.RelationTypes.ID))
                                .setName(documentType.getString(WebServiceConstants.GetRelationTypes.Result.RelationTypes.NAME))
                        );
                    }
                }
            } finally {
                loadRelationTypesTask = null;

                Student selectedStudent = lvStudents.getSelectionModel().getSelectedItem();
                Parent selectedParent = lvParents.getSelectionModel().getSelectedItem();

                cbRelationType.setVisible(selectedParent != null && selectedStudent != null && studentsCheckList.get(selectedStudent).get());
                lblRelationType.setVisible(cbRelationType.isVisible());
            }
        }
    }
}
