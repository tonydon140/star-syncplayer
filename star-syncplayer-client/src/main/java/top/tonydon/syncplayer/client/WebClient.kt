package top.tonydon.syncplayer.client

import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import top.tonydon.syncplayer.message.JsonMessage
import top.tonydon.syncplayer.message.MessageType
import top.tonydon.syncplayer.message.common.ActionMessage
import top.tonydon.syncplayer.message.common.MovieMessage
import top.tonydon.syncplayer.message.common.StringMessage
import top.tonydon.syncplayer.util.observer.ClientObserver
import top.tonydon.syncplayer.util.observer.Observable
import java.net.URI

class WebClient(serverUri: URI) : WebSocketClient(serverUri), Observable<ClientObserver> {
    private val log: Logger = LoggerFactory.getLogger(WebClient::class.java)
    private val observerSet: MutableSet<ClientObserver> = HashSet()

    // 连接建立
    override fun onOpen(serverHandshake: ServerHandshake) {}

    // 收到消息
    override fun onMessage(json: String) {
        // 1. 转换 json 到对象
        val message = JsonMessage.parse(json)

        // 2. 对不同的消息执行不同的处理
        when (message.type) {
            MessageType.ACTION -> {
                val code = (message as ActionMessage).actionCode
                observerSet.forEach { it.onAction(code) }
            }

            MessageType.STRING -> {
                val stringMessage = message as StringMessage
                val code = stringMessage.actionCode
                val content = stringMessage.content
                observerSet.forEach { it.onString(code, content) }
            }

            MessageType.MOVIE -> {
                val movieMessage = message as MovieMessage
                observerSet.forEach { it.onMovie(movieMessage) }
            }
        }
    }


    // 连接关闭
    override fun onClose(code: Int, reason: String, remote: Boolean) {
        log.warn("code = {}, reason = {}", code, reason)
    }

    // 发生异常
    override fun onError(ex: Exception) {
        ex.printStackTrace()
        log.error(ex.message)
        observerSet.forEach { it.onError(ex) }
    }

    // 添加观察者
    override fun addObserver(observer: ClientObserver) {
        observerSet.add(observer)
    }

    // 删除观察者
    override fun removeObserver(observer: ClientObserver) {
        observerSet.remove(observer)
    }
}
