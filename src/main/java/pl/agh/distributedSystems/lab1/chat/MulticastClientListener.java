package pl.agh.distributedSystems.lab1.chat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.agh.distributedSystems.lab1.chat.message.Message;
import pl.agh.distributedSystems.lab1.chat.message.MessageWrapper;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.concurrent.Callable;

/**
 * Created by Ola on 2016-03-11.
 */
public class MulticastClientListener implements Callable<Integer> {
    private static final Logger log = LoggerFactory.getLogger(MulticastClientListener.class);
    private InetAddress groupAddress;
    private int port;
    private String login;
    public MulticastClientListener(InetAddress groupAddress, int port, String login){
        this.groupAddress = groupAddress;
        this.port=port;
        this.login=login;
    }
    @Override
    public Integer call() throws Exception {
        log.debug("Listener is running");
        try(MulticastSocket socket = new MulticastSocket(port)){
            socket.joinGroup(groupAddress);
            while(true){
                byte[] buffer = new byte[61];
                DatagramPacket recv = new DatagramPacket(buffer, buffer.length);
                log.debug("waiting for receive");
                socket.receive(recv);
                log.debug("Message received");
                Message message = MessageWrapper.unwrapMessageFromByteArray(recv.getData());
                try{
                    MessageWrapper.checkChecksum(message);
                    if(!login.equals(message.getNick())){
                        String text = String.format("%s %s: %s", message.getDate(), message.getNick(), message.getData());
                        System.out.println(text);
                    }
                } catch(Exception e){
                    System.out.println("Wrong checksum!");
                }
            }
        }

    }
}
