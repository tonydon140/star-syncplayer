package top.tonydon.syncplayer.util

object URIUtils {
    fun encoder(uri: String): String {
        val charArray = uri.toCharArray()
        val sb = StringBuilder()
        for (ch in charArray) {
            when (ch) {
                ' ' -> sb.append("%20")
                '?' -> sb.append("%3F")
                '%' -> sb.append("%25")
                '#' -> sb.append("%23")
                '&' -> sb.append("%26")
                '=' -> sb.append("%3D")
                '{' -> sb.append("%7B")
                '}' -> sb.append("%7D")
                '"' -> sb.append("%22")
                else -> sb.append(ch)
            }
        }
        return sb.toString()
    }
}
