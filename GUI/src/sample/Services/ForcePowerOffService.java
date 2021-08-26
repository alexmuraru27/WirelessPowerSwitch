package sample.Services;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;

import java.net.HttpURLConnection;
import java.net.URL;

public class ForcePowerOffService extends Service<Void> {
    private final String ip;
    private final String port;
    private final String name;
    private final String hash;

    public ForcePowerOffService(String name, String ip, String port, String hash) {
        this.ip = ip;
        this.port = port;
        this.name = name;
        this.hash = hash;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<>() {
            @Override
            protected Void call() {
                try {
                    URL url=new URL("http://"+ip+":"+port+"/powerOffForced?key="+hash);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setConnectTimeout(10000);
                    con.setDoOutput(true);


                    int returnCode = con.getResponseCode();
                    if (returnCode == HttpURLConnection.HTTP_OK) {
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Status");
                            alert.setHeaderText("Operatie efectuata cu succes pentru "+name);
                            alert.setContentText("Press OK to return");
                            alert.showAndWait().ifPresent(rs -> {  });
                        });
                    }
                } catch (Exception e) {
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Fail");
                        alert.setHeaderText("Conexiunea nu s-a putut realiza pentru "+name+" -> " + e.getMessage());
                        alert.setContentText("Press OK to return");
                        alert.showAndWait().ifPresent(rs -> {
                        });
                    });
                }
                return null;
            }
        };
    }
}
