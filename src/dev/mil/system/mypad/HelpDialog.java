package dev.mil.system.mypad;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class HelpDialog extends JDialog {
    private final JTextArea explanationArea;
    private final Map<String, String> helpTopics;

    public HelpDialog(JFrame parent) {
        super(parent, "Help - MyPad", true);
        setSize(600, 400);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));
        setResizable(false);

        helpTopics = new LinkedHashMap<>();
        helpTopics.put("New Notes",
            "What it is:\nCreate a brand new note that is not yet saved.\n\n" +
            "How to do it:\nClick the 'New Notes' button to start typing. Then click Save to store it.");

        helpTopics.put("Save",
            "What it is:\nSave your current note to disk.\n\n" +
            "How to do it:\nClick the 'Save' button. If it's a new note, you'll be prompted to name it.");

        helpTopics.put("Print",
            "What it is:\nSend your note to a printer.\n\n" +
            "How to do it:\nClick the 'Print' button. It appears only when text exists in the editor.");

        helpTopics.put("Delete",
            "What it is:\nRemoves a selected note permanently.\n\n" +
            "How to do it:\nSelect a note from the list, then click 'Delete'. You'll be asked to confirm.");

        helpTopics.put("Note List",
            "What it is:\nDisplays all saved notes with title and last updated time.\n\n" +
            "How to do it:\nSelect a note from the list to load it into the editor.");

        helpTopics.put("Editor",
            "What it is:\nMain writing area for your notes.\n\n" +
            "How to do it:\nStart typing directly. Word wrap and font are pre-configured.");

        helpTopics.put("Storage",
            "What it is:\nMyPad stores your notes in local files and a simple database.\n\n" +
            "How to do it:\nAll notes are saved as text files, and managed via CSV index automatically.");

        JList<String> topicList = new JList<>(helpTopics.keySet().toArray(new String[0]));
        topicList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane listScroll = new JScrollPane(topicList);
        listScroll.setPreferredSize(new Dimension(180, 0));

        explanationArea = new JTextArea();
        explanationArea.setEditable(false);
        explanationArea.setLineWrap(true);
        explanationArea.setWrapStyleWord(true);
        explanationArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        explanationArea.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane explanationScroll = new JScrollPane(explanationArea);
        explanationScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        topicList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selected = topicList.getSelectedValue();
                if (selected != null) {
                    explanationArea.setText(helpTopics.get(selected));
                    explanationArea.setCaretPosition(0);
                }
            }
        });

        topicList.setSelectedIndex(0);

        add(listScroll, BorderLayout.WEST);
        add(explanationScroll, BorderLayout.CENTER);
    }
}
