package pl.agh.distributedSystems.lab1.sendingFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.agh.distributedSystems.lab1.sendingFile.message.FileToSendWrapper;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Ola on 2016-03-12.
 */
public class TcpSender {
    private static final Logger log = LoggerFactory.getLogger(TcpSender.class);

    public static void main(String[] args){
        if (args.length != 3) {
            System.out.println("Input parameters: <IP> <port> <filename>");
            System.exit(-1);
        }
        InetAddress address = null;
        String filename = args[2];
        try {
            address = InetAddress.getByName(args[0]);
        } catch (UnknownHostException e) {
            log.error("No such host: {}", args[0]);
            System.exit(-1);
        }
        int port = Integer.parseInt(args[1]);
        try(Socket socket = new Socket(address, port)){
            try(OutputStream out = socket.getOutputStream()){
                File file = new File(filename);
                if(!file.exists()) {
                    System.out.println("No such file!");
                } else {
                    byte[] filenameByteArr = FileToSendWrapper.wrapFilenameToByteArray(filename);
                    out.write(filenameByteArr);
                    FileToSendWrapper.sendFile(file, out);
                }
            }
        } catch (IOException e) {
            System.out.println("Socket error: "+e);
        }
    }
}
