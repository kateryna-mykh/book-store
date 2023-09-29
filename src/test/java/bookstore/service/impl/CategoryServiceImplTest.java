package bookstore.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import bookstore.dto.CategoryDto;
import bookstore.dto.CreateCategoryDto;
import bookstore.exception.EntityNotFoundException;
import bookstore.mapper.CategoryMapper;
import bookstore.model.Category;
import bookstore.repository.CategoryRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {
    private static Long id = 1L;
    private static Category category;
    private static CategoryDto categoryDto;
    private static CreateCategoryDto createCategoryDto;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;
    @InjectMocks
    private CategoryServiceImpl categoryService;

    @BeforeAll
    static void setUpBeforeClass() {
        category = new Category();
        category.setId(id);
        category.setName("Fiction");
        category.setDescription("""
                literature created from the imagination, not presented as fact, \
                though it may be based on a true story or situation.""");
        categoryDto = new CategoryDto();
        categoryDto.setId(id);
        categoryDto.setName(category.getName());
        categoryDto.setDescription(category.getDescription());
        createCategoryDto = new CreateCategoryDto();
        ReflectionTestUtils.setField(categoryDto, "name", "Fiction");
    }

    @Test
    @DisplayName("Given valid id, retrieve the CategoryDto")
    void getById_WithValidId_ReturnCategoryDto() {
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);
        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));
        
        CategoryDto actual = categoryService.getById(id);
        
        assertThat(actual).isEqualTo(categoryDto);
    }

    @Test
    @DisplayName("Given not existing id, retrieve the EntityNotFoundException exception")
    void getById_WithNotExistingId_ThrowEntityNotFoundException() {
        Long notExistingId = 100L;
        String expectedException = "Can't find category by id: " + notExistingId;
        when(categoryRepository.findById(notExistingId)).thenReturn(Optional.empty());

        EntityNotFoundException actualException = assertThrows(EntityNotFoundException.class,
                () -> categoryService.getById(notExistingId));

        assertEquals(expectedException, actualException.getMessage());
    }

    @Test
    @DisplayName("Given dafault Pagable, return List<CategoryDto>")
    void findAll_DefaultPagable_ReturnListOfCategoryDtos() {
        PageRequest pageable = PageRequest.of(0, 20);
        PageImpl<Category> categoryList = new PageImpl<>(List.of(category));
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);
        when(categoryRepository.findAll(pageable)).thenReturn(categoryList);

        int actualSize = categoryService.findAll(pageable).size();

        assertEquals(1, actualSize);
    }

    @Test
    @DisplayName("Verify save() method works")
    void save_ValidCreateCategoryDto_ReturnCategoryDto() {       
        when(categoryMapper.toModel(createCategoryDto)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);
        
        CategoryDto actual = categoryService.save(createCategoryDto);
        
        assertThat(actual).isEqualTo(categoryDto);
    }

    @Test
    @DisplayName("Verify delete() method works")
    void deleteById_AnyId_CallOneTime() {
        doNothing().when(categoryRepository).deleteById(anyLong());
        categoryService.deleteById(anyLong());
        verify(categoryRepository, times(1)).deleteById(anyLong());
    }

    @Test
    @DisplayName("Verify update() method works")
    void update_ValidParams_ReturnCategoryDto() {
        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));
        when(categoryMapper.toModel(createCategoryDto)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);        
        
        String descriptionBefore = categoryService.update(id, createCategoryDto).getDescription();
        category.setDescription("new description");
        categoryDto.setDescription(category.getDescription());        
        String descriptionAfter = categoryService.update(id, createCategoryDto).getDescription();

        assertThat(descriptionAfter).isNotEqualTo(descriptionBefore);
    }
    
    @Test
    @DisplayName("""            
            Verify update(), given not existing id, retrieve the 
            EntityNotFoundException exception""")
    void update_WithNotExistingId_ThrowEntityNotFoundException() {
        Long notExistingId = 100L;
        String expectedException = "Can't find category by id: " + notExistingId;
        when(categoryRepository.findById(notExistingId)).thenReturn(Optional.empty());

        EntityNotFoundException actualException = assertThrows(EntityNotFoundException.class,
                () -> categoryService.update(notExistingId, createCategoryDto));

        assertEquals(expectedException, actualException.getMessage());
    }
}
