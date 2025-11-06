package ru.dbudyak.entangler;

import org.jgraph.JGraph;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgrapht.ext.JGraphModelAdapter;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import ru.dbudyak.entangler.models.base.BaseElement;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Created by dbudyak on 27.05.2014.
 */
public class GraphBuilder {

    private static GraphBuilder instance;
    private DefaultDirectedWeightedGraph graph;
    private JGraphModelAdapter adapter;

    private GraphBuilder() {
        setGraph(new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class));
        adapter = new JGraphModelAdapter(getGraph());
    }

    public static GraphBuilder getInstance() {
        if (instance == null) {
            instance = new GraphBuilder();
        }
        return instance;
    }

    public JGraphModelAdapter getAdapter() {
        return adapter;
    }

    public void addSource(QElement source) {
        getGraph().addVertex(source);
    }

    public void addWaveguide(QElement wguide) {
        getGraph().addVertex(wguide);
    }

    public void addMirror(QElement mirror) {
        getGraph().addVertex(mirror);
    }

    public void addBS(QElement bs) {
        getGraph().addVertex(bs);
    }

    public void addDetector(QElement detector) {
        getGraph().addVertex(detector);
    }

    public void addEdge(QElement from, QElement to) {
        getGraph().addEdge(from, to);
    }

    /**
     * Add edge with channel information encoded as weight.
     * @param from Source element
     * @param to Destination element
     * @param channel Channel number (1 or 2, use 0 for default)
     */
    public void addEdge(QElement from, QElement to, int channel) {
        DefaultWeightedEdge edge = (DefaultWeightedEdge) getGraph().addEdge(from, to);
        if (edge != null) {
            getGraph().setEdgeWeight(edge, (double) channel);
        }
    }

    public void show() {
        SwingUtilities.invokeLater(() -> {

            int maxw = 800;
            int maxh = 600;

            int sourceszoneh = 100;
            int detectorzoneh = 100;

            System.out.println("try show applet");
            adapter = new JGraphModelAdapter(getGraph());
            JGraph jgraph = new JGraph(adapter);

            jgraph.setAntiAliased(true);
            jgraph.setAutoResizeGraph(true);


            JFrame jframe = new JFrame();
            jframe.setMinimumSize(new Dimension(maxw, maxh));
            jframe.getContentPane().add(jgraph);
            jframe.setVisible(true);

            System.out.println(getGraph().vertexSet().size());
            System.out.println(getGraph().edgeSet().size());

            Set<QElement> qElementSet = getGraph().vertexSet();
            for (QElement el : qElementSet) {
                Random rnd = new Random();
                int rndw = rnd.nextInt((maxw) + 1);
                int rndh = rnd.nextInt(((maxh - detectorzoneh) - sourceszoneh) + 1) + sourceszoneh;
                if (el.getBase().getElementType() == BaseElement.ElementType.SOURCE) {
                    positionVertexAt(el, rndw, sourceszoneh / 2);
                    continue;
                } else if (el.getBase().getElementType() == BaseElement.ElementType.DETECTOR) {
                    positionVertexAt(el, rndw, maxh - detectorzoneh / 2);
                    continue;
                } else if (el.getBase().getElementType() == BaseElement.ElementType.BS) {
                } else if (el.getBase().getElementType() == BaseElement.ElementType.MIRROR) {

                } else if (el.getBase().getElementType() == BaseElement.ElementType.WAVEGUIDE) {

                }
                positionVertexAt(el, rndw, rndh);
            }
        });
    }

    private void positionVertexAt(Object vertex, int x, int y) {
        DefaultGraphCell cell = adapter.getVertexCell(vertex);
        Map attr = cell.getAttributes();
        Rectangle2D b = GraphConstants.getBounds(attr);

        GraphConstants.setBounds(attr, new Rectangle(x, y, (int) b.getWidth(), (int) b.getHeight()));
        Map cellAttr = new HashMap();
        cellAttr.put(cell, attr);
        adapter.edit(cellAttr, null, null, null);
    }


    public DefaultDirectedWeightedGraph getGraph() {
        return graph;
    }

    void setGraph(DefaultDirectedWeightedGraph graph) {
        this.graph = graph;
    }
}
