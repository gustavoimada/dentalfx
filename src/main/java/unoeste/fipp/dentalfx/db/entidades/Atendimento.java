package unoeste.fipp.dentalfx.db.entidades;

import java.util.ArrayList;
import java.util.List;

public class Atendimento {

    public static record MatItem(Material material, int quant){};
    public static record ProcItem(Procedimento procedimento, int quant){};

    private int id;
    private Agenda agenda;
    private String relato;

    private List<MatItem> materialList;
    private List<ProcItem> procedimentoList;

    public Atendimento() {
        materialList = new ArrayList<>();
        procedimentoList = new ArrayList<>();
    }

    public Atendimento(String relato) {
        this();
        this.relato = relato;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Agenda getAgenda() { return agenda; }
    public void setAgenda(Agenda agenda) {
        this.agenda = agenda;
    }

    public String getRelato() { return relato; }
    public void setRelato(String relato) { this.relato = relato; }

    public boolean addMaterial(int quantidade, Material material){
        return materialList.add(new MatItem(material,quantidade));
    }
    public boolean addProcedimento(Procedimento procedimento){
        return procedimentoList.add(new ProcItem(procedimento, 1));
    }

    public List<MatItem> getMaterialList() { return materialList; }
    public List<ProcItem> getProcedimentoList() { return procedimentoList; }

    public void setMaterialList(List<MatItem> materialList) { this.materialList = materialList; }
    public void setProcedimentoList(List<ProcItem> procedimentoList) { this.procedimentoList = procedimentoList; }
}