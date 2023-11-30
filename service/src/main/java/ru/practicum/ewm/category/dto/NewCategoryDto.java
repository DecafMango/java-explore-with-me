package ru.practicum.ewm.category.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@ToString
@Getter
public class NewCategoryDto {
    @NotBlank
    @Size(min = 1, max = 50, message = "Name's length must be from 1 to 50 symbols.")
    private String name;
}
