package com.hiero.elektronicznydziennik.Administration.Dictionaries;

import com.hiero.elektronicznydziennik.Helpers.Classes.Subject;
import com.hiero.elektronicznydziennik.Helpers.Functions;
import com.hiero.elektronicznydziennik.WebService.MySQLWebService;
import com.hiero.elektronicznydziennik.WebService.WebServiceConstants;
import javafx.application.Platform;
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

public class Subjects extends Dictionary {
    public Button btnSave;
    public Button btnCancel;
    public TextField edtName;

    private LoadSubjectsTask loadSubjectsTask;
    private SaveSubjectTask saveSubjectTask;

    public void onBtnSaveAction(ActionEvent actionEvent) {
        if (saveSubjectTask != null) {
            return;
        }

        Subject selectedSubject = (Subject) incDictionaryController.gridMain.getSelectionModel().getSelectedItem();

        if (selectedSubject == null || selectedSubject.getId() == -1) {
            (saveSubjectTask = new SaveSubjectTask(new Subject(-1)
                    .setName(edtName.getText()))).execute();
        } else {
            Subject group = new Subject(selectedSubject.getId())
                    .setName(edtName.getText());
            (saveSubjectTask = new SaveSubjectTask(group)).execute();
        }
    }

    public void onBtnCancelAction(ActionEvent actionEvent) {
        LoadSubjectToEdition((Subject) incDictionaryController.gridMain.getSelectionModel().getSelectedItem());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        TableColumn<Subject, String> nameColumn = new TableColumn<>("Nazwa");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        incDictionaryController.gridMain.getColumns().addAll(nameColumn);

        incDictionaryController.gridMain.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> LoadSubjectToEdition((Subject) newValue));

        if (loadSubjectsTask == null)
            (loadSubjectsTask = new LoadSubjectsTask()).execute();
    }

    void SetButtonActive(final Boolean active) {
        Platform.runLater(() -> {
            btnSave.setText(active ? "Zapisz" : "");
            btnSave.setGraphic(active ? null : new ProgressIndicator());
        });
    }

    private void LoadSubjectToEdition(Subject subject) {
        if (subject == null || subject.getId() == -1) {
            edtName.setText("");
            return;
        }

        edtName.setText(subject.getName());
    }

    public class LoadSubjectsTask extends SwingWorker {
        private JSONObject mResult;

        @Override
        protected Object doInBackground() {
            try {
                data.clear();
                mResult = new MySQLWebService().GetSubjects();
            } catch (Exception e) {
                e.printStackTrace();
                Functions.SaveLog(e);
            }
            return null;
        }

        @Override
        protected void done() {
            try {
                if (mResult.getBoolean(WebServiceConstants.GetSubjects.Result.RESULT)) {
                    for (int i = 0; i < mResult.getJSONArray(WebServiceConstants.GetSubjects.Result.SUBJECTS).length(); i++) {
                        JSONObject documentType = mResult.getJSONArray(WebServiceConstants.GetSubjects.Result.SUBJECTS).getJSONObject(i);
                        data.add(new Subject(documentType.getInt(WebServiceConstants.GetSubjects.Result.Subjects.ID))
                                .setName(documentType.getString(WebServiceConstants.GetSubjects.Result.Subjects.NAME))
                        );
                    }

                    data.add(new Subject(-1).setName("<Nowy...>"));
                }
            } finally {
                loadSubjectsTask = null;
            }
        }
    }

    public class SaveSubjectTask extends SwingWorker {
        private final Subject mSubject;
        private JSONObject mResult;

        public SaveSubjectTask(Subject subject) {
            mSubject = subject;
        }

        @Override
        protected Object doInBackground() {
            SetButtonActive(false);
            try {
                mResult = new MySQLWebService().SaveSubject(mSubject.getId(), mSubject.getName());
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

                if (!mResult.getBoolean(WebServiceConstants.SaveSubject.Result.RESULT)) {
                    return;
                }

                if (loadSubjectsTask == null)
                    (loadSubjectsTask = new LoadSubjectsTask()).execute();
                LoadSubjectToEdition((Subject) incDictionaryController.gridMain.getSelectionModel().getSelectedItem());
            } finally {
                saveSubjectTask = null;
            }
        }
    }
}
