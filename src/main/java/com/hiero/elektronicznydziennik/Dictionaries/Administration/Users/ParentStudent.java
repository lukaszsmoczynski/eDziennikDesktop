package com.hiero.elektronicznydziennik.Dictionaries.Administration.Users;

import com.hiero.elektronicznydziennik.Helpers.Classes.Users.Parent;
import com.hiero.elektronicznydziennik.Helpers.Controller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.ResourceBundle;

public class ParentStudent extends Controller implements Initializable {
    public ListView<Parent> lvParents;
    public ListView lvStudents;

    private ObservableList<Parent> data = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        data.add(new Parent(15));

        lvParents.setCellFactory(list -> new ColorRectCell());
        lvParents.getItems().addAll(data);
    }


    static class ColorRectCell extends ListCell<Parent> {
        @Override
        public void updateItem(Parent item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null)
            setText(item.getId().toString());
        }
    }
}
