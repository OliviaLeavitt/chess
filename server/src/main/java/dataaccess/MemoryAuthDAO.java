package dataaccess;

import model.Auth;
import model.User;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO {
    private final HashMap<String, Auth> authTokens = new HashMap<>();

    public Auth createAuth(Auth auth) {
        Auth newAuth = new Auth(auth.authToken(), auth.username());
        authTokens.put(newAuth.authToken(), newAuth);
        return newAuth;
    }
    public Auth getAuth(String authToken) {
        return authTokens.get(authToken);
    }
    public void clear() {
        authTokens.clear();
    }
    public void deleteAuth(String authToken) {
        authTokens.remove(authToken);
    }
}
