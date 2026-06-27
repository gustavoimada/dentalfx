package unoeste.fipp.dentalfx.db.dals;

import unoeste.fipp.dentalfx.db.entidades.Material;
import unoeste.fipp.dentalfx.db.util.IDAL;
import unoeste.fipp.dentalfx.db.util.SingletonDB;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MaterialDAL implements IDAL<Material> {
    @Override
    public boolean gravar(Material entidade) {
        String sql="""
          INSERT INTO material (mat_desc, mat_preco) 
          VALUES ('#1',#2)""";
        sql=sql.replace("#1",entidade.getDescricao());
        sql=sql.replace("#2",String.format(Locale.US,"%.2f",entidade.getValor()));
        return SingletonDB.getConexao().manipular(sql);
    }

    @Override
    public boolean alterar(Material entidade) {
        String sql="""
          UPDATE material SET mat_desc='#1', mat_preco=#2 
          WHERE mat_id=#3""";
        sql=sql.replace("#1",entidade.getDescricao());
        sql=sql.replace("#2",String.format(Locale.US,"%.2f",entidade.getValor()));
        sql=sql.replace("#3",""+entidade.getId());
        return SingletonDB.getConexao().manipular(sql);
    }

    @Override
    public boolean apagar(Material entidade) {
        return SingletonDB.getConexao().manipular("DELETE FROM material WHERE mat_id="+entidade.getId());
    }

    @Override
    public Material get(int id) {
        Material material=null;
        String sql="SELECT * FROM material WHERE mat_id="+id;
        ResultSet resultSet=SingletonDB.getConexao().consultar(sql);
        try {
            if(resultSet.next()){
                material=new Material(resultSet.getInt("mat_id"),resultSet.getString("mat_desc"),
                        resultSet.getDouble("mat_preco"));
            }
        } catch (Exception e) {  throw new RuntimeException(e); }
        return material;
    }

    @Override
    public List<Material> get(String filtro) {
        List <Material> materialList=new ArrayList<>();
        String sql="SELECT * FROM material";
        if(!filtro.isEmpty())
            sql+=" WHERE "+filtro;
        ResultSet resultSet=SingletonDB.getConexao().consultar(sql);
        try {
            while(resultSet.next()){
                Material material=new Material(resultSet.getInt("mat_id"),resultSet.getString("mat_desc"),
                        resultSet.getDouble("mat_preco"));
                materialList.add(material);
            }
        } catch (Exception e) {  throw new RuntimeException(e); }
        return materialList;
    }
}
