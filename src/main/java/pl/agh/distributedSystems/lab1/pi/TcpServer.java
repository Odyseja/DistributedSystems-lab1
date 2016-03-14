package pl.agh.distributedSystems.lab1.pi;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Ola on 2016-03-12.
 */
public class TcpServer {
    public static void main(String[] args){
        if (args.length != 1) {
            System.out.println("Input parameters: <port> ");
            System.exit(-1);
        }
        int port = Integer.parseInt(args[0]);
        try(ServerSocket socket = new ServerSocket(port)){
            while(true){
                System.out.println("waiting for clients");
                try(Socket clientSocket = socket.accept()){
                    byte[] buff = new byte[8];
                    try(InputStream in = clientSocket.getInputStream()) {
                        try (OutputStream out = clientSocket.getOutputStream()) {
                            System.out.println("Waiting for message");
                            int size = in.read(buff);
                            long num = NumberConverter.getNumber(buff, size);
                            System.out.println("I've got "+num+" with size: "+size);
                            byte piDigit = PiCounter.getPiDigit(num);
                            System.out.println("Sending back " + piDigit);
                            out.write(new byte[]{piDigit});
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Socket error: "+e);
        }
    }
}
