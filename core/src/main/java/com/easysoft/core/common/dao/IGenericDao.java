package com.easysoft.core.common.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.Criterion;

import com.easysoft.core.common.dao.hibernate.support.AbstractHibernateQry;
import com.easysoft.framework.db.PageOption;

/**
 * 新通用DAO接口
 * @author : andy.huang
 * @since : 1.0
 */
public interface IGenericDao<T,PK extends Serializable> {
    /**
     * 保存实体
     * @param entity
     */
    public void save(T entity);

    /**
     * 保存或者更新实体
     * @param entity
     */
    public void saveOrUpdate(T entity);

    /**
     * 查询列表
     * @return
     */
    public List<T> queryForList();
    /**
     * 查询列表
     * @return
     */
    public List<T> queryForList(Map<String,Object> params);
    public List<T> queryForQry(AbstractHibernateQry searchCondition);
    /**
     * 通过参数查询列表
     * @param hql
     * @return
     */
    public List<T> queryForListByHql(String hql);

    /**
     * 要页查询记录
     * @param pageOption
     * @return
     */
    public List<T> queryForPage(PageOption pageOption);
    /**
     * 分页条件查询记录
     * @param pageOption
     * @param criterions
     * @return
     */
    public List<T> queryForPage(PageOption pageOption, List<Criterion> criterions);
    public List<T> queryForPageByQry(PageOption pageOption, AbstractHibernateQry searchCondition);
    public List<T> queryForHQL(String hql,Map<String,Object> params);

    public T queryById(PK id);

    public void update(T entity);

    public void deleteById(PK id);
    /**
     * 执行原生sql
     * @param sql
     */
    public void excuteBySql(String sql);
}
