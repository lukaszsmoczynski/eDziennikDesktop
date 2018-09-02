package com.hiero.elektronicznydziennik.Administration.Dictionaries;

import com.hiero.elektronicznydziennik.Helpers.Controller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.ResourceBundle;

public class Dictionary extends Controller implements Initializable {
    public TableView gridMain;
    protected final ObservableList data = FXCollections.observableArrayList();

    public Dictionary incDictionaryController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (incDictionaryController != null) {
            //noinspection unchecked
            incDictionaryController.gridMain.setItems(data);
        }

        NotifyAll();
    }
}
