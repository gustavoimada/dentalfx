package unoeste.fipp.dentalfx.db.dals;

import unoeste.fipp.dentalfx.db.entidades.*;
import unoeste.fipp.dentalfx.db.util.IDAL;
import unoeste.fipp.dentalfx.db.util.SingletonDB;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PessoaDAL implements IDAL<Pessoa> {

    @Override
    public boolean gravar(Pessoa entidade) {
        try (PreparedStatement ps =
                     SingletonDB.getConexao().getPreparedStatement(getSqlInsert(entidade))) {

            setParameters(ps, entidade);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Erro ao gravar: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean alterar(Pessoa entidade) {
        String sql = getSqlUpdate(entidade);
        int id = entidade.getId();
        int paramCount = getParameterCount(entidade);

        try (PreparedStatement ps =
                     SingletonDB.getConexao().getPreparedStatement(sql)) {

            setParameters(ps, entidade);

            ps.setInt(paramCount + 1, id);

            int linhasAfetadas = ps.executeUpdate();

            System.out.println("Linhas afetadas: " + linhasAfetadas);

            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.out.println("Erro ao alterar: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean apagar(Pessoa entidade) {
        String sql = "";

        if (entidade instanceof Paciente)
            sql = "DELETE FROM paciente WHERE pac_id = ?";
        else if (entidade instanceof Dentista)
            sql = "DELETE FROM dentista WHERE den_id = ?";
        else if (entidade instanceof Usuario)
            sql = "DELETE FROM usuario WHERE uso_id = ?";

        try (PreparedStatement ps =
                     SingletonDB.getConexao().getPreparedStatement(sql)) {

            ps.setInt(1, entidade.getId());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Erro ao apagar: " + e.getMessage());
            return false;
        }
    }

    private String getSqlInsert(Pessoa entidade) {

        if (entidade instanceof Paciente)
            return "INSERT INTO paciente(" +
                    "pac_nome, pac_cpf, pac_email, pac_fone, pac_cep, pac_rua, pac_bairro, pac_cidade, pac_uf, pac_numero, pac_histo" +
                    ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        if (entidade instanceof Dentista)
            return "INSERT INTO dentista(den_nome, den_cro, den_fone, den_email) VALUES (?, ?, ?, ?)";

        if (entidade instanceof Usuario)
            return "INSERT INTO usuario(uso_nome, uso_nivel, uso_senha) VALUES (?, ?, ?)";

        return "";
    }

    private String getSqlUpdate(Pessoa entidade) {

        if (entidade instanceof Paciente)
            return "UPDATE paciente SET " +
                    "pac_nome=?, pac_cpf=?, pac_email=?, pac_fone=?, pac_cep=?, pac_rua=?, pac_bairro=?, pac_cidade=?, pac_uf=?, pac_numero=?, pac_histo=? " +
                    "WHERE pac_id=?";

        if (entidade instanceof Dentista)
            return "UPDATE dentista SET den_nome=?, den_cro=?, den_fone=?, den_email=? WHERE den_id=?";

        if (entidade instanceof Usuario)
            return "UPDATE usuario SET uso_nome=?, uso_nivel=?, uso_senha=? WHERE uso_id=?";

        return "";
    }

    private void setParameters(PreparedStatement ps, Pessoa entidade) throws SQLException {

        if (entidade instanceof Paciente p) {
            ps.setString(1, p.getNome());
            ps.setString(2, p.getCpf());
            ps.setString(3, p.getEmail());
            ps.setString(4, p.getTelefone());
            ps.setString(5, p.getCep());
            ps.setString(6, p.getRua());
            ps.setString(7, p.getBairro());
            ps.setString(8, p.getCidade());
            ps.setString(9, p.getUf());
            ps.setString(10, p.getNumero());
            ps.setString(11, p.getHistorico());

        } else if (entidade instanceof Dentista d) {
            ps.setString(1, d.getNome());
            ps.setDouble(2, d.getCro());
            ps.setString(3, d.getFone());
            ps.setString(4, d.getEmail());

        } else if (entidade instanceof Usuario u) {
            ps.setString(1, u.getNome());
            ps.setDouble(2, u.getNivel());
            ps.setString(3, u.getSenha());
        }
    }

    private int getParameterCount(Pessoa entidade) {
        if (entidade instanceof Paciente) return 11;
        if (entidade instanceof Dentista) return 4;
        if (entidade instanceof Usuario) return 3;
        return 0;
    }

    @Override
    public Pessoa get(int id) {
        return null;
    }

    @Override
    public List<Pessoa> get(String filtro) {
        return List.of();
    }

    public List<Pessoa> get(String filtro, Pessoa tipo) {

        List<Pessoa> pessoas = new ArrayList<>();
        String sqlSelect = "";
        String campoID = "";
        String campoNome = "";

        if (tipo instanceof Paciente) {
            sqlSelect = "SELECT * FROM paciente";
            campoID = "pac_id";
            campoNome = "pac_nome";
        } else if (tipo instanceof Dentista) {
            sqlSelect = "SELECT * FROM dentista";
            campoID = "den_id";
            campoNome = "den_nome";
        } else if (tipo instanceof Usuario) {
            sqlSelect = "SELECT * FROM usuario";
            campoID = "uso_id";
            campoNome = "uso_nome";
        }

        String sql = sqlSelect;

        if (!filtro.isEmpty()) {
            try {
                int id = Integer.parseInt(filtro);
                sql += " WHERE " + campoID + " = " + id;
            } catch (NumberFormatException e) {
                sql += " WHERE " + campoNome + " ILIKE '%" + filtro + "%'";
            }
        }

        try (ResultSet rs = SingletonDB.getConexao().consultar(sql)) {

            while (rs != null && rs.next()) {

                if (tipo instanceof Paciente) {
                    pessoas.add(new Paciente(
                            rs.getInt("pac_id"),
                            rs.getString("pac_nome"),
                            rs.getString("pac_cpf"),
                            rs.getString("pac_cep"),
                            rs.getString("pac_cidade"),
                            rs.getString("pac_bairro"),
                            rs.getString("pac_numero"),
                            rs.getString("pac_rua"),
                            rs.getString("pac_uf"),
                            rs.getString("pac_email"),
                            rs.getString("pac_fone"),
                            rs.getString("pac_histo")
                    ));

                } else if (tipo instanceof Dentista) {
                    pessoas.add(new Dentista(
                            rs.getInt("den_id"),
                            rs.getString("den_nome"),
                            rs.getString("den_email"),
                            rs.getString("den_fone"),
                            rs.getDouble("den_cro")
                    ));

                } else if (tipo instanceof Usuario) {
                    pessoas.add(new Usuario(
                            rs.getInt("uso_id"),
                            rs.getString("uso_nome"),
                            rs.getDouble("uso_nivel"),
                            rs.getString("uso_senha")
                    ));
                }
            }

        } catch (SQLException ex) {
            throw new RuntimeException("Erro ao consultar PessoaDAL: " + ex.getMessage() + "\nSQL com Erro: " + sql, ex);
        }

        return pessoas;
    }

    public Dentista getDentistaPorNomeUsuario(String nomeUsuario) {
        String sql = "SELECT * FROM dentista WHERE den_nome = ?";

        try (PreparedStatement ps = SingletonDB.getConexao().getPreparedStatement(sql)) {
            ps.setString(1, nomeUsuario);
            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return new Dentista(
                            rs.getInt("den_id"),
                            rs.getString("den_nome"),
                            rs.getString("den_email"),
                            rs.getString("den_fone"),
                            rs.getDouble("den_cro")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar dentista por nome de usuário: " + e.getMessage());
        }
        return null;
    }
}