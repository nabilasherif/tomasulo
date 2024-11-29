package Core;

import java.io.*;
import java.util.*;

public class FileParser {

    public static List<String> fileParser(String filePath) {
        List<String> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return lines;
    }

    public static void main(String[] args) {
        // later we can add reading the file path from the os args
        String filePath = "src/main/java/Core/test1.txt";
        List<String> lines = fileParser(filePath); // Parse the file

        for (String line : lines) {
            System.out.println(line);
        }
    }
}
