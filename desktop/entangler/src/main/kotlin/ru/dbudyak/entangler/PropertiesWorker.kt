package ru.dbudyak.entangler

import javafx.scene.control.Button
import javafx.scene.control.ListView
import javafx.scene.control.SelectionMode
import javafx.scene.control.cell.TextFieldListCell
import javafx.scene.layout.AnchorPane
import ru.dbudyak.entangler.models.BaseElement

/**
 * Manages the properties panel UI for quantum elements.
 * Singleton pattern for global access.
 */
object PropertiesWorker {
    private var propertiesUpdate: OnPropertiesUpdate? = null
    private var props: AnchorPane? = null
    private var keys: ListView<String>? = null
    private var values: ListView<String>? = null

    private val data = HashMap<String, String>()

    fun getElementData(): HashMap<String, String> = data

    fun setOnPropertiesListener(listener: OnPropertiesUpdate) {
        propertiesUpdate = listener
    }

    fun setPropertiesLayout(lookup: AnchorPane) {
        props = lookup
        val bDelete = props?.lookup("#propBtnDelete") as? Button
        val bRotate = props?.lookup("#propBtnRotate") as? Button
        val bFlip = props?.lookup("#propBtnFlip") as? Button

        bDelete?.setOnAction { propertiesUpdate?.onDelete() }
        bRotate?.setOnAction { propertiesUpdate?.onRotate() }
        bFlip?.setOnAction { propertiesUpdate?.onFlip() }

        keys = props?.lookup("#propKeysList") as? ListView<String>
        values = (props?.lookup("#propValuesList") as? ListView<String>)?.apply {
            isEditable = true
            cellFactory = TextFieldListCell.forListView()
            selectionModel.selectionMode = SelectionMode.SINGLE
        }
    }

    fun getPropertiesAnchor(): AnchorPane? = props

    fun setResult(s: String) {
        data["Result"] = s
    }

    fun setTop(name: String, isConnected: String) {
        data[name] = isConnected
    }

    fun setRight(name: String, isConnected: String) {
        data[name] = isConnected
    }

    fun setBottom(name: String, isConnected: String) {
        data[name] = isConnected
    }

    fun setLeft(name: String, isConnected: String) {
        data[name] = isConnected
    }

    fun setDetectorCounts() {
        data["Clicks"] = "measured"
    }

    fun setPropertiesVisibility(visibility: Boolean) {
        props?.isVisible = visibility
    }

    fun setName(name: String) {
        data["Name"] = name
    }

    fun setSourceOutputParam(value: Double) {
        data["Mode"] = "cos($value)|0> + sin($value)|1>"
    }

    fun setBSTheta(value: Double) {
        data["\u03B8"] = value.toString()
    }

    fun setType(type: BaseElement.ElementType) {
        // No-op for now
    }

    fun clearProperties() {
        keys?.items?.clear()
        values?.items?.clear()
    }

    fun clearData() {
        data.clear()
    }

    fun updateProperties() {
        clearProperties()
        data.forEach { (key, value) ->
            keys?.items?.add(key)
            values?.items?.add(value)
        }
    }

    /**
     * Interface for property update callbacks.
     */
    interface OnPropertiesUpdate {
        fun onDelete()
        fun onFlip()
        fun onRotate()
    }
}
