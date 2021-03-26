import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {
    public final static int PORT = 8189;
    private List<ClientHandler> mClients;
    private AtomicInteger number = new AtomicInteger(1);
    private AuthService mAuthService = new DBAuthService();

    public AuthService getAuthService() {
        return mAuthService;
    }

    public Server(){
        mClients = new ArrayList<>();
        try(ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is listening in " + PORT);
            while (true) {
                Socket socket = serverSocket.accept();
                new ClientHandler(number.getAndIncrement(),this, socket);
                System.out.println("Подключился новый клиент");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void broadcastMessage(String message) {
        for (ClientHandler client: mClients) {
            client.sendMessage(message);
        }
    }

    public void broadcastClientsList() {
        StringBuilder sb = new StringBuilder(15 * mClients.size());
        sb.append("/clients ");
        for (ClientHandler client : mClients) {
            sb.append(client.getNickname()).append(" ");
        }
        String out = sb.toString();
        broadcastMessage(out);
    }

    public void subscribe(ClientHandler client){
        mClients.add(client);
        broadcastClientsList();
    }

    public void unSubscribe(ClientHandler client){
        mClients.remove(client);
        broadcastClientsList();
    }

}
