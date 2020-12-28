package chapter14;

import java.sql.*;
import java.util.ArrayList;

/**
 * @ProjectName internetProgram
 * @ClassName DBMetaTest
 * @Description TODO
 * @Author Lyn
 * @Date 2020/12/7 16:30
 * @Version 1.0
 * @Function
 */

public class DBMetaTest {

    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException, SQLException {
        //指定数据库所在位置，先用本地地址测试，访问本地的数据库
        String dbUrl = "jdbc:mysql://202.116.195.71:3306/mypeopledb?serverTimezome=Hongkong";
        //指定用户名和密码
        String dbUser="student";
        String dbPwd="student";

        Class jdbcDriver = Class.forName("com.mysql.jdbc.Driver");
        //注册MySQL驱动器
        java.sql.DriverManager.registerDriver((Driver)jdbcDriver.newInstance());

        Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPwd);

        DatabaseMetaData metaData = connection.getMetaData();//返回数据库的一些元信息

        ResultSet rsTables = metaData.getTables(null,null,null,new String[]{"TABLE"});
        //用于保存表名的数组列表，供之后遍历访问
        ArrayList<String> tablesName = new ArrayList<>();
        System.out.println("该数据库中包含的表：");
        try {
            while (rsTables.next()) {
                System.out.print(rsTables.getString("TABLE_NAME") + "\t");
                tablesName.add(rsTables.getString("TABLE_NAME"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rsTables != null)
                rsTables.close();
        }
        System.out.println("");
        System.out.println("_____________________");

//        PreparedStatement stmt = null;
//        ResultSet rs = null;
        for (String tableName : tablesName) {
            System.out.println(tableName);
            //保存字段名
            ArrayList<String> filedsName = new ArrayList<>();
            String sql = "select * from " + tableName ;
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            ResultSetMetaData fields = rs.getMetaData();//会返回该表的字段信息
            int n = fields.getColumnCount();//有多少个字段
            System.out.println(tableName + "字段：");
            for(int i = 1; i <= n; i++ ) {
                //getColumnName可以获得字段名
                String fieldName = fields.getColumnName(i);
                System.out.print(fieldName + "\t");
            }
            System.out.println(tableName + "数据：");
            //如果有必要，还可以循环遍历列表结果，获取有价值信息
            int num = fields.getColumnCount();
            try {
                while (rs.next()) {
                    for (int i = 1; i <= num; i ++ ) {
                        System.out.print(rs.getObject(i) + "\t" + "\t");
                    }
                    System.out.println("");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (rs != null)
                    rs.close();
                if (stmt != null)
                    stmt.close();
            }
            System.out.println("");
            System.out.println("____________________");
        }
        connection.close();


    }

}
