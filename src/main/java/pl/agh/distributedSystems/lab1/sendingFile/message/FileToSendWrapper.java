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
public class FileToSendWrapper {
    private static final Logger logger = LoggerFactory.getLogger(FileToSendWrapper.class);
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
    public static byte[] wrapMessageToByteArray(FileToSend fileToSend) throws UnsupportedEncodingException {
        byte[] filename = fileToSend.getFilename().getBytes("US-ASCII");
        byte[] file = fileToSend.getFile();
        int dataSize = file.length+filename.length+4+4;
        logger.debug("File to wrap: {}, size: {}", fileToSend.getFilename(), fileToSend.getFile().length);

        ByteBuffer buffer = ByteBuffer.allocate(dataSize);
        buffer.putInt(filename.length);
        buffer.put(filename);
        buffer.putInt(file.length);
        buffer.put(file);
        logger.debug("Wrapped fileToSend: {}", fileToSend.toString());
        return buffer.array();
    }
    public static FileToSend unwrapMessageFromByteArray(byte[] givenDatagram) {
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

        FileToSend fileToSend = new FileToSend(new String(filename), file);
        logger.debug("Unwrapped fileToSend: {}", fileToSend.getFilename());
        return fileToSend;
    }
    private static byte[] readField(int fieldSize, ByteBuffer buffer) {
        byte[] arr = new byte[fieldSize];
        buffer.get(arr, 0, fieldSize);
        return arr;
    }
}
