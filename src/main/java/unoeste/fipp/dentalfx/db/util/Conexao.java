package unoeste.fipp.dentalfx.db.util;

import java.sql.*;

public class Conexao {
    private Connection connect;
    private String erro;

    public Conexao() {
        erro = "";
        connect = null;
    }

    public boolean conectar(String local, String banco, String usuario, String senha) {
        boolean conectado = false;
        try {
            String url = local + banco;
            connect = DriverManager.getConnection(url, usuario, senha);
            conectado = true;
        } catch (SQLException sqlex) {
            erro = "Impossivel conectar com a base de dados: " + sqlex.toString();
        } catch (Exception ex) {
            erro = "Outro erro: " + ex.toString();
        }
        return conectado;
    }

    public PreparedStatement getPreparedStatement(String sql) {
        try {
            return connect.prepareStatement(sql);
        } catch (SQLException e) {
            System.out.println("Erro ao preparar statement: " + e.getMessage());
            return null;
        }
    }

    public String getMensagemErro() {
        return erro;
    }

    public boolean getEstadoConexao() {
        return (connect != null);
    }

    @Deprecated
    public boolean manipular(String sql) {
        try (Statement statement = connect.createStatement()) {
            return statement.executeUpdate(sql) >= 1;
        } catch (SQLException sqlex) {
            erro = "Erro: " + sqlex.toString();
            return false;
        }
    }

    @Deprecated
    public ResultSet consultar(String sql) {
        ResultSet rs = null;
        try {
            Statement statement = connect.createStatement();
            rs = statement.executeQuery(sql);
        } catch (SQLException sqlex) {
            erro = "Erro: " + sqlex.toString();
            rs = null;
        }
        return rs;
    }

    public int getMaxPK(String tabela, String chave) {
        String sql = "select max(" + chave + ") from " + tabela;
        int max = 0;
        try (ResultSet rs = this.consultar(sql)) {
            if (rs.next())
                max = rs.getInt(1);
        } catch (SQLException sqlex) {
            erro = "Erro: " + sqlex.toString();
            max = -1;
        }
        return max;
    }
}