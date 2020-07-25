import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DBManager {
    private final String url = "jdbc:sqlite:parkingDB.db";
    public DBManager(){
        initDB();
    }

    /**
     * inserts a record of parking lot authorization to the database
     * if the reason parameter is "approved", it inserts the record to the Approved table. otherwise, inserts to the Denied with the reason
     * @param plate
     * @param reason
     */
    public void insert(String plate,String reason){
        String sql;
        if(reason.equals("approved")) {
            sql = "INSERT INTO Approved(timestamp,plate) VALUES(?,?)";
        }
        else{
            sql = "INSERT INTO Denied(timestamp,plate,reason) VALUES(?,?,?)";
        }

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, getTime());
            pstmt.setString(2, plate);
            if(!reason.equals("approved")){
                pstmt.setString(3, reason);
            }
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        ParkingLogger.getInstance().logger.info("Inserted the plate and the authorization result into the DB");
    }

    private void initDB(){
        String sql1 = "CREATE TABLE IF NOT EXISTS Approved(\n"
                + "	timestamp text PRIMARY KEY,\n"
                + "	plate text NOT NULL\n"
                + ");";
        String sql2 = "CREATE TABLE IF NOT EXISTS Denied(\n"
                + "	timestamp text PRIMARY KEY,\n"
                + "	plate text NOT NULL,\n"
                + "	reason text NOT NULL\n"
                + ");";
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql1);
            stmt.execute(sql2);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    private String getTime(){
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        return dateFormat.format(date);
    }
}

