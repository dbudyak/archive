package ru.dbudyak.entangler;

import javafx.beans.binding.Bindings;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.CacheHint;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.ColorInput;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import ru.dbudyak.entangler.models.base.BaseElement;
import ru.dbudyak.entangler.math.KroneckerOperation;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

import static java.lang.Math.*;
import static ru.dbudyak.entangler.Utils.print;

/**
 * Created by dbudyak on 07.05.14.
 */
public class QElement extends ImageView implements Initializable, PropertiesWorker.OnPropertiesUpdate, Cloneable {

    public boolean marked;
    private QElement qleft;
    private QElement qtop;
    private QElement qright;
    private QElement qbottom;
    private String tag = null;
    private Side sleft;
    private Side stop;
    private Side sright;
    private Side sbottom;
    private BaseElement base;
    private boolean isComputed = false;
    private PropertiesWorker pw;


    public QElement() {
        super();
    }

    @Override
    public void onDelete() {
        // Remove from graph before clearing base
        if (getBase() != null) {
            GraphBuilder.getInstance().getGraph().removeVertex(QElement.this);
        }
        setOnHover();
        addEventHandler();
        setTag(null);
        setSleft(null);
        setStop(null);
        setSright(null);
        setSbottom(null);
        setBase(null);

    }

    private boolean isConnected() {
        if (getBase().getElementType() == BaseElement.ElementType.BS) {
            return (getSideLeft().isConnected() && getSideTop().isConnected()) || (getSideTop().isConnected() && getSideRight().isConnected()) || (getSideRight().isConnected() && getSideBbottom().isConnected()) || (getSideBbottom().isConnected() && getSideLeft().isConnected());
        } else {
            return getSideLeft().isConnected() || getSideTop().isConnected() || getSideRight().isConnected() || getSideBbottom().isConnected();
        }
    }

    @Override
    public void onFlip() {

        ArrayList<Side> sides = new ArrayList<>(Arrays.asList(getSideLeft(), getSideTop(), getSideRight(), getSideBbottom()));

        for (Side side : sides) {
            if (side.getDirection() == Side.Direction.INPUT) {
                side.setDirection(Side.Direction.OUTPUT);
            } else if (side.getDirection() == Side.Direction.OUTPUT) {
                side.setDirection(Side.Direction.INPUT);
            }
        }

        if (getSideTop().isConnected()) {
            getSideTop().setConnected(false);
            getElementTop().getSideBbottom().setConnected(false);
        }
        if (getSideLeft().isConnected()) {
            getSideLeft().setConnected(false);
            getElementLeft().getSideRight().setConnected(false);
        }
        if (getSideBbottom().isConnected()) {
            getSideBbottom().setConnected(false);
            getElementBottom().getSideTop().setConnected(false);
        }
        if (getSideRight().isConnected()) {
            getSideRight().setConnected(false);
            getElementRight().getSideLeft().setConnected(false);
        }

        setIO();

    }

    @Override
    public void onRotate() {
        if (getBase() != null) {
            getTransforms().add(new Rotate(45, 35, 35));
            getTransforms().add(new Rotate(45, 35, 35));
            if (getSideTop().isConnected()) {
                getSideTop().setConnected(false);
                getElementTop().getSideBbottom().setConnected(false);
            }
            if (getSideLeft().isConnected()) {
                getSideLeft().setConnected(false);
                getElementLeft().getSideRight().setConnected(false);
            }
            if (getSideBbottom().isConnected()) {
                getSideBbottom().setConnected(false);
                getElementBottom().getSideTop().setConnected(false);
            }
            if (getSideRight().isConnected()) {
                getSideRight().setConnected(false);
                getElementRight().getSideLeft().setConnected(false);
            }
            Side nstop = new Side();
            Side nsright = new Side();
            Side nsbottom = new Side();
            Side nsleft = new Side();

            nstop = getSideLeft();
            nsright = getSideTop();
            nsbottom = getSideRight();
            nsleft = getSideBbottom();

            setSleft(nsleft);
            setStop(nstop);
            setSright(nsright);
            setSbottom(nsbottom);


            setIO();

        }
    }

    Side getSideBbottom() {
        return sbottom;
    }

    void setSbottom(Side sbottom) {
        this.sbottom = sbottom;
    }

    Side getSideTop() {
        return stop;
    }

    void setStop(Side stop) {
        this.stop = stop;
    }

    QElement getElementLeft() {
        return qleft;
    }

    public void setQleft(QElement qleft) {
        this.qleft = qleft;
    }

    QElement getElementTop() {
        return qtop;
    }

    public void setQtop(QElement qtop) {
        this.qtop = qtop;
    }

    QElement getElementRight() {
        return qright;
    }

    public void setQright(QElement qright) {
        this.qright = qright;
    }

    QElement getElementBottom() {
        return qbottom;
    }

    public void setQbottom(QElement qbottom) {
        this.qbottom = qbottom;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    Side getSideLeft() {
        return sleft;
    }

    void setSleft(Side sleft) {
        this.sleft = sleft;
    }

    Side getSideRight() {
        return sright;
    }

    void setSright(Side sright) {
        this.sright = sright;
    }

    PropertiesWorker getPropertiesWorker() {
        return pw;
    }

    void setPw(PropertiesWorker pw) {
        this.pw = pw;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    public void addEventHandler() {
        addEventHandler(DragEvent.ANY, new QEventHandler());
        setOnMouseClicked(mouseEvent -> {
            if (getBase() != null) {
                getPropertiesWorker().setPropertiesVisibility(true);
                if (getBase() != null) {
                    getPropertiesWorker().updateProperties();
                }
            }
        });
    }


    private void setIO() {
        if (getElementLeft() != null && getElementLeft().getBase() != null) {
            if (getSideLeft().getDirection() == Side.Direction.INPUT) {
                if (getElementLeft().getSideRight().getDirection() == Side.Direction.OUTPUT) {
                    getElementLeft().getSideRight().setConnected(true);
                    getSideLeft().setConnected(true);
                }
            } else if (getSideLeft().getDirection() == Side.Direction.OUTPUT) {
                if (getElementLeft().getSideRight().getDirection() == Side.Direction.INPUT) {
                    getElementLeft().getSideRight().setConnected(true);
                    getSideLeft().setConnected(true);
                }
            }
            if (getElementLeft().getSideRight().isConnected() && getSideLeft().isConnected()) {
                GraphBuilder.getInstance().addEdge(getElementLeft(), QElement.this);
            } else {
                GraphBuilder.getInstance().getGraph().removeEdge(getElementLeft(), QElement.this);
            }
        }

        if (getElementTop() != null && getElementTop().getBase() != null) {
            if (getSideTop().getDirection() == Side.Direction.INPUT) {
                if (getElementTop().getSideBbottom().getDirection() == Side.Direction.OUTPUT) {
                    getElementTop().getSideBbottom().setConnected(true);
                    getSideTop().setConnected(true);
                }
            } else if (getSideTop().getDirection() == Side.Direction.OUTPUT) {
                if (getElementTop().getSideBbottom().getDirection() == Side.Direction.INPUT) {
                    getElementTop().getSideBbottom().setConnected(true);
                    getSideTop().setConnected(true);
                }
            }
            if (getElementTop().getSideBbottom().isConnected() && getSideTop().isConnected()) {
                GraphBuilder.getInstance().addEdge(getElementTop(), QElement.this);
            } else {
                GraphBuilder.getInstance().getGraph().removeEdge(getElementTop(), QElement.this);
            }
        }

        if (getElementRight() != null && getElementRight().getBase() != null) {
            if (getSideRight().getDirection() == Side.Direction.INPUT) {
                if (getElementRight().getSideLeft().getDirection() == Side.Direction.OUTPUT) {
                    getElementRight().getSideLeft().setConnected(true);
                    getSideRight().setConnected(true);
                }
            } else if (getSideRight().getDirection() == Side.Direction.OUTPUT) {
                if (getElementRight().getSideLeft().getDirection() == Side.Direction.INPUT) {
                    getElementRight().getSideLeft().setConnected(true);
                    getSideRight().setConnected(true);
                }
            }
            if (getElementRight().getSideLeft().isConnected() && getSideRight().isConnected()) {
                // For BS, right side is channel1 output
                if (getBase().getElementType() == BaseElement.ElementType.BS) {
                    GraphBuilder.getInstance().addEdge(QElement.this, getElementRight(), 1);
                } else {
                    GraphBuilder.getInstance().addEdge(getElementRight(), QElement.this);
                }
            } else {
                GraphBuilder.getInstance().getGraph().removeEdge(getElementRight(), QElement.this);
            }
        }

        if (getElementBottom() != null && getElementBottom().getBase() != null) {
            if (getSideBbottom().getDirection() == Side.Direction.INPUT) {
                if (getElementBottom().getSideTop().getDirection() == Side.Direction.OUTPUT) {
                    getElementBottom().getSideTop().setConnected(true);
                    getSideBbottom().setConnected(true);
                }
            } else if (getSideBbottom().getDirection() == Side.Direction.OUTPUT) {
                if (getElementBottom().getSideTop().getDirection() == Side.Direction.INPUT) {
                    getElementBottom().getSideTop().setConnected(true);
                    getSideBbottom().setConnected(true);
                }
            }
            if (getElementBottom().getSideTop().isConnected() && getSideBbottom().isConnected()) {
                // For BS, bottom side is channel2 output
                if (getBase().getElementType() == BaseElement.ElementType.BS) {
                    GraphBuilder.getInstance().addEdge(QElement.this, getElementBottom(), 2);
                } else {
                    GraphBuilder.getInstance().addEdge(getElementBottom(), QElement.this);
                }
            } else {
                GraphBuilder.getInstance().getGraph().removeEdge(getElementBottom(), QElement.this);
            }
        }
        setOnHover();
    }

    private BaseElement.ElementType getElementType(String id) {
        switch (id) {
            case "elBS":
                return BaseElement.ElementType.BS;
            case "elMirror":
                return BaseElement.ElementType.MIRROR;
            case "elDetector":
                return BaseElement.ElementType.DETECTOR;
            case "elSource":
                return BaseElement.ElementType.SOURCE;
            default:
                if (id.startsWith("w")) return BaseElement.ElementType.WAVEGUIDE;
        }
        return null;
    }

    public void setOnHover() {
        ColorAdjust monochrome = new ColorAdjust();
        monochrome.setSaturation(-1.0);

        Blend hoverAcceptable = new Blend(
                BlendMode.MULTIPLY,
                monochrome,
                new ColorInput(
                        0,
                        0,
                        getImage().getWidth(),
                        getImage().getHeight(),
                        Color.LIGHTYELLOW
                )
        );

        Blend hoverDenied = new Blend(
                BlendMode.MULTIPLY,
                monochrome,
                new ColorInput(
                        0,
                        0,
                        getImage().getWidth(),
                        getImage().getHeight(),
                        Color.RED
                )
        );

        Blend connected = new Blend(
                BlendMode.MULTIPLY,
                monochrome,
                new ColorInput(
                        0,
                        0,
                        getImage().getWidth(),
                        getImage().getHeight(),
                        new Color(1.0f, 1.0f, 0.8784314f, 0.7)
                )
        );

        Blend notconnected = new Blend(
                BlendMode.SOFT_LIGHT,
                monochrome,
                new ColorInput(
                        0,
                        0,
                        getImage().getWidth(),
                        getImage().getHeight(),
                        new Color(0.827451f, 0.827451f, 0.827451f, 1.0)
                )
        );

        Blend empty = new Blend(
                BlendMode.MULTIPLY,
                monochrome,
                new ColorInput(
                        0,
                        0,
                        getImage().getWidth(),
                        getImage().getHeight(),
                        Color.WHITE
                )
        );

        Light.Spot light = new Light.Spot();
        light.setX(35);
        light.setY(35);
        light.setZ(50);
        light.setPointsAtX(0);
        light.setPointsAtY(0);
        light.setPointsAtZ(-10);
        light.setSpecularExponent(1);

        Lighting lighting = new Lighting();
        lighting.setLight(light);
        lighting.setSurfaceScale(1.0);

        effectProperty().bind(
                Bindings
                        .when(hoverProperty())
                        .then(hoverAcceptable)
                        .otherwise((getBase() != null && getBase().getElementType() != null) ? (isConnected() ? connected : notconnected) : empty)
        );

        setCache(true);
        setCacheHint(CacheHint.SPEED);
    }

    public BaseElement getBase() {
        return this.base;
    }

    void setBase(BaseElement base) {
        this.base = base;
    }

    @Override
    public String toString() {
        return getBase().getElementType().name() + getTag().replaceAll(" ", "");
    }


    public void compute() {
        print("Compute for " + getElementType().name());
        Data inData = getBase().getIn();
        Data outData = getBase().getOut();

        switch (getBase().getElementType()) {
            case MIRROR:
                getBase().setOut(inData);
                break;
            case BS:
                Data in = getBase().getIn();
                RealMatrix in1 = in.getChannel1();
                RealMatrix in2 = in.getChannel2();

                print("BS input channel 1:");
                Utils.printData(in1.getData());
                print("BS input channel 2:");
                Utils.printData(in2.getData());

                double theta = Double.parseDouble(getPropertiesWorker().getElementData().get("\u03B8".toString()));

                // Beam splitter transformation for each basis state
                // Inputs: two normalized states |in1⟩ and |in2⟩ (each with norm 1)
                // Outputs: |out1⟩ = (cos(θ)|in1⟩ + sin(θ)|in2⟩)/√2
                //          |out2⟩ = (-sin(θ)|in1⟩ + cos(θ)|in2⟩)/√2
                // The 1/√2 normalization ensures each output has norm 1
                double cosTheta = cos(theta);
                double sinTheta = sin(theta);
                double normFactor = 1.0 / sqrt(2.0);

                print("BS transform: cos(θ)=" + cosTheta + ", sin(θ)=" + sinTheta + ", norm=1/√2=" + normFactor);

                // Apply transformation to each amplitude component
                double[][] out1Data = new double[in1.getRowDimension()][1];
                double[][] out2Data = new double[in2.getRowDimension()][1];

                for (int i = 0; i < in1.getRowDimension(); i++) {
                    double a = in1.getEntry(i, 0);
                    double b = in2.getEntry(i, 0);

                    out1Data[i][0] = normFactor * (cosTheta * a + sinTheta * b);
                    out2Data[i][0] = normFactor * (-sinTheta * a + cosTheta * b);
                }

                print("BS output channel 1:");
                Utils.printData(out1Data);
                print("BS output channel 2:");
                Utils.printData(out2Data);

                RealMatrix res1 = new Array2DRowRealMatrix(out1Data);
                RealMatrix res2 = new Array2DRowRealMatrix(out2Data);

                Data bsOut = new Data.DataBuilder().channel1(res1).channel2(res2).build();
                getBase().setOut(bsOut);

                isComputed = true;
                break;
            case SOURCE:
                break;
            case DETECTOR:
                // Calculate detection probability
                if (inData != null && !inData.isEmptyChannel1()) {
                    RealMatrix detectorInput = inData.getChannel1();
                    // Calculate probability as |amplitude|^2
                    double probability = 0.0;
                    for (int i = 0; i < detectorInput.getRowDimension(); i++) {
                        for (int j = 0; j < detectorInput.getColumnDimension(); j++) {
                            double amplitude = detectorInput.getEntry(i, j);
                            probability += amplitude * amplitude;
                        }
                    }
                    print("DETECTOR", "Detection probability: " + probability);
                    getPropertiesWorker().setResult(String.format("%.4f", probability));
                    getPropertiesWorker().setDetectorCounts();
                } else {
                    print("DETECTOR", "No input data");
                    getPropertiesWorker().setResult("0.0000");
                }
                break;
            case PHASE_SHIFTER:
                break;
            case WAVEGUIDE:
                getBase().setOut(inData);
                break;
        }
    }

    public boolean isComputed() {
        return isComputed;
    }

    public boolean isBS() {
        return getBase().getElementType() == BaseElement.ElementType.BS;
    }

    public boolean isSource() {
        return getBase().getElementType() == BaseElement.ElementType.SOURCE;
    }

    public boolean isDetector() {
        return getBase().getElementType() == BaseElement.ElementType.DETECTOR;
    }

    public boolean isWaveguide() {
        return getBase().getElementType() == BaseElement.ElementType.WAVEGUIDE;
    }

    public boolean isMirror() {
        return getBase().getElementType() == BaseElement.ElementType.MIRROR;
    }

    public BaseElement.ElementType getElementType() {
        return getBase().getElementType();
    }

    public Data getOut() {
        return getBase().getOut();
    }

    public void setOut(Data out) {
        base.setOut(out);
    }

    public Data getIn() {
        return getBase().getIn();
    }

    public void setIn(Data in) {
        base.setIn(in);
    }

    public QElement clone() throws CloneNotSupportedException {
        return (QElement) super.clone();
    }

    private void initializeSideDirections(BaseElement.ElementType elementType) {
        switch (elementType) {
            case SOURCE:
                // Source outputs light
                getSideLeft().setDirection(Side.Direction.OUTPUT);
                getSideTop().setDirection(Side.Direction.OUTPUT);
                getSideRight().setDirection(Side.Direction.OUTPUT);
                getSideBbottom().setDirection(Side.Direction.OUTPUT);
                break;
            case DETECTOR:
                // Detector receives light
                getSideLeft().setDirection(Side.Direction.INPUT);
                getSideTop().setDirection(Side.Direction.INPUT);
                getSideRight().setDirection(Side.Direction.INPUT);
                getSideBbottom().setDirection(Side.Direction.INPUT);
                break;
            case MIRROR:
                // Mirror can input from one side and output from another
                getSideLeft().setDirection(Side.Direction.INPUT);
                getSideTop().setDirection(Side.Direction.INPUT);
                getSideRight().setDirection(Side.Direction.OUTPUT);
                getSideBbottom().setDirection(Side.Direction.OUTPUT);
                break;
            case BS:
                // Beam splitter has two inputs and two outputs
                getSideLeft().setDirection(Side.Direction.INPUT);
                getSideTop().setDirection(Side.Direction.INPUT);
                getSideRight().setDirection(Side.Direction.OUTPUT);
                getSideBbottom().setDirection(Side.Direction.OUTPUT);
                // Initialize default theta value for beam splitter
                getPropertiesWorker().setBSTheta(Math.PI / 4); // 45 degrees
                break;
            case WAVEGUIDE:
                // Waveguide passes light through
                getSideLeft().setDirection(Side.Direction.INPUT);
                getSideTop().setDirection(Side.Direction.INPUT);
                getSideRight().setDirection(Side.Direction.OUTPUT);
                getSideBbottom().setDirection(Side.Direction.OUTPUT);
                break;
            case PHASE_SHIFTER:
                getSideLeft().setDirection(Side.Direction.INPUT);
                getSideTop().setDirection(Side.Direction.INPUT);
                getSideRight().setDirection(Side.Direction.OUTPUT);
                getSideBbottom().setDirection(Side.Direction.OUTPUT);
                break;
        }
    }


    private class QEventHandler implements EventHandler<DragEvent> {
        @Override
        public void handle(DragEvent event) {
            String eventName = event.getEventType().toString();
            if (eventName.equals(DragEvent.DRAG_ENTERED.getName())) {
                if (event.getGestureSource() != this &&
                        event.getDragboard().hasString()) {
                }
                event.consume();
            }
            if (eventName.equals(DragEvent.DRAG_OVER.getName())) {
                if (event.getGestureSource() != this && event.getDragboard().hasString()) {
                    event.acceptTransferModes(TransferMode.ANY);
                }
                event.consume();
            }
            if (eventName.equals(DragEvent.DRAG_EXITED.getName())) {
                event.consume();
            }
            if (eventName.equals(DragEvent.DRAG_DROPPED.getName())) {
                setSleft(new Side());
                setStop(new Side());
                setSright(new Side());
                setSbottom(new Side());
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasString()) {
                    String id = db.getString();
                    setImage(new Utils().getImageByElId(id));
                    BaseElement.ElementType elementType = getElementType(id);
                    setBase(new BaseElement(elementType));
                    setPw(new PropertiesWorker());
                    getPropertiesWorker().setPropertiesLayout((AnchorPane) getScene().lookup("#props"));
                    getPropertiesWorker().setType(getBase().getElementType());
                    getPropertiesWorker().setOnPropertiesListener(QElement.this);
                    getPropertiesWorker().setName(getBase().getElementType().name());

                    // Initialize sides based on element type
                    initializeSideDirections(elementType);

                    // Add element to graph
                    GraphBuilder.getInstance().getGraph().addVertex(QElement.this);

                    success = true;
                    setIO();
                }
                event.setDropCompleted(success);
                event.consume();
            }
            if (eventName.equals(DragEvent.DRAG_DONE.getName())) {

            }
        }
    }
}
