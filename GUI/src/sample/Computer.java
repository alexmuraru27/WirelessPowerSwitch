package sample;

import javafx.concurrent.WorkerStateEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import sample.Services.ForcePowerOffService;
import sample.Services.GetStatusService;
import sample.Services.PowerService;

import java.io.File;
import java.util.Random;


public class Computer extends VBox {
    private String name;
    private String ip;
    private String port;
    private String hash;
    private Boolean status;

    Label deviceName;

    private Button startButton;
    private Button stopButton;
    private Button forceStopButton;


    public Computer(String name, String ip, String port, String hash) {
        this.name = name;
        this.ip = ip;
        this.port = port;
        this.hash = hash;
        this.setAlignment(Pos.CENTER);


        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);

        //Add image
        Image img = new Image(new File("./Assets/PC.jpg").toURI().toString());
        ImageView view = new ImageView(img);
        view.setFitHeight(200);
        view.setPreserveRatio(true);

        this.getChildren().add(view);

        //Add name
        deviceName=new Label(name);
        deviceName.getStyleClass().add("computer-label");
        this.getChildren().add(deviceName);

        //Add buttons
        startButton=new Button();
        stopButton=new Button();
        forceStopButton=new Button();

        Image startImg = new Image(new File("./Assets/startIcon.png").toURI().toString());
        ImageView startImgView = new ImageView(startImg);
        startImgView.setFitHeight(25);
        startImgView.setPreserveRatio(true);
        startButton.setGraphic(startImgView);
        startButton.getStyleClass().add("computer-button");

        Image stopImg = new Image(new File("./Assets/stopIcon.png").toURI().toString());
        ImageView stopImgView = new ImageView(stopImg);
        stopImgView.setFitHeight(25);
        stopImgView.setPreserveRatio(true);
        stopButton.setGraphic(stopImgView);
        stopButton.getStyleClass().add("computer-button");

        Image forcedStopImg = new Image(new File("./Assets/forceStopIcon.png").toURI().toString());
        ImageView forcedStopImgView = new ImageView(forcedStopImg);
        forcedStopImgView.setFitHeight(25);
        forcedStopImgView.setPreserveRatio(true);
        forceStopButton.setGraphic(forcedStopImgView);
        forceStopButton.getStyleClass().add("computer-button");

        hBox.getChildren().addAll(startButton,stopButton,forceStopButton);
        this.getChildren().add(hBox);

        handlerSetup();
        setStatus(false);
    }

    private void handlerSetup(){
        startButton.setOnMouseClicked(mouseEvent->{
            startButton.setDisable(true);
            if(!getStatus()){
                PowerService powerService=new PowerService(name, ip, port, hash);
                powerService.start();
            }
        });

        stopButton.setOnMouseClicked(mouseEvent->{
            stopButton.setDisable(true);
            if(getStatus()){
                PowerService powerService=new PowerService(name, ip, port, hash);
                powerService.start();
            }
        });

        forceStopButton.setOnMouseClicked(mouseEvent->{
            forceStopButton.setDisable(true);
            ForcePowerOffService forcePowerOffService=new ForcePowerOffService(name, ip, port, hash);
            forcePowerOffService.start();
        });
    }


    public String getIp() {
        return ip;
    }

    public String getPort() {
        return port;
    }

    public String getHash() {
        return hash;
    }

    public Boolean getStatus() {
        if(status){
            deviceName.setTextFill(Color.GREEN);
            startButton.setDisable(true);
            stopButton.setDisable(false);
            forceStopButton.setDisable(false);
        }else{
            deviceName.setTextFill(Color.RED);
            startButton.setDisable(false);
            stopButton.setDisable(true);
            forceStopButton.setDisable(true);
        }
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
        if(status!=null){
            if(status){
                System.out.println("Online "+ip);
                deviceName.setTextFill(Color.GREEN);
                startButton.setDisable(true);
                stopButton.setDisable(false);
                forceStopButton.setDisable(false);
            }else{
                System.out.println("Offline "+ip);
                deviceName.setTextFill(Color.RED);
                startButton.setDisable(false);
                stopButton.setDisable(true);
                forceStopButton.setDisable(true);
            }
        }
        else{
            System.out.println("No device found for ip "+ip);
            deviceName.setTextFill(Color.BLACK);
            startButton.setDisable(true);
            stopButton.setDisable(true);
            forceStopButton.setDisable(true);
        }

    }

    public void updateStatus(){
        GetStatusService getStatusService=new GetStatusService(ip,port);
        getStatusService.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, workerStateEvent -> {
            setStatus(getStatusService.getValue());
        });
        getStatusService.start();
    }
}
