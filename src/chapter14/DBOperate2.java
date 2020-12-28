package chapter14;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.*;

public class DBOperate2 {
    private ConnectionProvider provider;
    private Connection con = null;
    private PreparedStatement stmt = null;
    private ResultSet rs = null;

    public DBOperate2(ConnectionProvider provider) {
        this.provider = provider;
    }

    public void addStudent(String sNo, String sName, int age, String sClass) throws SQLException {

        try {
            con = provider.getConnection();
            String sql = "insert into STUDENTS(NO,NAME,AGE,CLASS) values(?,?,?,?)";
            stmt = con.prepareStatement(sql);
            stmt.setObject(1, sNo);
            stmt.setObject(2, sName);
            stmt.setObject(3, age);
            stmt.setObject(4, sClass);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeStatement();
            closeConnection();
        }


    }


    public void deleteStudent(String name) {
        try {
            con = provider.getConnection();
            String sql = "delete from STUDENTS where NAME=?";
            stmt = con.prepareStatement(sql);
            stmt.setObject(1, name);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeStatement();
            closeConnection();
        }
    }

    public void printAllStudents() throws SQLException {
        try {
            con = provider.getConnection();
            String sql = "SELECT NO,NAME,AGE,CLASS from STUDENTS";
            stmt = con.prepareStatement(sql);
            rs = stmt.executeQuery();
            //输出查询结果
            while (rs.next()) {
                System.out.print(rs.getString(1) + "\t");
                System.out.print(rs.getString(2) + "\t");
                System.out.print(rs.getInt(3) + "\t");
                System.out.print(rs.getString(4) + "\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeAll();
        }
    }


    public void closeAll() {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (con != null) con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void closeStatement() {
        try {
            if (stmt != null) stmt.close();
        } catch (SQLException e) {
        }
    }

    private void closeConnection() {
        try {
            if (con != null) con.close();
        } catch (SQLException e) {
        }
    }

    public static void main(String args[]) throws Exception {
        DBOperate2 tester = new DBOperate2(new ConnectionProvider());
        tester.addStudent("2009", "小王五", 40, "软件工程");
        tester.printAllStudents();
        tester.deleteStudent("小王五");
    }
}
