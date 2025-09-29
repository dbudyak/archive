package ru.dbudyak.entangler;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;

/**
 * Created by dbudyak on 12.05.14.
 */
class ElementsWorker {

    private GridPane[] grids;
    private double initialX;
    private double initialY;


    public void setGridPane(GridPane... gridPanes) {
        this.grids = gridPanes;
    }

    public void initGrid() {
        for (GridPane grid : grids) {
            grid.setAlignment(Pos.CENTER);
            for (Node n : grid.getChildren()) {
                QElement qElement = (QElement) n;
                qElement.getStyleClass().add("qelement");
                Image image = new Utils().getImageByElId(qElement.getId());
                qElement.setImage(image);
                qElement.setOnDragDetected(event -> {
                    Dragboard db = qElement.startDragAndDrop(TransferMode.COPY);
                    ClipboardContent content = new ClipboardContent();
                    content.putImage(qElement.getImage());
                    content.putString(qElement.getId());
                    db.setContent(content);
                    event.consume();
                });
                qElement.setOnDragDone(event -> {
                    if (event.getTransferMode() == TransferMode.COPY) {
                        event.consume();
                    }
                });
                addDraggableNode(qElement);
            }
        }
    }

    private void addDraggableNode(final Node node) {

        node.setOnMousePressed(me -> {
            if (me.getButton() != MouseButton.MIDDLE) {
                initialX = me.getSceneX();
                initialY = me.getSceneY();
            }
        });

        node.setOnMouseDragged(me -> {
            if (me.getButton() != MouseButton.MIDDLE) {
                node.getScene().getWindow().setX(me.getScreenX() - initialX);
                node.getScene().getWindow().setY(me.getScreenY() - initialY);
            }
        });
    }

}
