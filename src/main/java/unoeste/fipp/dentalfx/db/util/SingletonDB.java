package unoeste.fipp.dentalfx.db.util;

import java.io.RandomAccessFile;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class SingletonDB {
    private static Conexao conexao=null;
    private SingletonDB(){
    }

    public static boolean conectar(){
        conexao=new Conexao();
        return conexao.conectar(getJdbcBaseUrl(), getDatabaseName(), getUser(), getPassword());
    }

    public static Conexao getConexao() {
        return conexao;
    }

    public static String getHost() {
        return getConfig("dentalfx.db.host", "DENTALFX_DB_HOST", "localhost");
    }

    public static String getPort() {
        return getConfig("dentalfx.db.port", "DENTALFX_DB_PORT", "5432");
    }

    public static String getDatabaseName() {
        return getConfig("dentalfx.db.name", "DENTALFX_DB_NAME", "sisdentaldb");
    }

    public static String getUser() {
        return getConfig("dentalfx.db.user", "DENTALFX_DB_USER", "postgres");
    }

    public static String getPassword() {
        return getConfig("dentalfx.db.password", "DENTALFX_DB_PASSWORD", "");
    }

    public static String getPgDumpPath() {
        return getConfig("dentalfx.pg.dump", "DENTALFX_PG_DUMP", "pg_dump");
    }

    public static String getPgRestorePath() {
        return getConfig("dentalfx.pg.restore", "DENTALFX_PG_RESTORE", "pg_restore");
    }

    private static String getJdbcBaseUrl() {
        return "jdbc:postgresql://" + getHost() + ":" + getPort() + "/";
    }

    private static String getJdbcUrl(String database) {
        return getJdbcBaseUrl() + database;
    }

    private static String getConfig(String propertyName, String envName, String defaultValue) {
        String propertyValue = System.getProperty(propertyName);
        if (propertyValue != null && !propertyValue.isBlank()) {
            return propertyValue;
        }

        String envValue = System.getenv(envName);
        if (envValue != null && !envValue.isBlank()) {
            return envValue;
        }

        return defaultValue;
    }

    public static boolean criarDatabase(String database,String usuario,String senha){
        try {
            Connection connect = DriverManager.getConnection(getJdbcBaseUrl(),usuario,senha);
            Statement statement = connect.createStatement();
            statement.execute("CREATE DATABASE "+database);
            statement.close();
            connect.close();
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }
    public static boolean criarTabelas(String script,String BD){
        try{
            Connection connection = DriverManager.getConnection(getJdbcUrl(BD), getUser(), getPassword());

            Statement statement = connection.createStatement();
            RandomAccessFile arq=new RandomAccessFile(script, "r");
            while(arq.getFilePointer() < arq.length())
                statement.addBatch(arq.readLine());
            statement.executeBatch();

            statement.close();
            connection.close();
            arq.close();
        }
        catch(Exception e){
            System.out.println(e.getMessage()); return false;}
        return true;
    }

}
