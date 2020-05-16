package clientFiles;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MessageField {
    JTextArea messageField;
    JScrollPane messageScroll;

    String backgroundColor = "#000000";
    String fieldBgColor = "#f1e3cb";
    String fontColor = "#000000";
    String buttonColor = "#FFFFFF";
    String borderColor ="#f9b384";
    String activeTabColor ="#F0F000";


    public void createTab() {
        messageField = new JTextArea();
        messageScroll = new JScrollPane(messageField, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        messageField.setEditable(false);
        messageField.setLineWrap(true);
        messageField.setWrapStyleWord(true);

        messageField.setBorder(new BevelBorder(BevelBorder.LOWERED));
        messageField.setMargin(new Insets(5, 5, 5, 5));

        messageScroll.setBorder(new EmptyBorder(0,0,0,5));
        messageScroll.setMinimumSize(new Dimension(250, 700));
        messageScroll.setPreferredSize(new Dimension(250, 700));


        messageScroll.setBackground(Color.decode(backgroundColor));
        messageScroll.setForeground(Color.decode(fontColor));

        messageField.setBackground(Color.decode(fieldBgColor));
        messageField.setForeground(Color.decode(fontColor));



    }
}