package com.bemonovoid.playqd.datasource.jdbc.batch;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import lombok.Getter;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

public class InsertBatch {

    private final int batchSize;

    @Getter
    private final BatchTable batchTable;

    @Getter
    private final Stack<List<SqlParameterSource>> inserts;

    public InsertBatch(int batchSize, BatchTable batchTable) {
        this.batchSize = batchSize;
        this.batchTable = batchTable;
        this.inserts = new Stack<>();
    }

    public void append(SqlParameterSource sqlParameterSource) {
        if (inserts.isEmpty() || inserts.peek().size() == batchSize) {
            inserts.push(new LinkedList<>());
        }
        inserts.peek().add(sqlParameterSource);
    }
}
