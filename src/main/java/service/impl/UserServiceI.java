package service.impl;

import dao.LoginLogMapper;
import dao.UserDetailedMapper;
import dao.UserMapper;
import dao.showInfoDao;
import model.LoginLog;
import model.RecentUser;
import model.User;
import model.UserDetailed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import service.UserService;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceI implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private LoginLogMapper loginLogMapper;
    @Autowired
    private UserDetailedMapper userDetailedMapper;
    @Autowired
    private showInfoDao showInfoDao;

    public User findUserByName(String username) {
        return userMapper.selectByUsername(username);
    }
    public boolean checkLogin(String username, String password) {
        User user = findUserByName(username);
        if (user != null) {
            return password.equals(user.getPassword());
        }
        return false;
    }

    public int returnSumUser() {
        return userMapper.findUserTotalNum();
    }

    public void lockUser(String username) {
        int id = findUserByName(username).getUserId();
        User user = new User();
        user.setUserId(id);
        user.setLocked(User.USER_LOCK);
        userMapper.updateByPrimaryKeySelective(user);
    }

    public boolean isLocked(String username) {
        if (findUserByName(username).getLocked() == User.USER_LOCK) {
            return true;
        }
        return false;
    }

    public void addCredit(String username, int creditNum) {
        User user = findUserByName(username);
        User user1 = new User();
        user1.setUserId(user.getUserId());
        user1.setCredit(user.getCredit() + creditNum);
        userMapper.updateByPrimaryKeySelective(user1);
    }

    public void setLoginLogAndDate(String username,Date date,String addr) {
        User user = findUserByName(username);
        user.setLastVisit(date);
        user.setLastIp(addr);
        userMapper.updateByPrimaryKey(user);
        //注册登录日志
        int id = userMapper.selectByUsername(username).getUserId();
        LoginLog loginLog = new LoginLog();
        loginLog.setUserId(id);
        loginLog.setIp(addr);
        loginLog.setLoginDatetime(date);
        loginLogMapper.insertSelective(loginLog);
    }

    public User selectByUserId(int id) {
        return userMapper.selectByPrimaryKey(id);
    }

    public void register(String username, String password) {
        Byte b = 1;
        User user = new User();
        user.setCredit(50);
        user.setLocked(User.USER_UNLOCK);
        user.setUserType(b);
        user.setUserName(username);
        user.setPassword(password);
        userMapper.insertSelective(user);
    }

    public List<User> getAllUsers(){
        //List<User> list=new ArrayList<>();
        return showInfoDao.getAllUsers();

    }

    @Override
    public void updateAddressOfPic(int userId,String address) {
        UserDetailed userDetailed = new UserDetailed();
        userDetailed.setUserId(userId);
        userDetailed.setImagePath(address);
        userDetailedMapper.updateByPrimaryKeySelective(userDetailed);
    }

    @Override
    public UserDetailed findById(int userId) {
        return userDetailedMapper.selectByPrimaryKey(userId);
    }

    @Override
    public List<RecentUser> RecentUserData() {
        return userDetailedMapper.RecentUserData();
    }

    //根据用户id返回用户详细信息
   public UserDetailed getDetailById(int id){
        UserDetailed userDetailed=userDetailedMapper.getDetailById(id);
        return userDetailed;

    }
}
