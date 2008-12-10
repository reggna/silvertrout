package silvertrout.plugins;

import silvertrout.Network;
import silvertrout.User;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class DCCFileSender implements Runnable {

    Socket socket;
    ServerSocket serverSocket;
    Network network;
    User recipient;
    File file;
    OutputStream os;
    FileInputStream fis;

    public DCCFileSender(File file, User recipient, Network network) {

        if (file.exists() && file.isFile() && file.canRead()) {
            try {
                this.file = file;
                this.recipient = recipient;
                this.network = network;
                fis = new FileInputStream(file);

                new Thread(this).start();
            } catch (java.io.FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {

            System.out.println("DCCFileSender: File (" + file.getAbsolutePath() + file.getName() + ") can't be found or read");
        }
    }
    // TODO: use User instead

    public DCCFileSender(String filename, User recipient, Network network) {
        this(new File(filename), recipient, network);
    }

    @Override
    public void run() {

        try {
            System.out.println("DCCFileSender: Setting up things");
            // Set up listener:
            serverSocket = new ServerSocket();
            serverSocket.bind(null, 1);
            int listenPort = serverSocket.getLocalPort();

            // Inform user:

            System.out.println(file.getName());
            System.out.println(serverSocket.getLocalPort());
            System.out.println(serverSocket.getInetAddress().getHostAddress());
            System.out.println(file.length());
            String message = new String("DCC SEND " + file.getName() + " " + "3238780477" + " " + serverSocket.getLocalPort() + " " + file.length());

            String ctcpMessage = CTCP.quote(message);

            network.sendPrivmsg(recipient.getNickname(), ctcpMessage);

            System.out.println("DCCFileSender: Sent message: " + message);

            // Try to get connection:
            socket = serverSocket.accept();
            os = socket.getOutputStream();
            serverSocket.close();

            System.out.println("DCCFileSender: Have connection");

            // Put out file:
            long bytesSent = 0;
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer, 0, 1024)) != -1) {
                os.write(buffer, 0, bytesRead);
                bytesSent += bytesRead;
            }

            System.out.println("DCCFileSender: Done sending file");

        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
}
