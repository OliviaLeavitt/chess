package dataaccess;
import model.User;
import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {
    @Override
    public String toString() {
        return "MemoryUserDAO{" +
                "users=" + users +
                '}';
    }

    final private HashMap<String, User> users = new HashMap<>();

    public User createUser(User user) {

        User newUser = new User(user.username(), user.password(), user.email());
        users.put(newUser.username(), newUser);

        return newUser;
    }
    public User getUser(String username) {

        return users.get(username);
    }
    public void clear() {
        users.clear();
    }

}
