package Core;

import java.io.*;
import java.util.*;

public class FileParser {

    // Method to read the file and return lines in a List
    public static List<String> parseFile(String filePath) {
        List<String> lines = new ArrayList<>(); // List to store lines

        // Create a BufferedReader to read the file
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line); // Add each line to the list
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return lines;
    }

    public static void main(String[] args) {
        // later we can add reading the file path from the os args
        String filePath = "src/main/java/Core/test1.txt";
        List<String> lines = parseFile(filePath); // Parse the file

        // Print the lines
        for (String line : lines) {
            System.out.println(line);
        }
    }
}
