package com.quarkussocial.rest.dto;

import jakarta.validation.ConstraintViolation;
import lombok.Data;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class ResponseError {

    private String message;

    private Collection<FieldError> errors;

    public ResponseError(String message, Collection<FieldError> errors) {
        this.message = message;
        this.errors = errors;
    }

    public static <T> ResponseError createFromValidation(Set<ConstraintViolation<T>> violations) {
        List<FieldError> errors = violations.stream().
                map(v -> new FieldError(v.getPropertyPath().toString(), v.getMessage()))
                .collect(Collectors.toList());
        String message = "Validation Error";
        var responseError = new ResponseError(message, errors);
        return responseError;
    }

}
