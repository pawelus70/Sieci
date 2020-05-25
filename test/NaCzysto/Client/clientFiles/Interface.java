/*Created by Gabriel Ćwiek and Paweł Blak
Last update date: 24.05.2020*/

package clientFiles;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.regex.Pattern;

/**
 * Interfejs Klienta
 * Okno:
 * @see #frame
 * Pole do pisania:
 * @see #textField
 * Przycisk wyslij:
 * @see #sendButton
 * Pole wiadomosci:
 * @see #messagesField
 * Wzor:
 * @see #pattern
 * Nazwa:
 * @see #name
 * Uzytkownicy:
 * @see #users
 * Status:
 * @see #userStatus
 * Menu dodatkowe, gorne:
 * @see #m2
 * @see #m3
 * @see #m21
 * @see #m22
 * @see #m23
 * @see #m31
 * @see #m32
 *
 * Ustawianie kolorow:
 * @see #backgroundColor
 * @see #fieldBgColor
 * @see #fontColor
 * @see #buttonColor
 * @see #borderColor
 * @see #activeTabColor
 *
 * @author Created by Gabriel Cwiek and Pawel Blak
 * Last update date: 24.05.2020
 */
public class Interface implements Runnable {
    JFrame frame;
    JTextField textField = new JTextField(); // Pole do pisania wiadomości
    JButton sendButton = new JButton("Send");
    JTextArea messagesField = new JTextArea();
    Pattern pattern = Pattern.compile("^[a-zA-Z0-9]+$");
    String name;
    JTextArea users;
    JTextArea userStatus;
    JMenu  m2, m3;
    JMenuItem  m21, m22,m23, m31, m32;
    JScrollPane messageScroll;
    JTabbedPane messageTabs;

    //Setup colors
    String backgroundColor = "#000000";
    String fieldBgColor = "#f1e3cb";
    String fontColor = "#000000";
    String buttonColor = "#FFFFFF";
    String borderColor = "#f9b384";
    String activeTabColor = "#F0F000";


    public class CustomSquare extends JPanel {
        int sizeX = 150;
        int sizeY = 30;
        String label = "";
        int fPosX = sizeX / 2;
        int fPosY = sizeY / 2;
        String fColor = "#000000";
        String bgColor = "#FFFFFF";
        int fSize = 10;
        boolean haveBorder = false;

        //plain square
        CustomSquare() {
        }

        //designated size square
        CustomSquare(int x, int y) {
            sizeX = x;
            sizeY = y;
        }

        //designated size and color square
        CustomSquare(int x, int y, String bgColor) {
            sizeX = x;
            sizeY = y;
            this.bgColor = bgColor;
        }

        //designated size and color square
        //what text,  text relative position,text size, text color
        CustomSquare(int x, int y, String bgColor, String label, int fSize, int fPosX, int fPosY, String fColor) {
            sizeX = x;
            sizeY = y;
            this.bgColor = bgColor;

            this.label = label;
            this.fPosX = fPosX;
            this.fPosY = fPosY;
            this.fColor = fColor;
            this.fSize = fSize;
        }


        protected void paintComponent(Graphics g) {

            //Wymuś rozmiar bo się pierdoli
            super.setMaximumSize(new Dimension(sizeX, sizeY));
            super.setMinimumSize(new Dimension(sizeX, sizeY));
            super.setPreferredSize(new Dimension(sizeX, sizeY));
            super.setSize(new Dimension(sizeX, sizeY));
            super.setBackground(Color.decode(backgroundColor));
            super.setBorder(new EmptyBorder(2, 2, 2, 2));
            super.paintComponent(g);


            g.setColor(Color.decode(bgColor));
            g.fillRect(0, 0, sizeX, sizeY);

            g.setColor(Color.decode(borderColor));
            g.drawRect(0, 0, sizeX - 1, sizeY - 1);
            g.drawRect(1, 1, sizeX - 2, sizeY - 2);

            if (!label.equals("")) {
                g.setFont(new Font("TimesRoman", Font.PLAIN, fSize));
                g.setColor(Color.decode(fColor));
                g.drawString(label, fPosX, fPosY);
            }

        }

    }


    public class mouseListner implements MouseListener {
        @Override
        public void mouseClicked(MouseEvent e) {

        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {
            ((JMenu) e.getSource()).doClick(30);
        }

        @Override
        public void mouseExited(MouseEvent e) {

        }

    }

    CustomSquare customSquare;

    /**
     * Input box na nazwe uzytkownika, jezeli nie poda wstaw anonymous, lub popraw sie jesli zle
     * @param firstTime - nick
     * @return name
     */
    public String inputBox(boolean firstTime) {
        String name = "";
        if (firstTime) {
            while (true) {
                name = JOptionPane.showInputDialog(frame, "Enter your name:", "Set your name", JOptionPane.QUESTION_MESSAGE); /*new BufferedReader(new InputStreamReader(System.in)).readLine()*/
                if (name != null) {
                    if (!(name.length() < 4)) {
                        if (pattern.matcher(name).matches()) {
                            break;
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Name can only contain letters [A-Z], numbers [0-9] and its size must be larger than 3 characters.",
                                "Correct yourself", JOptionPane.ERROR_MESSAGE);
                    }
                }
                int input = JOptionPane.showConfirmDialog(frame, "Do you want to connect as Anonymous", "Choose action", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (input == 0) {
                    name = "Anonymous";
                    break;
                } else if (input == 2) {
                    System.exit(1);
                }

                //System.out.println("Name can only contain letters [A-Z], numbers [0-9] and its size must be larger than 3 characters.");
            }
        } else {
            name = JOptionPane.showInputDialog(frame, "Enter your name:", "Set your name", JOptionPane.QUESTION_MESSAGE); /*new BufferedReader(new InputStreamReader(System.in)).readLine()*/
            if (!(name.length() < 4)) {
                if (pattern.matcher(name).matches()) {
                    JOptionPane.showMessageDialog(frame, "Name changed successfully", "Name changed successfully", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Name can only contain letters [A-Z], numbers [0-9] and its size must be larger than 3 characters.",
                        "Correct yourself", JOptionPane.ERROR_MESSAGE);
                name = "";
            }
        }


        return name;
    }

    public static class MessageField {
        JTextArea textArea = new JTextArea();

    }

    /**
     * Do zmiany IP
     * @param ip - ip
     * @return ip
     */
    public String changeIpPort(String ip){
         JPanel pane;
         JTextField newIp;

        {
            pane = new JPanel();
            pane.setLayout(new GridLayout(0, 2, 2, 2));

            newIp = new JTextField(5);

            pane.add(new JLabel("New IP: "));
            pane.add(newIp);


            int option = JOptionPane.showConfirmDialog(frame, pane, "Please fill all the fields", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

            if (option == JOptionPane.YES_OPTION) {
                Pattern pattern = Pattern.compile("^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$");
                if(pattern.matcher(newIp.getText()).matches()){
                    return newIp.getText().trim();
                }
                else{
                    JOptionPane.showMessageDialog(frame,"Wrong IP format","Error",JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
        return ip;
    };

    /**
     * Skladanie wszystkiego w calosc
     * Tworzenie ramki:
     * @see #frame
     * Tworzenie menu i komponentow:
     * @see #m2
     * Tworzenie Panelu
     * Glowny obszar tekstowy:
     * @see #messageTabs
     * A takze rozne ustawienia rozmiarow i kolorow
     */
    public void run() {
        try {
            //System.out.println("Enter your name: ");
            /////////********************GUI************************////////////////
            //Tworzenie ramki
            frame = new JFrame("MessageBox9000");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 800);
            frame.setMinimumSize(new Dimension(800, 800));
            frame.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width - frame.getWidth()) / 2, (Toolkit.getDefaultToolkit().getScreenSize().height - frame.getHeight()) / 2);

            //Tworzenie menu i komponentów
            JMenuBar menuBar = new JMenuBar();


            m2 = new JMenu("Server");
            m3 = new JMenu("Help"); // Przycisk pomocy


            m2.addSeparator();
            m2.setPreferredSize(new Dimension(51, 30));
            m2.setBorder(new MatteBorder(0, 1, 0, 1, Color.BLACK));
            m2.addMouseListener(new mouseListner());

            m3.addSeparator();
            m3.setPreferredSize(new Dimension(37, 30));
            m3.setBorder(new MatteBorder(0, 1, 0, 1, Color.BLACK));
            m3.addMouseListener(new mouseListner());


            menuBar.add(m2);
            menuBar.add(m3);


            m21 = new JMenuItem("Try connect to server");
            m22 = new JMenuItem("Check ping");
            m23 = new JMenuItem("Change server IP");
            m31 = new JMenuItem("Help");
            m32 = new JMenuItem("Credits");


            m2.add(m21);
            m2.add(m22);
            m2.add(m23);
            m3.add(m31);
            m3.add(m32);


            //Tworzenie panelu
            JPanel inputPanel = new JPanel(); //Panel
            JPanel userPanel = new JPanel();
            JPanel messagePanel = new JPanel();
            JPanel menuPanel = new JPanel();

            textField.setPreferredSize(new Dimension(700, 25));

            // Głowny obszar tekstowy do wiadomości
            UIManager.put("TabbedPane.contentBorderInsets", new Insets(0, 0, 0, 0));
            UIManager.put("TabbedPane.selectedTabPadInsets", new Insets(0, 0, 0, 0));
            UIManager.put("TabbedPane.selectHighlight", null);
            UIManager.put("TabbedPane.background", Color.decode(backgroundColor));
            UIManager.put("TabbedPane.foreground", Color.decode(fontColor));
            UIManager.put("TabbedPane.selected", Color.decode(activeTabColor));
            UIManager.put("TabbedPane.darkShadow", Color.decode(backgroundColor));


            messageTabs = new JTabbedPane(JTabbedPane.TOP);
            messageTabs.setOpaque(true);

            messageTabs.setMinimumSize(new Dimension(250, 700));
            messageTabs.setPreferredSize(new Dimension(250, 700));


            messageScroll = new JScrollPane(messagesField, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            messageScroll.setMinimumSize(new Dimension(250, 700));
            messageScroll.setPreferredSize(new Dimension(250, 700));

            messagesField.setEditable(false);
            messagesField.setLineWrap(true);
            messagesField.setWrapStyleWord(true);
            messagesField.setMargin(new Insets(5, 5, 5, 5));
            // Wiadomoscii.setSize(new Dimension(600,400));
            //messagesField.setPreferredSize(new Dimension(250, 400));

            messagePanel.setLayout(new BorderLayout());
            //messagePanel.add(BorderLayout.CENTER,messagesField);
            //messagePanel.add(BorderLayout.CENTER,messageTabs);
            messagePanel.add(BorderLayout.CENTER, messageScroll);

            //Użytkownicy
            users = new JTextArea("Users:");
            users.setMargin(new Insets(5, 5, 5, 5));
            //users.setPreferredSize(new Dimension(100,500));
            users.setEditable(false);
            users.setLineWrap(true);
            users.setBorder(new SoftBevelBorder(SoftBevelBorder.LOWERED));

            JScrollPane usersScrollPane = new JScrollPane(users, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            usersScrollPane.setMinimumSize(new Dimension(200, 400));
            usersScrollPane.setPreferredSize(new Dimension(200, 400));


            userStatus = new JTextArea();
            userStatus.setMinimumSize(new Dimension(200, 50));
            userStatus.setMaximumSize(new Dimension(200, 50));
            userStatus.setPreferredSize(new Dimension(200, 50));
            userStatus.setEditable(false);
            userStatus.setMargin(new Insets(5, 5, 5, 5));
            userStatus.setBorder(new SoftBevelBorder(SoftBevelBorder.LOWERED));

            //Rzeczy do ramki
            frame.getContentPane().add(BorderLayout.SOUTH, inputPanel);
            frame.getContentPane().add(BorderLayout.NORTH, menuPanel);
            frame.getContentPane().add(BorderLayout.CENTER, messageTabs);
            frame.getContentPane().add(BorderLayout.WEST, userPanel);
            // frame.getContentPane().add(BorderLayout.WEST, users);

            customSquare = new CustomSquare(200, 20, buttonColor, "Change user name", 12, 47, 15, "#000000");

            userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));
            userPanel.add(customSquare);
            userPanel.add(Box.createRigidArea(new Dimension(200, 5)));
            userPanel.add(userStatus);
            userPanel.add(Box.createRigidArea(new Dimension(200, 5)));
            userPanel.add(usersScrollPane);


            inputPanel.add(textField);
            inputPanel.add(sendButton);


            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            menuPanel.setLayout(new GridBagLayout());
            menuPanel.setPreferredSize(new Dimension(800, 30));
            gridBagConstraints.fill = GridBagConstraints.NONE;

            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.weightx = 1;
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.ipadx = 0;
            gridBagConstraints.insets = new Insets(0, 0, 0, 0);
            menuPanel.add(menuBar, gridBagConstraints);


            // all colors have to be in hex


            frame.setBackground(Color.decode(backgroundColor));
            inputPanel.setBackground(Color.decode(backgroundColor));
            menuPanel.setBackground(Color.decode(backgroundColor));
            messagePanel.setBackground(Color.decode(backgroundColor));
            userPanel.setBackground(Color.decode(backgroundColor));

            messagesField.setForeground(Color.decode(fontColor));
            userStatus.setForeground(Color.decode(fontColor));
            users.setForeground(Color.decode(fontColor));
            textField.setForeground(Color.decode(fontColor));
            messageScroll.setForeground(Color.decode(fontColor));
            //messageTabs.setForeground(Color.decode(fontColor));

            messagesField.setBackground(Color.decode(fieldBgColor));
            userStatus.setBackground(Color.decode(fieldBgColor));
            users.setBackground(Color.decode(fieldBgColor));
            textField.setBackground(Color.decode(fieldBgColor));
            messageScroll.setBackground(Color.decode(fieldBgColor));
            //messageTabs.setBackground(Color.decode(backgroundColor));


            //userStatus.setBorder(new EmptyBorder(0,0,5,0));

            userPanel.setBorder(new EmptyBorder(5, 5, 0, 5));
            messagePanel.setBorder(new EmptyBorder(0, 0, 0, 5));
            menuPanel.setBorder(new EmptyBorder(0, 5, 5, 5));
            inputPanel.setBorder(new EmptyBorder(0, 0, 0, 0));


            frame.setVisible(true);

            /////////********************************************////////////////


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
/*Created by Gabriel Ćwiek and Paweł Blak
Last update date: 24.05.2020*/
