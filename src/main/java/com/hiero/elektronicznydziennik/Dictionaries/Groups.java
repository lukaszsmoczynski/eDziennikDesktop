package com.hiero.elektronicznydziennik.Dictionaries;

import com.hiero.elektronicznydziennik.Helpers.Classes.Group;
import com.hiero.elektronicznydziennik.Helpers.Functions;
import com.hiero.elektronicznydziennik.WebService.MySQLWebService;
import com.hiero.elektronicznydziennik.WebService.WebServiceConstants;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.json.JSONObject;

import javax.swing.*;
import java.net.URL;
import java.util.Calendar;
import java.util.ResourceBundle;

public class Groups extends Dictionary {
    ObservableList<Group> parentGroupData = FXCollections.observableArrayList();

    public Button btnSave;
    public Button btnCancel;
    public TextField edtName;
    public ChoiceBox<Integer> cbCreationYear;
    public ComboBox<Group> cbParentGroup;

    private LoadGroupsTask loadGroupsTask;
    private SaveGroupTask saveGroupsTask;

    public void onBtnSaveAction(ActionEvent actionEvent) {
        if (saveGroupsTask != null) {
            return;
        }

        Group selectedGroup = (Group) incDictionaryController.gridMain.getSelectionModel().getSelectedItem();

         if (selectedGroup == null || selectedGroup.getId() == -1) {
            (saveGroupsTask = new SaveGroupTask(new Group(-1, cbParentGroup.getValue() == null ? -1 : cbParentGroup.getValue().getId())
                    .setName(edtName.getText())
                    .setCreationYear(cbCreationYear.getValue() == null ? -1 : cbCreationYear.getValue())
                    .setParentGroup(cbParentGroup.getValue()))).execute();
        } else {
            Group group = new Group(selectedGroup.getId(), cbParentGroup.getValue() == null ? -1 : cbParentGroup.getValue().getId())
                    .setName(edtName.getText())
                    .setCreationYear(cbCreationYear.getValue() == null ? -1 : cbCreationYear.getValue())
                    .setParentGroup(cbParentGroup.getValue());
            (saveGroupsTask = new SaveGroupTask(group)).execute();
        }
    }

    public void onBtnCancelAction(ActionEvent actionEvent) {
        LoadGroupToEdition((Group) incDictionaryController.gridMain.getSelectionModel().getSelectedItem());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        TableColumn<Group, String> nameColumn = new TableColumn<>("Nazwa");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Group, Integer> creationYearColumn = new TableColumn<>("Rok");
        creationYearColumn.setCellValueFactory(new PropertyValueFactory<>("creationYear"));

        TableColumn<Group, String> parentGroupNameColumn = new TableColumn<>("Nazwa");
        parentGroupNameColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getParentGroup() == null ? null : param.getValue().getParentGroup().getName()));

        TableColumn<Group, Integer> parentGroupCreationYearColumn = new TableColumn<>("Rok");
        parentGroupCreationYearColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getParentGroup() == null ? null : param.getValue().getParentGroup().getCreationYear()));

        TableColumn<Group, Integer> parentGroupColumn = new TableColumn<>("Grupa nadrzędna");
        parentGroupColumn.getColumns().addAll(parentGroupNameColumn, parentGroupCreationYearColumn);

        incDictionaryController.gridMain.getColumns().addAll(nameColumn, creationYearColumn, parentGroupColumn);

        incDictionaryController.gridMain.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> LoadGroupToEdition((Group) newValue)));

        ObservableList<Integer> creationYearData = FXCollections.observableArrayList();
        for (int i = 2000; i < Calendar.getInstance().get(Calendar.YEAR) + 1; i++) {
            creationYearData.add(i);
        }
        cbCreationYear.setItems(creationYearData);
        cbParentGroup.setItems(parentGroupData);
        cbParentGroup.setCellFactory(new Callback<ListView<Group>, ListCell<Group>>() {
            @Override
            public ListCell<Group> call(ListView<Group> param) {
                final ListCell<Group> cell = new ListCell<Group>() {

                    @Override
                    protected void updateItem(Group group, boolean empty) {
                        super.updateItem(group, empty);

                        if (empty || group == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            setText(String.format("%s (%d)", group.getName(), group.getCreationYear()));
                        }
                    }
                };

                return cell;
            }
        });

        cbParentGroup.setConverter(new StringConverter<Group>() {
            @Override
            public String toString(Group object) {
                return object.getName();
            }

            @Override
            public Group fromString(String string) {
                return null;
            }
        });

        if (loadGroupsTask == null) {
            (loadGroupsTask = new LoadGroupsTask()).execute();
        }
    }

    void SetButtonActive(final Boolean active) {
        Platform.runLater(() -> {
            btnSave.setText(!active ? "" : "Zapisz");
            btnSave.setGraphic(!active ? new ProgressIndicator() : null);
        });
    }

    private void LoadGroupToEdition(Group group) {
        if (group == null) {
            edtName.setText("");
            cbCreationYear.setValue(cbCreationYear.getItems().get(0));
            cbParentGroup.setValue(null);
            return;
        }

        edtName.setText(group.getId() == -1 ? "" : group.getName());
        cbCreationYear.setValue(group.getId() == -1 ? cbCreationYear.getItems().get(0) : group.getCreationYear());
        cbParentGroup.setValue(group.getId() == -1 ? null : group.getParentGroup());
    }

    public class LoadGroupsTask extends SwingWorker {
        private JSONObject mResult;

        @Override
        protected Object doInBackground() {
            try {
                data.clear();
                parentGroupData.clear();
                mResult = new MySQLWebService().GetGroups();
            } catch (Exception e) {
                e.printStackTrace();
                Functions.SaveLog(e);
            }
            return null;
        }

        @Override
        protected void done() {
            try {
                if (mResult.getBoolean(WebServiceConstants.GetGroups.Result.RESULT)) {
                    for (int i = 0; i < mResult.getJSONArray(WebServiceConstants.GetGroups.Result.GROUPS).length(); i++) {
                        JSONObject group = mResult.getJSONArray(WebServiceConstants.GetGroups.Result.GROUPS).getJSONObject(i);
                        data.add(new Group(group.getInt(WebServiceConstants.GetGroups.Result.Groups.ID),
                                group.get(WebServiceConstants.GetGroups.Result.Groups.PARENT_GROUP_ID).equals(null)
                                        ? null
                                        : group.getInt(WebServiceConstants.GetGroups.Result.Groups.PARENT_GROUP_ID))
                                .setName(group.getString(WebServiceConstants.GetGroups.Result.Groups.NAME))
                                .setCreationYear(group.get(WebServiceConstants.GetGroups.Result.Groups.CREATION_YEAR).equals(null)
                                        ? -1
                                        : group.getInt(WebServiceConstants.GetGroups.Result.Groups.CREATION_YEAR))
                        );
                    }

                    FindParentGroups(data);

                    parentGroupData.addAll(data);

                    data.add(new Group(-1, null).setName("<Nowy...>"));
                }
            } finally {
                loadGroupsTask = null;
            }
        }
    }

    private void FindParentGroups(ObservableList<Group> data) {
        for (Group baseGroup : data) {
            if (baseGroup.getParentGroupId() == null) {
                continue;
            }

            if (baseGroup.getId() == baseGroup.getParentGroupId()) {
                continue;
            }

            for (Group otherGroups : data) {
                if (baseGroup == otherGroups) {
                    continue;
                }

                if (baseGroup.getParentGroupId() == otherGroups.getId()) {
                    baseGroup.setParentGroup(otherGroups);
                }
            }
        }
    }

    public class SaveGroupTask extends SwingWorker {
        private final Group mGroup;
        private JSONObject mResult;

        public SaveGroupTask(Group group) {
            mGroup = group;
        }

        @Override
        protected Object doInBackground() {
            SetButtonActive(false);
            try {
                mResult = new MySQLWebService().SaveGroup(mGroup.getId(), mGroup.getName(), mGroup.getCreationYear(),
                        (mGroup.getParentGroup() == null) ? null : mGroup.getParentGroup().getId());
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

                if (!mResult.getBoolean(WebServiceConstants.SaveGroup.Result.RESULT)) {
                    return;
                }

                if (loadGroupsTask == null)
                    (loadGroupsTask = new LoadGroupsTask()).execute();
                LoadGroupToEdition((Group) incDictionaryController.gridMain.getSelectionModel().getSelectedItem());
            } finally {
                saveGroupsTask = null;
            }
        }
    }
}
