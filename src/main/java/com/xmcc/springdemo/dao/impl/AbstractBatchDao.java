package com.xmcc.springdemo.dao.impl;

import com.xmcc.springdemo.dao.BatchDao;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;


public class AbstractBatchDao<T> implements BatchDao<T>{

    @PersistenceContext
    protected EntityManager em;

    @Transactional
    public void batchInsert(List<T> list) {
        long length = list.size();
        for (int i = 0; i <length; i++) {
            em.persist(list.get(i));
            if (i % 100 == 0||i==length-1) {//每100条执行一次写入数据库操作 不足100条，就在最后一条执行
                em.flush();
                em.clear();
            }
        }

    }
}
