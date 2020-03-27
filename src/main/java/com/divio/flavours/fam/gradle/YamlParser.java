package com.divio.flavours.fam.gradle;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import java.util.List;
import java.util.stream.Collectors;

public class YamlParser<AST> {
    private final ObjectMapper objectMapper;
    private final ValidatorFactory validatorFactory;
    private final Class<AST> astClass;

    public YamlParser(Class<AST> astClass) {
        this.objectMapper = new ObjectMapper(new YAMLFactory());
        this.validatorFactory = Validation.buildDefaultValidatorFactory();
        this.astClass = astClass;
    }

    public AST parse(List<String> lines) throws YamlParseException {
        var joined = String.join("\n", lines);
        AST ast;

        try {
            ast = objectMapper.readValue(joined, astClass);
        } catch (JsonProcessingException e) {
            throw new YamlParseException(e.getMessage());
        }

        var validationResult = validatorFactory.getValidator().validate(ast);

        if (validationResult.isEmpty()) {
            return ast;
        }

        var errorMessage = validationResult.stream()
                .map(cv -> cv.getPropertyPath() + " " + cv.getMessage())
                .collect(Collectors.joining("\n"));

        throw new YamlParseException(errorMessage);
    }
}
