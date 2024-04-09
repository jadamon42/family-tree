package com.github.jadamon42.family;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(Neo4jConfig.class)
public class FamilyTreeApplication {

    public static void main(String[] args) {
        SpringApplication.run(FamilyTreeApplication.class, args);
    }
}
