package bookstore.service.impl;

import bookstore.dto.CategoryDto;
import bookstore.dto.CreateCategoryDto;
import bookstore.exception.EntityNotFoundException;
import bookstore.mapper.CategoryMapper;
import bookstore.model.Category;
import bookstore.repository.CategoryRepository;
import bookstore.service.CategoryService;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository,
            CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public List<CategoryDto> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable).stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    @Override
    public CategoryDto getById(Long id) {
        return categoryMapper.toDto(categoryRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find category by id: " + id)));
    }

    @Override
    public CategoryDto save(CreateCategoryDto categoryDto) {
        return categoryMapper.toDto(categoryRepository.save(categoryMapper.toModel(categoryDto)));
    }

    @Override
    public CategoryDto update(Long id, CreateCategoryDto categoryDto) {
        categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Can't find category by id: " + id));
        Category category = categoryMapper.toModel(categoryDto);
        category.setId(id);
        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }
}
