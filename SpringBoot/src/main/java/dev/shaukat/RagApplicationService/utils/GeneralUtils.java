package dev.shaukat.RagApplicationService.utils;

public class GeneralUtils {
    public static String maskString(String input){
        if(input == null || input.length() <= 4)
            return "*".repeat(input.length());

        String toMask = "*".repeat(input.length() - 4);
        String toKeep = input.substring(input.length() -4 );

        return toMask + toKeep;
    }
}
