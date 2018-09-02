package com.hiero.elektronicznydziennik;

import coam.hiero.elektronicznydziennik.Enums.UserType;
import com.hiero.elektronicznydziennik.Helpers.Functions;
import com.hiero.elektronicznydziennik.Helpers.SharedPreferences.PreferencesHelper;
import com.hiero.elektronicznydziennik.WebService.MySQLWebService;
import com.hiero.elektronicznydziennik.WebService.WebServiceConstants;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.json.JSONObject;

import javax.swing.*;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

public class Login extends Application implements Initializable {
    private static final UserType AUTO_LOGIN_AS = UserType.PARENT;

    public TextField edtLogin;
    public PasswordField edtPassword;
    public Button btnLogin;
    public Button btnExit;
    public ImageView imgWarningLogin;
    public ImageView imgWarningPassword;

    private LoginTask loginTask;

    private static Stage mStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.initStyle(StageStyle.UNDECORATED);
        Parent root = FXMLLoader.load(getClass().getResource("/layout/login.fxml"));
        Scene scene = new Scene(root, null);
        primaryStage.setScene(scene);

        primaryStage.show();

        mStage = primaryStage;
    }

    public void btnExitClicked() {
        Platform.exit();
    }

    public void btnLoginClicked() {
        try {
            if (loginTask != null) {
                loginTask.cancel(true);
            } else {
                Boolean stop = false;
                if (edtLogin.getText().isEmpty()) {
                    FadeTransition ft = new FadeTransition(Duration.millis(500), imgWarningLogin);
                    ft.setFromValue(0);
                    ft.setToValue(1);
                    ft.setCycleCount(5);
                    ft.setAutoReverse(true);
                    ft.play();
                    imgWarningLogin.setVisible(true);
                    Tooltip.install(imgWarningLogin, new Tooltip("Brak loginu"));

                    stop = true;
                }

                if (edtPassword.getText().isEmpty()) {
                    FadeTransition ft = new FadeTransition(Duration.millis(500), imgWarningPassword);
                    ft.setFromValue(0);
                    ft.setToValue(1);
                    ft.setCycleCount(5);
                    ft.setAutoReverse(true);
                    ft.play();
                    imgWarningPassword.setVisible(true);
                    Tooltip.install(imgWarningPassword, new Tooltip("Brak hasła"));

                    stop = true;
                }

                if (stop) {
                    return;
                }

                imgWarningLogin.setVisible(false);
                imgWarningPassword.setVisible(false);

                (loginTask = new LoginTask(edtLogin.getText(), edtPassword.getText())).execute();
            }
        } catch (Exception e) {
            Functions.SaveLog(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        edtLogin.setText(PreferencesHelper.getLastLogin());

        UnaryOperator<TextFormatter.Change> filter = change -> {
            String text = change.getText();

            if (text.matches("[a-ż]*")) {
                change.setText(text.toUpperCase());
            }

            return change;
        };

        edtLogin.setTextFormatter(new TextFormatter <> (filter));


        switch (AUTO_LOGIN_AS) {
            case ADMIN:
                edtLogin.setText("ADMIN_LOGIN");
                edtPassword.setText("12345678");
                btnLogin.fire();
                break;
            case STUDENT:
                edtLogin.setText("STUDENT_1");
                edtPassword.setText("12345678");
                btnLogin.fire();
                break;
            case TEACHER:
                edtLogin.setText("A_Dembczynska");
                edtPassword.setText("12345678");
                btnLogin.fire();
                break;
            case PARENT:
                edtLogin.setText("PARENT_1");
                edtPassword.setText("12345678");
                btnLogin.fire();
                break;
            case NONE:
                break;
            default:
                break;
        }
    }

    public void onEdtLoginKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            if (keyEvent.isShiftDown())
                return;

            edtPassword.requestFocus();
        }
    }

    public void onEdtPasswordKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            if (keyEvent.isShiftDown()) {
                edtLogin.requestFocus();
            } else {
                btnLogin.fire();
            }
        }
    }

    private class LoginTask extends SwingWorker {
        private JSONObject mResult;
        private final String mLogin, mPassword;

        private LoginTask(String login, String password) {
            this.mLogin = login;
            this.mPassword = password;
        }

        @Override
        protected Object doInBackground() {
            try {
                SetLoginButtonActivity(true);
                mResult = new MySQLWebService().Login(mLogin, mPassword);
            } catch (Exception e){
                e.printStackTrace();
                Functions.SaveLog(e);
                Platform.runLater(() -> edtPassword.requestFocus());

                loginTask = null;
            }
            return null;
        }

        @Override
        protected void done() {
            try {
                if (mResult == null) {
                    return;
                }

                if (mResult.getBoolean(WebServiceConstants.Login.Result.RESULT)) {
                    PreferencesHelper.setLastLogin(edtLogin.getText().toString());
                    PreferencesHelper.setUserID(mResult.getInt(WebServiceConstants.Login.Result.USER_ID));
                    PreferencesHelper.setUserName(edtLogin.getText().toString());
                    PreferencesHelper.setIsParent(mResult.getBoolean(WebServiceConstants.Login.Result.IS_PARENT));
                    PreferencesHelper.setIsStudent(mResult.getBoolean(WebServiceConstants.Login.Result.IS_STUDENT));
                    PreferencesHelper.setIsAdmin(mResult.getBoolean(WebServiceConstants.Login.Result.IS_ADMIN));
                    PreferencesHelper.setIsTeacher(mResult.getBoolean(WebServiceConstants.Login.Result.IS_TEACHER));


                    Platform.runLater(() -> {
                        try {
                            mStage.hide();
                            new Main().start(new Stage());
                        } catch (Exception e) {
                            e.printStackTrace();
                            Functions.SaveLog(e);
                        }
                    });
                } else {
                    FadeTransition ft = new FadeTransition(Duration.millis(500), imgWarningPassword);
                    ft.setFromValue(0);
                    ft.setToValue(1);
                    ft.setCycleCount(5);
                    ft.setAutoReverse(true);
                    ft.play();
                    imgWarningPassword.setVisible(true);

                    Tooltip.install(imgWarningPassword, new Tooltip("Nieprawidłowe login i/lub hasło"));
                }
            } catch (Exception e) {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText("Wystąpił błąd operacji");
                    alert.showAndWait();
                });

                e.printStackTrace();
                Functions.SaveLog(e);
            } finally {
                Platform.runLater(() -> {
                    edtPassword.setText("");
                    edtPassword.requestFocus();
                });

                SetLoginButtonActivity(false);
                loginTask = null;
            }
        }

        @Override
        protected void process(List chunks) {
            super.process(chunks);
        }
    }

    void SetLoginButtonActivity(final Boolean active) {
        Platform.runLater(() -> {
            btnLogin.setText(active ? "PRZERWIJ" : "ZALOGUJ");
            btnLogin.setGraphic(active ? new ProgressIndicator() : null);
        });
    }
}
