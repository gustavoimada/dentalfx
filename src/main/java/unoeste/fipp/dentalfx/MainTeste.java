package unoeste.fipp.dentalfx;

import unoeste.fipp.dentalfx.db.dals.MaterialDAL;
import unoeste.fipp.dentalfx.db.dals.PessoaDAL;
import unoeste.fipp.dentalfx.db.entidades.Material;
import unoeste.fipp.dentalfx.db.entidades.Paciente;
import unoeste.fipp.dentalfx.db.entidades.Pessoa;
import unoeste.fipp.dentalfx.db.util.SingletonDB;

import java.util.List;

public class MainTeste {
    public static void main(String[] args) {
        SingletonDB.conectar();
//        MaterialDAL dal=new MaterialDAL();
//        Material novo=new Material("ampola anestésica",18.25);
//        dal.gravar(novo);

//        Material materialAlterado=dal.get(3);
//        materialAlterado.setDescricao("Gase");
//        dal.alterar(materialAlterado);

//        if(!dal.apagar(materialAlterado))
//            System.out.println("Erro: "+SingletonDB.getConexao().getMensagemErro());

//        List<Material> materialList = dal.get("");
//        materialList.forEach(m-> System.out.println(m.getDescricao()));
        PessoaDAL dal = new PessoaDAL();
        List<Pessoa> pacienteList = dal.get("", new Paciente());
        for(Pessoa p : pacienteList)
            System.out.println(p.getNome());
    }
}
