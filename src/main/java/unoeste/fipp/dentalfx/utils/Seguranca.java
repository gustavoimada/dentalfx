package unoeste.fipp.dentalfx.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import unoeste.fipp.dentalfx.db.util.SingletonDB;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Seguranca {
    public static boolean backup(String arquivo, String database) throws Exception
    {
        final ArrayList<String> comandos = new ArrayList();
        comandos.add(SingletonDB.getPgDumpPath()); comandos.add("--host");
        comandos.add(SingletonDB.getHost()); //ou  comandos.add("192.168.0.1");
        comandos.add("--port"); comandos.add(SingletonDB.getPort()); comandos.add("--username");
        comandos.add(SingletonDB.getUser());comandos.add("--format");comandos.add("custom");
        comandos.add("--blobs");comandos.add("--verbose");comandos.add("--file");
        //comandos.add("bdutil/"+arquivo); tirou isso qndo colocou pro usuario escoler o arquivo
        comandos.add(arquivo);
        comandos.add(database);
        ProcessBuilder pb = new ProcessBuilder(comandos);
        pb.environment().put("PGPASSWORD", SingletonDB.getPassword());
        String lines="";
        try {
            final Process process = pb.start();
            final BufferedReader r = new BufferedReader(
                    new InputStreamReader(process.getErrorStream()));
            String line = r.readLine();
            while (line != null) {
                lines += line+"\n";
                System.err.println(line); line = r.readLine();
            }
            r.close();
            process.waitFor();
            process.destroy();
            //JOptionPane.showMessageDialog(null,"Backup realizado com sucesso!");

            TextArea textArea = new TextArea(lines);
            textArea.setEditable(false); // Make it read-only
            textArea.setWrapText(true); // Enable text wrapping

            ScrollPane scrollPane = new ScrollPane(textArea);
            scrollPane.setFitToWidth(true);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Backup realizado com sucesso!");
            alert.getDialogPane().setExpandableContent(scrollPane);
            alert.showAndWait();
            return true;
        } catch (Exception e) {
            // bota um alert aq tbm
            JOptionPane.showMessageDialog(null,"Erro na realização do backup.");
            return false;
        }
    }

    public static boolean restaurar(String arquivo, String database) throws Exception
    {
        final ArrayList<String> comandos = new ArrayList();
        comandos.add(SingletonDB.getPgRestorePath()); comandos.add("-c");
        comandos.add("--host"); comandos.add(SingletonDB.getHost());
        comandos.add("--port"); comandos.add(SingletonDB.getPort());
        comandos.add("--username"); comandos.add(SingletonDB.getUser());
        comandos.add("--dbname"); comandos.add(database);
        comandos.add("--verbose");
        //comandos.add("bdutil/"+arquivo);
        comandos.add(arquivo);
        ProcessBuilder pb = new ProcessBuilder(comandos);
        pb.environment().put("PGPASSWORD", SingletonDB.getPassword());
        String lines="";
        try {
            final Process process = pb.start();
            final BufferedReader r = new BufferedReader(
                    new InputStreamReader(process.getErrorStream()));
            String line = r.readLine();
            while (line != null) {
                lines += line+"\n";
                System.err.println(line); line = r.readLine();
            }
            r.close();
            process.waitFor();
            process.destroy();
            //JOptionPane.showMessageDialog(null,"Restauração com sucesso!");
            TextArea textArea = new TextArea(lines);
            textArea.setEditable(false); // Make it read-only
            textArea.setWrapText(true); // Enable text wrapping

            ScrollPane scrollPane = new ScrollPane(textArea);
            scrollPane.setFitToWidth(true);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("O banco de dados foi restaurado!");
            alert.getDialogPane().setExpandableContent(scrollPane);
            alert.showAndWait();
            return true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,"Erro na restauração.");
            return false;
        }
    }
}
