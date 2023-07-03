import connection.Server;
import gui.ServerWindow;

public class Main {
    public static void main(String[] args) {
        ServerWindow windowServer = new ServerWindow("Mesa de Voto");


        Server server = new Server(windowServer);
        windowServer.setServer(server);
        server.start();
        windowServer.setVisible(true);




    }
}
