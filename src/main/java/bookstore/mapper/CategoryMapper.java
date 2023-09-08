package bookstore.mapper;

import bookstore.config.MapperConfig;
import bookstore.dto.CategoryDto;
import bookstore.dto.CreateCategoryDto;
import bookstore.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface CategoryMapper {
    CategoryDto toDto(Category category);

    @Mapping(target = "id", ignore = true)
    Category toModel(CreateCategoryDto categoryDto);
}
