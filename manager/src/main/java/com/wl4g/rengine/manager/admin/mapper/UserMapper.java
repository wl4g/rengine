package com.wl4g.rengine.manager.admin.mapper;

import com.wl4g.rengine.common.bean.User;
import com.wl4g.rengine.common.bean.UserKey;

public interface UserMapper {
    int deleteByPrimaryKey(UserKey key);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(UserKey key);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);
}