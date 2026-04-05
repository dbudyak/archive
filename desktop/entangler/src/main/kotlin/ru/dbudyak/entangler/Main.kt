package ru.dbudyak.entangler

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.BorderPane
import javafx.scene.layout.GridPane
import javafx.stage.Stage

/**
 * Main JavaFX application entry point.
 */
class Main : Application() {

    override fun start(primaryStage: Stage) {
        initLayout(primaryStage)
    }

    private fun initLayout(stage: Stage) {
        val root = Group()
        val qRoot = FXMLLoader.load<BorderPane>(javaClass.classLoader.getResource("qRoot.fxml"))
        root.children.add(qRoot)

        val scene = Scene(root).apply {
            stylesheets.add(javaClass.classLoader.getResource("style.css")?.toString())
            stage.title = "Entangler"
            stage.isResizable = false
            stage.scene = this
            stage.show()
        }

        val gridPane = scene.lookup("#circuit") as QCircuit

        val cw = CircuitWorker().apply {
            setGridPane(gridPane)
            initGrid()
        }

        ElementsWorker().apply {
            setGridPane(
                scene.lookup("#qElementsGrid") as GridPane,
                scene.lookup("#qWavesGrid") as GridPane
            )
            initGrid()
        }

        (scene.lookup("#mainBtnStart") as Button).apply {
            style = "-fx-background-color: #009933;"
            setOnAction { cw.process() }
        }

        (scene.lookup("#mainBtnRotate") as Button).setOnAction { cw.rotate() }
        (scene.lookup("#mainBtnGraph") as Button).setOnAction { GraphBuilder.show() }
        (scene.lookup("#mainBtnGrid") as Button).setOnAction { cw.showGrid() }
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(Main::class.java, *args)
        }
    }
}
