package unoeste.fipp.dentalfx.utils;

import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import unoeste.fipp.dentalfx.db.entidades.Paciente;
import unoeste.fipp.dentalfx.db.util.SingletonDB;

import java.awt.Desktop;
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

public class FichaPaciente {
    private static String val(String s)
    {
        if (s == null)
            return "Não informado";
        if (s.trim().isEmpty())
            return "Não informado";
        return s;
    }

    static public boolean gerarPDF(Paciente pac, String pdfFile){
        try
        {
            if (pac == null)
                return false;

            new File("fichas").mkdirs();

            PdfWriter writer = new PdfWriter(pdfFile);
            PdfDocument pdf = new PdfDocument(writer);
            Document doc = new Document(pdf);

            PdfFont fonteTitulo = PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD);
            PdfFont fontePadrao = PdfFontFactory.createFont(FontConstants.HELVETICA);

            Paragraph titulo = new Paragraph("Ficha do Paciente")
                    .setFont(fonteTitulo)
                    .setFontSize(26)
                    .setMarginBottom(15);
            doc.add(titulo);

            float[] colWidths = {150F, 300F};
            Table tabela = new Table(colWidths);

            tabela.addCell("ID");
            tabela.addCell(String.valueOf(pac.getId()));

            tabela.addCell("Nome");
            tabela.addCell(val(pac.getNome()));

            tabela.addCell("CPF");
            tabela.addCell(val(pac.getCpf()));

            tabela.addCell("Telefone");
            tabela.addCell(val(pac.getTelefone()));

            tabela.addCell("Email");
            tabela.addCell(val(pac.getEmail()));

            tabela.addCell("Endereço");
            String endereco = val(pac.getRua()) + ", " + val(pac.getNumero())
                    + " - " + val(pac.getBairro()) + "\n"
                    + val(pac.getCidade()) + " - " + val(pac.getUf())
                    + "  CEP: " + val(pac.getCep());
            tabela.addCell(endereco);

            doc.add(tabela);

            doc.add(new Paragraph("\nHistórico de Atendimentos Realizados")
                    .setFont(fonteTitulo)
                    .setFontSize(18)
                    .setMarginTop(20)
                    .setMarginBottom(10));

            String sql = "SELECT c.con_data, c.con_horario, d.den_nome, c.con_relato " +
                    "FROM consulta c " +
                    "JOIN dentista d ON c.den_id = d.den_id " +
                    "WHERE c.pac_id = ? AND c.con_efetivado = TRUE " +
                    "ORDER BY c.con_data DESC, c.con_horario DESC";

            System.out.println("teste de mesa: " + sql + " (ID: " + pac.getId() + ")");

            try (PreparedStatement ps = SingletonDB.getConexao().getPreparedStatement(sql))
            {

                if (ps == null)
                {
                    System.err.println("ps eh null, ou seja, conexão com o banco falhou.");
                    doc.add(new Paragraph("Erro ao consultar o banco de dados. Conexão inativa.").setFont(fontePadrao).setFontSize(12));
                } else
                {
                    ps.setInt(1, pac.getId());

                    try (ResultSet rs = ps.executeQuery()) {

                        if (!rs.isBeforeFirst()) {
                            doc.add(new Paragraph("Nenhum atendimento efetivado encontrado.").setFont(fontePadrao).setFontSize(12));
                        } else {
                            float[] colWidthsAtendimentos = {100F, 50F, 120F, 230F};
                            Table tabelaAtendimentos = new Table(colWidthsAtendimentos);

                            tabelaAtendimentos.addHeaderCell(new Paragraph("Data").setBold().setFontSize(10));
                            tabelaAtendimentos.addHeaderCell(new Paragraph("Hora").setBold().setFontSize(10));
                            tabelaAtendimentos.addHeaderCell(new Paragraph("Dentista").setBold().setFontSize(10));
                            tabelaAtendimentos.addHeaderCell(new Paragraph("Descrição do Atendimento").setBold().setFontSize(10));

                            while (rs.next()) {
                                String horaFormatada = String.format("%02d:00", rs.getInt("con_horario"));

                                tabelaAtendimentos.addCell(new Paragraph(rs.getString("con_data")).setFont(fontePadrao).setFontSize(10));
                                tabelaAtendimentos.addCell(new Paragraph(horaFormatada).setFont(fontePadrao).setFontSize(10));
                                tabelaAtendimentos.addCell(new Paragraph(rs.getString("den_nome")).setFont(fontePadrao).setFontSize(10));

                                tabelaAtendimentos.addCell(new Paragraph(val(rs.getString("con_relato"))).setFont(fontePadrao).setFontSize(10));
                            }
                            doc.add(tabelaAtendimentos);
                        }
                    }
                }
            } catch (SQLException e) {
                System.err.println("Erro SQL: " + e.getMessage());
                doc.add(new Paragraph("Erro SQL: " + e.getMessage()).setFont(fontePadrao).setFontSize(12));
            }
            doc.close();
            return true;

        } catch (Exception e) {
            System.err.println("Erro ao gerar ficha: " + e);
            return false;
        }
    }

    static public void abrirPDF(String pdfFile){
        try {
            Desktop.getDesktop().open(new File(pdfFile));
        } catch (Exception e) {
            System.err.println("Erro ao abrir PDF: " + e.getMessage());
        }
    }
}