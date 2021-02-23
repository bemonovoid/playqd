package com.bemonovoid.playqd.datasource.jdbc.batch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

public class BatchOperation {

    private final JdbcTemplate jdbcTemplate;
    private final Queue<InsertBatch> batches = new LinkedList<>();

    public BatchOperation(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public BatchOperation insert(InsertBatch batch) {
        batches.add(batch);
        return this;
    }

    public int execute() {
        int rowsInserted = 0;
        while (!batches.isEmpty()) {
            InsertBatch insertBatch = batches.poll();
            SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                    .withTableName(insertBatch.getBatchTable().getTableName())
                    .usingGeneratedKeyColumns(insertBatch.getBatchTable().getKeyColumnName());
            while (!insertBatch.getInserts().isEmpty()) {
                List<SqlParameterSource> insertSql = insertBatch.getInserts().pop();
                int[] rows = simpleJdbcInsert.executeBatch(insertSql.toArray(SqlParameterSource[]::new));
                rowsInserted += rows.length;
            }
        }
        return rowsInserted;
    }
}
