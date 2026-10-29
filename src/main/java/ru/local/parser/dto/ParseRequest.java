package ru.local.parser.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.util.List;

@Data
public class ParseRequest {
    @NotEmpty
    private List<@NotBlank String> urls;
}