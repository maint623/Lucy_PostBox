package kr.lucymc.lucy_postbox;


import com.google.gson.JsonArray;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import static kr.lucymc.lucy_postbox.Lucy_PostBox.connection;

public class PostBox_DB {
    public static void PBInsert(UUID userid, String Prefix, int Count) {
        String sql = "insert into postbox (UserID, Array, Count) values ('" + userid +"','" + Prefix +"','" + Count +"');";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void PBUpdate(UUID userid, JsonArray Prefix, int Count) {
        String sql = "update postbox set Array='"+Prefix+"',Count='"+Count+"' where UserID='"+userid+"';";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static ResultSet PBSelect(UUID userid) {
        String sql = "select * from postbox where UserID='"+userid+"';";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            return stmt.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
