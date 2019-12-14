package com.hykc.cityfreight.db;

import com.hykc.cityfreight.entity.LocationEntity;
import com.hykc.cityfreight.entity.User;

import java.util.List;

/**
 * Created by zzu on 2016/4/6.
 */
public interface DBDao {

    //查询所有用户信息
    public List<User> findAllUser();

    //根据id查找用户信息
    public User findUserInfoById(String userid);

    //删除用户信息
    public void delUserInfoById(String userid);

    //判断用户是否存在
    public boolean findUserIsExist(String userid);

    //添加用户信息
    public void addUserInfo(User user);

    //修改用户信息
    public void updateUserInfo(User user, String userid);

    //删除位置信息
    public void delLocInfo(String rowid);
    //修改位置信息
    public void updateLocInfo(LocationEntity locationEntity, String rowid);
    //添加位置信息
    public void addLocInfo(LocationEntity locationEntity);

    public LocationEntity findLocInfoById(String rowid);

    public boolean findLocInfoIsExist(String rowid);

}
