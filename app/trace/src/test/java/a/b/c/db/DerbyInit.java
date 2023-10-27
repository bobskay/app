package a.b.c.db;

import cn.hutool.core.io.FileUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

@Slf4j
public class DerbyInit {
    static final String SQL="/Users/rokey/Documents/bob/app/app/trace/document/sql/derby.sql";
    static final String DB_FILE="/Users/rokey/Documents/bob/app/db";
    static final String SHOW_TABLES="SELECT * FROM SYS.SYSTABLES WHERE TABLETYPE = 'T'";
    static final String SHOW_COLUMNS="SELECT * FROM SYS.SYSCOLUMNS";
    public static void main(String[] args) throws ClassNotFoundException {
        String txt=FileUtil.readString(SQL,"UTF-8");
        String[] sqls=txt.split(";");
        //run(sqls);

       // query(SHOW_TABLES);

        String createTable=showCreateTable("CONFIG");
        System.out.println(createTable);
        query(createTable);
    }

    @SneakyThrows
    private static void query(String sql){
        // 指定Derby数据库的连接URL，这里是连接到名为"mydb"的数据库
        String dbUrl = "jdbc:derby:"+DB_FILE+";create=true";
        // 加载Derby数据库的驱动程序
        Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        // 创建数据库连接
        try(Connection connection = DriverManager.getConnection(dbUrl)){
            try(ResultSet rs= connection.createStatement().executeQuery(sql)){
                for(int i=0;i<rs.getMetaData().getColumnCount();i++){
                    System.out.print(rs.getMetaData().getColumnLabel(i+1));
                    System.out.print("   ");
                }
                System.out.println("");
                while (rs.next()){
                    for(int i=0;i<rs.getMetaData().getColumnCount();i++){
                        System.out.print(rs.getString(i+1));
                        System.out.print("   ");
                    }
                    System.out.println("");
                }

            }
        }
    }

    @SneakyThrows
    private static void run(String[] sqls){
        // 指定Derby数据库的连接URL，这里是连接到名为"mydb"的数据库
        String dbUrl = "jdbc:derby:"+DB_FILE+";create=true";
        // 加载Derby数据库的驱动程序
        Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        // 创建数据库连接
        try(Connection connection = DriverManager.getConnection(dbUrl)){
            for(String sql:sqls){
               try{
                   boolean result=connection.createStatement().execute(sql);
                   log.info("sql执行结果"+result+"："+sql);
               }catch (Exception ex){
                  log.info("执行sql出错："+sql,ex);
               }
            }
        }
    }

    private static String showCreateTable(String tableName){
        return "select * from SYS.SYSCOLUMNS where REFERENCEID = (SELECT TABLEID FROM SYS.SYSTABLES WHERE TABLENAME = '"+tableName+"')";
    }
}
