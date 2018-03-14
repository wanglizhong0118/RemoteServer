package Messages;

public class MsgToClient {

    public final static String NEWLINE = System.getProperty("line.separator");

    final public static String WELCOME_MSG = "Welcome to the server without SSL /liz" + NEWLINE;
    final public static String WELCOME_MSG_SSL = "Welcome to the server SSL /liz" + NEWLINE;

    /* Response Message Header definition */
    final public static String HEADER_FILE = "<FILE>";
    final public static String HEADER_INFO = "<INFO>";
    final public static String HEADER_LOGIN = "<LOGIN>";
    final public static String HEADER_SYSTEM = "<SYSTEM>";

    /* Response Message File Header definition */
    final public static String FILE_DOWNLOAD = "<DownloadFile>";
    final public static String FILE_UPLOAD = "<UploadFile>";
    final public static String FILE_DELETE = "<DeleteFile>";

    final public static String EXIT_CONNECTION = "Good bye" + NEWLINE;

    /* ERROR message definition */
    final public static String ERROR_BAD_PARAMETER = "ERROR bad parameter" + NEWLINE;
    final public static String ERROR_USERNAME_NOTEXIST = "ERROR username does not exists" + NEWLINE;
    final public static String ERROR_PASSWORD_FAILED = "ERROR password failed" + NEWLINE;
    final public static String ERROR_BAD_FILEID = "ERROR bad file id" + NEWLINE;
    final public static String ERROR_NOT_FILEOWNER = "ERROR you are not the file owner" + NEWLINE;
    final public static String ERROR_NOT_FILETAGET = "ERROR you are not the file taget" + NEWLINE;

    /* SUCCESS message definition */
    final public static String SUCCESS_lOGIN = "OK login succeed" + NEWLINE;
    final public static String SUCESS_REQUEST = "OK access suceed" + NEWLINE;

    /* SUCCESS file operation */
    final public static String SUCESS_FILE_UPLOAD = "OK upload down" + NEWLINE;
    final public static String SUCESS_FILE_DOWNLOAD = "OK download done" + NEWLINE;
    final public static String SUCESS_FILE_DELETE = "OK delete done" + NEWLINE;

    /* ERROR file operation */
    final public static String ERROR_FILE_DELETE_UNABLE = "ERROR unable to delete the file on server" + NEWLINE;

    /* INFO email authentication */
    final public static String ERROR_LOGIN_REQUIRED = "Please login frist" + NEWLINE;
    final public static String EMAIL_AUTHN = "Please check email to get one time password" + NEWLINE;
    final public static String EMAIL_AUTHN_SUCCESS = "Email authentication succeed. Welcome to the file system"
            + NEWLINE;
    final public static String EMAIL_AUTHN_FAILED = "Email authentication failed. Pleas try to login again" + NEWLINE;
    final public static String ALLREADY_LOGIN = "You are already inlogged" + NEWLINE;

}
