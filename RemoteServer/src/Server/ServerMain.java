package Server;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import FileHandler.FileHandler;
import InfoHandler.InforHandler;
import LoginHandler.LoginHandler;
import Messages.MsgFromClient;
import Messages.MsgToClient;
import SystemHandler.SystemHandler;

public class ServerMain {

    public static void main(String[] args) throws NoSuchAlgorithmException {
        ServerSocket serverSocket = null;
        try {
            Connection conn = DriverManager.getConnection(Setter.JDBC_PATH, Setter.SQL_USERNAME, Setter.SQL_PASSWORD);
            System.out.println("SQL Server Connected");

            serverSocket = new ServerSocket(Setter.PORT_NUMBER);
            Socket clientSocket = serverSocket.accept();
            String clientAddress = clientSocket.getRemoteSocketAddress().toString();

            System.out.println("Client Address:   " + clientAddress);
            System.out.println("Keep Alive:   " + clientSocket.getKeepAlive());

            try (OutputStream outWriter = clientSocket.getOutputStream();
                    BufferedInputStream inReader = new BufferedInputStream((clientSocket.getInputStream()));) {

                while (true) {
                    boolean loginSucc = false;

                    while (!loginSucc) {
                        String loginRequest = Connector.readAndDecryptMsg(inReader);
                        String[] loginRequestArray = loginRequest.split(MsgFromClient.NEWLINE);
                        if (!loginRequestArray[0].equals(MsgFromClient.HEADER_LOGIN)) {
                            Connector.response(outWriter, LoginHandler.loginHeader + MsgToClient.ERROR_LOGIN_REQUIRED);
                        } else {
                            boolean firstlogin = LoginHandler.login(conn, outWriter, loginRequestArray);
                            if (firstlogin) {
                                // String randomAuth = "123123";
                                String randomAuth = LoginHandler.randomString();
                                LoginHandler.emailAuthentication(conn, outWriter, loginRequestArray, randomAuth);
                                String authRequest = Connector.readAndDecryptMsg(inReader);
                                String[] authRequestArray = authRequest.split(MsgFromClient.NEWLINE);
                                // System.out.println("authRequest is " + authRequest);
                                if (authRequestArray[1].equals(randomAuth)) {
                                    Connector.response(outWriter,
                                            LoginHandler.loginHeader + MsgToClient.EMAIL_AUTHN_SUCCESS);
                                    loginSucc = true;
                                    break;
                                } else {
                                    Connector.response(outWriter,
                                            LoginHandler.loginHeader + MsgToClient.EMAIL_AUTHN_FAILED);
                                    break;
                                }
                            }
                        }
                    }

                    while (loginSucc) {
                        String deASERequest = Connector.readAndDecryptMsg(inReader);
                        String[] requestArray = deASERequest.split(MsgFromClient.NEWLINE);

                        String requestHeader = requestArray[0];
                        Connector.printRequest(requestArray);

                        switch (requestHeader) {
                        case MsgFromClient.HEADER_INFO:
                            InforHandler.init(conn, outWriter, requestArray);
                            break;
                        case MsgFromClient.HEADER_FILE:
                            FileHandler.init(requestArray, conn, outWriter, inReader);
                            break;
                        case MsgFromClient.HEADER_SYSTEM:
                            SystemHandler.init(outWriter, requestArray);
                        case MsgFromClient.HEADER_LOGIN:
                            LoginHandler.alreadyLogin(outWriter);
                        default:
                            Connector.response(outWriter, MsgToClient.ERROR_BAD_PARAMETER);
                            break;
                        }
                    }
                }
            } catch (InvalidKeyException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
                e.printStackTrace();
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            System.out.println("Disconnected");
        }
    }
}
