package com.cmd.bookmark.manager;

import com.cmd.bookmark.constants.Gender;
import com.cmd.bookmark.constants.UserType;
import com.cmd.bookmark.entities.User;
import com.cmd.bookmark.dao.UserDao;
import com.cmd.bookmark.util.StringUtil;

import java.util.List;

public class UserManager {

    private static UserManager instance = new UserManager();
    private static UserDao dao = new UserDao();
    private UserManager(){

    }
    public static UserManager getInstance() {
        return instance;
    }

    public User createUser(long id, String email, String password, String firstName, String lastName, Gender gender, UserType userType) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setPassword(password);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setGender(gender);
        user.setUserType(userType);

        return user;
    }

    public List<User> getUsers() {
        return dao.getUsers();
    }

    public User getUser(long userId) {

        return dao.getUser(userId);
    }

    public long authenticate(String email, String password) {

        return dao.authenticate(email, StringUtil.encodePassword(password));
    }
}
