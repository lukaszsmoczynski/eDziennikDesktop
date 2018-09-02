package com.hiero.elektronicznydziennik.Administration.Dictionaries;

import com.hiero.elektronicznydziennik.Helpers.Functions;
import com.hiero.elektronicznydziennik.WebService.MySQLWebService;
import com.hiero.elektronicznydziennik.WebService.WebServiceConstants;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import org.json.JSONObject;

import javax.swing.*;
import java.net.URL;
import java.util.ResourceBundle;

public class Modules extends Dictionary {
    public Button btnSave;
    public Button btnCancel;
    private LoadModulesTask loadModulesTask;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        TableColumn<Module, String> nameColumn = new TableColumn<>("Nazwa");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        incDictionaryController.gridMain.getColumns().add(nameColumn);

        if (loadModulesTask == null)
            (loadModulesTask = new LoadModulesTask()).execute();
    }

    public class LoadModulesTask extends SwingWorker {
        private JSONObject mResult;

        @Override
        protected Object doInBackground() {
            try {
                data.clear();
                mResult = new MySQLWebService().GetModules();
            } catch (Exception e) {
                Functions.SaveLog(e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void done() {
            try {
                if (mResult.getBoolean(WebServiceConstants.GetModules.Result.RESULT)) {
                    for (int i = 0; i < mResult.getJSONArray(WebServiceConstants.GetModules.Result.MODULES).length(); i++) {
                        JSONObject module = mResult.getJSONArray(WebServiceConstants.GetModules.Result.MODULES).getJSONObject(i);
                        data.add(new Module(module.getInt(WebServiceConstants.GetModules.Result.Modules.ID))
                                .setName(module.getString(WebServiceConstants.GetModules.Result.Modules.NAME))
                        );
                    }
                }
            } finally {
                loadModulesTask = null;
            }
        }
    }

    public class Module {
        private final SimpleIntegerProperty mId = new SimpleIntegerProperty();
        private final SimpleStringProperty mName = new SimpleStringProperty();

        public Module(Integer id) {
            this.mId.set(id);
        }

        public String getName() {
            return mName.get();
        }

        public SimpleStringProperty nameProperty() {
            return mName;
        }

        public Module setName(String name) {
            this.mName.set(name);
            return this;
        }

        public int getId() {
            return mId.get();
        }

        public SimpleIntegerProperty idProperty() {
            return mId;
        }
    }
}
