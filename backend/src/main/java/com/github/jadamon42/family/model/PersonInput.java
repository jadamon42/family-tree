package com.github.jadamon42.family.model;

import org.springframework.graphql.data.ArgumentValue;

import java.time.LocalDate;

public class PersonInput {
    ArgumentValue<String> firstName;
    ArgumentValue<String> lastName;
    ArgumentValue<Sex> sex;
    ArgumentValue<LocalDate> birthDate;
    ArgumentValue<LocalDate> deathDate;
}
