package com.divio.flavours.fam.gradle;

import com.divio.flavours.fam.gradle.model.Addon;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.validation.ConstraintViolation;
import javax.validation.ValidatorFactory;
import java.util.List;
import java.util.stream.Collectors;

public class AddonParser {
    private final ObjectMapper objectMapper;
    private final ValidatorFactory validatorFactory;

    public AddonParser(final ObjectMapper objectMapper, final ValidatorFactory validatorFactory) {
        this.objectMapper = objectMapper;
        this.validatorFactory = validatorFactory;
    }

    public Addon parse(List<String> lines) throws AddonParseException {
        var joined = String.join("\n", lines);
        Addon addon;

        try {
            addon = objectMapper.readValue(joined, Addon.class);
        } catch (JsonProcessingException e) {
            throw new AddonParseException(e.getMessage());
        }

        var validationResult = validatorFactory.getValidator().validate(addon);

        if (validationResult.isEmpty()) {
            return addon;
        }

        var errorMessage = validationResult.stream().map(cv -> cv.getPropertyPath() + " " + cv.getMessage()).collect(Collectors.joining("\n"));

        throw new AddonParseException(errorMessage);
    }
}
