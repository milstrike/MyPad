package dev.mil.system.mypad;

import java.io.*;
import java.util.*;

public class NoteDatabase {
    private static final String DB_FILE = "notes_db.csv";
    private final List<String[]> rows = new ArrayList<>();

    public NoteDatabase() {
        load();
    }

    private void load() {
        File file = new File(DB_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            rows.clear();
            String line;
            while ((line = reader.readLine()) != null) {
                rows.add(line.split(",", -1));
            }
        } catch (IOException ignored) {}
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DB_FILE))) {
            for (String[] row : rows) {
                writer.write(String.join(",", row));
                writer.newLine();
            }
        } catch (IOException ignored) {}
    }

    public void addNote(String title, String reference, String date) {
        int newId = rows.size() + 1;
        rows.add(new String[]{String.valueOf(newId), title, reference, date});
        save();
    }

    public int getIndexByTitle(String title) {
        for (int i = 0; i < rows.size(); i++) {
            if (rows.get(i)[1].equals(title)) return i;
        }
        return -1;
    }

    public String getReferenceByIndex(int index) {
        return rows.get(index)[2];
    }

    public void updateDate(int index, String newDate) {
        rows.get(index)[3] = newDate;
        save();
    }
    
    public List<String> getAllTitles() {
        List<String> titles = new ArrayList<>();
        for (String[] row : rows) {
            if (row.length >= 2) {
                titles.add(row[1]);
            }
        }
        return titles;
    }
    
    public List<String[]> getTitleAndDates() {
        List<String[]> result = new ArrayList<>();
        for (String[] row : rows) {
            if (row.length >= 4) {
                result.add(new String[]{row[1], row[3]}); 
            }
        }
        return result;
    }
    
    public void deleteNote(int index) {
        if (index >= 0 && index < rows.size()) {
            rows.remove(index);
            save();
        }
    }



}
