package Messages;

public class MsgFromClient {

    public final static String NEWLINE = System.getProperty("line.separator");

    /* Request Header Overall */
    final public static String HEADER_FILE = "FILE";
    final public static String HEADER_INFO = "INFO";
    final public static String HEADER_LOGIN = "LOGIN";
    final public static String HEADER_SYSTEM = "SYSTEM";

    /* Request Header File */
    final public static String FILE_DOWNLOAD = "DownloadFile";
    final public static String FILE_UPLOAD = "UploadFile";
    final public static String FILE_DELETE = "DeleteFile";

    /* Request Header Info */
    final public static String INFO_USER = "UserInfo";
    final public static String INFO_FILE = "FileInfo";

    /* Request Header System */
    final public static String SYSTEM_EXIT = "EXIT";

}
