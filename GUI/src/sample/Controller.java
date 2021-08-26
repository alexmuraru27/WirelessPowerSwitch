package sample;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.stream.Stream;

public class Controller {
    @FXML
    FlowPane flowPane;

    @FXML
    ScrollPane scrollPane;

    String HASH = "";

    @FXML
    public void initialize() {
        flowPane.prefWidthProperty().bind(scrollPane.widthProperty());
        flowPane.prefHeightProperty().bind(scrollPane.heightProperty());
        File configFile = new File("./config.txt");
        if(!configFile.exists()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Fisierul de configurare config.txt nu exista! Va fi creat automat, va rugam completati datele necesare, apoi reporniti programul");
            alert.setContentText("Press OK to exit");
            alert.showAndWait().ifPresent(rs -> {
                if (rs == ButtonType.OK) {
                    try{
                        configFile.createNewFile();
                    }
                    catch (Exception e){e.printStackTrace();}
                    Platform.exit();
                }
            });
        }else{
            var ref = new Object() {
                boolean isWithHash = false;
            };

            try (Stream<String> stream = Files.lines(Paths.get(configFile.getAbsolutePath()))) {
                stream.forEach(it->{
                    String[] tokens=it.split(",");
                    if(tokens.length == 1  && !ref.isWithHash)
                    {
                        MessageDigest digest = null;
                        try {
                            digest = MessageDigest.getInstance("SHA-256");
                            byte[] hash = digest.digest(tokens[0].getBytes(StandardCharsets.UTF_8));
                            for (byte b : hash) {
                                HASH += (Integer.toString((b & 0xff) + 0x100, 16).substring(1));
                            }
                            ref.isWithHash = true;
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        }
                    }
                    else if(tokens.length == 3 && ref.isWithHash)
                    {
                        Computer computer=new Computer(tokens[0],tokens[1],tokens[2], HASH);
                        flowPane.getChildren().add(computer);
                    }
                    else{
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText("Fisierul de configurare contine elemente invalide");
                        alert.setContentText("Press OK to exit");
                        alert.showAndWait().ifPresent(rs -> {
                            if (rs == ButtonType.OK) {
                                Platform.exit();
                            }
                        });
                    }

                });
                if(!ref.isWithHash)
                {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Fisierul de configurare nu contine un cuvant cheie");
                    alert.setContentText("Press OK to exit");
                    alert.showAndWait().ifPresent(rs -> {
                        if (rs == ButtonType.OK) {
                            Platform.exit();
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Timeline updateStatus = new Timeline(new KeyFrame(Duration.seconds(5), event -> timeoutHandler()));
            updateStatus.setCycleCount(Timeline.INDEFINITE);
            updateStatus.play();
        }
    }


    void timeoutHandler(){
        ObservableList<Node> computersList=flowPane.getChildren();
        for (Node a:computersList){
            if(a instanceof Computer) {
                ((Computer)a).updateStatus();
            }
        }
    }
}
