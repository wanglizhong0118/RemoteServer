package FileHandler;

import java.io.BufferedInputStream;
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

public class FileHandler {

    public static StringBuilder init(String[] request, Connection conn, OutputStream outWriter,
            BufferedInputStream inReader) throws SQLException, IOException, InvalidKeyException,
            NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {

        StringBuilder resultBuffer = new StringBuilder();
        String requestOption = request[1];

        switch (requestOption) {
        case MsgFromClient.FILE_DOWNLOAD:
            FileFunctionHandler.downloadFile(request, conn, outWriter);
            break;
        case MsgFromClient.FILE_UPLOAD:
            FileFunctionHandler.uploadFile(request, conn, outWriter);
            break;
        case MsgFromClient.FILE_DELETE:
            FileFunctionHandler.deleteFile(request, conn, outWriter);
            break;
        default:
            Connector.response(outWriter, MsgToClient.ERROR_BAD_PARAMETER);
            break;
        }
        return resultBuffer;
    }
}
