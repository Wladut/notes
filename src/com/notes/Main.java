package com.notes;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.concurrent.atomic.AtomicBoolean;

public class Main extends Application {
    static Stage stage;
    static Parent root;
    private double x,y = 0;

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("Notes.fxml"));
        stage.getIcons().add(new Image("Notes.png"));
        stage.setTitle("Notes");
        stage.initStyle(StageStyle.UNDECORATED);
        Main.stage = stage;
        Main.root = root;
        AtomicBoolean isResizibleBottomRight = new AtomicBoolean(false);
        root.setOnMousePressed(mouseEvent -> {
            if(mouseEvent.getSceneX() > stage.getWidth() - 10 && mouseEvent.getSceneX() < stage.getWidth() + 10
            && mouseEvent.getSceneY() > stage.getHeight() - 10 && mouseEvent.getSceneY() < stage.getHeight() + 10){
                isResizibleBottomRight.set(true);
                x = stage.getWidth() - mouseEvent.getSceneX();
                y = stage.getHeight() - mouseEvent.getSceneY();
            } else {
                isResizibleBottomRight.set(false);
            x = mouseEvent.getSceneX();
            y = mouseEvent.getSceneY();}
        });

        root.setOnMouseDragged(mouseEvent -> {
            if(isResizibleBottomRight.get()){
                stage.setWidth(mouseEvent.getSceneX() + x);
                stage.setHeight(mouseEvent.getSceneY() + y);
            } else {
            stage.setX(mouseEvent.getScreenX() - x);
            stage.setY(mouseEvent.getScreenY() - y);}
        });

        stage.setScene(new Scene(root, 919, 698));
        stage.show();
    }

    public static void main(String[] args) {
        Notes.rootFolder = args[0];
        launch(args);
    }
}
