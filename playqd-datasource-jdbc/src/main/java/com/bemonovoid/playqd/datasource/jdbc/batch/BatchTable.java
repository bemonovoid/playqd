package com.bemonovoid.playqd.datasource.jdbc.batch;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class BatchTable {

    private final String tableName;
    private final String keyColumnName;

}
