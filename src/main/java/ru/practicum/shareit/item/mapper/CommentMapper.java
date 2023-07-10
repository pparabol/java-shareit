package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.util.Mapper;

@Component
public class CommentMapper implements Mapper<Comment, CommentDto> {
    @Override
    public Comment toEntity(CommentDto dto) {
        return new Comment(
                dto.getId(),
                dto.getText()
        );
    }

    @Override
    public CommentDto toDto(Comment entity) {
        return new CommentDto(
                entity.getId(),
                entity.getText(),
                entity.getAuthor().getName(),
                entity.getCreated()
        );
    }
}
