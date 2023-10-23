package com.nowcoder.community1.community1.dao;

import org.springframework.stereotype.Repository;

@Repository("aliphaHibernate")
public class AliphaDaoHibernateImpl implements AlphaDao {
    @Override
    public String select() {
        return "Hibernate";
    }
}
