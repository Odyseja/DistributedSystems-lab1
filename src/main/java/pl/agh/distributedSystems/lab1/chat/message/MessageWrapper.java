package pl.agh.distributedSystems.lab1.chat.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.CRC32;

/**
 * Created by Ola on 2016-03-10.
 */
public class MessageWrapper {
    private static final Logger logger = LoggerFactory.getLogger(MessageWrapper.class);
    private static DateFormat format;
    private static String dateFormat = "yyyy-mm-dd hh:mm:ss";

    static{
        format = new SimpleDateFormat(dateFormat);
    }

    /**Returns datagram for given dataArr in the following form:
     *	| size of nickArr	| nickArr		| size of dataArr	| dataArr			| dateArr		| checksum	|
     *  | 4 bytes		    | <=6 bytes	    | 4 bytes		    | <=20 bytes	    | 19 bytes	    | 8 bytes	|
     * Checksum is counted using CRC32
     * @return byte array containing information to send with UDP
     */
    public static byte[] wrapMessageToByteArray(Message message) throws UnsupportedEncodingException {
        byte[] dataArr = message.getData().getBytes("US-ASCII");
        byte[] nickArr = message.getNick().getBytes("US-ASCII");
        byte[] dateArr = format.format(new Date()).getBytes("US-ASCII");
        long checksum;
        int datagramSize = dataArr.length+nickArr.length+4+4+19;

        message.setDate(new String(dateArr));
        logger.debug("Message to wrap: {}", message.toString());

        ByteBuffer bufferWithoutChecksum = ByteBuffer.allocate(datagramSize);
        bufferWithoutChecksum.putInt(nickArr.length);
        bufferWithoutChecksum.put(nickArr);
        bufferWithoutChecksum.putInt(dataArr.length);
        bufferWithoutChecksum.put(dataArr);
        bufferWithoutChecksum.put(dateArr);
        checksum=countChecksum(bufferWithoutChecksum.array());
        message.setChecksum(checksum);

        ByteBuffer buffer = ByteBuffer.allocate(datagramSize+8);
        buffer.put(bufferWithoutChecksum.array());
        buffer.putLong(checksum);
        message.setDatagram(buffer.array());
        logger.debug("Wrapped message: {}", message.toString());
        return buffer.array();
    }
    public static Message unwrapMessageFromByteArray(byte[] givenDatagram) {
        logger.debug("Datagram to unwrap length: {}", givenDatagram.length);
        int fieldSize;
        long checksum;
        byte[] nickArr;
        byte[] dataArr;
        byte[] dateArr;

        ByteBuffer buffer = ByteBuffer.wrap(givenDatagram);
        buffer.position(0);

        fieldSize = buffer.getInt();
        logger.debug("Nick size: {}", fieldSize);
        nickArr=readField(fieldSize, buffer);

        fieldSize = buffer.getInt();
        logger.debug("Data size: {}", fieldSize);
        dataArr = readField(fieldSize, buffer);

        fieldSize = 19;
        dateArr = readField(fieldSize, buffer);
        checksum = buffer.getLong();

        Message message = new Message(new String(nickArr), new String(dataArr), new String(dateArr), checksum);
        message.setDatagram(givenDatagram);
        logger.debug("Unwrapped message: {}", message.toString());
        return message;
    }
    private static byte[] readField(int fieldSize, ByteBuffer buffer) {
        byte[] arr = new byte[fieldSize];
        buffer.get(arr, 0, fieldSize);
        return arr;
    }
    public static Long countChecksum(byte[] datagramMessage){
        logger.debug("Datagram's length to count checksum: {}", datagramMessage.length);
        CRC32 checksumCounter = new CRC32();
        checksumCounter.update(datagramMessage);
        return checksumCounter.getValue();
    }
    public static void checkChecksum(byte[] givenDatagram, long checksum) throws Exception {
        long countChecksum = countChecksum(givenDatagram);
        logger.debug("Expected checksum: {}, actual checksum: {}", checksum, countChecksum);

        if(countChecksum != checksum){
            logger.error("Wrong checksum!");
            throw(new Exception("Wrong checksum, data will not be read properly!"));
        }
    }
    public static void checkChecksum(Message message) throws Exception {
        byte[] givenDatagram = message.getDatagram();
        byte[] buff = new byte[61];

        ByteBuffer buffer = ByteBuffer.wrap(givenDatagram);
        int nickSize = buffer.getInt();
        buffer.get(buff, 0, nickSize);
        int dataSize = buffer.getInt();

        int datagramSize=nickSize+dataSize+19+8+4+4;
        ByteBuffer bufferWithoutChecksum = ByteBuffer.allocate(datagramSize-8);
        bufferWithoutChecksum.put(givenDatagram, 0, datagramSize-8);
        long checksum = buffer.getLong(datagramSize-8);
        checkChecksum(bufferWithoutChecksum.array(), checksum);
    }
}
