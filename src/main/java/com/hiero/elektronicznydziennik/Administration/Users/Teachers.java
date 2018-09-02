package com.hiero.elektronicznydziennik.Administration.Users;

import com.hiero.elektronicznydziennik.Administration.Dictionaries.Dictionary;
import com.hiero.elektronicznydziennik.Helpers.Classes.FormOfEmployment;
import com.hiero.elektronicznydziennik.Helpers.Classes.Users.Teacher;
import com.hiero.elektronicznydziennik.Helpers.ColumnFormatter;
import com.hiero.elektronicznydziennik.Helpers.Functions;
import com.hiero.elektronicznydziennik.WebService.MySQLWebService;
import com.hiero.elektronicznydziennik.WebService.WebServiceConstants;
import com.sun.istack.internal.NotNull;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import org.json.JSONObject;

import javax.swing.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.ResourceBundle;

public class Teachers extends Dictionary {
    public Button btnSave;
    public Button btnCancel;
    public DatePicker edtHireDate;
    public ComboBox cbFormOfEmployment;

    ObservableList<FormOfEmployment> formsOfEmployment = FXCollections.observableArrayList();

    private LoadTeachersTask loadTeachersTask;
    private SaveTeachersTask saveTeachersTask;
    private LoadFormsOfEmploymentTaskTask loadFormsOfEmploymentTask;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        TableColumn<Teacher, String> loginColumn = new TableColumn<>("Login");
        loginColumn.setCellValueFactory(new PropertyValueFactory<>("login"));

        TableColumn<Teacher, String> firstNameColumn = new TableColumn<>("Imię");
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn<Teacher, String> lastNameColumn = new TableColumn<>("Nazwisko");
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        TableColumn<Teacher, Date> hireDateColumn = new TableColumn<>("Data zatrudnienia");
        hireDateColumn.setCellValueFactory(new PropertyValueFactory<>("hireDate"));
        hireDateColumn.setCellFactory(new ColumnFormatter<>(new SimpleDateFormat("YYYY-MM-dd")));

        TableColumn<Teacher, String> formOfEmploymentColumn = new TableColumn<>("Forma zatrudnienia");
        formOfEmploymentColumn.setCellValueFactory(new PropertyValueFactory<>("FormOfEmployment"));

        incDictionaryController.gridMain.getColumns().addAll(loginColumn, firstNameColumn, lastNameColumn, hireDateColumn, formOfEmploymentColumn);

        incDictionaryController.gridMain.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> LoadTeacherToEdition((Teacher) newValue));

        edtHireDate.setConverter(new StringConverter<LocalDate>() {
            String pattern = "yyyy-MM-dd";
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);
            {
                edtHireDate.setPromptText(pattern.toLowerCase());
            }

            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        });

        cbFormOfEmployment.setItems(formsOfEmployment);
        cbFormOfEmployment.setConverter(new StringConverter() {
            @Override
            public String toString(Object object) {
                if (object != null) {
                    return ((FormOfEmployment) object).getName();
                }

                return null;
            }

            @Override
            public Object fromString(String string) {
                FilteredList<FormOfEmployment> filteredList =
                        new FilteredList<>(formsOfEmployment, documentType -> documentType.getName().equals(string));
                if (filteredList.isEmpty())
                    return null;
                return filteredList.get(0);
            }
        });

        if (loadFormsOfEmploymentTask == null)
            (loadFormsOfEmploymentTask = new LoadFormsOfEmploymentTaskTask()).execute();

        if (loadTeachersTask == null)
            (loadTeachersTask = new LoadTeachersTask()).execute();
    }

    public void onBtnSaveAction(ActionEvent actionEvent) {
        if (saveTeachersTask != null) {
            return;
        }

        Teacher selectedTeacher = (Teacher) incDictionaryController.gridMain.getSelectionModel().getSelectedItem();

        if (selectedTeacher == null) {
            return;
        }

        (saveTeachersTask = new SaveTeachersTask(new Teacher(selectedTeacher == null || selectedTeacher.getId() == -1 ? -1 : selectedTeacher.getId())
                .setHireDate(Date.from(edtHireDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .setFormOfEmployment((FormOfEmployment) cbFormOfEmployment.getValue()))).execute();
    }

    public void onBtnCancelAction(ActionEvent actionEvent) {
        LoadTeacherToEdition((Teacher) incDictionaryController.gridMain.getSelectionModel().getSelectedItem());
    }

    private void LoadTeacherToEdition(@NotNull Teacher teacher) {
        if (teacher == null) {
            edtHireDate.setValue(null);
            cbFormOfEmployment.setValue(null);
            return;
        }

        edtHireDate.setValue(teacher.getId() == -1 ? null : teacher.getHireDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        cbFormOfEmployment.setValue(teacher.getId() == -1 ? "" : teacher.getFormOfEmployment());
    }

    private class LoadTeachersTask extends SwingWorker {
        private JSONObject mResult;

        @Override
        protected Object doInBackground() throws Exception {
            try {
                mResult = new MySQLWebService().GetTeachers();
            } catch (Exception e) {
                e.printStackTrace();
                Functions.SaveLog(e);
            }
            return null;
        }

        @Override
        protected void done() {
            try {
                if (mResult.getBoolean(WebServiceConstants.GetTeachers.Result.RESULT)) {
                    Platform.runLater(() -> {
                        try {
                            data.clear();
                            for (int i = 0; i < mResult.getJSONArray(WebServiceConstants.GetTeachers.Result.TEACHERS).length(); i++) {
                                JSONObject teacher = mResult.getJSONArray(WebServiceConstants.GetTeachers.Result.TEACHERS).getJSONObject(i);
                                data.add(new Teacher(teacher.getInt(WebServiceConstants.GetTeachers.Result.Teachers.ID))
                                        .setLogin(teacher.getString(WebServiceConstants.GetTeachers.Result.Teachers.LOGIN))
                                        .setFirstName(teacher.getString(WebServiceConstants.GetTeachers.Result.Teachers.NAME))
                                        .setLastName(teacher.getString(WebServiceConstants.GetTeachers.Result.Teachers.SURNAME))
                                        .setHireDate(new SimpleDateFormat("yyyy-MM-dd").parse(teacher.getString(WebServiceConstants.GetTeachers.Result.Teachers.HIRE_DATE)))
                                        .setFormOfEmployment(new FormOfEmployment(teacher.getInt(WebServiceConstants.GetTeachers.Result.Teachers.ID_FORM_OF_EMPLOYMENT))
                                                .setName(teacher.getString(WebServiceConstants.GetTeachers.Result.Teachers.FORM_OF_EMPLOYMENT)))
                                );
                            }

                            LoadTeacherToEdition(null);
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
                loadTeachersTask = null;
            }
        }
    }

    void SetButtonActive(final Boolean active) {
        Platform.runLater(() -> {
            btnSave.setText(!active ? "" : "Zapisz");
            btnSave.setGraphic(!active ? new ProgressIndicator() : null);
        });
    }

    private class SaveTeachersTask extends SwingWorker {
        private final Teacher mTeacher;
        private JSONObject mResult;

        public SaveTeachersTask(Teacher teacher) {
            mTeacher = teacher;
        }

        @Override
        protected Object doInBackground() {
            SetButtonActive(false);
            try {
                mResult = new MySQLWebService().SaveTeacher(mTeacher);
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

                if (!mResult.getBoolean(WebServiceConstants.SaveUser.Result.RESULT)) {
                    return;
                }

                if (loadTeachersTask == null)
                    (loadTeachersTask = new LoadTeachersTask()).execute();
            } finally {
                saveTeachersTask = null;
            }
        }
    }

    private class LoadFormsOfEmploymentTaskTask extends SwingWorker {
        private JSONObject mResult;

        @Override
        protected Object doInBackground() {
            try {
                formsOfEmployment.clear();
                mResult = new MySQLWebService().GetFormsOfEmployment();
            } catch (Exception e) {
                e.printStackTrace();
                Functions.SaveLog(e);
            }
            return null;
        }

        @Override
        protected void done() {
            try {
                if (mResult.getBoolean(WebServiceConstants.GetFormsOfEmployment.Result.RESULT)) {
                    for (int i = 0; i < mResult.getJSONArray(WebServiceConstants.GetFormsOfEmployment.Result.FORMS_OF_EMPLOYMENT).length(); i++) {
                        JSONObject documentType = mResult.getJSONArray(WebServiceConstants.GetFormsOfEmployment.Result.FORMS_OF_EMPLOYMENT).getJSONObject(i);
                        formsOfEmployment.add(new FormOfEmployment(documentType.getInt(WebServiceConstants.GetFormsOfEmployment.Result.FormsOfEmployment.ID))
                                .setName(documentType.getString(WebServiceConstants.GetFormsOfEmployment.Result.FormsOfEmployment.NAME))
                        );
                    }
                }
            } finally {
                loadFormsOfEmploymentTask = null;
            }
        }
    }
}
