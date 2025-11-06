package ru.dbudyak.entangler;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ResourceBundle;

/**
 * Created by dbudyak on 30.04.14.
 */
public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException, CloneNotSupportedException {
        initLayout(primaryStage);
    }

    private void initLayout(Stage stage) throws IOException, CloneNotSupportedException {
        Group root = new Group();
        BorderPane qRoot = FXMLLoader.load(getClass().getClassLoader().getResource("qRoot.fxml"));
        root.getChildren().addAll(qRoot);

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("style.css").toString());
        stage.setTitle("Entangler");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();

        QCircuit gridPane = (QCircuit) scene.lookup("#circuit");

        CircuitWorker cw = new CircuitWorker();
        cw.setGridPane(gridPane);
        cw.initGrid();

        ElementsWorker ew = new ElementsWorker();
        ew.setGridPane((GridPane) scene.lookup("#qElementsGrid"), (GridPane) scene.lookup("#qWavesGrid"));
        ew.initGrid();

        Button startBtn = (Button) scene.lookup("#mainBtnStart");
        startBtn.setStyle("-fx-background-color: #009933;");
        startBtn.setOnAction(event -> cw.process());

        Button rotateBtn = (Button) scene.lookup("#mainBtnRotate");
        rotateBtn.setOnAction(event -> cw.rotate());

        Button graphBtn = (Button) scene.lookup("#mainBtnGraph");
        graphBtn.setOnAction(event -> GraphBuilder.INSTANCE.show());

        Button gridBtn = (Button) scene.lookup("#mainBtnGrid");
        gridBtn.setOnAction(event -> cw.showGrid());
    }


}
