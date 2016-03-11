package pl.agh.distributedSystems.lab1.chat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Console;
import java.io.IOException;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import pl.agh.distributedSystems.lab1.chat.message.Message;
import pl.agh.distributedSystems.lab1.chat.message.MessageWrapper;

public class MulticastUdpClient {
	private static final Logger log = LoggerFactory.getLogger(MulticastUdpClient.class);

	public static void main(String[] args) throws UnknownHostException {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        if (args.length != 3) {
            System.out.println("Input parameters: <IP> <port> <login>");
            System.exit(-1);
        }
        Console c = System.console();
        InetAddress groupAddress = InetAddress.getByName(args[0]);
        int port = Integer.parseInt(args[1]);
        String login = args[2];
		try(MulticastSocket socket = new MulticastSocket(port)) {
            socket.joinGroup(groupAddress);
            MulticastClientListener listener = new MulticastClientListener(groupAddress, port, login);
            executor.submit(listener);
            while(true){
                String text = c.readLine("");
                Message message = new Message();
                message.setData(text);
                message.setNick(login);
                sendMessage(message, socket, groupAddress, port);
            }
		} catch (IOException e) {
			log.error(e.getMessage());
		} finally {
            executor.shutdown();
        }
	}
    private static void sendMessage(Message message, MulticastSocket socket, InetAddress groupAddress, int port) throws IOException {
        byte[] data = MessageWrapper.wrapMessageToByteArray(message);
        socket.send(new DatagramPacket(data, data.length, groupAddress, port));
    }
}