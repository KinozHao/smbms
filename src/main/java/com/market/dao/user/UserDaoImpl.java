package com.market.dao.user;

import com.market.dao.BaseDao;
import com.market.entity.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author kinoz
 * @Date 2022/7/8 - 15:08
 * @apiNote
 */
public class UserDaoImpl implements UserDao{
    //得到要登陆的用户
    @Override
    public User getLoginUser(Connection con, String userCode) throws SQLException {
        PreparedStatement pst = null;
        ResultSet result = null;
        User user = null;

        //判断数据库连接是否成功
        if (con != null){
            String sql = "select * from smbms_user where userCode=?";
            Object[] params = {userCode};

            result = BaseDao.execute(con, pst, result, sql, params);
            if (result.next()){
                //为实体类User里面的属性和SQL进行结合
                user = new User();
                user.setId(result.getInt("id"));
                user.setUserCode(result.getString("userCode"));
                user.setUserName(result.getString("userName"));
                user.setUserpassword(result.getString("userPassword"));
                user.setGender(result.getInt("gender"));
                user.setBirthday(result.getDate("birthday"));
                user.setPhone(result.getString("phone"));
                user.setAddress(result.getString("address"));
                user.setUserrole(result.getInt("userRole"));
                user.setCreatedby(result.getInt("createdBy"));
                user.setCreationdate(result.getDate("creationDate"));
                user.setModifyby(result.getInt("modifyBy"));
                user.setModifydate(result.getDate("modifyDate"));
            }

            //连接会存在业务，暂时不关闭
            BaseDao.CloseConnection(null,pst,result);
        }
        //若以上执行成功返回user信息
        return user;
    }

    //修改当前用户密码
    @Override
    public int updatePwd(Connection con, long id, String password) throws SQLException {
        PreparedStatement pstm = null;
        int execute = 0;
        if (con != null){
            String sql = "update smbms_user set userPassword=? where id=?";
            ArrayList<Object> param = new ArrayList<>();
            param.add(password);
            param.add(id);
            Object[] change_param = param.toArray();
            execute = BaseDao.execute(con, pstm, sql, change_param);
            BaseDao.CloseConnection(null,pstm,null);
        }
        return execute;
    }

    //动态sql 较难 多表查询
    @Override
    public int getUserCount(Connection con, String userName, int userRole) throws SQLException {
        ResultSet result = null;
        PreparedStatement state = null;
        //最终需要返回的值
        int count = 0;

        if (con != null){
            StringBuilder sql = new StringBuilder();
            //主查询语句
            sql.append("select count(1) as sql_count from smbms_user u,smbms_role r where u.userRole = r.id");
            //用于存放sql
            List<Object> param = new ArrayList<>();
            //userName不为空时对sql做拼接模糊查询
            if (userName != null){
                sql.append(" and u.userName like ?");
                param.add("%"+userName+"%");
            }
            //对应级别的人数
            if (userRole > 0){
                sql.append(" and u.userRole = ?");
                param.add(userRole);
            }
            //转换为Base公共sql类需要的类型
            Object[] change_param = param.toArray();
            String change_sql = sql.toString();
            //Test
            System.out.println("sql -->"+sql.toString());

            //调用sql公共类返回值供service层调用
            result = BaseDao.execute(con,state,result,change_sql,change_param);
            if (result.next()){
                count = result.getInt("sql_count");//""里面的sql_count为我们sql语句中as的别名
            }
            //释放流资源
            BaseDao.CloseConnection(null,state,result);
        }
        return count;
    }

    //条件查询-用户列表
    @Override
    public List<User> getUserList(Connection con, String userName, int userRole, int currentPageNo, int pageSize) throws SQLException {
        ResultSet result = null;
        PreparedStatement state = null;
        List<User> userList = new ArrayList<>();
        if (con != null){
            StringBuilder sql = new StringBuilder();
            sql.append("select u.*,r.roleName as roleName from smbms_user u,smbms_role r where u.userRole = r.id");
            ArrayList<Object> param = new ArrayList<>();
            if (userName!=null){
                sql.append(" and u.userName like ?");
                param.add("%"+userName+"%");
            }
            if (userRole > 0){
                sql.append(" and u.userRole = ?");
                param.add(userRole);
            }

            //数据库分页操作
            //在数据库中，分页使用 limit startIndex pageSize
            //当前页  （当前页-1）*页面大小
            //0,5    1   0    012345
            //6,5    2   5    26789
            //11,5   3   10
            sql.append(" order by creationDate DESC limit ?,?");
            currentPageNo = (currentPageNo-1)*pageSize;
            param.add(currentPageNo);
            param.add(pageSize);

            Object[] change_param = param.toArray();
            String change_sql = sql.toString();
            System.out.println("sql --->"+change_sql);

            result = BaseDao.execute(con,state,result,change_sql,change_param);
            while (result.next()){
                User user = new User();
                user.setId(result.getInt("id"));
                user.setUserCode(result.getString("userCode"));
                user.setUserName(result.getString("userName"));
                user.setGender(result.getInt("gender"));
                user.setAge(result.getInt("age"));
                user.setBirthday(result.getDate("birthday"));
                user.setPhone(result.getString("phone"));
                user.setUserrole(result.getInt("userRole"));
                user.setUserRoleName(result.getString("roleName"));
                userList.add(user);
            }
            BaseDao.CloseConnection(null,state,result);
        }
        return userList;
    }

    @Override
    //增×
    public int addUser(Connection con, User user) throws SQLException {
        PreparedStatement state = null;
        int updateRows = 0;
        if (con != null){
            String sql = "insert into smbms_user (userCode, userName, userPassword, gender, birthday, phone, address," +
                    "userRole, createdBy, creationDate)" +
                    "values (?,?,?,?,?,?,?,?,?,?);";

                ArrayList<Object> list = new ArrayList<>();
                list.add(user.getUserCode());
                list.add(user.getUserName());
                list.add(user.getUserpassword());
                list.add(user.getGender());
                list.add(user.getBirthday());
                list.add(user.getPhone());
                list.add(user.getAddress());
                list.add(user.getUserrole());
                list.add(user.getCreatedby());
                list.add(user.getCreationdate());
                Object[] param = list.toArray();

            Object[] params = {user.getUserCode(),user.getUserName(),user.getUserpassword(),
                    user.getUserrole(),user.getGender(),user.getBirthday(),
                    user.getPhone(),user.getAddress(),user.getCreationdate(),user.getCreatedby()};
            updateRows = BaseDao.execute(con, state, sql,param);

            BaseDao.CloseConnection(null,state,null);
        }
        return updateRows;
    }

    @Override
    //删√
    public int delUser(Connection con, Long delID) throws SQLException {
        PreparedStatement state = null;
        int delFlag = 0;
        if (con != null){
            String sql = "delete from smbms_user where id=?";
            Object[] param = {delID};
            delFlag = BaseDao.execute(con, state, sql, param);

            BaseDao.CloseConnection(null,state,null);
        }
        return delFlag;
    }

    @Override
    //改×
    public int modify(Connection con, User user) throws SQLException {
        PreparedStatement state = null;
        int flag = 0;
        if (con != null){
            String sql = "update smbms_user set userName=?,gender=?,birthday=?,phone=?,address=?,userRole=?,modifyBy=?,modifyDate=? where id = ? ";
            ArrayList<Object> param = new ArrayList<>();
            param.add(user.getUserName());
            param.add(user.getGender());
            param.add(user.getBirthday());
            param.add(user.getPhone());
            param.add(user.getAddress());
            param.add(user.getUserrole());
            param.add(user.getModifyby());
            param.add(user.getModifydate());
            param.add(user.getId());
            Object[] change_param = param.toArray();

            //方式二
            Object[] params = {user.getUserName(),user.getGender(),user.getBirthday(),
                    user.getPhone(),user.getAddress(),user.getUserrole(),user.getModifyby(),
                    user.getModifydate(),user.getId()};
            flag = BaseDao.execute(con,state,sql,params);
        }
        return flag;
    }

    @Override
    //查√
    public User getUserById(Connection con, String id) throws SQLException {
        ResultSet rs = null;
        PreparedStatement state = null;
        User user = null;
        if (con != null){
            String sql = "select u.*,r.roleName as userRoleName from smbms_user u,smbms_role r where u.id=? and u.userRole = r.id";
            Object[] param = {id};
            rs = BaseDao.execute(con, state, rs, sql, param);
            while (rs.next()){
                user = new User();
                user.setId(rs.getInt("id"));
                user.setUserCode(rs.getString("userCode"));
                user.setUserName(rs.getString("userName"));
                user.setUserpassword(rs.getString("userPassword"));
                user.setGender(rs.getInt("gender"));
                user.setBirthday(rs.getDate("birthday"));
                user.setPhone(rs.getString("phone"));
                user.setAddress(rs.getString("address"));
                user.setUserrole(rs.getInt("userRole"));
                user.setCreatedby(rs.getInt("createdBy"));
                user.setCreationdate(rs.getTimestamp("creationDate"));
                user.setModifyby(rs.getInt("modifyBy"));
                user.setModifydate(rs.getTimestamp("modifyDate"));
                user.setUserRoleName(rs.getString("userRoleName"));
            }
            BaseDao.CloseConnection(con,null,null);
        }
        return user;
    }
}
