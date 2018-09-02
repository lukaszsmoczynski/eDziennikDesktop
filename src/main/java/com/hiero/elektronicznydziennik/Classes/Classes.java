package com.hiero.elektronicznydziennik.Classes;

import com.hiero.elektronicznydziennik.Enums.UserType;
import com.hiero.elektronicznydziennik.Helpers.Classes.Class;
import com.hiero.elektronicznydziennik.Helpers.Controller;
import com.hiero.elektronicznydziennik.Helpers.Functions;
import com.hiero.elektronicznydziennik.Helpers.SharedPreferences.PreferencesHelper;
import com.hiero.elektronicznydziennik.WebService.MySQLWebService;
import com.hiero.elektronicznydziennik.WebService.WebServiceConstants;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import jfxtras.scene.control.agenda.Agenda;
import jfxtras.scene.control.agenda.Agenda.Appointment;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class Classes extends Controller implements Initializable {
    public Agenda agendaClasses;
    private LoadScheduleTask loadScheduleTask;
    private Appointment mClickedAppointment, mAppointmentForContextMenu;
    private ContextMenu agendaContextMenu;
    private Map<Appointment, Class> mClassesList = new HashMap<>();

    private void loadSchedule(Date pStartDate) {
        if (loadScheduleTask != null) {
            loadScheduleTask.cancel(true);
        }

        Calendar firstDayOfWeek = Calendar.getInstance();
        firstDayOfWeek.setTime(pStartDate);

        while (firstDayOfWeek.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            firstDayOfWeek.add(Calendar.DAY_OF_YEAR, -1);
        }

        LoadScheduleTask loadScheduleTask = new LoadScheduleTask(firstDayOfWeek.getTime());
        loadScheduleTask.execute();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        agendaClasses.setLocalDateTimeRangeCallback(param -> {
            loadSchedule(java.sql.Date.valueOf(agendaClasses.getDisplayedLocalDateTime().toLocalDate()));
            return null;
        });

        agendaClasses.setAppointmentChangedCallback(param -> {
            loadSchedule(java.sql.Date.valueOf(agendaClasses.getDisplayedLocalDateTime().toLocalDate()));
            return null;
        });

        agendaClasses.setEditAppointmentCallback(param -> {
            mClickedAppointment = param;
            return null;
        });

        agendaContextMenu = new ContextMenu();
        MenuItem menuItem = new MenuItem("Rozpocznij zajęcia");
        menuItem.setOnAction(event -> StartClass(mClassesList.get(mAppointmentForContextMenu)));
        agendaContextMenu.getItems().add(menuItem);

        agendaContextMenu.setOnHidden(event -> mAppointmentForContextMenu = null);

        agendaClasses.setOnContextMenuRequested(event -> {
            if (mClickedAppointment == null) {
                return;
            }

            agendaContextMenu.show(agendaClasses, event.getScreenX(), event.getScreenY());
            mAppointmentForContextMenu = mClickedAppointment;
            mClickedAppointment = null;
        });
    }

    public void onBtnPrevWeekAction(ActionEvent actionEvent) {
        agendaClasses.setDisplayedLocalDateTime(agendaClasses.getDisplayedLocalDateTime().minusWeeks(1));
    }

    public void onBtnNextWeekAction(ActionEvent actionEvent) {
        agendaClasses.setDisplayedLocalDateTime(agendaClasses.getDisplayedLocalDateTime().plusWeeks(1));
    }

    public void onBtnTodayAction(ActionEvent actionEvent) {
        agendaClasses.setDisplayedLocalDateTime(LocalDateTime.now());
    }

    @Override
    public void Notify(Object pListener, Object... pObjects) {
        super.Notify(pListener, pObjects);

        if (pListener instanceof ClassStartedListener) {
            for (Object object : pObjects) {
                ((ClassStartedListener) pListener).OnClassStarted((Class) object);
            }
        }
    }

    private class LoadScheduleTask extends SwingWorker {
        private JSONObject mResult;
        private final Date mStartDate;

        public LoadScheduleTask(Date pStartDate) {
            mStartDate = pStartDate;
        }

        @Override
        protected Object doInBackground() {
            try {
                UserType userType;
                if (PreferencesHelper.getIsTeacher()) {
                    userType = UserType.TEACHER;
                } else if (PreferencesHelper.getIsStudent()) {
                    userType = UserType.STUDENT;
                } else {
                    return null;
                }

                mResult = new MySQLWebService().GetSchedule(PreferencesHelper.getUserID(), userType, mStartDate, 7);
            } catch (Exception e) {
                e.printStackTrace();
                Functions.SaveLog(e);
            }
            return null;
        }

        @Override
        protected void done() {
            try {
                if (mResult == null) {
                    return;
                }

                if (mResult.getBoolean(WebServiceConstants.GetSchedule.Result.RESULT)) {
                    JSONArray schedules = mResult.getJSONArray(WebServiceConstants.GetSchedule.Result.SCHEDULE);

                    mClassesList.clear();
                    ArrayList<Appointment> appointments = new ArrayList<>();

                    for (int i = 0; i < schedules.length(); i++) {
                        JSONObject dayOfSchedule = schedules.getJSONObject(i);

                        LocalDate date = LocalDate.parse(dayOfSchedule.getString(WebServiceConstants.GetSchedule.Result.Schedule.DATE));
                        Calendar calDate = Calendar.getInstance();
                        calDate.setTime(java.sql.Date.valueOf(date));
                        Calendar calBeginTime = Calendar.getInstance();
                        Calendar calEndTime = Calendar.getInstance();

                        JSONArray classes = dayOfSchedule.getJSONArray(WebServiceConstants.GetSchedule.Result.Schedule.CLASSES);
                        for (int j = 0; j < classes.length(); j++) {
                            calBeginTime.setTime(new SimpleDateFormat("hh:mm:ss").parse(classes.getJSONObject(j).getString(WebServiceConstants.GetSchedule.Result.Schedule.Classes.BEGIN_TIME)));
                            calEndTime.setTime(new SimpleDateFormat("hh:mm:ss").parse(classes.getJSONObject(j).getString(WebServiceConstants.GetSchedule.Result.Schedule.Classes.END_TIME)));

                            mClassesList.put(new Agenda.AppointmentImplLocal()
                                            .withStartLocalDateTime(LocalDateTime.of(date, LocalTime.parse(classes.getJSONObject(j).getString(WebServiceConstants.GetSchedule.Result.Schedule.Classes.BEGIN_TIME))))
                                            .withEndLocalDateTime(LocalDateTime.of(date, LocalTime.parse(classes.getJSONObject(j).getString(WebServiceConstants.GetSchedule.Result.Schedule.Classes.END_TIME))))
                                            .withSummary(classes.getJSONObject(j).getString(WebServiceConstants.GetSchedule.Result.Schedule.Classes.CLASS_NAME))
                                            .withDescription(classes.getJSONObject(j).getJSONArray(WebServiceConstants.GetSchedule.Result.Schedule.Classes.GROUPS).join(", ")),
                                    new Class(classes.getJSONObject(j).getInt(WebServiceConstants.GetSchedule.Result.Schedule.Classes.CLASS_ID))
                                            .setName(classes.getJSONObject(j).getString(WebServiceConstants.GetSchedule.Result.Schedule.Classes.CLASS_NAME))
                                            .setBeginDate(calDate)
                                            .setBeginTime(calBeginTime)
                                            .setEndDate(calDate)
                                            .setEndTime(calEndTime));
//                            appointments.add(new Agenda.AppointmentImplLocal()
//                                    .withStartLocalDateTime(LocalDateTime.of(date, LocalTime.parse(classes.getJSONObject(j).getString(WebServiceConstants.GetSchedule.Result.Schedule.Classes.BEGIN_TIME))))
//                                    .withEndLocalDateTime(LocalDateTime.of(date, LocalTime.parse(classes.getJSONObject(j).getString(WebServiceConstants.GetSchedule.Result.Schedule.Classes.END_TIME))))
//                                    .withSummary(classes.getJSONObject(j).getString(WebServiceConstants.GetSchedule.Result.Schedule.Classes.CLASS_NAME))
//                                    .withDescription(classes.getJSONObject(j).getJSONArray(WebServiceConstants.GetSchedule.Result.Schedule.Classes.GROUPS).join(", ")));
//                            mClassesList.add(ScheduleListAdapter.Class.NewStudentClassInstance(
//                                    classes.getJSONObject(j).getInt(WebServiceConstants.GetSchedule.Result.Schedule.Classes.CLASS_ID),
//                                    classes.getJSONObject(j).getString(WebServiceConstants.GetSchedule.Result.Schedule.Classes.CLASS_NAME),
//                                    headerDate,
//                                    new SimpleDateFormat("hh:mm:ss").parse(classes.getJSONObject(j).getString(WebServiceConstants.GetSchedule.Result.Schedule.Classes.BEGIN_TIME)),
//                                    new SimpleDateFormat("hh:mm:ss").parse(classes.getJSONObject(j).getString(WebServiceConstants.GetSchedule.Result.Schedule.Classes.END_TIME)),
//                                    classes.getJSONObject(j).getString(WebServiceConstants.GetSchedule.Result.Schedule.Classes.TEACHER),
//                                    classes.getJSONObject(j).get(WebServiceConstants.GetSchedule.Result.Schedule.Classes.PRESENT).equals(null) ? null : classes.getJSONObject(j).getBoolean(WebServiceConstants.GetSchedule.Result.Schedule.Classes.PRESENT)));
                        }
                    }

                    Platform.runLater(() -> {
                        agendaClasses.appointments().clear();
                        agendaClasses.appointments().addAll(mClassesList.keySet());
                    });
                }
            } catch (ParseException e) {
                Functions.SaveLog(e);
                e.printStackTrace();
            } finally {
                loadScheduleTask = null;
            }
        }
    }

    private void StartClass(Class pClass) {
        NotifyAll(pClass);
    }

    public interface ClassStartedListener {
        void OnClassStarted(Class pClass);
    }
}
