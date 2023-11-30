package ru.practicum.ewm.category.mapper;

import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.model.Category;

public class CategoryMapper {

    public static CategoryDto toCategoryDto(Category category) {
        return new CategoryDto()
                .toBuilder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public static Category toCategory(CategoryDto categoryDto) {
        return new Category()
                .toBuilder()
                .id(categoryDto.getId())
                .name(categoryDto.getName())
                .build();
    }

    public static Category toNewCategory(NewCategoryDto newCategoryDto) {
        return new Category()
                .toBuilder()
                .name(newCategoryDto.getName())
                .build();
    }
}
