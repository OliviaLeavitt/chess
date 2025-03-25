package server;

import model.Auth;
import model.User;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public Auth register(User user) throws ResponseException {
        var path = "/user";
        record RegisterRequest(String username, String password, String email) {}
        var request = new RegisterRequest(user.username(), user.password(), user.email());
        return this.makeRequest("POST", path, request, Auth.class, null);
    }

}
