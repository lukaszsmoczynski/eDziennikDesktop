package com.hiero.elektronicznydziennik.Administration.Users;

import com.hiero.elektronicznydziennik.Administration.Dictionaries.Dictionary;
import com.hiero.elektronicznydziennik.Helpers.Classes.Users.Admin;
import com.hiero.elektronicznydziennik.Helpers.Functions;
import com.hiero.elektronicznydziennik.WebService.MySQLWebService;
import com.hiero.elektronicznydziennik.WebService.WebServiceConstants;
import javafx.application.Platform;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import org.json.JSONObject;

import javax.swing.*;
import java.net.URL;
import java.util.ResourceBundle;

public class Admins extends Dictionary {
    private LoadAdminsTask loadAdminsTask;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        TableColumn<Admin, String> loginColumn = new TableColumn<>("Login");
        loginColumn.setCellValueFactory(new PropertyValueFactory<>("login"));

        TableColumn<Admin, String> firstNameColumn = new TableColumn<>("Imię");
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn<Admin, String> lastNameColumn = new TableColumn<>("Nazwisko");
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        incDictionaryController.gridMain.getColumns().addAll(loginColumn, firstNameColumn, lastNameColumn);

        if (loadAdminsTask == null)
            (loadAdminsTask = new LoadAdminsTask()).execute();
    }

    private class LoadAdminsTask extends SwingWorker {
        private JSONObject mResult;

        @Override
        protected Object doInBackground() throws Exception {
            try {
                mResult = new MySQLWebService().GetAdmins();
            } catch (Exception e) {
                e.printStackTrace();
                Functions.SaveLog(e);
            }
            return null;
        }

        @Override
        protected void done() {
            try {
                if (mResult.getBoolean(WebServiceConstants.GetAdmins.Result.RESULT)) {
                    Platform.runLater(() -> {
                        try {
                            data.clear();
                            for (int i = 0; i < mResult.getJSONArray(WebServiceConstants.GetAdmins.Result.ADMINS).length(); i++) {
                                JSONObject parent = mResult.getJSONArray(WebServiceConstants.GetAdmins.Result.ADMINS).getJSONObject(i);

                                data.add(new Admin(parent.getInt(WebServiceConstants.GetAdmins.Result.Admins.ID))
                                        .setLogin(parent.getString(WebServiceConstants.GetAdmins.Result.Admins.LOGIN))
                                        .setFirstName(parent.getString(WebServiceConstants.GetAdmins.Result.Admins.NAME))
                                        .setLastName(parent.getString(WebServiceConstants.GetAdmins.Result.Admins.SURNAME))
                                );
                            }
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
                loadAdminsTask = null;
            }
        }
    }
}
