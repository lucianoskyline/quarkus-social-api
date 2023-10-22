package com.quarkussocial.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateUserRequest {

    @NotBlank(message = "Informe o nome")
    private String name;

    @NotNull(message = "Informe a idade")
    private Integer age;

}
