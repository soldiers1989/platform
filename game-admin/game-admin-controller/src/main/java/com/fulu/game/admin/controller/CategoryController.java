package com.fulu.game.admin.controller;

import com.fulu.game.common.Result;
import com.fulu.game.common.enums.CategoryParentEnum;
import com.fulu.game.common.enums.TechAttrTypeEnum;
import com.fulu.game.common.utils.OssUtil;
import com.fulu.game.core.entity.*;
import com.fulu.game.core.entity.vo.CategoryVO;
import com.fulu.game.core.entity.vo.TagVO;
import com.fulu.game.core.service.*;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/v1/category")
public class CategoryController extends BaseController {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private TechValueService techValueService;
    @Autowired
    private TechAttrService techAttrService;
    @Autowired
    private SalesModeService salesModeService;
    @Autowired
    private OssUtil ossUtil;



    /**
     * 内容列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    @PostMapping(value = "/list")
    public Result list(Integer pageNum,
                       Integer pageSize,
                       Boolean status) {
        PageInfo<Category> page = categoryService.list(pageNum, pageSize, status, null);
        return Result.success().data(page);
    }

    /**
     * 内容删除
     *
     * @param id
     * @return
     */
    @PostMapping(value = "/del")
    public Result delete(Integer id) {
        categoryService.deleteById(id);
        return Result.success().msg("删除成功!");
    }

    /**
     * 查询所有内容
     *
     * @return
     */
    @PostMapping(value = "/list-all")
    public Result listAll() {
        List<Category> list = categoryService.findByPid(CategoryParentEnum.ACCOMPANY_PLAY.getType(), null);
        return Result.success().data(list);
    }

    /**
     * 查询单个内容所有信息
     * @param categoryId
     * @return
     */
    @PostMapping(value = "/query")
    public Result info(@RequestParam(required = true) Integer categoryId) {
        CategoryVO categoryVO = categoryService.findCategoryVoById(categoryId);
        return Result.success().data(categoryVO);
    }


    /**
     * 保存内容
     * @return
     */
    @PostMapping(value = "/save")
    public Result save(CategoryVO categoryVO) {
        if (categoryVO.getCharges()!= null) {
            categoryVO.setCharges(categoryVO.getCharges().divide(new BigDecimal(100)));
        }
        categoryVO.setIcon(ossUtil.activateOssFile(categoryVO.getIcon()));
        categoryVO.setIndexIcon(ossUtil.activateOssFile(categoryVO.getIndexIcon()));
        Category category =   categoryService.save(categoryVO);
        return Result.success().data(category).msg("内容修改成功!");
    }


    /**
     * 创建销售方式
     * @return
     */
    @PostMapping(value = "/salesmode/create")
    public Result salesModeCreate(@RequestParam(required = true) Integer categoryId,
                                  @RequestParam(required = true) String name,
                                  @RequestParam(required = false) BigDecimal price,
                                  @RequestParam(required = true) Integer rank) {
        SalesMode salesMode = new SalesMode();
        salesMode.setCategoryId(categoryId);
        salesMode.setName(name);
        salesMode.setPrice(price);
        salesMode.setRank(rank);
        salesMode.setCreateTime(new Date());
        salesMode.setUpdateTime(new Date());
        salesModeService.create(salesMode);
        return Result.success().msg("销售方式创建成功!").data(salesMode);
    }



    @PostMapping(value = "/salesmode/update")
    public Result salesModeUpdate(@RequestParam(required = true) Integer id,
                                  @RequestParam(required = true) String name,
                                  @RequestParam(required = false) BigDecimal price,
                                  @RequestParam(required = true) Integer rank) {
        SalesMode salesMode = salesModeService.update(id, name, price, rank);
        return Result.success().msg("销售方式修改成功!").data(salesMode);
    }


    /**
     * 删除销售方式
     *
     * @return
     */
    @PostMapping(value = "/salesmode/delete")
    public Result saleModeDelete(@RequestParam(required = true) Integer id) {
        salesModeService.deleteById(id);
        return Result.success().msg("销售方式删除成功!");
    }


    /**
     * 创建段位
     **/
    @PostMapping(value = "/dan/create")
    public Result danCreate(@RequestParam(required = true) Integer categoryId,
                            @RequestParam(required = true) String name,
                            @RequestParam(required = true) Integer rank) {
        TechValue techValue = techValueService.createDan(categoryId, name, rank);
        return Result.success().msg("段位创建成功!").data(techValue);
    }


    /**
     * 创建大区
     */
    @PostMapping(value = "/area/create")
    public Result areaCreate(@RequestParam(required = true) Integer categoryId,
                             @RequestParam(required = true) String name,
                             @RequestParam(required = true) Integer rank){
        TechValue techValue = techValueService.createArea(categoryId, name, rank);
        return Result.success().msg("游戏大区创建成功!").data(techValue);
    }


    /**
     * 修改段位
     **/
    @PostMapping(value = "/dan/update")
    @Deprecated
    public Result danUpdate(@RequestParam(required = true) Integer id,
                            @RequestParam(required = true) String name,
                            @RequestParam(required = true) Integer rank) {
        TechValue techValue = techValueService.updateAttrVal(id, name, rank);
        return Result.success().msg("段位修改成功!").data(techValue);
    }

    /**
     * 删除段位
     */
    @PostMapping(value = "/dan/delete")
    @Deprecated
    public Result danDelete(@RequestParam(required = true) Integer id) {
        techValueService.deleteById(id);
        return Result.success().msg("段位删除成功!");
    }

    /**
     * 修改动态属性
     **/
    @PostMapping(value = "/attr/update")
    public Result attrUpdate(@RequestParam(required = true) Integer id,
                            @RequestParam(required = true) String name,
                            @RequestParam(required = true) Integer rank) {
        TechValue techValue = techValueService.updateAttrVal(id, name, rank);
        return Result.success().msg("修改成功!").data(techValue);
    }

    /**
     * 删除动态属性
     */
    @PostMapping(value = "/attr/delete")
    public Result attrDelete(@RequestParam(required = true) Integer id) {
        techValueService.deleteById(id);
        return Result.success().msg("删除成功!");
    }


    /**
     * 查询游戏所有段位
     * @return
     */
    @PostMapping(value = "/dan/list")
    public Result danList(@RequestParam(required = true, name = "categoryId") Integer categoryId) {
        TechAttr techAttr = techAttrService.findByCategoryAndType(categoryId, TechAttrTypeEnum.DAN.getType());
        List<TechValue> techValueList = techValueService.findByTechAttrId(techAttr.getId());
        return Result.success().data(techValueList);
    }


}
