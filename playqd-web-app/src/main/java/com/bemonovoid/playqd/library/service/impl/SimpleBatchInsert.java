package com.bemonovoid.playqd.library.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

public class SimpleBatchInsert implements BatchInsert {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleBatchInsert.class);

    private final int maxBatchSize;
    private final List<SqlParameterSource> batch;
    private final SimpleJdbcInsert simpleJdbcInsert;

    public SimpleBatchInsert(JdbcTemplate jdbcTemplate, int batchSize, String targetTable) {
        this(jdbcTemplate, batchSize, targetTable, null);
    }

    public SimpleBatchInsert(JdbcTemplate jdbcTemplate, int batchSize, String targetTable, String generatedKeyColumn) {
        this.maxBatchSize = batchSize;
        this.batch = new ArrayList<>(maxBatchSize);
        this.simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName(targetTable);
        if (generatedKeyColumn != null) {
            this.simpleJdbcInsert.usingGeneratedKeyColumns(generatedKeyColumn);
        }
    }

    @Override
    public void insertAll() {
        insert(null, true);
    }

    @Override
    public void insert(SqlParameterSource parameterSource) {
        insert(parameterSource, false);
    }

    private void insert(SqlParameterSource parameterSource, boolean insertAll) {
        if (insertAll || batch.size() == this.maxBatchSize) {
            simpleJdbcInsert.executeBatch(batch.toArray(SqlParameterSource[]::new));
            LOG.info("Batch insert of {} items completed", batch.size());
            batch.clear();
        } else {
            batch.add(parameterSource);
        }
    }
}
