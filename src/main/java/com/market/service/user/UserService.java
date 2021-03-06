package com.market.service.user;

import com.market.entity.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * @author kinoz
 * @Date 2022/7/8 - 15:33
 * @apiNote
 */
public interface UserService {
    //用户登录
    User Login(String userCode,String password);

    //根据id修改用户密码
    boolean updatePwd(long id, String password);

    //根据条件查询用户表记录数
    int getUserCount(String QueryUserName,int QueryUserRole);

    //根据条件查询用户列表
    List<User> getUserList(String userName, int userRole, int currentPageNo, int pageSize);

    //添加用户
    boolean addUser(User user);

    //删除用户 根据用户id
    boolean delUser(Long delID);

    //修改用户信息
    boolean modify(User user);

    //查看用户信息 根据用户id
    User getUserByID(String id);



}
