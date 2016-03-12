package pl.agh.distributedSystems.lab1.sendingFile.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

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
     *	| size of filename	| filename		| size of file  	| file		|
     *  | 4 bytes		    | x bytes	    | 4 bytes		    | y bytes	|
     * Checksum is counted using CRC32
     * @return byte array containing information to send
     */
    public static byte[] wrapMessageToByteArray(Message message) throws UnsupportedEncodingException {
        byte[] filename = message.getFilename().getBytes("US-ASCII");
        byte[] file = message.getFile();
        int dataSize = file.length+filename.length+4+4;
        logger.debug("File to wrap: {}, size: {}", message.getFilename(), message.getFile().length);

        ByteBuffer buffer = ByteBuffer.allocate(dataSize);
        buffer.putInt(filename.length);
        buffer.put(filename);
        buffer.putInt(file.length);
        buffer.put(file);
        logger.debug("Wrapped message: {}", message.toString());
        return buffer.array();
    }
    public static Message unwrapMessageFromByteArray(byte[] givenDatagram) {
        logger.debug("Datagram to unwrap length: {}", givenDatagram.length);
        int fieldSize;
        byte[] filename;
        byte[] file;

        ByteBuffer buffer = ByteBuffer.wrap(givenDatagram);
        buffer.position(0);

        fieldSize = buffer.getInt();
        logger.debug("Filename size: {}", fieldSize);
        filename=readField(fieldSize, buffer);

        fieldSize = buffer.getInt();
        logger.debug("File size: {}", fieldSize);
        file = readField(fieldSize, buffer);

        Message message = new Message(new String(filename), file);
        logger.debug("Unwrapped message: {}", message.getFilename());
        return message;
    }
    private static byte[] readField(int fieldSize, ByteBuffer buffer) {
        byte[] arr = new byte[fieldSize];
        buffer.get(arr, 0, fieldSize);
        return arr;
    }
}
