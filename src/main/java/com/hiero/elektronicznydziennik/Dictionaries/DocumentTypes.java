package com.hiero.elektronicznydziennik.Dictionaries;

import com.hiero.elektronicznydziennik.Helpers.Classes.DocumentType;
import com.hiero.elektronicznydziennik.Helpers.Functions;
import com.hiero.elektronicznydziennik.WebService.MySQLWebService;
import com.hiero.elektronicznydziennik.WebService.WebServiceConstants;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.json.JSONObject;

import javax.swing.*;
import java.net.URL;
import java.util.ResourceBundle;

public class DocumentTypes extends Dictionary {
    public Button btnSave;
    public Button btnCancel;
    public TextField edtName;

    private LoadDocumentTypesTask loadDocumentTypesTask;
    private SaveDocumentTypeTask saveDocumentTypeTask;

    public void onBtnSaveAction(ActionEvent actionEvent) {
        if (saveDocumentTypeTask != null) {
            return;
        }

        DocumentType selectedDocumentType = (DocumentType) incDictionaryController.gridMain.getSelectionModel().getSelectedItem();

        if (selectedDocumentType == null || selectedDocumentType.getId() == -1) {
            (saveDocumentTypeTask = new SaveDocumentTypeTask(new DocumentType(-1)
                    .setName(edtName.getText()))).execute();
        } else {
            DocumentType group = new DocumentType(selectedDocumentType.getId())
                    .setName(edtName.getText());
            (saveDocumentTypeTask = new SaveDocumentTypeTask(group)).execute();
        }
    }

    public void onBtnCancelAction(ActionEvent actionEvent) {
        LoadDocumentTypeToEdition((DocumentType) incDictionaryController.gridMain.getSelectionModel().getSelectedItem());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        TableColumn<DocumentType, String> nameColumn = new TableColumn<>("Nazwa");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        incDictionaryController.gridMain.getColumns().addAll(nameColumn);

        incDictionaryController.gridMain.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> LoadDocumentTypeToEdition((DocumentType) newValue));

        if (loadDocumentTypesTask == null)
            (loadDocumentTypesTask = new LoadDocumentTypesTask()).execute();
    }

    void SetButtonActive(final Boolean active) {
        Platform.runLater(() -> {
            btnSave.setText(active ? "Zapisz" : "");
            btnSave.setGraphic(active ? null : new ProgressIndicator());
        });
    }

    private void LoadDocumentTypeToEdition(DocumentType documentType) {
        if (documentType == null || documentType.getId() == -1) {
            edtName.setText("");
            return;
        }

        edtName.setText(documentType.getName());
    }

    public class LoadDocumentTypesTask extends SwingWorker {
        private JSONObject mResult;

        @Override
        protected Object doInBackground() {
            try {
                data.clear();
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
                        data.add(new DocumentType(documentType.getInt(WebServiceConstants.GetDocumentTypes.Result.DocumentTypes.ID))
                                .setName(documentType.getString(WebServiceConstants.GetDocumentTypes.Result.DocumentTypes.NAME))
                        );
                    }

                    data.add(new DocumentType(-1).setName("<Nowy...>"));
                }
            } finally {
                loadDocumentTypesTask = null;
            }
        }
    }

    public class SaveDocumentTypeTask extends SwingWorker {
        private final DocumentType mDocumentType;
        private JSONObject mResult;

        public SaveDocumentTypeTask(DocumentType subject) {
            mDocumentType = subject;
        }

        @Override
        protected Object doInBackground() {
            SetButtonActive(false);
            try {
                mResult = new MySQLWebService().SaveDocumentType(mDocumentType.getId(), mDocumentType.getName());
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

                if (!mResult.getBoolean(WebServiceConstants.SaveDocumentType.Result.RESULT)) {
                    return;
                }

                if (loadDocumentTypesTask == null)
                    (loadDocumentTypesTask = new LoadDocumentTypesTask()).execute();
                LoadDocumentTypeToEdition((DocumentType) incDictionaryController.gridMain.getSelectionModel().getSelectedItem());
            } finally {
                saveDocumentTypeTask = null;
            }
        }
    }
}
