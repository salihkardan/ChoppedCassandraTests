package ctest;

import com.google.inject.AbstractModule;

public class GuiceModule extends AbstractModule {
    protected void configure() {
        bind( CassandraClient.class ).to( SimpleCassandraClient.class );
    }
}