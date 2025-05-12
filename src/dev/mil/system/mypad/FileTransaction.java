package dev.mil.system.mypad;

import java.io.*;
import java.security.*;
import java.util.UUID;

public class FileTransaction {

    public static String saveTextToFile(String content) {
        String uniqueName = generateUniqueHash() + ".txt";
        File file = new File(uniqueName);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(content);
        } catch (IOException ignored) {}

        return uniqueName;
    }

    public static void overwriteFile(String fileName, String content) {
        File file = new File(fileName);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(content);
        } catch (IOException ignored) {}
    }

    private static String generateUniqueHash() {
        try {
            String base = UUID.randomUUID().toString() + System.nanoTime();
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(base.getBytes());
            StringBuilder hex = new StringBuilder();
            for (byte b : digest) {
                hex.append(String.format("%02x", b));
            }
            return hex.substring(0, 20);
        } catch (Exception e) {
            return UUID.randomUUID().toString().replace("-", "");
        }
    }
    
    public static String readTextFromFile(String fileName) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException ignored) {}
        return sb.toString();
    }
    
    public static void deleteFile(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            file.delete();
        }
    }
}
