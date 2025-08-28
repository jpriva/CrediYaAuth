package co.com.pragma.r2dbc.util;

import co.com.pragma.model.user.Role;
import co.com.pragma.model.user.User;

public class UserUtil {
    public static User setUserRole(User user, Role role){
        if (user == null) return null;
        user.setRole(role);
        return user;
    }
}
