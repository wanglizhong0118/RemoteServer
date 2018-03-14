package InfoHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import Messages.MsgFromClient;
import Messages.MsgToClient;
import Server.Connector;

public class InforHandler {

    public static void init(Connection conn, OutputStream outWriter, String[] request)
            throws SQLException, IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
            IllegalBlockSizeException, BadPaddingException {
        String response = MsgToClient.HEADER_INFO + MsgToClient.NEWLINE + InforHandler.init(request, conn).toString();
        Connector.response(outWriter, response);
    }

    public static StringBuilder init(String[] request, Connection conn) throws SQLException {

        StringBuilder resultBuffer = new StringBuilder();

        String requestOption = request[1];
        switch (requestOption) {
        case MsgFromClient.INFO_USER:
            resultBuffer = InfoFunctionHandler.getUserInfor(request, conn);
            break;
        case MsgFromClient.INFO_FILE:
            resultBuffer = InfoFunctionHandler.getFileInfor(request, conn);
            break;
        default:
            resultBuffer.append(MsgToClient.ERROR_BAD_PARAMETER);
            break;
        }

        return resultBuffer;
    }
}
