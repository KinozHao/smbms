package com.market.service.user;

import com.market.dao.BaseDao;
import com.market.dao.user.UserDao;
import com.market.dao.user.UserDaoImpl;
import com.market.entity.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * @author kinoz
 * @Date 2022/7/8 - 15:35
 * @apiNote 业务层引入DAO层并调用
 */
public class UserServiceImpl implements UserService{
    //获取DAO里面的操纵对象,为后面调用Login方法做准备
    private final UserDao userDao;
    public UserServiceImpl(){
        userDao = new UserDaoImpl();
    }

    @Override
    //用户登录
    public User Login(String userCode, String password) {
        Connection con = null;
        User user = null;
        try {
            //获取连接，确定要登录对象
            con = BaseDao.getConnection();
            user = userDao.getLoginUser(con,userCode);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            //释放资源
            BaseDao.CloseConnection(con,null,null);
        }
        //密码校验
        if (user != null){
            if (!user.getUserpassword().equals(password)){
                user = null;
            }
        }
        return user;
    }

    @Override
    //修改登录用户密码
    public boolean updatePwd(long id, String password) {
        boolean flag = false;
        Connection con = null;

        con = BaseDao.getConnection();

        try {
            if (userDao.updatePwd(con,id,password) > 0) {
                flag = true;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            BaseDao.CloseConnection(con,null,null);
        }
        return flag;
    }

    @Override
    //获取总用户数,根据用户角色
    public int getUserCount(String QueryUserName, int QueryUserRole) {
        Connection con = null;
        int count = 0;
        try {
            con = BaseDao.getConnection();
            count = userDao.getUserCount(con, QueryUserName, QueryUserRole);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            BaseDao.CloseConnection(con,null,null);
        }
        return count;
    }

    @Override
    //用户表分页
    public List<User> getUserList(String userName, int userRole, int currentPageNo, int pageSize) {
        Connection con = null;
        List<User> userList = null;
        try {
            con = BaseDao.getConnection();
            userList = userDao.getUserList(con, userName, userRole, currentPageNo, pageSize);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            BaseDao.CloseConnection(con,null,null);
        }
        return userList;
    }

    @Override
    //增
    public boolean addUser(User user) {
        boolean flag = false;
        Connection con = null;
        try {
            con = BaseDao.getConnection();
            con.setAutoCommit(false); //开启事务
            int updateRows = userDao.addUser(con, user);
            con.commit();
            if (updateRows>0){
                flag = true;
                System.out.println("添加成功!");
            }else {
                System.out.println("添加失败!");
            }
        } catch (SQLException e) {
            try {
                System.out.println("事件提交失败，回滚版本");
                con.rollback();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            e.printStackTrace();
        }finally {
            //释放流资源
            BaseDao.CloseConnection(con,null,null);
        }
        return flag;
    }

    @Override
    //删
    public boolean delUser(Long delID) {
        Connection con = null;
        boolean flag = false;

        try {
            con = BaseDao.getConnection();
            if (userDao.delUser(con,delID)>0){
                flag = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            BaseDao.CloseConnection(con,null,null);
        }
        return flag;
    }

    @Override
    //改
    public boolean modify(User user) {
        Connection con = null;
        boolean flag = false;
        try {
            con = BaseDao.getConnection();
            if (userDao.modify(con, user)>0){
                flag = true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            BaseDao.CloseConnection(con,null,null);
        }
        return flag;
    }

    @Override
    //查
    public User getUserByID(String id) {
        Connection con = null;
        User user = null;
        try {
            con = BaseDao.getConnection();
            user = userDao.getUserById(con, id);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            BaseDao.CloseConnection(con,null,null);
        }
        return user;
    }


}
