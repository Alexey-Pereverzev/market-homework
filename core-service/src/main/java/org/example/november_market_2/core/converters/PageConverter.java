package org.example.november_market_2.core.converters;

import org.example.november_market_2.api.PageDto;
import org.example.november_market_2.api.ProductDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class PageConverter {

    public PageDto entityToDto(Page<ProductDto> p) {
        PageDto pageDto = new PageDto();
        pageDto.setTotalPages(p.getTotalPages());
        pageDto.setNumber(p.getNumber());
        pageDto.setContent(new ArrayList<>(p.getContent()));
        return pageDto;
    }
}
