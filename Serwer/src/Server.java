import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server  implements Runnable{
    private ArrayList<ConnectionHandler> connections;
    @Override
    public void run(){
        try {
            ServerSocket serverSocket = new ServerSocket(1234);
            Socket client = serverSocket.accept();
            ConnectionHandler handler = new ConnectionHandler(client);
            connections.add(handler);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void broadcast(String message) throws IOException {
        for(ConnectionHandler ch : connections){
            if(ch != null){
                ch.sendMessage(message);
            }
        }
    }
    class ConnectionHandler implements Runnable {
        private Socket client;
        private InputStreamReader inputStreamReader = null;
        private OutputStreamWriter outputStreamWriter = null;
        private BufferedReader bufferedReader = null;
        private BufferedWriter bufferedWriter = null;
        private String nickname;
        public ConnectionHandler(Socket client) throws IOException {
            this.client = client;
            broadcast(nickname + " connected!");
        }
        @Override
        public void run(){
            try {

                inputStreamReader = new InputStreamReader(client.getInputStream());
                outputStreamWriter = new OutputStreamWriter(client.getOutputStream());

                bufferedWriter = new BufferedWriter(outputStreamWriter);
                bufferedReader = new BufferedReader(inputStreamReader);

                bufferedWriter.write("Please enter a nickname: ");
                nickname = bufferedReader.readLine();
                System.out.println(nickname + " joined the chat>");

                while (true) {
                    String msgFromClient = bufferedReader.readLine();

                    System.out.println("Client: " + msgFromClient);

                    bufferedWriter.write("MSG Received");
                    bufferedWriter.newLine();
                    bufferedWriter.flush();

                    if (msgFromClient.equalsIgnoreCase("BYE")) {
                        break;
                    }
                }

                client.close();
                inputStreamReader.close();;
                outputStreamWriter.close();
                bufferedWriter.close();
                bufferedReader.close();

            }catch (IOException e){
                e.printStackTrace();
            }
        }
        public void sendMessage(String message) throws IOException {
            bufferedWriter.write(message);
        }
    }
}
