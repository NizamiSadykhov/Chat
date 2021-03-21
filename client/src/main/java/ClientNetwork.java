import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientNetwork {
    private Socket mSocket;
    private DataInputStream mIn;
    private DataOutputStream mOut;
    private Callback<String> mCallOnMsgReceived;
    private Callback<String> mCallOnChangeClientList;
    private Callback<String> mCallOnAuth;
    private Callback<String> mCallOnError;


    public void connect() {
        try {
            mSocket = new Socket("localhost", 8189);
            mIn = new DataInputStream(mSocket.getInputStream());
            mOut = new DataOutputStream(mSocket.getOutputStream());
            new Thread(() -> {
                boolean goOn = true;
                boolean isAuthorise = false;
                try {
                    while (!isAuthorise && goOn) {
                        String message = mIn.readUTF();
                        if (message.startsWith("/aut")){
                            mCallOnAuth.callback("/authok ");
                            isAuthorise = true;
                        } else if(message.equalsIgnoreCase("/end")) {
                            goOn = false;
                        }
                        else {
                            mCallOnError.callback("You login or password is wrong");
                        }
                    }

                    while (goOn) {
                        String msg = mIn.readUTF();
                        if (msg.equalsIgnoreCase("/end")) {
                            goOn = false;
                        } else if (msg.startsWith("/clients")) {
                            mCallOnChangeClientList.callback(msg.substring(8));
                        } else {
                            mCallOnMsgReceived.callback(msg);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    closeConnect();
                }
            }).start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean sendMessage(String msg) {
        try {
            mOut.writeUTF(msg);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void closeConnect() {
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

    public void setCallOnError(Callback<String> callOnError) {
        mCallOnError = callOnError;
    }

    public void setCallOnAuth(Callback<String> callOnAuth) {
        mCallOnAuth = callOnAuth;
    }

    public void setCallOnMsgReceived(Callback<String> callOnMsgReceived) {
        mCallOnMsgReceived = callOnMsgReceived;
    }

    public void setCallOnChangeClientList(Callback<String> callOnChangeClientList) {
        mCallOnChangeClientList = callOnChangeClientList;
    }
}
