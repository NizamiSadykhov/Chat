import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientHandler {
    private String mNickname;
    private Socket mSocket;
    private DataInputStream mIn;
    private DataOutputStream mOut;
    private Server mServer;

    public ClientHandler(Integer number, Server server, Socket socket) {
        try {
            mSocket = socket;
            mServer = server;
            mIn = new DataInputStream(mSocket.getInputStream());
            mOut = new DataOutputStream(mSocket.getOutputStream());
            mNickname = "Client #" + number;
            new Thread(() -> {
                boolean continueChat = true;
                String msg = null;
                boolean isAuthorised = false;
                try {
                    while (!isAuthorised && continueChat) {
                        msg = mIn.readUTF();
                        if (msg.startsWith("/auth")) {
                            String[] tokens = msg.split("\\s");
                            String login = tokens[1];
                            String password = tokens[2];
                            mNickname = mServer.getAuthService()
                                    .getNicknameByLoginAndPassword(login, password);
                            if (mNickname != null) {
                                isAuthorised = true;
                                sendMessage("/authok");
                                server.subscribe(this);
                            } else {
                                sendMessage("/error");
                            }
                        }
                        if (msg.equals("/end")) {
                            sendMessage("/end");
                            continueChat = false;
                        }
                    }
                    while (continueChat) {
                        msg = mIn.readUTF();
                        if (msg.startsWith("/")) {
                            if (msg.equals("/end")) {
                                sendMessage("/end");
                                continueChat = false;
                            } else if (msg.equals("/auth")) {
                                String[] tokens = msg.split("\\s");
                                String login = tokens[1];
                                String password = tokens[2];
                                mNickname = mServer.getAuthService()
                                        .getNicknameByLoginAndPassword(login, password);
                            } else if (msg.startsWith("/w")){
                                String[] tokens = msg.split(" ", 3);
                                String name = tokens[1];
                                String message = tokens[2];
                                mServer.sendForClient(name, message);
                            }
                        } else {
                            server.broadcastMessage(mNickname + ": " + msg);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    disconnect();
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        try {
            mOut.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getNickname() {
        return mNickname;
    }

    public void disconnect() {
        mServer.unSubscribe(this);
        try {
            mIn.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
