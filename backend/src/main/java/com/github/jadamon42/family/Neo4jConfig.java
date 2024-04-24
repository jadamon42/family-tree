package com.github.jadamon42.family;

import org.neo4j.configuration.connectors.BoltConnector;
import org.neo4j.cypherdsl.core.renderer.Dialect;
import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.dbms.api.DatabaseManagementServiceBuilder;
import org.neo4j.graphdb.GraphDatabaseService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.File;
import java.nio.file.Path;

import static org.neo4j.configuration.GraphDatabaseSettings.DEFAULT_DATABASE_NAME;

@Configuration
public class Neo4jConfig {
    private GraphDatabaseService graphDb;

    @Bean
    org.neo4j.cypherdsl.core.renderer.Configuration cypherDslConfiguration() {
        return  org.neo4j.cypherdsl.core.renderer.Configuration.newConfig().withDialect(Dialect.NEO4J_5).build();
    }

    @Bean
    @Profile("prod")
    public GraphDatabaseService graphDb() {
        String dbPath = System.getProperty("user.home") + File.separator + ".familytree" + File.separator + "db";
        Path dbPathFile = new File(dbPath).toPath();
        DatabaseManagementService managementService = new DatabaseManagementServiceBuilder(dbPathFile)
                .setConfig(BoltConnector.enabled, true)
                .setConfig(BoltConnector.encryption_level, BoltConnector.EncryptionLevel.DISABLED)
                                                              .build();
        graphDb = managementService.database(DEFAULT_DATABASE_NAME);
        registerShutdownHook(managementService);
        return graphDb;
    }

    private static void registerShutdownHook( final DatabaseManagementService managementService ) {
        Runtime.getRuntime().addShutdownHook(new Thread(managementService::shutdown));
    }
}
