package SystemHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import Messages.MsgToClient;
import Server.Connector;

public class SystemHandler {

    public static void init(OutputStream outWriter, String[] requestArray) throws IOException, InvalidKeyException,
            NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {

        if (requestArray.length != 2) {
            Connector.response(outWriter, MsgToClient.ERROR_BAD_PARAMETER);
        } else {
            exit(outWriter);
        }
    }

    private static void exit(OutputStream outWriter) throws IOException, InvalidKeyException, NoSuchAlgorithmException,
            NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        String exitResponse = MsgToClient.HEADER_SYSTEM + MsgToClient.NEWLINE + MsgToClient.EXIT_CONNECTION;
        Connector.response(outWriter, exitResponse);
    }
}
