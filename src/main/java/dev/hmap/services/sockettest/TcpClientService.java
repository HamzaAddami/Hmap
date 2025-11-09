package dev.hmap.services.sockettest;

import dev.hmap.models.Message;
import org.apache.commons.net.SocketClient;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public class TcpClientService extends SocketClient {

    private BufferedReader reader;
    private PrintWriter printer;
    private Thread receiverTask;
    private boolean connected = false;

    private Consumer<Message> onReceiveMessage;
    private Consumer<String> onStatusChanged;
    private Consumer<String> onError;


    public TcpClientService() throws IOException {
        super();
        setDefaultTimeout(5000);
    }

    @Override
    public void connect(String host, int port) throws IOException {
        super.connect(host, port);

        reader = new BufferedReader(new InputStreamReader(_socket_.getInputStream(), StandardCharsets.UTF_8));
        printer = new PrintWriter(_socket_.getOutputStream(), true);

        connected = true;
        notifyStatusChanged("Connexion to " + host + ":" + port);

        startReceiving();

    }

    @Override
    public void disconnect(){
        connected = false;
        try {
            if(reader != null) {
                reader.close();
            }
            if(printer != null){
                printer.close();
            }
            if(receiverTask != null && receiverTask.isAlive()){
                receiverTask.interrupt();
            }

            super.disconnect();
            notifyStatusChanged("Disconnected");

        } catch (IOException e){
            notifyError("Error during disconnecting:" + e.getMessage());
        }
    }

    public void startReceiving() {
        receiverTask = new Thread(() -> {
            try{
                String line;
                while(connected && ( line = reader.readLine()) != null){
                    Message message = new Message(line, Message.Type.RECEIVED);
                    notifyMessageReceived(message);
                }

            } catch (IOException e){
                notifyError("Connexion lost :" + e.getMessage());
            }
        }, "TCP-RECEIVER-THREAD");
        receiverTask.setDaemon(true);
        receiverTask.start();

    }

    public void sendMessage(Message message){
        if(!isConnected()){
            notifyStatusChanged("Not connected to server");
        }
        if(printer != null){
            printer.write(message.getContent());
        }
        if(printer.checkError()){
            notifyError("Error sending the message");
        }
    }

    @Override
    public boolean isConnected(){
        return connected && _socket_.isConnected() && !_socket_.isClosed();
    }

    public String getLocalAdress() {
        if(_socket_ != null && _socket_.getLocalAddress() != null){
            return _socket_.getLocalAddress().getHostAddress();
        }
        return null;
    }

    @Override
    public int getLocalPort(){
        if(_socket_ != null){
            return _socket_.getLocalPort();
        }
        return 0;
    }

    // Notification methods
    private void notifyStatusChanged(String status){
        if(onReceiveMessage != null){
            onStatusChanged.accept(status);
        }
    }

    private void notifyMessageReceived(Message message){
        if(onReceiveMessage != null){
            onReceiveMessage.accept(message);
        }
    }

    private void notifyError(String errorMessage){
        if(onError != null){
            onError.accept(errorMessage);
        }
    }

    // Notification Setters
    public void setOnReceiveMessage(Consumer<Message> callback){
        this.onReceiveMessage = callback;
    }

    public void setOnStatusChanged(Consumer<String> callback){
        this.onStatusChanged = callback;
    }

    public void setOnError(Consumer<String> callback){
        this.onError = callback;
    }

}
