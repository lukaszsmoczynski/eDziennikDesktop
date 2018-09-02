package com.hiero.elektronicznydziennik.Classes;

import com.hiero.elektronicznydziennik.Helpers.Classes.Class;
import com.hiero.elektronicznydziennik.Helpers.Controller;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

public class ClassInProgress extends Controller implements Initializable {
    public BorderPane pnlMain;
    private Class mClassInProgress;

    public void onBtnEndClassAction(ActionEvent actionEvent) {
        EndClass();
    }

    private void EndClass() {
        NotifyAll(pnlMain);
    }

    @Override
    public void Notify(Object pListener, Object... pObjects) {
        super.Notify(pListener, pObjects);

        if (pListener instanceof ClassEndedListener) {
            for (Object object : pObjects) {
                if (object instanceof Node) {
                    ((ClassEndedListener) pListener).OnClassEnded((Node) object);
                } else if (object instanceof Class) {
                    ((ClassEndedListener) pListener).OnClassEnded((Class) object);
                }
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public ClassInProgress setClassInProgress(Class pClass) {
        this.mClassInProgress = pClass;
        return this;
    }

    public Class getClassInProgress() {
        return mClassInProgress;
    }

    public interface ClassEndedListener {
        void OnClassEnded(Class pClass);
        void OnClassEnded(Node pNode);
    }
}
