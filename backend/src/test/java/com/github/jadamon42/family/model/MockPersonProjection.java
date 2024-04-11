package com.github.jadamon42.family.model;

import java.util.List;

public class MockPersonProjection implements PersonProjection {
    private final String id;
    private final String firstName;
    private final String lastName;
    private final List<Partnership> partnerships;

    public MockPersonProjection(String id, String firstName, String lastName, List<Partnership> partnerships) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.partnerships = partnerships;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public List<Partnership> getPartnerships() {
        return partnerships;
    }
}
