package com.xmcc.springdemo;

import com.google.common.collect.Lists;
import com.xmcc.springdemo.entity.ProductCategory;
import com.xmcc.springdemo.repository.ProductCategoryRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringdemoApplicationTests {
    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Test//增加测试
    public void insert(){
        ProductCategory productCategory = new ProductCategory();
        productCategory.setCategoryName("酒水");
        productCategory.setCategoryType(6);
        productCategoryRepository.save(productCategory);
    }
    @Test//查询单个，与修改测试
    public void findOne(){
        Optional<ProductCategory> byId = productCategoryRepository.findById(1);
        ProductCategory productCategory =null;
        if(byId.isPresent()){//判断是否有  我们这里直接用有的测试了
            productCategory = byId.get();
            System.out.println(productCategory);
        }
        productCategory.setCategoryName("家电");
        //修改测试 也是save方法  如果实体类一点没变 不会去修改的
        productCategoryRepository.save(productCategory);
    }

    @Test
    public void test2(){

        List<ProductCategory> byCategoryTypeIn = productCategoryRepository.findByCategoryTypeIn(Lists.newArrayList(1));
        byCategoryTypeIn.stream().forEach(System.out::println);

    }

    @Test
    public void test3(){

        String s = productCategoryRepository.queryNameByIdAndType(1, 1);
        System.out.println(s);

    }

}
