package top.tonydon.util;

public class URIUtils {

    public static String encoder(String uri) {
        char[] charArray = uri.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (char ch : charArray) {
            if (ch == ' ') {
                sb.append("%20");
            } else if (ch == '?') {
                sb.append("%3F");
            } else if (ch == '%') {
                sb.append("%25");
            } else if (ch == '#') {
                sb.append("%23");
            } else if (ch == '&') {
                sb.append("%26");
            } else if (ch == '=') {
                sb.append("%3D");
            } else if (ch == '{') {
                sb.append("%7B");
            } else if (ch == '}') {
                sb.append("%7D");
            } else if (ch == '"') {
                sb.append("%22");
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }
}
