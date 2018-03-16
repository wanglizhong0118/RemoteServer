package Server;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import Authentication.Encryption;

public class Connector {

    public static void response(OutputStream outWriter, String response) throws IOException, InvalidKeyException,
            NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {

        byte[] responseArray = new byte[response.length()];
        String aseResponse = Encryption.encrypt(response, Setter.SECURITY_KEY);
        responseArray = aseResponse.getBytes(StandardCharsets.UTF_8);
        outWriter.write(responseArray, 0, responseArray.length);
    }

    public static String readAndDecryptMsg(BufferedInputStream inReader)
            throws IOException, UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        byte[] fromClient = new byte[Setter.FILE_SIZE];
        int bytesRead = 0;
        int bufferLength;
        while ((bufferLength = inReader.read(fromClient, bytesRead, 256)) != -1) {
            bytesRead += bufferLength;
            if (bytesRead == Setter.FILE_SIZE || inReader.available() == 0) {
                break;
            }
        }
        String request = new String(fromClient, 0, bytesRead);
        String deASERequest = Encryption.decrypt(request, Setter.SECURITY_KEY);
        return deASERequest;
    }

    public static void printRequest(String[] requestArray) {
        StringBuilder sb = new StringBuilder();
        for (String str : requestArray) {
            sb.append(str).append(" ");
        }
        System.out.println(sb.toString());
    }

}
