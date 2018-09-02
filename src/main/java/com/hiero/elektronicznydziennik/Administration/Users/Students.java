package com.hiero.elektronicznydziennik.Administration.Users;

import com.hiero.elektronicznydziennik.Administration.Dictionaries.Dictionary;
import com.hiero.elektronicznydziennik.Helpers.Classes.DocumentType;
import com.hiero.elektronicznydziennik.Helpers.Classes.Users.Student;
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

public class Students extends Dictionary {
    public Button btnSave;
    public Button btnCancel;
    public DatePicker edtDateOfBirth;
    public TextField edtPlaceOfBirth;
    public ComboBox cbTypeOfDocument;
    public TextField edtDocumentNumber;
    public TextField edtPESEL;
    public TextField edtCountry;
    public TextField edtCity;
    public TextField edtRegion;
    public TextField edtStreet;
    public TextField edtHouseNumber;
    public TextField edtFlatNumber;
    public TextField edtPostalCode;
    public TextField edtReasonForLeave;

    private LoadStudentsTask loadStudentsTask;
    private SaveStudentsTask saveStudentsTask;
    private LoadDocumentTypesTask loadDocumentTypesTask;

    private ObservableList<DocumentType> documentTypes = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        TableColumn<Student, String> loginColumn = new TableColumn<>("Login");
        loginColumn.setCellValueFactory(new PropertyValueFactory<>("login"));

        TableColumn<Student, String> firstNameColumn = new TableColumn<>("Imię");
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn<Student, String> lastNameColumn = new TableColumn<>("Nazwisko");
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        TableColumn<Student, Date> dateOfBirthColumn = new TableColumn<>("Data urodzenia");
        dateOfBirthColumn.setCellValueFactory(new PropertyValueFactory<>("dateOfBirth"));
        dateOfBirthColumn.setCellFactory(new ColumnFormatter<>(new SimpleDateFormat("YYYY-MM-dd")));

        TableColumn<Student, String> placeOfBirthColumn = new TableColumn<>("Miejsce urodzenia");
        placeOfBirthColumn.setCellValueFactory(new PropertyValueFactory<>("placeOfBirth"));

        TableColumn<Student, String> documentColumn = new TableColumn<>("Dokument tożsamości");

        TableColumn<Student, String> typeOfDocumentColumn = new TableColumn<>("Typ");
        typeOfDocumentColumn.setCellValueFactory(new PropertyValueFactory<>("typeOfDocument"));

        TableColumn<Student, String> documentNumberColumn = new TableColumn<>("Numer");
        documentNumberColumn.setCellValueFactory(new PropertyValueFactory<>("documentNumber"));

        documentColumn.getColumns().addAll(typeOfDocumentColumn, documentNumberColumn);

        TableColumn<Student, String> PESELColumn = new TableColumn<>("PESEL");
        PESELColumn.setCellValueFactory(new PropertyValueFactory<>("PESEL"));

        TableColumn<Student, String> addressColumn = new TableColumn<>("Adres");

        TableColumn<Student, String> countryColumn = new TableColumn<>("Kraj");
        countryColumn.setCellValueFactory(new PropertyValueFactory<>("country"));

        TableColumn<Student, String> cityColumn = new TableColumn<>("Miasto");
        cityColumn.setCellValueFactory(new PropertyValueFactory<>("city"));

        TableColumn<Student, String> regionColumn = new TableColumn<>("Województwo");
        regionColumn.setCellValueFactory(new PropertyValueFactory<>("region"));

        TableColumn<Student, String> streetColumn = new TableColumn<>("Ulica");
        streetColumn.setCellValueFactory(new PropertyValueFactory<>("street"));

        TableColumn<Student, String> houseNumberColumn = new TableColumn<>("Numer domu");
        houseNumberColumn.setCellValueFactory(new PropertyValueFactory<>("houseNumber"));

        TableColumn<Student, String> flatNumberColumn = new TableColumn<>("Numer lokalu");
        flatNumberColumn.setCellValueFactory(new PropertyValueFactory<>("flatNumber"));

        TableColumn<Student, String> postalCodeColumn = new TableColumn<>("Kod pocztowy");
        postalCodeColumn.setCellValueFactory(new PropertyValueFactory<>("postalCode"));

        addressColumn.getColumns().addAll(countryColumn, cityColumn, regionColumn, streetColumn, houseNumberColumn, flatNumberColumn, postalCodeColumn);

        TableColumn<Student, String> reasonForLeaveColumn = new TableColumn<>("Powód opuszczenia placówki");
        reasonForLeaveColumn.setCellValueFactory(new PropertyValueFactory<>("reasonForLeave"));

        incDictionaryController.gridMain.getColumns().addAll(loginColumn, firstNameColumn, lastNameColumn, dateOfBirthColumn, placeOfBirthColumn, documentColumn, PESELColumn, addressColumn, reasonForLeaveColumn);

        incDictionaryController.gridMain.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> LoadStudentToEdition((Student) newValue));

        edtDateOfBirth.setConverter(new StringConverter<LocalDate>() {
            String pattern = "yyyy-MM-dd";
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);
            {
                edtDateOfBirth.setPromptText(pattern.toLowerCase());
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

        cbTypeOfDocument.setItems(documentTypes);
        cbTypeOfDocument.setConverter(new StringConverter() {
            @Override
            public String toString(Object object) {
                if (object != null) {
                    return ((DocumentType) object).getName();
                }

                return null;
            }

            @Override
            public Object fromString(String string) {
                FilteredList<DocumentType> filteredList =
                        new FilteredList<>(documentTypes, documentType -> documentType.getName().equals(string));
                if (filteredList.isEmpty())
                    return null;
                return filteredList.get(0);
            }
        });

        if (loadDocumentTypesTask == null)
            (loadDocumentTypesTask = new LoadDocumentTypesTask()).execute();

        if (loadStudentsTask == null)
            (loadStudentsTask = new LoadStudentsTask()).execute();
    }

    public void onBtnSaveAction(ActionEvent actionEvent) {
        if (saveStudentsTask != null) {
            return;
        }

        Student selectedStudent = (Student) incDictionaryController.gridMain.getSelectionModel().getSelectedItem();

        if (selectedStudent == null) {
            return;
        }

        (saveStudentsTask = new SaveStudentsTask(new Student(selectedStudent == null || selectedStudent.getId() == -1 ? -1 : selectedStudent.getId())
                .setDateOfBirth(Date.from(edtDateOfBirth.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .setPlaceOfBirth(edtPlaceOfBirth.getText())
                .setCity(edtCity.getText())
                .setCountry(edtCountry.getText())
                .setDocumentNumber(edtDocumentNumber.getText())
                .setFlatNumber(edtFlatNumber.getText())
                .setHouseNumber(edtHouseNumber.getText())
                .setDocumentType((DocumentType) cbTypeOfDocument.getValue())
                .setPESEL(edtPESEL.getText())
                .setPostalCode(edtPostalCode.getText())
                .setReasonForLeave(edtReasonForLeave.getText())
                .setStreet(edtStreet.getText())
                .setRegion(edtRegion.getText()))).execute();
    }

    public void onBtnCancelAction(ActionEvent actionEvent) {
        LoadStudentToEdition((Student) incDictionaryController.gridMain.getSelectionModel().getSelectedItem());
    }

    private void LoadStudentToEdition(@NotNull Student student) {
        if (student == null) {
            edtDateOfBirth.setValue(null);
            edtPlaceOfBirth.setText("");
            cbTypeOfDocument.setValue(null);
            edtDocumentNumber.setText("");
            edtPESEL.setText("");
            edtCountry.setText("");
            edtCity.setText("");
            edtRegion.setText("");
            edtStreet.setText("");
            edtHouseNumber.setText("");
            edtFlatNumber.setText("");
            edtPostalCode.setText("");
            edtReasonForLeave.setText("");
            return;
        }

        edtDateOfBirth.setValue(student.getId() == -1 ? null : student.getDateOfBirth().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        edtPlaceOfBirth.setText(student.getId() == -1 ? "" : student.getPlaceOfBirth());
        cbTypeOfDocument.setValue(student.getId() == -1 ? "" : student.getDocumentType());
        edtDocumentNumber.setText(student.getId() == -1 ? "" : student.getDocumentNumber());
        edtPESEL.setText(student.getId() == -1 ? "" : student.getPESEL());
        edtCountry.setText(student.getId() == -1 ? "" : student.getCountry());
        edtCity.setText(student.getId() == -1 ? "" : student.getCity());
        edtRegion.setText(student.getId() == -1 ? "" : student.getRegion());
        edtStreet.setText(student.getId() == -1 ? "" : student.getStreet());
        edtHouseNumber.setText(student.getId() == -1 ? "" : student.getHouseNumber());
        edtFlatNumber.setText(student.getId() == -1 ? "" : student.getFlatNumber());
        edtPostalCode.setText(student.getId() == -1 ? "" : student.getPostalCode());
        edtReasonForLeave.setText(student.getId() == -1 ? "" : student.getReasonForLeave());
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
                            data.clear();
                            for (int i = 0; i < mResult.getJSONArray(WebServiceConstants.GetStudents.Result.STUDENTS).length(); i++) {
                                JSONObject student = mResult.getJSONArray(WebServiceConstants.GetStudents.Result.STUDENTS).getJSONObject(i);
                                data.add(new Student(student.getInt(WebServiceConstants.GetStudents.Result.Students.ID))
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

                            LoadStudentToEdition(null);
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

    void SetButtonActive(final Boolean active) {
        Platform.runLater(() -> {
            btnSave.setText(!active ? "" : "Zapisz");
            btnSave.setGraphic(!active ? new ProgressIndicator() : null);
        });
    }

    public class SaveStudentsTask extends SwingWorker {
        private final Student mStudent;
        private JSONObject mResult;

        public SaveStudentsTask(Student student) {
            mStudent = student;
        }

        @Override
        protected Object doInBackground() {
            SetButtonActive(false);
            try {
                mResult = new MySQLWebService().SaveStudent(mStudent);
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

                if (loadStudentsTask == null)
                    (loadStudentsTask = new LoadStudentsTask()).execute();
            } finally {
                saveStudentsTask = null;
            }
        }
    }

    public class LoadDocumentTypesTask extends SwingWorker {
        private JSONObject mResult;

        @Override
        protected Object doInBackground() {
            try {
                documentTypes.clear();
                mResult = new MySQLWebService().GetDocumentTypes();
            } catch (Exception e) {
                e.printStackTrace();
                Functions.SaveLog(e);
            }
            return null;
        }

        @Override
        protected void done() {
            try {
                if (mResult.getBoolean(WebServiceConstants.GetDocumentTypes.Result.RESULT)) {
                    for (int i = 0; i < mResult.getJSONArray(WebServiceConstants.GetDocumentTypes.Result.DOCUMENT_TYPES).length(); i++) {
                        JSONObject documentType = mResult.getJSONArray(WebServiceConstants.GetDocumentTypes.Result.DOCUMENT_TYPES).getJSONObject(i);
                        documentTypes.add(new DocumentType(documentType.getInt(WebServiceConstants.GetDocumentTypes.Result.DocumentTypes.ID))
                                .setName(documentType.getString(WebServiceConstants.GetDocumentTypes.Result.DocumentTypes.NAME))
                        );
                    }
                }
            } finally {
                loadDocumentTypesTask = null;
            }
        }
    }
}