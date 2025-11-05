package ru.dbudyak.entangler;

import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.AnchorPane;
import ru.dbudyak.entangler.models.base.BaseElement;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dbudyak on 27.05.2014.
 */
@SuppressWarnings("ALL")
public class PropertiesWorker {

    private OnPropertiesUpdate propertiesUpdate;
    private AnchorPane props;
    private ListView keys, values;

    private HashMap<String, String> data = new HashMap<>();

    public HashMap<String, String> getElementData() {
        return data;
    }


    public void setOnPropertiesListener(OnPropertiesUpdate onPropertiesListener) {
        this.propertiesUpdate = onPropertiesListener;
    }

    public void setPropertiesLayout(AnchorPane lookup) {
        this.props = lookup;
        Button bDelete = (Button) this.props.lookup("#propBtnDelete");
        Button bRotate = (Button) this.props.lookup("#propBtnRotate");
        Button bFlip = (Button) this.props.lookup("#propBtnFlip");
        bDelete.setOnAction(event -> propertiesUpdate.onDelete());
        bRotate.setOnAction(event -> propertiesUpdate.onRotate());
        bFlip.setOnAction(event -> propertiesUpdate.onFlip());
        keys = (ListView) this.props.lookup("#propKeysList");
        values = (ListView) this.props.lookup("#propValuesList");
        values.setEditable(true);
        values.setCellFactory(TextFieldListCell.forListView());
        values.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    public AnchorPane getPropertiesAnchor() {
        return this.props;
    }

    public void setResult(String s) {
        data.put("Result", s);
    }

    public void setTop(String name, String isConnected) {
        data.put(name, String.valueOf(isConnected));
    }

    public void setRight(String name, String isConnected) {
        data.put(name, String.valueOf(isConnected));
    }

    public void setBottom(String name, String isConnected) {
        data.put(name, String.valueOf(isConnected));
    }

    public void setLeft(String name, String isConnected) {
        data.put(name, String.valueOf(isConnected));
    }

    public void setDetectorCounts() {
        data.put("Clicks", "measured");
    }

    public void setPropertiesVisibility(boolean visibility) {
        this.props.setVisible(visibility);
    }

    public void setName(String name) {
        data.put("Name", name);
    }

    public void setSourceOutputParam(double value) {
        data.put("Mode", "cos(" + String.valueOf(value) + ")|0> + sin(" + String.valueOf(value) + ")|1>");
    }

    public void setBSTheta(double val) {
        data.put("\u03B8", String.valueOf(val));
    }

    public void setType(BaseElement.ElementType type) {
    }

    public void updateProperties() {
        for (Map.Entry<String, String> e : data.entrySet()) {
            String key = e.getKey();
            String value = e.getValue();
            if (keys.getItems().contains(key)) {
                values.getItems().set(keys.getItems().indexOf(key), value);
            } else {
                keys.getItems().add(key);
                values.getItems().add(value);
            }
        }
    }

    public interface OnPropertiesUpdate {
        public void onDelete();

        public void onFlip();

        public void onRotate();
    }


}
