package a.b.c.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DerbyDemo {
    public static void main(String[] args) {
        Connection connection = null;
        try {
            // 指定Derby数据库的连接URL，这里是连接到名为"mydb"的数据库
            String dbUrl = "jdbc:derby:/opt/logs/derby/db;create=true";

            // 加载Derby数据库的驱动程序
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");

            // 创建数据库连接
            connection = DriverManager.getConnection(dbUrl);



            String create="CREATE TABLE config (\n" +
                    "    id BIGINT NOT NULL,\n" +
                    "    config_key VARCHAR(2000) NOT NULL,\n" +
                    "    config_content CLOB,\n" +
                    "    PRIMARY KEY (id)\n" +
                    ")";
            connection.createStatement().execute(create);

            connection.createStatement().execute("INSERT INTO config (id, config_key, config_content) VALUES (1, 'key1', 'This is the content for key1.')");

            // 创建一个Statement对象用于执行查询
            Statement statement = connection.createStatement();

            // 创建一个SQL查询
            String sql = "SELECT * FROM config";

            // 执行查询
            ResultSet resultSet = statement.executeQuery(sql);

            // 遍历查询结果
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("config_content");
                System.out.println("ID: " + id + ", Name: " + name);
            }

            // 关闭ResultSet、Statement和Connection
            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}