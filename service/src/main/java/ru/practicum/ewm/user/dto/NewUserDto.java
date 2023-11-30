package ru.practicum.ewm.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
@Getter
public class NewUserDto {
    @NotBlank
    @Size(min = 2, max = 250, message = "Description's length must be from 20 to 7000 symbols.")
    private String name;
    @NotBlank
    @Email
    @Size(min = 6, max = 254, message = "Description's length must be from 20 to 7000 symbols.")
    private String email;
}
