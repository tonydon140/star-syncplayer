package top.tonydon.syncplayer.util.observer

import top.tonydon.syncplayer.message.common.MovieMessage

interface ClientObserver {
    fun onAction(code: Int)
    fun onString(code: Int, content: String)
    fun onMovie(message: MovieMessage)
    fun onError(exception: Exception)
}
