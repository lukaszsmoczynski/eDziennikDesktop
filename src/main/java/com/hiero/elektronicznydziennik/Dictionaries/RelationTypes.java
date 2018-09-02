package com.hiero.elektronicznydziennik.Dictionaries;

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

public class RelationTypes extends Dictionary {
    public Button btnSave;
    public Button btnCancel;
    public TextField edtName;

    private LoadRelationTypesTask loadRelationTypesTask;
    private SaveRelationTypeTask saveRelationTypeTask;

    public void onBtnSaveAction(ActionEvent actionEvent) {
        if (saveRelationTypeTask != null) {
            return;
        }

        RelationType selectedSubject = (RelationType) incDictionaryController.gridMain.getSelectionModel().getSelectedItem();

        if (selectedSubject == null || selectedSubject.getId() == -1) {
            (saveRelationTypeTask = new SaveRelationTypeTask(new RelationType(-1)
                    .setName(edtName.getText()))).execute();
        } else {
            RelationType group = new RelationType(selectedSubject.getId())
                    .setName(edtName.getText());
            (saveRelationTypeTask = new SaveRelationTypeTask(group)).execute();
        }
    }

    public void onBtnCancelAction(ActionEvent actionEvent) {
        LoadRelationTypeTypeToEdition((RelationType) incDictionaryController.gridMain.getSelectionModel().getSelectedItem());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        TableColumn<RelationType, String> nameColumn = new TableColumn<>("Nazwa");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        incDictionaryController.gridMain.getColumns().addAll(nameColumn);

        incDictionaryController.gridMain.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> LoadRelationTypeTypeToEdition((RelationType) newValue));

        if (loadRelationTypesTask == null)
            (loadRelationTypesTask = new LoadRelationTypesTask()).execute();
    }

    void SetButtonActive(final Boolean active) {
        Platform.runLater(() -> {
            btnSave.setText(active ? "Zapisz" : "");
            btnSave.setGraphic(active ? null : new ProgressIndicator());
        });
    }

    private void LoadRelationTypeTypeToEdition(RelationType relationType) {
        if (relationType == null || relationType.getId() == -1) {
            edtName.setText("");
            return;
        }

        edtName.setText(relationType.getName());
    }

    public class LoadRelationTypesTask extends SwingWorker {
        private JSONObject mResult;

        @Override
        protected Object doInBackground() {
            try {
                data.clear();
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
                        data.add(new RelationType(documentType.getInt(WebServiceConstants.GetRelationTypes.Result.RelationTypes.ID))
                                .setName(documentType.getString(WebServiceConstants.GetRelationTypes.Result.RelationTypes.NAME))
                        );
                    }

                    data.add(new RelationType(-1).setName("<Nowy...>"));
                }
            } finally {
                loadRelationTypesTask = null;
            }
        }
    }

    public class SaveRelationTypeTask extends SwingWorker {
        private final RelationType mRelationType;
        private JSONObject mResult;

        public SaveRelationTypeTask(RelationType relationType) {
            this.mRelationType = relationType;
        }

        @Override
        protected Object doInBackground() {
            SetButtonActive(false);
            try {
                mResult = new MySQLWebService().SaveRelationType(mRelationType.getId(), mRelationType.getName());
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

                if (!mResult.getBoolean(WebServiceConstants.SaveRelationType.Result.RESULT)) {
                    return;
                }

                if (loadRelationTypesTask == null)
                    (loadRelationTypesTask = new LoadRelationTypesTask()).execute();
                LoadRelationTypeTypeToEdition((RelationType) incDictionaryController.gridMain.getSelectionModel().getSelectedItem());
            } finally {
                saveRelationTypeTask = null;
            }
        }
    }

    public class RelationType {
        private final SimpleIntegerProperty mId = new SimpleIntegerProperty();
        private final SimpleStringProperty mName = new SimpleStringProperty();

        RelationType(Integer id) {
            mId.set(id);
        }

        public int getId() {
            return mId.get();
        }

        public SimpleIntegerProperty idProperty() {
            return mId;
        }

        public String getName() {
            return mName.get();
        }

        public SimpleStringProperty nameProperty() {
            return mName;
        }

        public RelationType setName(String name) {
            this.mName.set(name);
            return this;
        }
    }
}
