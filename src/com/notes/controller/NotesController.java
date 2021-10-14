package com.notes.controller;

import com.notes.Notes;
import com.notes.readfolders.ReadFolders;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Created by Vrinceanu Vladut 01-04-2021
 * Time 17:03
 */

public class NotesController implements Initializable {

    @FXML
    private Image Img;
    @FXML
    Text dev;
    @FXML
    ImageView imageContinental;
    @FXML
    ImageView radioButton;
    @FXML
    Text labelOvertime;
    @FXML
    BorderPane borderPane;
    @FXML
    TreeView<String> foldersTreeView;
    @FXML
    HBox hBox = new HBox();
    @FXML
    TextArea notepadText = new TextArea();
    @FXML
    ImageView tray = new ImageView();
    @FXML
    ImageView minimize = new ImageView();
    @FXML
    ImageView exit = new ImageView();
    @FXML
    ImageView refresh = new ImageView();
    @FXML
    ImageView notesIcon = new ImageView();

    private Glow glowEffect = new Glow();
    private DropShadow dropShadowEffect = new DropShadow();
    private String lastPathTxt;
    private String newFilePath;

    /**
     * Paths for files
     */

    public static String rootFolder;
    private final String configFileName = "config.ini";
    private final String deleteStyleSheetWindow = "/dialog.css";
    private final String continentalOrangeJpg = "noteorange.PNG";
    private final String folderTreeViewDarkCss = "style.css";
    private final String continentalBlackJpg = "noteblack.JPG";

    private double xx, yy = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ReadFolders.getStructure(this.rootFolder, ReadFolders.root);
        foldersTreeView.setRoot(ReadFolders.root);
        try {
            if (readConfig().equals("night")) {
                nightMode();
            }
        } catch (IOException e) {
            System.out.println("Night mode for main window was not applied because of: " + e.getMessage());
        }

        Platform.runLater(() -> notepadText.getScene().getAccelerators().put(new KeyCodeCombination(
                KeyCode.S, KeyCombination.CONTROL_ANY), () -> {
            try {
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(lastPathTxt));
                bufferedWriter.write(notepadText.getText());
                bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
    }

    @FXML
    public void selectedTxtFile() {
        StringBuilder str = new StringBuilder();
        buildPath(foldersTreeView.getSelectionModel().getSelectedItem(), str);
        this.lastPathTxt = rootFolder + str.toString();
        newFilePath = this.lastPathTxt;
        //System.out.println(rootFolder + str.toString());

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(this.lastPathTxt));
            notepadText.clear();
            String line = bufferedReader.readLine();
            StringBuilder stringBuilder = new StringBuilder();
            while (line != null) {
                stringBuilder.append(line);
                stringBuilder.append("\n");
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
            notepadText.setText(stringBuilder.toString());
        } catch (IOException e) {
            notepadText.clear();
        }
    }

    @FXML
    public void nightMode() throws IOException {
        if (borderPane.styleProperty().toString().contains("F5F1F0")) {
            Img = new Image(this.continentalOrangeJpg);
            imageContinental.setImage(Img);
            imageContinental.setFitWidth(300);
            imageContinental.setFitHeight(85);
            imageContinental.blendModeProperty().setValue(BlendMode.SRC_OVER);
            borderPane.setStyle("-fx-background-color: #19181F; -fx-border-color: #AEAEAE");
            Platform.runLater(() -> notepadText.lookup(".content").setStyle("-fx-background-color: #23222E"));
            notepadText.setStyle("-fx-text-fill: white");
            labelOvertime.setStyle("-fx-fill: orange");
            dev.setStyle("-fx-fill: orange");
            radioButton.setStyle("-fx-text-fill: orange");
            foldersTreeView.getStylesheets().add(this.folderTreeViewDarkCss);
            writeConfig("night");


        } else {
            borderPane.setStyle("-fx-background-color: #F5F1F0; -fx-border-color: black");
            notepadText.lookup(".content").setStyle("-fx-background-color: white");
            notepadText.setStyle("-fx-text-fill: black");
            dev.setStyle("-fx-fill: black");
            notepadText.setStyle("-fx-text-fill: black");
            labelOvertime.setStyle("-fx-fill: black");
            radioButton.setStyle("-fx-text-fill: black");
            foldersTreeView.getStylesheets().remove(0);
            Img = new Image(this.continentalBlackJpg);
            imageContinental.setImage(Img);
            imageContinental.setFitWidth(426);
            imageContinental.setFitHeight(64);
            imageContinental.blendModeProperty().setValue(BlendMode.DARKEN);
            writeConfig("day");

        }
    }

    @FXML
    public void exit() {
        Platform.exit();
    }

    @FXML
    void resize() {
        if (Notes.stage.isMaximized()) {
            Notes.stage.setMaximized(false);
        } else {
            Notes.stage.setMaximized(true);
        }
        Notes.root.requestFocus();
    }

    @FXML
    void tray() {
        if (Notes.stage.isIconified()) {
            Notes.stage.setIconified(false);
        } else {
            Notes.stage.setIconified(true);
        }
        Notes.root.requestFocus();
    }

    private void writeConfig(String config) throws IOException {
        File configFile = new File(this.configFileName);
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(configFile));
        bufferedWriter.write(config);
        bufferedWriter.close();
    }

    private String readConfig() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader("config.ini"));
        String mode = bufferedReader.readLine();
        bufferedReader.close();
        return mode;
    }

    public void contextMenu() {
        if (!lastPathTxt.contains(".txt")) {
            Stage newStage = new Stage();
            newStage.setTitle("Create new note");
            newStage.initModality(Modality.WINDOW_MODAL);
            newStage.initOwner(Notes.stage);
            GridPane stackPane = new GridPane();
            Scene secondScene = new Scene(stackPane, 300, 150);
            newStage.setScene(secondScene);

            TextField pathFiled = new TextField();
            pathFiled.setText(newFilePath);
            pathFiled.setMinSize(100, 30);
            stackPane.setAlignment(Pos.CENTER);


            stackPane.setVgap(6);
            stackPane.add(pathFiled, 0, 0);

            Text text = new Text();
            text.setText("Choose file name:");


            Button create = new Button("Create");
            Button cancel = new Button("Cancel");
            Button deleteFolder = new Button("Delete selcted folder");

            try {
                if (readConfig().equals("night")) {
                    stackPane.setStyle("-fx-background-color: #19181F; -fx-border-color: black");
                    text.setStyle("-fx-fill: white");
                    create.setStyle("-fx-background-color: black; -fx-border-color: gray; -fx-text-fill: white");
                    cancel.setStyle("-fx-background-color: black; -fx-border-color: gray; -fx-text-fill: white");
                    deleteFolder.setStyle("-fx-background-color: black; -fx-border-color: gray; -fx-text-fill: white");
                } else {
                    stackPane.setStyle("-fx-background-color: #F5F1F0; -fx-border-color: black");
                    text.setStyle("-fx-fill: black");
                    create.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-text-fill: black");
                    cancel.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-text-fill: black");
                    deleteFolder.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-text-fill: black");
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }


            stackPane.add(text, 0, 1);

            TextField textField = new TextField();
            stackPane.add(textField, 0, 2);
            stackPane.add(create, 3, 3);
            stackPane.add(cancel, 0, 3);
            stackPane.add(deleteFolder, 3, 0);


            newStage.initStyle(StageStyle.UNDECORATED);

            stackPane.setOnMousePressed(mouseEvent -> {
                xx = mouseEvent.getSceneX();
                yy = mouseEvent.getSceneY();
            });

            stackPane.setOnMouseDragged(mouseEvent -> {
                newStage.setX(mouseEvent.getScreenX() - xx);
                newStage.setY(mouseEvent.getScreenY() - yy);
            });

            newStage.show();
            create.setOnAction(e -> {
                File newFile = new File(pathFiled.getText() + "\\" + textField.getText() + ".txt");
                try {
                    System.out.println(newFile.createNewFile());
                } catch (IOException ex) {
                    File folderPath = new File(pathFiled.getText() + "\\");
                    try {
                        FileUtils.forceMkdir(folderPath);
                    } catch (IOException exc) {
                        System.out.println("Folder was not created because: " + exc.getMessage());
                    }
                    try {
                        System.out.println(newFile.createNewFile());
                    } catch (IOException exc) {
                        System.out.println("File was not created because: " + exc);
                    }
                }
                newStage.close();
                refreshTree();
            });
            cancel.setOnAction(e -> newStage.close());

            deleteFolder.setOnAction(e -> {
                File deletePath = new File(pathFiled.getText() + "\\");
                try {
                    FileUtils.deleteDirectory(deletePath);
                } catch (Exception ex) {
                    System.out.println("Folder was not deleted because of: " + ex);
                }
                newStage.close();
                refreshTree();
            });
        } else {
            Alert alert =
                    new Alert(Alert.AlertType.WARNING,
                            "Do you want to delete selected item?",
                            ButtonType.OK,
                            ButtonType.CANCEL);
            alert.setTitle("Delete note");

            DialogPane dialogPane = alert.getDialogPane();

            try {
                if (readConfig().equals("night")) {
                    dialogPane.getStylesheets().add(
                            getClass().getResource(this.deleteStyleSheetWindow).toExternalForm());

                    dialogPane.getStyleClass().add("myDialog");
                }
            } catch (IOException|NullPointerException e) {
                System.out.println("Delete dialog css night style was not changed because: " + e.getMessage());
            }

            alert.initStyle(StageStyle.UNDECORATED);

            dialogPane.setOnMousePressed(mouseEvent -> {
                xx = mouseEvent.getSceneX();
                yy = mouseEvent.getSceneY();
            });

            dialogPane.setOnMouseDragged(mouseEvent -> {
                alert.setX(mouseEvent.getScreenX() - xx);
                alert.setY(mouseEvent.getScreenY() - yy);
            });

            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                File file = new File(lastPathTxt);
                System.out.println(file.delete());
                notepadText.clear();
                refreshTree();
            }
        }
    }

    public void refreshTree() {
        ReadFolders.clear();
        ReadFolders.getStructure(this.rootFolder, ReadFolders.root);
        foldersTreeView.setRoot(ReadFolders.root);
    }

    private void buildPath(TreeItem item, StringBuilder builder) {
        try {
            if (item.getParent() != null) {

                if (item.getParent().getValue() != "Root") {
                    buildPath(item.getParent(), builder);  //build path

                    builder.append("\\");
                }
            }
            builder.append(item.getValue());
        } catch (NullPointerException e) {
            //System.out.println("Null pointer exception buildPath.");
        }
    }

    public void setEffect() {
        setOrDisableGlowOrDropShadowEffects(labelOvertime, glowEffect);
        setOrDisableGlowOrDropShadowEffects(dev, glowEffect);
        setOrDisableGlowOrDropShadowEffects(notesIcon, glowEffect);
        setOrDisableGlowOrDropShadowEffects(imageContinental, dropShadowEffect);
        setOrDisableGlowOrDropShadowEffects(radioButton, dropShadowEffect);
        setOrDisableGlowOrDropShadowEffects(tray, dropShadowEffect);
        setOrDisableGlowOrDropShadowEffects(minimize, dropShadowEffect);
        setOrDisableGlowOrDropShadowEffects(exit, dropShadowEffect);
        setOrDisableGlowOrDropShadowEffects(notepadText, dropShadowEffect);
        setOrDisableGlowOrDropShadowEffects(foldersTreeView, dropShadowEffect);
        setOrDisableGlowOrDropShadowEffects(refresh, dropShadowEffect);
    }

    private void setOrDisableGlowOrDropShadowEffects(Object objectType, Object effectType) {
        dropShadowEffect.setColor(Color.color(0.4, 0.5, 0.5));
        ((Node) objectType).setOnMouseEntered(mouseEvent -> ((Node) objectType)
                .setEffect(effectType instanceof Glow ? (Glow) effectType : (DropShadow) effectType));
        ((Node) objectType).setOnMouseExited(mouseEvent -> ((Node) objectType)
                .setEffect(null));

    }
}
