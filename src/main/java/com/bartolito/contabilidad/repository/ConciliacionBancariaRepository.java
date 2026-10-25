package com.bartolito.contabilidad.repository;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ConciliacionBancariaRepository {

    @Autowired
    private JdbcTemplate sigoldJdbc;

    public List<Map<String, Object>> listarCuentasBancarias() {

        String sql =
                "EXEC sp_bart_Conciliacion_ListarCuentasBancarias";

        return sigoldJdbc.queryForList(sql);
    }

    public List<Map<String, Object>> listarAsientos(
            String cuenta,
            Integer mes,
            Integer ano) {

        String sql = "EXEC sp_bart_Conciliacion_ListarAsientos @Cuenta = ?, @Mes = ?, @Ano = ? ";

        return sigoldJdbc.queryForList(
                sql,
                cuenta,
                mes,
                ano
        );
    }
}