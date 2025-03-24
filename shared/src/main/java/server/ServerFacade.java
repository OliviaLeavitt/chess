

public class ServerFacade {
    private final ServerFacade server;

    public ServerFacade(String serverUrl, NotificationHandler notificationHandler) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }
}
