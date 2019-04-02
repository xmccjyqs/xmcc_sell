package com.xmcc.springdemo.repository;

import com.xmcc.springdemo.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

//第一个参数 是实体类名称  第二个参数是主键类型
public interface ProductCategoryRepository extends JpaRepository<ProductCategory,Integer> {

    //根据类型列表查询 集合
    List<ProductCategory> findByCategoryTypeIn(List<Integer> typeList);

    //根据类型和id查询

    /**
     * nativeQuery = true : 执行原生数据库语句
     * 如果没有，则使用的是 jpa默认的语句：jpal
     * 区别：jpal语句中的数据信息 都是针对 实体类对象的
     * @param id
     * @param type
     * @return
     */
//    @Query(value = "select category_name from product_category where category_id=?1 and category_type=?2",nativeQuery = true)
//    String queryNameByIdAndType( Integer id, Integer type);
   // @Query(value = "select categoryName from ProductCategory where categoryId=?1 and categoryType=?2")
//    String queryNameByIdAndType( Integer id, Integer type);
    @Query(value = "select categoryName from ProductCategory where categoryId=:id1 and categoryType=:type")
    String queryNameByIdAndType(@Param("id1") Integer id, Integer type);

}
