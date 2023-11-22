package ru.practicum.ewm.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.Pagination;
import ru.practicum.ewm.category.dao.CategoryRepository;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.exception.exceptions.ObjectNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository repository;

    public List<CategoryDto> getCategories(Integer from, Integer size) {
        Pageable page = Pagination.createPageTemplate(from, size);
        return repository.findAllBy(page)
                .stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    public CategoryDto getCategory(Long categoryId) {
        return CategoryMapper.toCategoryDto(repository.findById(categoryId)
                .orElseThrow(() -> new ObjectNotFoundException("Category with id=" + categoryId + " doesn't exist")));
    }

    @Transactional
    public CategoryDto postCategory(NewCategoryDto newCategoryDto) {
        return CategoryMapper.toCategoryDto(repository.save(CategoryMapper.toNewCategory(newCategoryDto)));
    }

    @Transactional
    public void deleteCategory(Long categoryId) {
        try {
            repository.deleteById(categoryId);
        } catch (EmptyResultDataAccessException e) {
            throw new ObjectNotFoundException("Category with id=" + categoryId + " doesn't exist");
        }
    }

    @Transactional
    public CategoryDto patchCategory(Long categoryId, CategoryDto categoryDto) {
        Category currentCategory = repository.findById(categoryId)
                .orElseThrow(() -> new ObjectNotFoundException("Category with id=" + categoryId + " doesn't exist"));
        currentCategory.setName(categoryDto.getName());
        return CategoryMapper.toCategoryDto(repository.save(currentCategory));
    }

}
