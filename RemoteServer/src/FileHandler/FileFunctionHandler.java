package FileHandler;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import Authentication.Encryption;
import Messages.MsgFromClient;
import Messages.MsgToClient;
import Server.Connector;
import Server.Setter;

public class FileFunctionHandler {

    public static void downloadFile(String[] request, Connection conn, OutputStream outWriter)
            throws SQLException, IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
            IllegalBlockSizeException, BadPaddingException {

        PreparedStatement downloadFile = conn
                .prepareStatement("SELECT filename, filepath, filetaget FROM fileinformation WHERE fileid = ?");
        PreparedStatement autoRemove = conn.prepareStatement("DELETE FROM fileinformation WHERE fileid = ?");
        PreparedStatement autoResetTarget = conn
                .prepareStatement("UPDATE fileinformation SET filetaget = 'admin' WHERE fileid = ?");

        if (request.length != 4) {
            Connector.response(outWriter,
                    MsgToClient.HEADER_FILE + MsgToClient.NEWLINE + MsgToClient.ERROR_BAD_PARAMETER);
        } else {
            String fileidString = request[2];
            int fileid = Integer.parseInt(fileidString);
            String filetaget = request[3];
            downloadFile.setInt(1, fileid);
            ResultSet rset = downloadFile.executeQuery();
            if (!rset.next()) {
                Connector.response(outWriter,
                        MsgToClient.HEADER_FILE + MsgToClient.NEWLINE + MsgToClient.ERROR_BAD_FILEID);
            } else {
                String storedTaget = rset.getString("filetaget");
                System.out.println(storedTaget);
                System.out.println(filetaget);
                if (!storedTaget.equals(filetaget)) {
                    Connector.response(outWriter,
                            MsgToClient.HEADER_FILE + MsgToClient.NEWLINE + MsgToClient.ERROR_NOT_FILETAGET);
                } else {
                    String filename = rset.getString("filename");
                    String filepath = rset.getString("filepath");
                    String fileFullPath = filepath + "\\" + filename;

                    String responseHeader = MsgToClient.HEADER_FILE + MsgToClient.NEWLINE + MsgToClient.FILE_DOWNLOAD
                            + MsgToClient.NEWLINE + filename + MsgToClient.NEWLINE;
                    String content = new String(Files.readAllBytes(Paths.get(fileFullPath)));
                    String response = responseHeader + content;
                    String AESresponse = Encryption.encrypt(response, Setter.SECURITY_KEY);
                    byte[] responseArray = new byte[AESresponse.length()];
                    responseArray = AESresponse.getBytes(StandardCharsets.UTF_8);
                    // autoRemove.setInt(1, fileid);
                    // autoResetTarget.setInt(1, fileid);
                    // autoRemove.executeUpdate();
                    outWriter.write(responseArray, 0, responseArray.length);
                }
            }
        }
    }

    public static void uploadFile(String[] request, Connection conn, OutputStream outWriter)
            throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
            IllegalBlockSizeException, BadPaddingException, SQLException {

        PreparedStatement uploadFile = conn.prepareStatement(
                "INSERT INTO fileinformation (filename, filepath, fileowner, filetaget) VALUES (?,?,?,?)");

        String filename = request[2];
        String fileowner = request[3];
        String filetarget = request[4];

        uploadFile.setString(1, filename);
        uploadFile.setString(2, Setter.FILE_DIRECTORY + "\\\\" + fileowner);
        uploadFile.setString(3, fileowner);
        uploadFile.setString(4, filetarget);

        String fileFullPath = Setter.FILE_DIRECTORY + "\\" + fileowner + "\\" + filename;

        String[] fileBody = Arrays.copyOfRange(request, 5, request.length);
        String fileContent = String.join(MsgFromClient.NEWLINE, fileBody);
        InputStream inputStream = new ByteArrayInputStream(fileContent.getBytes(StandardCharsets.UTF_8));

        int bytesRead;
        int current = 0;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        try {
            byte[] tempArray = new byte[Setter.FILE_SIZE];
            fos = new FileOutputStream(fileFullPath);
            bos = new BufferedOutputStream(fos);
            bytesRead = inputStream.read(tempArray, 0, tempArray.length);
            current = bytesRead;
            do {
                bytesRead = inputStream.read(tempArray, current, (tempArray.length - current));
                if (bytesRead >= 0)
                    current += bytesRead;
            } while (bytesRead > -1);
            bos.write(tempArray, 0, current);
            bos.flush();
        } finally {
            if (fos != null)
                fos.close();
            if (bos != null)
                bos.close();
        }
        uploadFile.executeUpdate();
        Connector.response(outWriter, MsgToClient.HEADER_FILE + MsgToClient.NEWLINE + MsgToClient.FILE_UPLOAD
                + MsgToClient.NEWLINE + MsgToClient.SUCESS_FILE_UPLOAD);
    }

    public static void deleteFile(String[] request, Connection conn, OutputStream outWriter)
            throws IOException, SQLException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
            IllegalBlockSizeException, BadPaddingException {

        PreparedStatement searchByFileId = conn
                .prepareStatement("SELECT filename, filepath, fileowner FROM fileinformation WHERE fileid = ?");
        PreparedStatement deleteByFileid = conn.prepareStatement("DELETE FROM fileinformation WHERE fileid = ?");

        if (request.length != 4) {
            Connector.response(outWriter,
                    MsgToClient.HEADER_FILE + MsgToClient.NEWLINE + MsgToClient.ERROR_BAD_PARAMETER);
        } else {
            String fileidString = request[2];
            int fileid = Integer.parseInt(fileidString);
            String fileowner = request[3];
            searchByFileId.setInt(1, fileid);
            ResultSet rsetById = searchByFileId.executeQuery();
            if (!rsetById.next()) {
                Connector.response(outWriter,
                        MsgToClient.HEADER_FILE + MsgToClient.NEWLINE + MsgToClient.ERROR_BAD_FILEID);
            } else {
                String storedOwner = rsetById.getString("fileowner");
                if (!storedOwner.equals(fileowner)) {
                    Connector.response(outWriter,
                            MsgToClient.HEADER_FILE + MsgToClient.NEWLINE + MsgToClient.ERROR_NOT_FILEOWNER);
                } else {
                    String filePath = rsetById.getString("filepath") + "\\" + rsetById.getString("filename");
                    System.out.println("filePath: " + filePath);
                    Path path = Paths.get(filePath);
                    if (Files.deleteIfExists(path)) {
                        deleteByFileid.setInt(1, fileid);
                        deleteByFileid.executeUpdate();
                        Connector.response(outWriter, MsgToClient.HEADER_FILE + MsgToClient.NEWLINE
                                + MsgToClient.FILE_DELETE + MsgToClient.NEWLINE + MsgToClient.SUCESS_FILE_DELETE);
                    } else {
                        Connector.response(outWriter,
                                MsgToClient.HEADER_FILE + MsgToClient.NEWLINE + MsgToClient.ERROR_FILE_DELETE_UNABLE);
                    }
                }
            }
        }
    }
}
