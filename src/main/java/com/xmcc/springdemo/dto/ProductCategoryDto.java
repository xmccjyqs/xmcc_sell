package com.xmcc.springdemo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.xmcc.springdemo.entity.ProductCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.BeanUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductCategoryDto implements Serializable{

    /** 类目名字. */
    @JsonProperty("name")
    private String categoryName;

    /** 类目编号. */
    @JsonProperty("type")
    private Integer categoryType;

    @JsonProperty("foods")
    //防止为null时被忽略
    public List<ProductInfoDto> productInfoDtoList;

    public static ProductCategoryDto adapt(ProductCategory productCategory){
        ProductCategoryDto productCategoryDto = new ProductCategoryDto();
        BeanUtils.copyProperties(productCategory,productCategoryDto);
        return productCategoryDto;
    }
}
