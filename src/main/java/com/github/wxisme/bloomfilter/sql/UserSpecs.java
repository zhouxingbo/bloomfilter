package com.github.wxisme.bloomfilter.sql;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;

public class UserSpecs {

    public static Specification<SysUserEntity> where(String keyword, Date createdAtBegin, Date createdAtEnd, List<String> userIds) {
        return (Root<SysUserEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.isNotBlank(keyword)) {
                List<Predicate> temp = new ArrayList<>();
                Set<String> keywordCopyStr =  new HashSet<>(Arrays.asList(StringUtils.split(keyword)));
                for (String oneKeyword : keywordCopyStr) {
                    temp.add(cb.like(root.<String>get("mobile"), "%" + oneKeyword + "%"));
                    temp.add(cb.like(root.<String>get("trueName"), "%" + oneKeyword + "%"));
                }
                predicates.add(cb.or(temp.toArray(new Predicate[temp.size()])));
            }
            //未删除
            predicates.add(cb.equal(root.get("isDelete"), 1));
            query.where(predicates.toArray(new Predicate[predicates.size()]));
            return query.getRestriction();
        };
    }
}
