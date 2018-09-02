package com.hiero.elektronicznydziennik.Administration.Dictionaries;

import com.hiero.elektronicznydziennik.Helpers.Classes.FormOfEmployment;
import com.hiero.elektronicznydziennik.Helpers.Functions;
import com.hiero.elektronicznydziennik.WebService.MySQLWebService;
import com.hiero.elektronicznydziennik.WebService.WebServiceConstants;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.json.JSONObject;

import javax.swing.*;
import java.net.URL;
import java.util.ResourceBundle;

public class FormsOfEmployment extends Dictionary implements Initializable {
    public Button btnSave;
    public Button btnCancel;
    public TextField edtName;

    LoadFormsOfEmploymentTask loadFormsOfEmploymentTask;
    private SaveFormOfEmploymentTask saveFormOfEmploymentTask;

    public void onBtnSaveAction(ActionEvent actionEvent) {
        if (saveFormOfEmploymentTask != null) {
            return;
        }

        FormOfEmployment selectedFormOfEmployment = (FormOfEmployment) incDictionaryController.gridMain.getSelectionModel().getSelectedItem();

        if (selectedFormOfEmployment == null || selectedFormOfEmployment.getId() == -1) {
            (saveFormOfEmploymentTask = new SaveFormOfEmploymentTask(new FormOfEmployment(-1)
                    .setName(edtName.getText()))).execute();
        } else {
            FormOfEmployment group = new FormOfEmployment(selectedFormOfEmployment.getId())
                    .setName(edtName.getText());
            (saveFormOfEmploymentTask = new SaveFormOfEmploymentTask(group)).execute();
        }
    }

    public void onBtnCancelAction(ActionEvent actionEvent) {
        LoadFormOfEmploymentToEdition((FormOfEmployment) incDictionaryController.gridMain.getSelectionModel().getSelectedItem());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        TableColumn<FormOfEmployment, String> nameColumn = new TableColumn<>("Nazwa");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        incDictionaryController.gridMain.getColumns().addAll(nameColumn);

        incDictionaryController.gridMain.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> LoadFormOfEmploymentToEdition((FormOfEmployment) newValue)));

        if (loadFormsOfEmploymentTask == null)
            (loadFormsOfEmploymentTask = new LoadFormsOfEmploymentTask()).execute();
    }

    void SetButtonActive(final Boolean active) {
        Platform.runLater(() -> {
            btnSave.setText(!active ? "" : "Zapisz");
            btnSave.setGraphic(!active ? new ProgressIndicator() : null);
        });
    }

    private void LoadFormOfEmploymentToEdition(FormOfEmployment formOfEmployment) {
        if (formOfEmployment == null || formOfEmployment.getId() == -1) {
            edtName.setText("");
            return;
        }

        edtName.setText(formOfEmployment.getName());
    }

    public class LoadFormsOfEmploymentTask extends SwingWorker {
        private JSONObject mResult;

        @Override
        protected Object doInBackground() {
            try {
                data.clear();
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
                        data.add(new FormOfEmployment(documentType.getInt(WebServiceConstants.GetFormsOfEmployment.Result.FormsOfEmployment.ID))
                                .setName(documentType.getString(WebServiceConstants.GetFormsOfEmployment.Result.FormsOfEmployment.NAME))
                        );
                    }

                    data.add(new FormOfEmployment(-1).setName("<Nowy...>"));
                }
            } finally {
                loadFormsOfEmploymentTask = null;
            }
        }
    }

    public class SaveFormOfEmploymentTask extends SwingWorker {
        private final FormOfEmployment mFormOfEmployment;
        private JSONObject mResult;

        public SaveFormOfEmploymentTask(FormOfEmployment formOfEmployment) {
            mFormOfEmployment = formOfEmployment;
        }

        @Override
        protected Object doInBackground() {
            SetButtonActive(false);
            try {
                mResult = new MySQLWebService().SaveFormOfEmployment(mFormOfEmployment.getId(), mFormOfEmployment.getName());
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

                if (!mResult.getBoolean(WebServiceConstants.SaveFormOfEmployment.Result.RESULT)) {
                    return;
                }

                if (loadFormsOfEmploymentTask == null)
                    (loadFormsOfEmploymentTask = new LoadFormsOfEmploymentTask()).execute();
                LoadFormOfEmploymentToEdition((FormOfEmployment) incDictionaryController.gridMain.getSelectionModel().getSelectedItem());
            } finally {
                saveFormOfEmploymentTask = null;
            }
        }
    }
}
