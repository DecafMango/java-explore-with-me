package ru.practicum.ewm.user.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
public class UserShortDto {
    private Long id;
    @NotBlank
    private String name;
}
