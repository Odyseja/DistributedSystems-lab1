package pl.agh.distributedSystems.lab1.pl.agh.distributedSystems.lab1.pi;

import java.util.PrimitiveIterator;

/**
 * Created by Ola on 2016-03-12.
 */
public class PiCounter {
    private static final int DIVISOR = 7;

    public static byte getPiDigit(long num) {
        int dividend=22;
        int ret=0;
        while(num>=1){
            ret=dividend/DIVISOR;
            dividend=10*(dividend%DIVISOR);
            num--;
        }
        return (byte) ret;
    }

}
