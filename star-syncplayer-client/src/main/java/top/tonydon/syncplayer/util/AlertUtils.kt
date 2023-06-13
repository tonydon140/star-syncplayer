package top.tonydon.syncplayer.util

import javafx.application.Platform
import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType
import javafx.stage.Modality
import javafx.stage.Window

/**
 * 提示框工具类
 */
object AlertUtils {
    fun confirmation(head: String, context: String?, window: Window) {
        alter(head, context, window, AlertType.CONFIRMATION)
    }

    fun confirmation(head: String, window: Window) {
        confirmation(head, null, window)
    }

    fun none(head: String, context: String?, window: Window) {
        alter(head, context, window, AlertType.NONE)
    }

    fun none(head: String, window: Window) {
        none(head, null, window)
    }

    fun error(head: String, context: String?, window: Window) {
        alter(head, context, window, AlertType.ERROR)
    }

    fun error(head: String, window: Window) {
        error(head, null, window)
    }

    fun information(head: String, context: String?, window: Window) {
        alter(head, context, window, AlertType.INFORMATION)
    }

    fun information(head: String, window: Window) {
        information(head, null, window)
    }

    fun warning(head: String, context: String?, window: Window) {
        alter(head, context, window, AlertType.WARNING)
    }

    fun warning(head: String, window: Window) {
        warning(head, null, window)
    }

    private fun alter(head: String, context: String?, window: Window, type: AlertType) {
        Platform.runLater {
            val alert = Alert(type)
            alert.headerText = head
            alert.contentText = context
            alert.initModality(Modality.WINDOW_MODAL)
            alert.initOwner(window)
            alert.show()
        }
    }
}
