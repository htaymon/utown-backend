package com.utown.utown_backend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Schema(description = "Generic paginated response wrapper")
@Getter
@AllArgsConstructor
@Builder
public class PageResponseDTO<T> {

    @Schema(description = "Page content")
    private final List<T> content;

    @Schema(description = "Zero-based page index", example = "0")
    private final int page;

    @Schema(description = "Page size", example = "10")
    private final int size;

    @Schema(description = "Total number of elements across all pages", example = "42")
    private final long totalElements;

    @Schema(description = "Total number of pages", example = "5")
    private final int totalPages;

    @Schema(description = "Whether this is the last page", example = "false")
    private final boolean last;

    public static <T> PageResponseDTO<T> from(Page<T> page) {
        return PageResponseDTO.<T>builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }
}
