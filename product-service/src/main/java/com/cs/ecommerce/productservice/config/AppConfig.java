package com.cs.ecommerce.productservice.config;

import com.cs.ecommerce.productservice.dto.CategoryCreateRequestDTO;
import com.cs.ecommerce.productservice.dto.CategoryDTO;
import com.cs.ecommerce.productservice.dto.ProductDTO;
import com.cs.ecommerce.productservice.dto.ProductRequestDTO;
import com.cs.ecommerce.productservice.entities.Category;
import com.cs.ecommerce.productservice.entities.Product;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();

        mapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE);

        // Product mapping
        mapper.createTypeMap(ProductRequestDTO.class, Product.class)
                .addMappings(mapping -> {
                    mapping.skip(Product::setId);
                    mapping.skip(Product::setCategory);
                    mapping.skip(Product::setCreatedAt);
                    mapping.skip(Product::setUpdatedAt);
                });

        // Category creation mapping (request -> entity)
        mapper.createTypeMap(CategoryCreateRequestDTO.class, Category.class)
                .addMappings(mapping -> {
                    mapping.skip(Category::setId);
                    mapping.skip(Category::setParent);
                    mapping.skip(Category::setIsActive);
                    mapping.skip(Category::setCreatedAt);
                    mapping.skip(Category::setChildren);
                });

        // Category -> DTO mapping
        mapper.createTypeMap(Category.class, CategoryDTO.class)
                .addMappings(mapping -> {
                    mapping.skip(CategoryDTO::setChildren);
                    mapping.skip(CategoryDTO::setParentId);
                    mapping.skip(CategoryDTO::setParentName);
                });

        mapper.createTypeMap(Product.class, ProductDTO.class)
                .addMappings(mapping -> {
                    mapping.map(src -> src.getCategory().getName(), ProductDTO::setCategory);
                    mapping.map(src -> src.getCategory().getId(), ProductDTO::setCategoryId);
                });

        return mapper;
    }

}
