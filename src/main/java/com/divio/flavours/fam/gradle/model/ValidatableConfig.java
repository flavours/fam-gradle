package com.divio.flavours.fam.gradle.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.validation.ConstraintViolation;
import java.util.Set;

public interface ValidatableConfig<AST> {
    @JsonIgnore
    Set<ConstraintViolation<AST>> validate();
}
