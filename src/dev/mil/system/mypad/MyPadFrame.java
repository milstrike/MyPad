package dev.mil.system.mypad;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MyPadFrame extends JFrame {
    private DefaultListModel<String[]> noteListModel = new DefaultListModel<>();
    private JList<String[]> noteList = new JList<>(noteListModel);
    private JLabel noteTitleLabel;
    private JTextArea noteContentArea;
    private JButton saveButton;
    private JButton newNoteButton;
    private JButton printButton;
    private JButton deleteButton;
    private NoteDatabase database = new NoteDatabase();
    private boolean isNewNote = false;
    
    
    public MyPadFrame() {
        setTitle("MyPad");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(200, 600));

        JLabel listTitle = new JLabel("MyPad List");
        listTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
        listTitle.setBorder(BorderFactory.createEmptyBorder(0, 2, 4, 2));
        listTitle.setHorizontalAlignment(SwingConstants.LEFT);

        noteListModel = new DefaultListModel<>();
        noteList = new JList<>(noteListModel);
        
        noteList.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
            panel.setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());

            JLabel title = new JLabel(value[0]);
            title.setFont(title.getFont().deriveFont(Font.BOLD));

            JLabel date = new JLabel(value[1]);
            date.setFont(date.getFont().deriveFont(Font.ITALIC, 10f));
            date.setForeground(Color.GRAY);

            panel.add(title, BorderLayout.NORTH);
            panel.add(date, BorderLayout.SOUTH);
            return panel;
        });


        JPanel paddedListPanel = new JPanel(new BorderLayout());
        paddedListPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        paddedListPanel.add(noteList, BorderLayout.CENTER);

        JScrollPane listScroll = new JScrollPane(paddedListPanel);

        leftPanel.add(listTitle, BorderLayout.NORTH);
        leftPanel.add(listScroll, BorderLayout.CENTER);

        JPanel spacer = new JPanel();
        spacer.setPreferredSize(new Dimension(5, 1));
        spacer.setOpaque(false);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());
        rightPanel.setPreferredSize(new Dimension(590, 600));

        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBorder(BorderFactory.createEmptyBorder(0, 2, 4, 2)); 

        noteTitleLabel = new JLabel("Please choose a notes first");
        noteTitleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        noteTitleLabel.setHorizontalAlignment(SwingConstants.LEFT);

        noteContentArea = new JTextArea();
        noteContentArea.setLineWrap(true);
        noteContentArea.setWrapStyleWord(true);
        noteContentArea.setFont(new Font("SansSerif", Font.PLAIN, 13));
        noteContentArea.setMargin(new Insets(2, 2, 2, 2));

        JScrollPane contentScroll = new JScrollPane(noteContentArea);
        contentScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        rightPanel.add(titleBar, BorderLayout.NORTH);
        rightPanel.add(contentScroll, BorderLayout.CENTER);
        
        saveButton = new JButton("Save");
        saveButton.addActionListener(new SaveNoteAction(noteContentArea, noteTitleLabel, noteListModel, database, this));
        saveButton.setPreferredSize(new Dimension(80, 30));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        buttonPanel.add(saveButton);

        newNoteButton = new JButton("New Notes");
        newNoteButton.setPreferredSize(new Dimension(100, 30));
        newNoteButton.addActionListener(e -> {
            noteContentArea.setText("");
            noteTitleLabel.setText("New Note...");
            noteList.clearSelection();
            isNewNote = true;
            deleteButton.setVisible(false);
        });
        buttonPanel.add(newNoteButton);
        
        printButton = new JButton("Print");
        printButton.setPreferredSize(new Dimension(80, 30));
        printButton.setVisible(false);
        printButton.addActionListener(e -> {
            try {
                noteContentArea.print();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Print failed: " + ex.getMessage());
            }
        });
        buttonPanel.add(printButton);
        
        deleteButton = new JButton("Delete");
        deleteButton.setPreferredSize(new Dimension(80, 30));
        deleteButton.setVisible(false); 
        deleteButton.addActionListener(e -> {
            String[] selected = noteList.getSelectedValue();
            if (selected != null) {
                int confirm = JOptionPane.showConfirmDialog(this, "Are you sure remove this file from MyPad?", "Notes Removal", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    String title = selected[0];
                    int index = database.getIndexByTitle(title);
                    if (index >= 0) {
                        String reference = database.getReferenceByIndex(index);
                        FileTransaction.deleteFile(reference);
                        database.deleteNote(index);
                        noteListModel.removeElementAt(index);
                        noteContentArea.setText("");
                        noteTitleLabel.setText("Please choose a notes first");
                        deleteButton.setVisible(false);
                        printButton.setVisible(false);
                    }
                }
            }
        });
        buttonPanel.add(deleteButton);

        titleBar.add(noteTitleLabel, BorderLayout.CENTER);
        titleBar.add(buttonPanel, BorderLayout.EAST);

        
        for (String[] pair : database.getTitleAndDates()) {
            noteListModel.addElement(pair);
        }

        noteList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String[] selected = noteList.getSelectedValue();
                if (selected != null) {
                    String title = selected[0];
                    noteTitleLabel.setText(title);
                    int index = database.getIndexByTitle(title);
                    if (index >= 0) {
                        String reference = database.getReferenceByIndex(index);
                        String content = FileTransaction.readTextFromFile(reference);
                        noteContentArea.setText(content);
                        deleteButton.setVisible(true);
                    }
                    else{
                        deleteButton.setVisible(false);
                    }
                }
            }
        });
        
        noteList.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
            panel.setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());

            JLabel title = new JLabel(value[0]);
            title.setFont(title.getFont().deriveFont(Font.BOLD));

            String formattedDate = value[1];
            try {
                LocalDateTime dateTime = LocalDateTime.parse(value[1], DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                formattedDate = dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
            } catch (Exception ignored) {
                // Use the original string if parsing fails
            }

            JLabel date = new JLabel(formattedDate);
            date.setFont(date.getFont().deriveFont(Font.ITALIC, 10f));
            date.setForeground(Color.GRAY);

            panel.add(title, BorderLayout.NORTH);
            panel.add(date, BorderLayout.SOUTH);
            return panel;
        });

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.X_AXIS));
        contentPanel.add(leftPanel);
        contentPanel.add(spacer);
        contentPanel.add(rightPanel);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        add(mainPanel);

        setVisible(true);
        
        noteContentArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void onContentChanged() {
                String text = noteContentArea.getText().trim();
                printButton.setVisible(!text.isEmpty());

                if (noteList.getSelectedIndex() == -1 && !text.isEmpty() && !isNewNote) {
                    noteTitleLabel.setText("New Note...");
                    isNewNote = true;
                }
            }

            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                onContentChanged();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                onContentChanged();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                onContentChanged();
            }
        });


        setJMenuBar(new MyPadMenuBar(this));
    }
    
    public boolean isNewNote() {
        return isNewNote;
}

    public void setNewNote(boolean value) {
        this.isNewNote = value;
    }
}