package com.cs.ecommerce.sharedmodules.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@Builder
public class PaginatedResponse<T> {

    private List<T> content;
    private Pagination pagination;
    private boolean first;
    private boolean last;

    public static <T> PaginatedResponse<T> of(List<T> content, Page<?> page) {
        Pagination pagination = new Pagination(
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );

        return PaginatedResponse.<T>builder()
                .content(content)
                .pagination(pagination)
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }
}
