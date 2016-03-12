package pl.agh.distributedSystems.lab1.sendingFile.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by Ola on 2016-03-10.
 */
public class FileToSendWrapper {
    private static final Logger logger = LoggerFactory.getLogger(FileToSendWrapper.class);
    private static final int BUFFLEN = 1024;
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
    public static byte[] wrapFilenameToByteArray(String filename) throws UnsupportedEncodingException {
        byte[] filenameArr = filename.getBytes("US-ASCII");
        int dataSize = filenameArr.length+4;
        logger.debug("File to wrap: {}", filename);

        ByteBuffer buffer = ByteBuffer.allocate(dataSize);
        buffer.putInt(filenameArr.length);
        buffer.put(filenameArr);
        return buffer.array();
    }

    public static void sendFile(File file, OutputStream out) throws IOException {
        byte[] dataChunk = new byte[BUFFLEN+1];
        try(BufferedInputStream buffIn = new BufferedInputStream(new FileInputStream(file))){
            int size = 0;
            while((size=buffIn.read(dataChunk, 0, BUFFLEN))>0){
                System.out.println("Chunk's size: "+size);
                dataChunk[size]=0;
                out.write(dataChunk, 0, size);
            }
        }
    }
}
