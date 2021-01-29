package com.bemonovoid.playqd.datasource.jdbc.batch;

import org.springframework.jdbc.core.namedparam.SqlParameterSource;

public interface BatchInsert {

    void insert(SqlParameterSource parameterSource);

    void insertAll();
}
