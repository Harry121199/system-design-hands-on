package com.systemdesign.cachedapi.config;

import org.hibernate.resource.jdbc.spi.StatementInspector;


public class QueryCounterInterceptor implements StatementInspector {
    @Override
    public String inspect(String sql) {
        QueryCounter.increment();
        return sql;
    }
}
