package pl.agh.distributedSystems.lab1.chat.message;

import org.junit.Test;

import java.io.UnsupportedEncodingException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by Ola on 2016-03-09.
 */
public class MessageTest {
    @Test
    public void messageTest()  {
        Message message = new Message("Ola", "Java");

        byte[] datagram = new byte[1];
        try{
            datagram = MessageWrapper.wrapMessageToByteArray(message);
        } catch (UnsupportedEncodingException e) {
            fail("Encoding not supported");
        }
        assertEquals(42, datagram.length);
        Message message2 = new Message();
        try{
            message2 = MessageWrapper.unwrapMessageFromByteArray(datagram);
        } catch (Exception e) {
            fail("Messages don't match");
        }
        assertEquals(message.getNick(), message2.getNick());
        assertEquals(message.getData(), message2.getData());
        assertEquals(message.getDate(), message2.getDate());
    }

}