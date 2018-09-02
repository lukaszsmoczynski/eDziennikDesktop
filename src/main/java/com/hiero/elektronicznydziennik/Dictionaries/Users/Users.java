package com.hiero.elektronicznydziennik.Dictionaries.Users;

import com.hiero.elektronicznydziennik.Dictionaries.Dictionary;
import com.hiero.elektronicznydziennik.Helpers.Classes.Users.User;
import com.hiero.elektronicznydziennik.Helpers.Functions;
import com.hiero.elektronicznydziennik.WebService.MySQLWebService;
import com.hiero.elektronicznydziennik.WebService.WebServiceConstants;
import com.sun.istack.internal.NotNull;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import org.json.JSONObject;

import javax.swing.*;
import java.net.URL;
import java.util.ResourceBundle;

public class Users extends Dictionary {
    public Button btnSave;
    public Button btnCancel;
    public TextField edtLogin;
    public PasswordField edtPassword;
    public TextField edtName;
    public TextField edtSurname;
    public CheckBox chbParent;
    public CheckBox chbTeacher;
    public CheckBox chbStudent;
    public CheckBox chbAdmin;

    private LoadUsersTask loadUsersTask;
    private SaveUserTask saveUserTask;

    private Boolean mPasswordChanged = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        TableColumn<User, String> loginColumn = new TableColumn<>("Login");
        loginColumn.setCellValueFactory(new PropertyValueFactory<>("login"));

        TableColumn<User, String> firstNameColumn = new TableColumn<>("Imię");
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn<User, String> lastNameColumn = new TableColumn<>("Nazwisko");
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        TableColumn<User, String> roleColumn = new TableColumn<>("Rola");

        TableColumn<User, Boolean> isParentColumn = new TableColumn<>("Rodzic");
        isParentColumn.setCellValueFactory(new PropertyValueFactory<>("parent"));
        isParentColumn.setCellFactory(tc -> new CheckBoxTableCell<>());

        TableColumn<User, Boolean> isTeacherColumn = new TableColumn<>("Nauczyciel");
        isTeacherColumn.setCellValueFactory(new PropertyValueFactory<>("teacher"));
        isTeacherColumn.setCellFactory(tc -> new CheckBoxTableCell<>());

        TableColumn<User, Boolean> isStudentColumn = new TableColumn<>("Uczeń");
        isStudentColumn.setCellValueFactory(new PropertyValueFactory<>("student"));
        isStudentColumn.setCellFactory(tc -> new CheckBoxTableCell<>());

        TableColumn<User, Boolean> isAdminColumn = new TableColumn<>("Administrator");
        isAdminColumn.setCellValueFactory(new PropertyValueFactory<>("admin"));
        isAdminColumn.setCellFactory(tc -> new CheckBoxTableCell<>());

        roleColumn.getColumns().addAll(isParentColumn, isStudentColumn, isTeacherColumn, isAdminColumn);
        incDictionaryController.gridMain.getColumns().addAll(loginColumn, firstNameColumn, lastNameColumn, roleColumn);

        incDictionaryController.gridMain.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> LoadUserToEdition((User) newValue));

        edtPassword.textProperty().addListener((observable, oldValue, newValue) -> mPasswordChanged = true);

        if (loadUsersTask == null)
            (loadUsersTask = new LoadUsersTask()).execute();
    }

    void SetButtonActive(final Boolean active) {
        Platform.runLater(() -> {
            btnSave.setText(!active ? "" : "Zapisz");
            btnSave.setGraphic(!active ? new ProgressIndicator() : null);
        });
    }

    public void onBtnSaveAction(ActionEvent actionEvent) {
        if (saveUserTask != null) {
            return;
        }

        User selectedUser = (User) incDictionaryController.gridMain.getSelectionModel().getSelectedItem();

        if (selectedUser == null || selectedUser.getId() == -1) {
            (saveUserTask = new SaveUserTask(new User(-1)
                    .setFirstName(edtName.getText())
                    .setLastName(edtSurname.getText())
                    .setLogin(edtLogin.getText())
                    .setPassword(edtPassword.getText())
                    .setParent(chbParent.isSelected())
                    .setStudent(chbStudent.isSelected())
                    .setTeacher(chbTeacher.isSelected())
                    .setAdmin(chbAdmin.isSelected()))).execute();
        } else {
            User user = new User(selectedUser.getId())
                    .setFirstName(edtName.getText())
                    .setLastName(edtSurname.getText())
                    .setLogin(edtLogin.getText())
                    .setParent(chbParent.isSelected())
                    .setStudent(chbStudent.isSelected())
                    .setTeacher(chbTeacher.isSelected())
                    .setAdmin(chbAdmin.isSelected());
            if (mPasswordChanged) {
                user.setPassword(edtPassword.getText());
            }
            (saveUserTask = new SaveUserTask(user)).execute();
        }
    }

    public void onBtnCancelAction(ActionEvent actionEvent) {
        LoadUserToEdition((User) incDictionaryController.gridMain.getSelectionModel().getSelectedItem());
    }

    private void LoadUserToEdition(@NotNull User user) {
        try {
            if (user == null) {
                edtLogin.setText("");
                edtName.setText("");
                edtPassword.setText("");
                edtSurname.setText("");
                chbParent.setSelected(false);
                chbStudent.setSelected(false);
                chbTeacher.setSelected(false);
                chbAdmin.setSelected(false);
                return;
            }

            edtLogin.setText(user.getId() == -1 ? "" : user.getLogin());
            edtName.setText(user.getId() == -1 ? "" : user.getFirstName());
            edtPassword.setText(user.getId() == -1 ? "" : user.getPassword());
            edtSurname.setText(user.getId() == -1 ? "" : user.getLastName());
            chbParent.setSelected(user.getId() != -1 && user.isParent());
            chbStudent.setSelected(user.getId() != -1 && user.isStudent());
            chbTeacher.setSelected(user.getId() != -1 && user.isTeacher());
            chbAdmin.setSelected(user.getId() != -1 && user.isAdmin());
        } finally {
            mPasswordChanged = false;
        }
    }

    public class LoadUsersTask extends SwingWorker {
        private JSONObject mResult;

        @Override
        protected Object doInBackground() {
            try {
                data.clear();
                mResult = new MySQLWebService().GetUsers();
            } catch (Exception e) {
                e.printStackTrace();
                Functions.SaveLog(e);
            }
            return null;
        }

        @Override
        protected void done() {
            try {
                if (mResult.getBoolean(WebServiceConstants.GetUsers.Result.RESULT)) {
                    Platform.runLater(() -> {
                        try {
                            for (int i = 0; i < mResult.getJSONArray(WebServiceConstants.GetUsers.Result.USERS).length(); i++) {
                                JSONObject user = mResult.getJSONArray(WebServiceConstants.GetUsers.Result.USERS).getJSONObject(i);
                                data.add(new User(user.getInt(WebServiceConstants.GetUsers.Result.Users.ID))
                                        .setLogin(user.getString(WebServiceConstants.GetUsers.Result.Users.LOGIN))
                                        .setPassword(user.getString(WebServiceConstants.GetUsers.Result.Users.PASSWORD))
                                        .setFirstName(user.getString(WebServiceConstants.GetUsers.Result.Users.NAME))
                                        .setLastName(user.getString(WebServiceConstants.GetUsers.Result.Users.SURNAME))
                                        .setParent(user.getBoolean(WebServiceConstants.GetUsers.Result.Users.PARENT))
                                        .setStudent(user.getBoolean(WebServiceConstants.GetUsers.Result.Users.STUDENT))
                                        .setTeacher(user.getBoolean(WebServiceConstants.GetUsers.Result.Users.TEACHER))
                                        .setAdmin(user.getBoolean(WebServiceConstants.GetUsers.Result.Users.ADMIN))
                                );
                            }
                            data.add(new User(-1).setLogin("<Nowy...>"));

                            incDictionaryController.gridMain.getSelectionModel().select(0);
                            LoadUserToEdition((User) incDictionaryController.gridMain.getSelectionModel().getSelectedItem());
                        } catch (Exception e) {
                            e.printStackTrace();
                            Functions.SaveLog(e);
                        }
                    });
                }
            } finally {
                loadUsersTask = null;
            }
        }
    }

    public class SaveUserTask extends SwingWorker {
        private final User mUser;
        private JSONObject mResult;

        public SaveUserTask(User user) {
            mUser = user;
        }

        @Override
        protected Object doInBackground() {
            SetButtonActive(false);
            try {
                mResult = new MySQLWebService().SaveUser(mUser.getId(), mUser.getLogin(), mUser.getPassword(), mUser.getFirstName(),
                        mUser.getLastName(), mUser.isParent(), mUser.isStudent(), mUser.isTeacher(), mUser.isAdmin());
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

                if (loadUsersTask == null)
                    (loadUsersTask = new LoadUsersTask()).execute();
            } finally {
                saveUserTask = null;
            }
        }
    }
}
