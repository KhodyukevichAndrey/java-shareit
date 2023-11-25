package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
public class UserDto {
    private long id;
    @NotBlank(groups = {Create.class})
    @Size(max = 50, groups = {Create.class, Update.class})
    private String name;
    @NotEmpty(groups = {Create.class})
    @Email(groups = {Create.class, Update.class})
    @Size(max = 100, groups = {Create.class, Update.class})
    private String email;

    public interface Create {
    }

    public interface Update {
    }
}
