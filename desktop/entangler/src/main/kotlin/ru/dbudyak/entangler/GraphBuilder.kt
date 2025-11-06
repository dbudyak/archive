package ru.dbudyak.entangler

import org.jgraph.JGraph
import org.jgraph.graph.DefaultGraphCell
import org.jgraph.graph.GraphConstants
import org.jgrapht.ext.JGraphModelAdapter
import org.jgrapht.graph.DefaultDirectedWeightedGraph
import org.jgrapht.graph.DefaultWeightedEdge
import ru.dbudyak.entangler.models.BaseElement
import java.awt.Dimension
import java.awt.Rectangle
import javax.swing.JFrame
import javax.swing.SwingUtilities
import kotlin.random.Random

/**
 * Manages the graph structure for quantum circuit elements.
 * Singleton pattern for global graph access.
 */
object GraphBuilder {
    private var graph: DefaultDirectedWeightedGraph<QElement, DefaultWeightedEdge> =
        DefaultDirectedWeightedGraph(DefaultWeightedEdge::class.java)

    private var adapter: JGraphModelAdapter<QElement, DefaultWeightedEdge> =
        JGraphModelAdapter(graph)

    fun getAdapter(): JGraphModelAdapter<QElement, DefaultWeightedEdge> = adapter

    fun getGraph(): DefaultDirectedWeightedGraph<QElement, DefaultWeightedEdge> = graph

    fun setGraph(newGraph: DefaultDirectedWeightedGraph<QElement, DefaultWeightedEdge>) {
        graph = newGraph
    }

    // Add vertex methods
    fun addSource(source: QElement) = graph.addVertex(source)
    fun addWaveguide(wguide: QElement) = graph.addVertex(wguide)
    fun addMirror(mirror: QElement) = graph.addVertex(mirror)
    fun addBS(bs: QElement) = graph.addVertex(bs)
    fun addDetector(detector: QElement) = graph.addVertex(detector)

    /**
     * Add an edge between two elements.
     */
    fun addEdge(from: QElement, to: QElement) {
        graph.addEdge(from, to)
    }

    /**
     * Add edge with channel information encoded as weight.
     * @param from Source element
     * @param to Destination element
     * @param channel Channel number (1 or 2, use 0 for default)
     */
    fun addEdge(from: QElement, to: QElement, channel: Int) {
        val edge = graph.addEdge(from, to)
        edge?.let {
            graph.setEdgeWeight(it, channel.toDouble())
        }
    }

    /**
     * Display the graph in a Swing window (for visualization/debugging).
     */
    fun show() {
        SwingUtilities.invokeLater {
            val maxw = 800
            val maxh = 600
            val sourceszoneh = 100
            val detectorzoneh = 100

            println("try show applet")
            adapter = JGraphModelAdapter(graph)
            val jgraph = JGraph(adapter).apply {
                isAntiAliased = true
                isAutoResizeGraph = true
            }

            JFrame().apply {
                minimumSize = Dimension(maxw, maxh)
                contentPane.add(jgraph)
                isVisible = true
            }

            println(graph.vertexSet().size)
            println(graph.edgeSet().size)

            graph.vertexSet().forEach { el ->
                val rndw = Random.nextInt(maxw + 1)
                val rndh = Random.nextInt((maxh - detectorzoneh - sourceszoneh) + 1) + sourceszoneh

                when (el.base.elementType) {
                    BaseElement.ElementType.SOURCE -> {
                        positionVertexAt(el, rndw, sourceszoneh / 2)
                        return@forEach
                    }
                    BaseElement.ElementType.DETECTOR -> {
                        positionVertexAt(el, rndw, maxh - detectorzoneh / 2)
                        return@forEach
                    }
                    else -> positionVertexAt(el, rndw, rndh)
                }
            }
        }
    }

    private fun positionVertexAt(vertex: Any, x: Int, y: Int) {
        val cell = adapter.getVertexCell(vertex)
        val attr = cell.attributes
        val bounds = GraphConstants.getBounds(attr)

        GraphConstants.setBounds(attr, Rectangle(x, y, bounds.width.toInt(), bounds.height.toInt()))
        val cellAttr = hashMapOf<Any, Any>(cell to attr)
        adapter.edit(cellAttr, null, null, null)
    }
}
