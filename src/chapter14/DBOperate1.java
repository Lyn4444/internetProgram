package chapter14;

import java.sql.*;

/**
 * @ProjectName internetProgram
 * @ClassName DBOperate1
 * @Description TODO
 * @Author Lyn
 * @Date 2020/12/7 15:04
 * @Version 1.0
 * @Function
 */

public class DBOperate1 {

    public static void main(String[] args) throws ClassNotFoundException, SQLException, IllegalAccessException, InstantiationException {
        //指定数据库所在位置，先用本地地址测试，访问本地的数据库
        String dbUrl = "jdbc:mysql://202.116.195.71:3306/STUDENTDB1?serverTimezome=Hongkong";
        //指定用户名和密码
        String dbUser="student";
        String dbPwd="student";

        Class jdbcDriver = Class.forName("com.mysql.jdbc.Driver");
        //注册MySQL驱动器
        java.sql.DriverManager.registerDriver((Driver)jdbcDriver.newInstance());

        Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPwd);

        //创建sql查询语句
        String sql="select NO,NAME,AGE,CLASS from students where id like ? ";

        //创建数据库执行对象
        PreparedStatement stmt = connection.prepareStatement(sql);
        //设置sql语句参数
        stmt.setObject(1, "2%");

//        从数据库的返回集合中读出数据
        ResultSet rs = stmt.executeQuery();


        //设置插入记录的sql语句(如何避免重复插入学号相同的信息？)
//        String sql = "insert into peoples2(NO,NAME,AGE,CLASS,IP) values(?,?,?,?,?)";
//        PreparedStatement stmt = connection.prepareStatement(sql);
//        stmt = connection.prepareStatement(sql);
//        stmt.setObject(1,"20181002837");
//        stmt.setObject(2,"罗杰鸿");
//        stmt.setObject(3,"22");
//        stmt.setObject(4,"软件工程1803");
//        stmt.setObject(5, "10.173.42.39");
//
//        stmt.executeUpdate();
//        //查询是否插入数据成功
//        sql = "select NO,NAME,AGE,CLASS from STUDENTS ";
//        stmt = connection.prepareStatement(sql);
//        ResultSet rs = stmt.executeQuery();


        try {
            //循环遍历结果
            while (rs.next())
            {
                //不知道字段类型的情况下，也可以用rs.getObject(…)来打印输出结果
                System.out.print(rs.getString(1)+"\t");
                System.out.print(rs.getString(2)+"\t");
                System.out.print(rs.getInt(3)+"\t");
                System.out.print(rs.getString(4)+"\n");
            }
            System.out.println("------------------------------------");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null)
                rs.close();
            if (stmt != null)
                stmt.close();
            if (connection != null)
                connection.close();

        }
    }

}
