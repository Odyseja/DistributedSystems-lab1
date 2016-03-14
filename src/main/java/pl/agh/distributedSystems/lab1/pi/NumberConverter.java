package pl.agh.distributedSystems.lab1.pi;


import java.nio.ByteBuffer;

/**
 * Created by Ola on 2016-03-12.
 */
public class NumberConverter {
    public static long getNumber(byte[] arr, int size){
        ByteBuffer buff = ByteBuffer.wrap(arr);
        switch(size){
            case 1:
                return (long) buff.get();
            case 2:
                return (long) buff.getChar();
            case 4:
                return (long) buff.getInt();
            case 8:
                return buff.getLong();
            default:
                return -1;
        }
    }
}
