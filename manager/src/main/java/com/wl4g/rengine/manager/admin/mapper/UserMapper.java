package com.wl4g.rengine.manager.admin.mapper;

import com.wl4g.rengine.common.entity.User;

public interface UserMapper {

    int deleteByPrimaryKey(User key);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(User key);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);
}