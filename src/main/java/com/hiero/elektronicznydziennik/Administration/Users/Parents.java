package com.hiero.elektronicznydziennik.Administration.Users;

import com.hiero.elektronicznydziennik.Administration.Dictionaries.Dictionary;
import com.hiero.elektronicznydziennik.Helpers.Classes.Users.Parent;
import com.hiero.elektronicznydziennik.Helpers.Functions;
import com.hiero.elektronicznydziennik.WebService.MySQLWebService;
import com.hiero.elektronicznydziennik.WebService.WebServiceConstants;
import com.sun.istack.internal.NotNull;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.json.JSONObject;

import javax.swing.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

public class Parents extends Dictionary {
    public Button btnSave;
    public Button btnCancel;

    public TextField edtCountry;
    public TextField edtCity;
    public TextField edtRegion;
    public TextField edtStreet;
    public TextField edtHouseNumber;
    public TextField edtFlatNumber;
    public TextField edtPostalCode;
    public TextField edtEMail;
    public TextArea mmoPhones;

    private LoadParentsTask loadParentsTask;
    private SaveParentTask saveParentTask;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        TableColumn<Parent, String> loginColumn = new TableColumn<>("Login");
        loginColumn.setCellValueFactory(new PropertyValueFactory<>("login"));

        TableColumn<Parent, String> firstNameColumn = new TableColumn<>("Imię");
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn<Parent, String> lastNameColumn = new TableColumn<>("Nazwisko");
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        TableColumn<Parent, String> addressColumn = new TableColumn<>("Adres");

        TableColumn<Parent, String> countryColumn = new TableColumn<>("Kraj");
        countryColumn.setCellValueFactory(new PropertyValueFactory<>("country"));

        TableColumn<Parent, String> cityColumn = new TableColumn<>("Miasto");
        cityColumn.setCellValueFactory(new PropertyValueFactory<>("city"));

        TableColumn<Parent, String> regionColumn = new TableColumn<>("Województwo");
        regionColumn.setCellValueFactory(new PropertyValueFactory<>("region"));

        TableColumn<Parent, String> streetColumn = new TableColumn<>("Ulica");
        streetColumn.setCellValueFactory(new PropertyValueFactory<>("street"));

        TableColumn<Parent, String> houseNumberColumn = new TableColumn<>("Numer domu");
        houseNumberColumn.setCellValueFactory(new PropertyValueFactory<>("houseNumber"));

        TableColumn<Parent, String> flatNumberColumn = new TableColumn<>("Numer lokalu");
        flatNumberColumn.setCellValueFactory(new PropertyValueFactory<>("flatNumber"));

        TableColumn<Parent, String> postalCodeColumn = new TableColumn<>("Kod pocztowy");
        postalCodeColumn.setCellValueFactory(new PropertyValueFactory<>("postalCode"));

        addressColumn.getColumns().addAll(countryColumn, cityColumn, regionColumn, streetColumn, houseNumberColumn, flatNumberColumn, postalCodeColumn);

        TableColumn<Parent, String> phonesColumn = new TableColumn<>("Numery telefonu");
        phonesColumn.setCellValueFactory(new PropertyValueFactory<>("Phones"));

        TableColumn<Parent, String> eMailColumn = new TableColumn<>("Adres e-mail");
        eMailColumn.setCellValueFactory(new PropertyValueFactory<>("EMail"));

        incDictionaryController.gridMain.getColumns().addAll(loginColumn, firstNameColumn, lastNameColumn, addressColumn, eMailColumn, phonesColumn);

        incDictionaryController.gridMain.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> LoadParentToEdition((Parent) newValue));

        if (loadParentsTask == null)
            (loadParentsTask = new LoadParentsTask()).execute();
    }

    private void LoadParentToEdition(@NotNull Parent parent) {
        if (parent == null) {
            edtCountry.setText("");
            edtCity.setText("");
            edtRegion.setText("");
            edtStreet.setText("");
            edtHouseNumber.setText("");
            edtFlatNumber.setText("");
            edtPostalCode.setText("");
            edtEMail.setText("");
            mmoPhones.setText("");
            return;
        }

        edtCountry.setText(parent.getId() == -1 ? "" : parent.getCountry());
        edtCity.setText(parent.getId() == -1 ? "" : parent.getCity());
        edtRegion.setText(parent.getId() == -1 ? "" : parent.getRegion());
        edtStreet.setText(parent.getId() == -1 ? "" : parent.getStreet());
        edtHouseNumber.setText(parent.getId() == -1 ? "" : parent.getHouseNumber());
        edtFlatNumber.setText(parent.getId() == -1 ? "" : parent.getFlatNumber());
        edtPostalCode.setText(parent.getId() == -1 ? "" : parent.getPostalCode());
        edtEMail.setText(parent.getId() == -1 ? "" : parent.getEMail());
        mmoPhones.setText(parent.getId() == -1 ? "" : String.join("\r\n", parent.getParentPhones()));
    }

    public void onBtnSaveAction(ActionEvent actionEvent) {
        if (saveParentTask != null) {
            return;
        }

        Parent selectedParent = (Parent) incDictionaryController.gridMain.getSelectionModel().getSelectedItem();

        if (selectedParent == null) {
            return;
        }

        (saveParentTask = new SaveParentTask(new Parent(selectedParent == null || selectedParent.getId() == -1 ? -1 : selectedParent.getId())
                .setCity(edtCity.getText())
                .setCountry(edtCountry.getText())
                .setFlatNumber(edtFlatNumber.getText())
                .setHouseNumber(edtHouseNumber.getText())
                .setPostalCode(edtPostalCode.getText())
                .setStreet(edtStreet.getText())
                .setRegion(edtRegion.getText())
                .setParentPhones(new ArrayList<>(Arrays.asList(mmoPhones.getText().split(","))))
                .setEMail(edtEMail.getText()))).execute();
    }

    public void onBtnCancelAction(ActionEvent actionEvent) {
        LoadParentToEdition((Parent) incDictionaryController.gridMain.getSelectionModel().getSelectedItem());
    }

    private class LoadParentsTask extends SwingWorker {
        private JSONObject mResult;

        @Override
        protected Object doInBackground() throws Exception {
            try {
                mResult = new MySQLWebService().GetParents();
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
                            data.clear();
                            for (int i = 0; i < mResult.getJSONArray(WebServiceConstants.GetParents.Result.PARENTS).length(); i++) {
                                JSONObject parent = mResult.getJSONArray(WebServiceConstants.GetParents.Result.PARENTS).getJSONObject(i);
                                ArrayList<String> phones = new ArrayList<>();

                                for (int j = 0; j < parent.getJSONArray(WebServiceConstants.GetParents.Result.Parents.PHONES).length(); j++) {
                                    phones.add(parent.getJSONArray(WebServiceConstants.GetParents.Result.Parents.PHONES).getString(j));
                                }

                                data.add(new Parent(parent.getInt(WebServiceConstants.GetParents.Result.Parents.ID))
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
                                );
                            }

                            LoadParentToEdition(null);
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

    void SetButtonActive(final Boolean active) {
        Platform.runLater(() -> {
            btnSave.setText(!active ? "" : "Zapisz");
            btnSave.setGraphic(!active ? new ProgressIndicator() : null);
        });
    }

    private class SaveParentTask extends SwingWorker {
        private final Parent mParent;
        private JSONObject mResult;

        public SaveParentTask(Parent parent) {
            mParent = parent;
        }

        @Override
        protected Object doInBackground() {
            SetButtonActive(false);
            try {
                mResult = new MySQLWebService().SaveParent(mParent);
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

                if (loadParentsTask == null)
                    (loadParentsTask = new LoadParentsTask()).execute();
            } finally {
                saveParentTask = null;
            }
        }
    }
}
