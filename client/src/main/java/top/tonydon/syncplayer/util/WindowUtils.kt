package top.tonydon.syncplayer.util

import top.tonydon.syncplayer.constant.ClientConstants

object WindowUtils {
    fun judgeVersion(latest: String): Boolean {
        val currentList = ClientConstants.VERSION.substring(1).split(".")
        val latestList = latest.substring(1).split(".")

        for (i in currentList.indices) {
            return if (latestList[i].toInt() > currentList[i].toInt())
                true
            else if (latestList[i].toInt() == currentList[i].toInt()) {
                continue
            } else
                false
        }
        return false
    }
}