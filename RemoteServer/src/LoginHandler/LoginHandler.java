package LoginHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import Authentication.AuthByEmail;
import Messages.MsgToClient;
import Server.Connector;

public class LoginHandler {

    public static String loginHeader = MsgToClient.HEADER_LOGIN + MsgToClient.NEWLINE;

    public static boolean login(Connection conn, OutputStream outWriter, String[] request)
            throws SQLException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException,
            IllegalBlockSizeException, BadPaddingException, IOException {

        PreparedStatement searchByUsername = conn
                .prepareStatement("SELECT password FROM userinformation WHERE username= ?");

        boolean firstlogin = false;

        if (request.length != 3) {
            Connector.response(outWriter, loginHeader + MsgToClient.ERROR_BAD_PARAMETER);
        } else {
            String inputUsername = request[1];
            String inputPassword = request[2];
            searchByUsername.setString(1, inputUsername);
            ResultSet rsetPassword = searchByUsername.executeQuery();
            if (!rsetPassword.next()) {
                Connector.response(outWriter, loginHeader + MsgToClient.ERROR_USERNAME_NOTEXIST);
            } else {
                String storedPassword = rsetPassword.getString(1);
                String hashedInputPassword = hashByMD5(inputPassword);
                if (storedPassword.equals(hashedInputPassword)) {
                    firstlogin = true;
                    Connector.response(outWriter, loginHeader + MsgToClient.SUCCESS_lOGIN + MsgToClient.EMAIL_AUTHN);
                } else {
                    Connector.response(outWriter, loginHeader + MsgToClient.ERROR_PASSWORD_FAILED);
                }
            }
        }
        return firstlogin;
    }

    public static String randomString() {
        char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++)
            sb.append(chars[rnd.nextInt(chars.length)]);
        return sb.toString();
    }

    public static String hashByMD5(String inputString) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        byte[] bytesOfMessage = inputString.getBytes("UTF-8");
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] thedigest = md.digest(bytesOfMessage);
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < thedigest.length; i++) {
            String hex = Integer.toHexString(0xff & thedigest[i]);
            if (hex.length() == 1)
                hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static void emailAuthentication(Connection conn, OutputStream outWriter, String[] loginRequestArray,
            String randomAuth) throws SQLException, InvalidKeyException, NoSuchAlgorithmException,
            NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
        String inputUsername = loginRequestArray[1];
        PreparedStatement searchByEmail = conn.prepareStatement("SELECT email FROM userinformation WHERE username= ?");
        searchByEmail.setString(1, inputUsername);
        ResultSet rsetEmail = searchByEmail.executeQuery();
        if (rsetEmail.next()) {
            String emailAddr = rsetEmail.getString(1);
            AuthByEmail.init(emailAddr, randomAuth);
        }
    }

    public static void alreadyLogin(OutputStream outWriter) throws InvalidKeyException, NoSuchAlgorithmException,
            NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
        Connector.response(outWriter, loginHeader + MsgToClient.ALLREADY_LOGIN);

    }
}
