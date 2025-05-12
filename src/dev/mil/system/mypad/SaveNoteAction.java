package dev.mil.system.mypad;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SaveNoteAction implements ActionListener {
    private final JTextArea textArea;
    private final JLabel titleLabel;
    private final DefaultListModel<String[]> noteListModel;
    private final NoteDatabase database;
    private final MyPadFrame frame;

    public SaveNoteAction(JTextArea textArea, JLabel titleLabel, DefaultListModel<String[]> model, NoteDatabase db, MyPadFrame frame) {
        this.textArea = textArea;
        this.titleLabel = titleLabel;
        this.noteListModel = model;
        this.database = db;
        this.frame = frame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String currentTitle = titleLabel.getText().trim();
        String content = textArea.getText();

        if (frame.isNewNote() || currentTitle.equals("New Note...")) {
            String inputTitle = JOptionPane.showInputDialog(null, "Enter title for the note:");
            if (inputTitle != null && !inputTitle.trim().isEmpty()) {
                String fileHash = FileTransaction.saveTextToFile(content);
                String date = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                database.addNote(inputTitle.trim(), fileHash, date);
                noteListModel.addElement(new String[]{inputTitle.trim(), date});
                titleLabel.setText(inputTitle.trim());
                frame.setNewNote(false); 
            }
        } else {
            int index = database.getIndexByTitle(currentTitle);
            if (index >= 0) {
                String reference = database.getReferenceByIndex(index);
                FileTransaction.overwriteFile(reference, content);
                String date = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                database.updateDate(index, date);
            }
        }
    }
}
