package com.fulu.game.core.dao;

import com.fulu.game.core.entity.Product;
import com.fulu.game.core.entity.vo.ProductVO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

/**
 * 商品表
 * @author yanbiao
 * @email ${email}
 * @date 2018-04-24 15:23:43
 */
@Mapper
public interface ProductDao extends ICommonDao<Product,Integer>{

    List<Product> findByParameter(ProductVO productVO);

}