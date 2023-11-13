package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentRequestDto {
    private long id;
    @NotNull
    private long authorId;
    @NotBlank
    @Size(max = 255)
    private String text;
    @JsonIgnore
    private LocalDateTime created = LocalDateTime.now();
}
