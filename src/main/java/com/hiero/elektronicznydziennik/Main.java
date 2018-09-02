package com.hiero.elektronicznydziennik;

import com.hiero.elektronicznydziennik.Classes.ClassInProgress;
import com.hiero.elektronicznydziennik.Classes.Classes;
import com.hiero.elektronicznydziennik.Helpers.Classes.Class;
import com.hiero.elektronicznydziennik.Helpers.ControllerInterface;
import com.hiero.elektronicznydziennik.Helpers.Functions;
import com.sun.istack.internal.NotNull;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class Main extends Application implements Initializable, Classes.ClassStartedListener, ClassInProgress.ClassEndedListener {
    public BorderPane pnlMain;

    public Accordion accMenu;
    public TabPane tabCenter;
    public VBox pnlClassesInProgress;
    public ScrollPane sbClassesInProgress;

    private Map<String, Tab> openedTabs = new HashMap<>();
    private static Stage mStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        mStage = primaryStage;
        mStage.setTitle("Elektroniczny Dziennik");
        Parent root = FXMLLoader.load(getClass().getResource("/layout/main.fxml"));
        Scene scene = new Scene(root, 800, 600);
        mStage.setScene(scene);

        mStage.show();
    }

    public void onBtnTestClick() {
        CreateTab("/layout/test2.fxml", "test");
    }

    public void onBtnTest1Click() {
        for (int i = 0; i < 5; i++) {
            OnClassStarted(null);
        }
    }

    private Tab CreateTab(@NotNull String fxml, String title) {
        if (openedTabs.containsKey(fxml)) {
            Tab tab = openedTabs.get(fxml);
            tabCenter.getSelectionModel().select(openedTabs.get(fxml));
            return tab;
        }

        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource(fxml));
            Node node = fxmlLoader.load(getClass().getResource(fxml).openStream());

            if (fxmlLoader.getController() instanceof ControllerInterface) {
                ControllerInterface controllerInterface = fxmlLoader.getController();
                controllerInterface.Attach(this);
            }

            Tab tab = new Tab(title);
            tab.setContent(node);
            tab.setClosable(true);
            tab.setOnClosed(event -> openedTabs.remove(fxml));

            tabCenter.getTabs().add(tab);
            tabCenter.getSelectionModel().select(tab);
            openedTabs.put(fxml, tab);

            return tab;
        } catch (IOException e) {
            Functions.SaveLog(e);
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        accMenu.setExpandedPane(accMenu.getPanes().get(0));
        pnlMain.setRight(null);
    }

    public void onBtnLogoutAction(ActionEvent actionEvent) {
        try {
            mStage.hide();
            new Login().start(new Stage());
        } catch (Exception e) {
            Functions.SaveLog(e);
            e.printStackTrace();
        }
    }

    public void onBtnExitAction(ActionEvent actionEvent) {
        Platform.exit();
    }

    public void onBtnUsersAction(ActionEvent actionEvent) {
        CreateTab("/layout/Dictionaries/Users/users.fxml", "Użytkownicy");
    }

    public void onBtnStudentsAction(ActionEvent actionEvent) {
        CreateTab("/layout/Dictionaries/Users/students.fxml", "Uczniowie");
    }

    public void onBtnParentsAction(ActionEvent actionEvent) {
        CreateTab("/layout/Dictionaries/Users/parents.fxml", "Rodzice");
    }

    public void onBtnTeachersAction(ActionEvent actionEvent) {
        CreateTab("/layout/Dictionaries/Users/teachers.fxml", "Nauczyciele");
    }

    public void onBtnAdminsAction(ActionEvent actionEvent) {
        CreateTab("/layout/Dictionaries/Users/admins.fxml", "Administratorzy");
    }

    public void onBtnFormsOfEmploymentAction(ActionEvent actionEvent) {
        CreateTab("/layout/Dictionaries/formsOfEmployment.fxml", "Rodzaje zatrudnienia");
    }

    public void onBtnDocumentTypesAction(ActionEvent actionEvent) {
        CreateTab("/layout/Dictionaries/documentTypes.fxml", "Rodzaje dokumentów");
    }

    public void onBtnRelationTypesAction(ActionEvent actionEvent) {
        CreateTab("/layout/Dictionaries/relationTypes.fxml", "Relacje opiekun-uczeń");
    }

    public void onBtnSubjectsAction(ActionEvent actionEvent) {
        CreateTab("/layout/Dictionaries/subjects.fxml", "Przedmioty");
    }

    public void onBtnGroupsAction(ActionEvent actionEvent) {
        CreateTab("/layout/Dictionaries/groups.fxml", "Grupy zajęciowe");
    }

    public void onBtnModulesAction(ActionEvent actionEvent) {
        CreateTab("/layout/Dictionaries/modules.fxml", "Moduły");
    }

    public void onBtnClassesAction() {
        CreateTab("/layout/Classes/classes.fxml", "Przegląd zajęć");
    }

    public void onBtnNewClassesAction() {
        CreateTab("/layout/Classes/newClass.fxml", "Nowe zajęcia");
    }

    @Override
    public void OnClassStarted(Class pClass) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/layout/Classes/classInProgress.fxml"));
            Node node = fxmlLoader.load(getClass().getResource("/layout/Classes/classInProgress.fxml").openStream());
            if (fxmlLoader.getController() instanceof ControllerInterface) {
                ControllerInterface controllerInterface = fxmlLoader.getController();
                controllerInterface.Attach(this);

                if (controllerInterface instanceof ClassInProgress) {
                    ((ClassInProgress) controllerInterface).setClassInProgress(pClass);
                }
            }

            pnlMain.setRight(sbClassesInProgress);
            pnlClassesInProgress.getChildren().add(node);
        } catch (IOException e) {
            Functions.SaveLog(e);
            e.printStackTrace();
        }
    }

    @Override
    public void OnClassEnded(Class pClass) {
        for (Node node : pnlClassesInProgress.getChildren()) {

        }
    }

    @Override
    public void OnClassEnded(Node pNode) {
        for (Node node : pnlClassesInProgress.getChildren()) {
            if (node.equals(pNode)) {
                pnlClassesInProgress.getChildren().remove(node);
                break;
            }
        }

        if (pnlClassesInProgress.getChildren().size() <= 0) {
            pnlMain.setRight(null);
        }
    }
}
