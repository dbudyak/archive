package ru.dbudyak.entangler;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.GridPane;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static ru.dbudyak.entangler.Utils.print;

/**
 * Created by dbudyak on 12.05.14.
 */
class CircuitWorker {

    private static final int elements = 20;
    private GridPane gridPane;
    private final QElement[][] all = new QElement[30][30];
    private boolean isRotated;
    private boolean isGridShowing;

    public void setGridPane(GridPane pane) {
        this.gridPane = pane;
    }

    public void initGrid() throws IOException, CloneNotSupportedException {
        initElements();
        for (int i = 0; i < elements; i++) {
            for (int j = 0; j < elements; j++) {
                QElement qElement = all[i][j];
                if (i != 0) {
                    all[i - 1][j].setQright(qElement);
                    qElement.setQleft(all[i - 1][j]);
                }
                if (j != 0) {
                    all[i][j - 1].setQbottom(qElement);
                    qElement.setQtop(all[i][j - 1]);
                }
                if (i != elements - 1) {
                    qElement.setQright(all[i + 1][j]);
                }
                if (j != elements - 1) {
                    qElement.setQbottom(all[i][j + 1]);
                }
                qElement.setOnHover();
                qElement.addEventHandler();

                qElement.setTag(i + " " + j);
                gridPane.add(qElement, i, j);
            }
        }
    }

    void initElements() throws IOException, CloneNotSupportedException {
        for (int i = 0; i < elements; i++) {
            for (int j = 0; j < elements; j++) {
                all[i][j] = FXMLLoader.load(getClass().getClassLoader().getResource("qEl.fxml"));
            }
        }
    }

    public void rotate() {
        if (isRotated) {
            gridPane.getTransforms().add(new Rotate(45, elements * 70 / 2, elements * 70 / 2));
            gridPane.getTransforms().add(new Translate(-300, 0));
            isRotated = false;
        } else {
            gridPane.getTransforms().add(new Translate(300, 0));
            gridPane.getTransforms().add(new Rotate(-45, elements * 70 / 2, elements * 70 / 2));
            isRotated = true;
        }

    }

    public void showGrid() {
        isGridShowing = !isGridShowing;
        gridPane.setGridLinesVisible(!isGridShowing);
    }

    public void process() {
        Thread process = new Thread(new CircuitWorkerProcessor());
        process.start();
    }

    private class CircuitWorkerProcessor implements Runnable {

        final ArrayList<QElement> startedSources;
        final DefaultDirectedWeightedGraph graph;
        final ArrayList<QElement> vertexes;
        final ArrayList<QElement> detectors;
        final Set edges;

        private CircuitWorkerProcessor() {
            graph = GraphBuilder.getInstance().getGraph();
            Set<QElement> set = graph.vertexSet();
            vertexes = new ArrayList<>();
            detectors = new ArrayList<>();
            for (QElement q : set) {
                vertexes.add(q);
                if (q.isDetector()) {
                    detectors.add(q);
                }
            }
            edges = graph.edgeSet();
            startedSources = new ArrayList<>();
            generateSources();
        }

        public void generateSources() {
            Random rnd = new Random();
            vertexes.stream().filter(QElement::isSource).forEach(qel -> {
                double theta = rnd.nextDouble();
                double[][] light1 = {{cos(theta)}, {sin(theta)}};
                RealMatrix light1rl = new Array2DRowRealMatrix(light1);
                qel.setOut(new Data.DataBuilder().channel1(light1rl).channel2(null).build());
                startedSources.add(qel);
            });
        }

        private void detectors(ArrayList<QElement> endedSources) {
            for (QElement source : endedSources) {
                detectors.stream().filter(detector -> graph.containsEdge(source, detector)).forEach(detector -> {
                    // Get edge weight to determine which channel to use
                    Object edge = graph.getEdge(source, detector);
                    double edgeWeight = graph.getEdgeWeight(edge);
                    int channel = (int) Math.round(edgeWeight);

                    print("DETECTOR", "checking data for " + source.getElementType().name() + " -> " + detector.getElementType().name() + " : " + detector.getTag() + " (channel " + channel + ")");
                    if (source.getOut() != null) {
                        if (source.isBS()) {
                            // Use edge weight to determine which channel
                            if (channel == 1) {
                                if (!source.getOut().isEmptyChannel1()) {
                                    detector.setIn(new Data.DataBuilder().channel1(source.getOut().getChannel1()).channel2(null).build());
                                    detector.compute();
                                } else {
                                    print("DETECTOR_ERR", "ch1 is empty");
                                }
                            } else if (channel == 2) {
                                if (!source.getOut().isEmptyChannel2()) {
                                    detector.setIn(new Data.DataBuilder().channel1(source.getOut().getChannel2()).channel2(null).build());
                                    detector.compute();
                                } else {
                                    print("DETECTOR_ERR", "ch2 is empty");
                                }
                            } else {
                                // Fallback: use old logic if no channel specified
                                if (!source.getOut().isEmptyChannel1()) {
                                    if (!source.getOut().isUsedChannel1()) {
                                        detector.setIn(new Data.DataBuilder().channel1(source.getOut().getChannel1()).channel2(null).build());
                                        source.getOut().setChannel1Used();
                                        detector.compute();
                                    } else {
                                        if (!source.getOut().isEmptyChannel2()) {
                                            if (!source.getOut().isUsedChannel2()) {
                                                detector.setIn(new Data.DataBuilder().channel1(source.getOut().getChannel2()).channel2(null).build());
                                                source.getOut().setChannel2isUsed();
                                                detector.compute();
                                            } else {
                                                print("DETECTOR_ERR", "channels is used");
                                            }
                                        } else {
                                            print("DETECTOR_ERR", "ch2 is empty");
                                        }
                                    }
                                } else {
                                    print("DETECTOR_ERR", "ch1 is empty");
                                }
                            }
                        } else {
                            if (!source.getOut().isEmptyChannel1()) {
                                detector.setIn(source.getOut());
                                detector.compute();
                            } else {
                                print("DETECTOR_ERR", "ch1 is empty");
                            }
                        }
                    } else {
                        print("DETECTOR_ERR", "output for detector is null");
                    }
                });
            }
        }

        @Override
        public void run() {
            ArrayList<QElement> newStartedSources = new ArrayList<>();
            ArrayList<QElement> endedSources = new ArrayList<>();

            main:
            while (!startedSources.isEmpty()) {
                for (QElement i : startedSources) {
                    print(i.getElementType().name() + " : " + i.getTag());
                    i.marked = true;
                }

                source:
                for (QElement sources : startedSources) {
                    QElement current = sources;
                    vertex:
                    for (QElement el : vertexes) {
                        if (!el.marked) {
                            if (graph.containsEdge(current, el)) {
                                // Get edge weight to determine which channel to route
                                Object edge = graph.getEdge(current, el);
                                double edgeWeight = graph.getEdgeWeight(edge);
                                int channel = (int) Math.round(edgeWeight);

                                if (el.isDetector()) {
                                    endedSources.add(current);
                                } else {
                                    Data childInputData;
                                    Data parentOutputData = current.getOut();
                                    print("\n");
                                    print(current.getElementType().name() + ":" + current.getTag() + " -> " + el.getElementType().name() + ":" + el.getTag() + " (channel " + channel + ")");
                                    if (el.isBS()) {
                                        if (el.getIn() == null) {
                                            print("BS", "create new input data");
                                            childInputData = new Data.DataBuilder().channel1(null).channel2(null).build();
                                            el.setIn(childInputData);
                                        } else {
                                            print("BS", "get input data");
                                            childInputData = el.getIn();
                                        }

                                        if (!current.isBS()) {
                                            if (childInputData.isEmptyChannel1() && childInputData.isEmptyChannel2()) {
                                                print("BS", "set channel 1");
                                                childInputData.setChannel1(parentOutputData.getChannel1());
                                                el.setIn(childInputData);
                                                break vertex;
                                            }
                                            if (!childInputData.isEmptyChannel1() && childInputData.isEmptyChannel2()) {
                                                print("BS", "set channel 2");
                                                childInputData.setChannel2(parentOutputData.getChannel1());
                                                el.setIn(childInputData);
                                                print("BS", "channels prepared");
                                                el.compute();
                                                newStartedSources.add(el);
                                            }
                                        }
                                    } else {
                                        // For BS outputs, route the correct channel based on edge weight
                                        if (current.isBS()) {
                                            childInputData = new Data.DataBuilder().channel2(null).channel1(null).build();
                                            if (channel == 1) {
                                                childInputData.setChannel1(parentOutputData.getChannel1());
                                            } else if (channel == 2) {
                                                childInputData.setChannel1(parentOutputData.getChannel2());
                                            } else {
                                                // Default to channel1 if no weight specified
                                                childInputData.setChannel1(parentOutputData.getChannel1());
                                            }
                                        } else {
                                            childInputData = new Data.DataBuilder().channel2(null).channel1(null).build();
                                            childInputData.setChannel1(parentOutputData.getChannel1());
                                        }

                                        el.setIn(childInputData);
                                        el.compute();
                                    }
                                    current = el;

                                }
                            }
                        }
                    }
                }

                print("sources iterated");
                startedSources.clear();
                startedSources.addAll(newStartedSources);
                newStartedSources.clear();
            }

            detectors(endedSources);
        }
    }
}
