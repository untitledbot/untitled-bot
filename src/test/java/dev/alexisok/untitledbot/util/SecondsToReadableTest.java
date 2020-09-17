package dev.alexisok.untitledbot.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SecondsToReadableTest {
    
    @Test
    void convert() {
        System.out.printf("%s%n", SecondsToReadable.convert(1));
        System.out.printf("%s%n", SecondsToReadable.convert(2));
        System.out.printf("%s%n", SecondsToReadable.convert(8));
        System.out.printf("%s%n", SecondsToReadable.convert(61));
        System.out.printf("%s%n", SecondsToReadable.convert(65));
        System.out.printf("%s%n", SecondsToReadable.convert(1872));
        System.out.printf("%s%n", SecondsToReadable.convert(43287));
        System.out.printf("%s%n", SecondsToReadable.convert(342873));
        System.out.printf("%s%n", SecondsToReadable.convert(2877827340L));
    }
}
