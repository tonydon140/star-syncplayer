package top.tonydon.syncplayer.util

object TimeFormat {
    private var total = ""

//    fun setTotal(duration: Duration) {
//        total = convertDuration(duration.toSeconds())
//        setTotal(du)
//    }

    fun setTotal(milliseconds: Long) {
        total = convertDuration(milliseconds)
    }

    private fun convertDuration(milliseconds: Long): String {
        var seconds = (milliseconds / 1000).toInt()
        var minutes = 0
        while (seconds >= 60) {
            minutes++
            seconds -= 60
        }
        var minutesStr = minutes.toString()
        if (minutesStr.length == 1) minutesStr = "0$minutesStr"
        var secondsStr = seconds.toString()
        if (secondsStr.length == 1) secondsStr = "0$secondsStr"
        return "$minutesStr:$secondsStr"
    }


    fun getText(milliseconds: Long): String {
        return convertDuration(milliseconds) + "/" + total
    }

//    fun getText(duration: Duration): String {
//        return getText(duration.toSeconds())
//    }
}
