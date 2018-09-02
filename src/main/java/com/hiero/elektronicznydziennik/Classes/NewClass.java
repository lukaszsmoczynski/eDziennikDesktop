package com.hiero.elektronicznydziennik.Classes;

import com.hiero.elektronicznydziennik.Helpers.Classes.Class;
import com.hiero.elektronicznydziennik.Helpers.Classes.Group;
import com.hiero.elektronicznydziennik.Helpers.Classes.Subject;
import com.hiero.elektronicznydziennik.Helpers.Classes.Users.User;
import com.hiero.elektronicznydziennik.Helpers.Controller;
import com.hiero.elektronicznydziennik.Helpers.Functions;
import com.hiero.elektronicznydziennik.WebService.MySQLWebService;
import com.hiero.elektronicznydziennik.WebService.WebServiceConstants;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import jfxtras.scene.control.CalendarTimePicker;
import org.controlsfx.control.CheckComboBox;
import org.json.JSONObject;

import javax.swing.*;
import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class NewClass extends Controller implements Initializable {
    public CheckComboBox<Group> cbGroups;
    public CalendarTimePicker tpStartTime;
    public ComboBox<DayOfWeek> cbDayOfOccurence;
    public ComboBox<Subject> cbSubject;
    public Button btnClear;
    public Button btnSave;
    public CalendarTimePicker tpEndTime;
    public ComboBox<User> cbTeacher;
    public TextField edtName;
    public DatePicker edtBeginDate;
    public DatePicker edtEndDate;
    private LoadGroupsTask loadGroupsTask;
    private LoadUsersTask loadUsersTask;
    private LoadSubjectsTask loadSubjectsTask;
    private SaveClassTask saveClassTask;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LocalDate localDate = LocalDate.now();
        edtBeginDate.setValue(localDate.minusDays(localDate.getDayOfYear() - 1));
        edtEndDate.setValue(localDate.plusYears(1).minusDays(localDate.getDayOfYear()));

        cbDayOfOccurence.setConverter(new StringConverter<DayOfWeek>() {
            @Override
            public String toString(DayOfWeek object) {
                switch (object) {
                    case MONDAY:
                        return "Poniedziałek";
                    case TUESDAY:
                        return "Wtorek";
                    case WEDNESDAY:
                        return "Środa";
                    case THURSDAY:
                        return "Czwartek";
                    case FRIDAY:
                        return "Piątek";
                    case SATURDAY:
                        return "Sobota";
                    case SUNDAY:
                        return "Niedziela";
                    default:
                        return "";
                }
            }

            @Override
            public DayOfWeek fromString(String string) {
                return null;
            }
        });
        cbDayOfOccurence.getItems().addAll(DayOfWeek.values());

        tpStartTime.setValueValidationCallback(param -> {
            if (param.after(tpEndTime.getCalendar())) {
                tpStartTime.setCalendar(tpEndTime.getCalendar());
                return false;
            }

            return true;
        });
        tpStartTime.getCalendar().setTimeInMillis(25200000);

        tpEndTime.setValueValidationCallback(param -> {
            if (param.before(tpStartTime.getCalendar())) {
                tpEndTime.setCalendar(tpStartTime.getCalendar());
                return false;
            }

            return true;
        });
        tpEndTime.getCalendar().setTimeInMillis(27900000);

        cbGroups.setConverter(new StringConverter<Group>() {
            @Override
            public String toString(Group object) {
                return object.getName();
            }

            @Override
            public Group fromString(String string) {
                for (Group group : cbGroups.getItems()) {
                    if (group.getName().equals(string)) {
                        return group;
                    }
                }
                return null;
            }
        });

        cbGroups.getCheckModel().getCheckedItems().addListener((ListChangeListener<Group>) c -> cbGroups.getCheckModel().getCheckedItems());

        if (loadGroupsTask == null) {
            (loadGroupsTask = new LoadGroupsTask()).execute();
        }

        cbTeacher.setConverter(new StringConverter<User>() {
            @Override
            public String toString(User object) {
                return object.getLastName() + " " + object.getFirstName();
            }

            @Override
            public User fromString(String string) {
                return null;
            }
        });

        if (loadUsersTask == null) {
            (loadUsersTask = new LoadUsersTask()).execute();
        }

        cbSubject.setConverter(new StringConverter<Subject>() {
            @Override
            public String toString(Subject object) {
                return object.getName();
            }

            @Override
            public Subject fromString(String string) {
                return null;
            }
        });

        if (loadSubjectsTask == null) {
            (loadSubjectsTask = new LoadSubjectsTask()).execute();
        }
    }

    public void onBtnSaveAction(javafx.event.ActionEvent actionEvent) {
        if (saveClassTask == null) {
            List<Integer> groupIds = new ArrayList<>();
            for (Group group : cbGroups.getCheckModel().getCheckedItems()) {
                groupIds.add(group.getId());
            }

            Calendar beginDate = Calendar.getInstance();
            beginDate.setTime(Date.from(edtBeginDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
            Calendar endDate = Calendar.getInstance();
            endDate.setTime(Date.from(edtEndDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));

            Class aClass = new Class(null)
                    .setName(edtName.getText())
                    .setTeacherId(cbTeacher.getSelectionModel().getSelectedItem().getId())
                    .setSubjectId(cbSubject.getSelectionModel().getSelectedItem().getId())
                    .setDayOfOccurence(cbDayOfOccurence.getSelectionModel().getSelectedItem())
                    .setGroupIds(groupIds)
                    .setBeginDate(beginDate)
                    .setEndDate(endDate)
                    .setBeginTime(tpStartTime.getCalendar())
                    .setEndTime(tpEndTime.getCalendar());
            (saveClassTask = new SaveClassTask(aClass)).execute();
        }
    }

    public void onBtnClearAction(javafx.event.ActionEvent actionEvent) {
        edtName.setText(null);
        cbTeacher.setValue(null);
        cbGroups.getCheckModel().clearChecks();
        cbSubject.setValue(null);
        cbDayOfOccurence.setValue(null);
    }

    public void onEdtBeginDateAction(ActionEvent actionEvent) {
        if (edtBeginDate.getValue().isAfter(edtEndDate.getValue())) {
            edtBeginDate.setValue(edtEndDate.getValue());
        }
    }

    public void onEdtEndDateAction(ActionEvent actionEvent) {
        if (edtEndDate.getValue().isBefore(edtBeginDate.getValue())) {
            edtEndDate.setValue(edtBeginDate.getValue());
        }
    }

    public class LoadGroupsTask extends SwingWorker {
        private JSONObject mResult;

        @Override
        protected Object doInBackground() {
            try {
                cbGroups.getItems().clear();
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
                        cbGroups.getItems().add(new Group(group.getInt(WebServiceConstants.GetGroups.Result.Groups.ID),
                                group.get(WebServiceConstants.GetGroups.Result.Groups.PARENT_GROUP_ID).equals(null)
                                        ? null
                                        : group.getInt(WebServiceConstants.GetGroups.Result.Groups.PARENT_GROUP_ID))
                                .setName(group.getString(WebServiceConstants.GetGroups.Result.Groups.NAME))
                                .setCreationYear(group.get(WebServiceConstants.GetGroups.Result.Groups.CREATION_YEAR).equals(null)
                                        ? -1
                                        : group.getInt(WebServiceConstants.GetGroups.Result.Groups.CREATION_YEAR))
                        );
                    }
                }
            } finally {
                loadGroupsTask = null;
            }
        }
    }

    public class LoadUsersTask extends SwingWorker {
        private JSONObject mResult;

        @Override
        protected Object doInBackground() {
            try {
                cbTeacher.getItems().clear();
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
                    for (int i = 0; i < mResult.getJSONArray(WebServiceConstants.GetUsers.Result.USERS).length(); i++) {
                        JSONObject user = mResult.getJSONArray(WebServiceConstants.GetUsers.Result.USERS).getJSONObject(i);

                        if (user.getBoolean(WebServiceConstants.GetUsers.Result.Users.TEACHER)) {
                            cbTeacher.getItems().add(new User(user.getInt(WebServiceConstants.GetUsers.Result.Users.ID))
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
                    }
                }
            } finally {
                loadUsersTask = null;
            }
        }
    }

    public class LoadSubjectsTask extends SwingWorker {
        private JSONObject mResult;

        @Override
        protected Object doInBackground() {
            try {
                cbSubject.getItems().clear();
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
                        cbSubject.getItems().add(new Subject(documentType.getInt(WebServiceConstants.GetSubjects.Result.Subjects.ID))
                                .setName(documentType.getString(WebServiceConstants.GetSubjects.Result.Subjects.NAME))
                        );
                    }
                }
            } finally {
                loadSubjectsTask = null;
            }
        }
    }

    public class SaveClassTask extends SwingWorker {
        private JSONObject mResult;

        private final Class mClass;

        public SaveClassTask(Class pClass) {
            mClass = pClass;
        }

        @Override
        protected Object doInBackground() {
            try {
                mResult = new MySQLWebService().SaveClass(mClass);
            } catch (Exception e) {
                e.printStackTrace();
                Functions.SaveLog(e);
            }
            return null;
        }

        @Override
        protected void done() {
            try {
                if (mResult.getBoolean(WebServiceConstants.SaveClass.Result.RESULT)) {
                    Platform.runLater(() -> {
                        new Alert(Alert.AlertType.CONFIRMATION, "Dodano nowe zajęcia.").showAndWait();
                        onBtnClearAction(null);
                    });
                } else {
                    Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, "Wystąpił błąd operacji.").showAndWait());
                }
            } catch (Exception e) {
                Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, "Wystąpił błąd operacji.").showAndWait());
                Functions.SaveLog(e);
                e.printStackTrace();
            } finally {
                saveClassTask = null;
            }
        }
    }
}
