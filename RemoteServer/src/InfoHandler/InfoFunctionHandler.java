package InfoHandler;

import java.sql.*;

import Messages.MsgToClient;

public class InfoFunctionHandler {

    public static StringBuilder getUserInfor(String[] request, Connection conn) throws SQLException {

        PreparedStatement getAlluserInfo = conn.prepareStatement("SELECT username, publickey FROM userinformation");

        StringBuilder sb = new StringBuilder();
        if (request.length != 2) {
            sb.append(MsgToClient.ERROR_BAD_PARAMETER);
        } else {
            ResultSet rset = getAlluserInfo.executeQuery();
            while (rset.next()) {
                String username = rset.getString("username");
                String publickey = rset.getString("publickey");
                sb.append(username).append(" ").append(publickey).append(MsgToClient.NEWLINE);
            }
        }
        return sb;
    }

    public static StringBuilder getFileInfor(String[] request, Connection conn) throws SQLException {

        PreparedStatement getFileByUser = conn
                .prepareStatement("SELECT fileid, filename FROM fileinformation WHERE filetaget = ?");

        StringBuilder sb = new StringBuilder();
        if (request.length != 3) {
            sb.append(MsgToClient.ERROR_BAD_PARAMETER);
        } else {
            String filetaget = request[2];
            getFileByUser.setString(1, filetaget);
            ResultSet rset = getFileByUser.executeQuery();
            while (rset.next()) {
                int fileid = rset.getInt("fileid");
                String fileName = rset.getString("filename");
                sb.append(String.valueOf(fileid)).append(" ").append(fileName).append(MsgToClient.NEWLINE);
            }
        }
        return sb;
    }
}
