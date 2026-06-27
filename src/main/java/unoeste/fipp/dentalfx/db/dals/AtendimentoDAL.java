package unoeste.fipp.dentalfx.db.dals;

import unoeste.fipp.dentalfx.db.entidades.*;
import unoeste.fipp.dentalfx.db.entidades.Atendimento.MatItem;
import unoeste.fipp.dentalfx.db.entidades.Atendimento.ProcItem;
import unoeste.fipp.dentalfx.db.util.SingletonDB;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AtendimentoDAL {

    private final MaterialDAL dalMat = new MaterialDAL();
    private final ProcedimentoDAL dalProc = new ProcedimentoDAL();

    public boolean gravar(Atendimento atendimento) {
        boolean sucesso = true;

        int conId = atendimento.getId();

        if (conId <= 0) {
            System.out.println("Erro: Atendimento sem ID (conId). Não é possível gravar.");
            return false;
        }

        String sqlUpdateConsulta = "UPDATE consulta SET con_relato = ?, con_efetivado = TRUE WHERE con_id = ?";
        try (PreparedStatement ps = SingletonDB.getConexao().getPreparedStatement(sqlUpdateConsulta)) {
            ps.setString(1, atendimento.getRelato());
            ps.setInt(2, conId);
            if (ps.executeUpdate() <= 0) {
                System.out.println("Erro ao atualizar relato e status da consulta.");
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar relato e status da consulta: " + e.getMessage());
            return false;
        }

        String sqlDeleteMaterial = "DELETE FROM cons_mat WHERE con_id = ?";
        String sqlInsertMaterial = "INSERT INTO cons_mat (con_id, mat_id, cm_quant) VALUES (?, ?, ?)";

        try (PreparedStatement psDelete = SingletonDB.getConexao().getPreparedStatement(sqlDeleteMaterial)) {
            psDelete.setInt(1, conId);
            psDelete.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erro ao deletar materiais antigos: " + e.getMessage());
            sucesso = false;
        }

        if (sucesso) {
            try (PreparedStatement psInsert = SingletonDB.getConexao().getPreparedStatement(sqlInsertMaterial)) {
                for (MatItem item : atendimento.getMaterialList()) {
                    psInsert.setInt(1, conId);
                    psInsert.setInt(2, item.material().getId());
                    psInsert.setInt(3, item.quant());
                    psInsert.addBatch();
                }
                psInsert.executeBatch();
            } catch (SQLException e) {
                System.out.println("Erro ao inserir novos materiais: " + e.getMessage());
                sucesso = false;
            }
        }

        String sqlDeleteProcedimento = "DELETE FROM cons_proc WHERE con_id = ?";
        String sqlInsertProcedimento = "INSERT INTO cons_proc (con_id, pro_id) VALUES (?, ?)";

        try (PreparedStatement psDelete = SingletonDB.getConexao().getPreparedStatement(sqlDeleteProcedimento)) {
            psDelete.setInt(1, conId);
            psDelete.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erro ao deletar procedimentos antigos: " + e.getMessage());
            sucesso = false;
        }

        if (sucesso) {
            try (PreparedStatement psInsert = SingletonDB.getConexao().getPreparedStatement(sqlInsertProcedimento)) {
                for (ProcItem item : atendimento.getProcedimentoList()) {
                    psInsert.setInt(1, conId);
                    psInsert.setInt(2, item.procedimento().getId());
                    psInsert.addBatch();
                }
                psInsert.executeBatch();
            } catch (SQLException e) {
                System.out.println("Erro ao inserir novos procedimentos: " + e.getMessage());
                sucesso = false;
            }
        }

        return sucesso;
    }

    public Atendimento buscarPorAgenda(int conId) {
        String relato = null;
        boolean efetivado = false;

        String sqlAtendimento = "SELECT con_relato, con_efetivado FROM consulta WHERE con_id = ?";
        try (PreparedStatement ps = SingletonDB.getConexao().getPreparedStatement(sqlAtendimento)) {
            ps.setInt(1, conId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                relato = rs.getString("con_relato");
                efetivado = rs.getBoolean("con_efetivado");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar relato da consulta: " + e.getMessage());
            return null;
        }

        if (!efetivado || relato == null || relato.trim().isEmpty()) {
            return null;
        }

        Atendimento atendimento = new Atendimento();
        atendimento.setId(conId);
        atendimento.setRelato(relato);

        String sqlMateriais = "SELECT cm.cm_quant, cm.mat_id FROM cons_mat cm WHERE cm.con_id = ?";
        List<MatItem> materiais = new ArrayList<>();

        try (PreparedStatement ps = SingletonDB.getConexao().getPreparedStatement(sqlMateriais)) {
            ps.setInt(1, conId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Material m = dalMat.get(rs.getInt("mat_id"));
                materiais.add(new MatItem(m, rs.getInt("cm_quant")));
            }
            atendimento.setMaterialList(materiais);
        } catch (SQLException e) {
            System.out.println("Erro ao carregar materiais do atendimento: " + e.getMessage());
        }

        String sqlProcedimentos = "SELECT cp.pro_id FROM cons_proc cp WHERE cp.con_id = ?";
        List<ProcItem> procedimentos = new ArrayList<>();

        try (PreparedStatement ps = SingletonDB.getConexao().getPreparedStatement(sqlProcedimentos)) {
            ps.setInt(1, conId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Procedimento p = dalProc.get(rs.getInt("pro_id"));
                procedimentos.add(new ProcItem(p, 1));
            }
            atendimento.setProcedimentoList(procedimentos);
        } catch (SQLException e) {
            System.out.println("Erro ao carregar procedimentos do atendimento: " + e.getMessage());
        }

        return atendimento;
    }

    public boolean existeAtendimento(int conId) {
        String sql = "SELECT con_efetivado, con_relato FROM consulta WHERE con_id = ?";
        try (PreparedStatement ps = SingletonDB.getConexao().getPreparedStatement(sql)) {
            ps.setInt(1, conId);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getBoolean("con_efetivado") &&
                    rs.getString("con_relato") != null && !rs.getString("con_relato").trim().isEmpty();
        } catch (SQLException e) {
            System.out.println("Erro ao verificar Atendimento: " + e.getMessage());
            return false;
        }
    }
}