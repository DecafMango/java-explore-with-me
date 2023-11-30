package ru.practicum.ewm.user.mapper;

import ru.practicum.ewm.user.dto.NewUserDto;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.dto.UserShortDto;
import ru.practicum.ewm.user.model.User;

public class UserMapper {

    public static UserDto toUserDto(User user) {
        return new UserDto()
                .toBuilder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static UserShortDto toUserShortDto(User user) {
        return new UserShortDto()
                .toBuilder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }

    public static User toUser(UserDto userDto) {
        return new User()
                .toBuilder()
                .id(userDto.getId())
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }

    public static User toNewUser(NewUserDto newUserDto) {
        return new User()
                .toBuilder()
                .name(newUserDto.getName())
                .email(newUserDto.getEmail())
                .build();
    }
}
