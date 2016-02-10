package fr.ecp.sio.gameout.salon.message;

/**
 * Created by od on 2/10/2016.
 */
public class PleaseContactServerMessage extends  Message {
    public static final String TAG = PleaseContactServerMessage.class.getSimpleName();
    private String serverInetAddress;

    public PleaseContactServerMessage(){}

    public PleaseContactServerMessage(String serverInetAddress){
        super(TAG);
        this.serverInetAddress = serverInetAddress;
    }

    public String getServerInetAddress() {
        return serverInetAddress;
    }

    public void setServerInetAddress(String serverInetAddress) {
        this.serverInetAddress = serverInetAddress;
    }
}
