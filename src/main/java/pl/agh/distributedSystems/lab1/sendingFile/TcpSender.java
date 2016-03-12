package pl.agh.distributedSystems.lab1.sendingFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.agh.distributedSystems.lab1.sendingFile.message.FileToSend;
import pl.agh.distributedSystems.lab1.sendingFile.message.FileToSendWrapper;

import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Ola on 2016-03-12.
 */
public class TcpSender {
    private static final Logger log = LoggerFactory.getLogger(TcpSender.class);
    public static void main(String[] args){
        if (args.length != 2) {
            System.out.println("Input parameters: <IP> <port>");
            System.exit(-1);
        }
        InetAddress address = null;
        try {
            address = InetAddress.getByName(args[0]);
        } catch (UnknownHostException e) {
            log.error("No such host: {}", args[0]);
            System.exit(-1);
        }
        int port = Integer.parseInt(args[1]);
        Console c = System.console();
        try(Socket socket = new Socket(address, port)){
            try(OutputStream out = socket.getOutputStream()){
                while(true){
                    String filename = c.readLine();
                    File file = new File(filename);
                    if(!file.exists()) {
                        System.out.println("No such file!");
                    } else {
                        FileToSend fileToSend = new FileToSend(file);
                        byte[] data = FileToSendWrapper.wrapMessageToByteArray(fileToSend);
                        out.write(data);
                    }
                }
            }
        } catch (IOException e) {
            log.error("Socket error: {}", e);
        }
    }
}
