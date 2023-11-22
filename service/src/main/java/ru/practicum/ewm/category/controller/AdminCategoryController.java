package ru.practicum.ewm.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.service.CategoryService;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
@Slf4j
public class AdminCategoryController {

    private final CategoryService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto postCategory(@Valid @RequestBody NewCategoryDto newCategoryDto) {
        log.info("Posting category: {}", newCategoryDto);
        return service.postCategory(newCategoryDto);
    }

    @DeleteMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long categoryId) {
        log.info("Deleting category with id={}", categoryId);
        service.deleteCategory(categoryId);
    }

    @PatchMapping("/{categoryId}")
    public CategoryDto patchCategory(@PathVariable Long categoryId, @Valid @RequestBody CategoryDto categoryDto) {
        log.info("Patching category with id={} by category: {}", categoryId, categoryDto);
        return service.patchCategory(categoryId, categoryDto);
    }

}
