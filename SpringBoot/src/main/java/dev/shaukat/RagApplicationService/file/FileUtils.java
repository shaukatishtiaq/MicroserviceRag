package dev.shaukat.RagApplicationService.file;

public class FileUtils {
    public static String getFormattedFilename(String filename){
        return filename.replace(" ", "_");
    }
}
