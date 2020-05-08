package Client;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.regex.Pattern;

public class Interface implements Runnable {
    JFrame frame;
    JTextField textField = new JTextField(); // Pole do pisania wiadomości
    JButton sendButton = new JButton("Send");
    JTextArea messagesField = new JTextArea();
    Pattern pattern = Pattern.compile("^[a-zA-Z0-9]+$");
    String name;
    JTextArea users;
    JTextArea userStatus;
    JMenu m1, m2, m3;
    JMenuItem m11, m12, m21, m22, m31, m32;
    JScrollPane messageScroll;


    String bgColor = "#000000";
    String fieldBgColor = "#f1e3cb";
    String fontColor = "#000000";
    String buttonColor = "";

    public class CustomButton extends JPanel{
        int sizeX = 150;
        int sizeY =30;
        String label = "Dupa";

        CustomButton(){
        };

        CustomButton(String a){
            label=a;
        };
        CustomButton(int x,int y){
            sizeX=x;
            sizeY=y;
        };
        CustomButton(String s,int x,int y){
            label = s;
            sizeX=x;
            sizeY=y;
        };

        protected void paintComponent(Graphics g){

            //Wymuś rozmiar bo się pierdoli
            super.setMaximumSize(new Dimension(sizeX,sizeY));
            super.setMinimumSize(new Dimension(sizeX,sizeY));
            super.setPreferredSize(new Dimension(sizeX,sizeY));
            super.setSize(new Dimension(sizeX,sizeY));

            super.setBackground(Color.decode(bgColor));
            super.setBorder(new EmptyBorder(0,0,0,0));
            super.paintComponent(g);

            g.setColor(Color.GREEN);
            g.fillRect(0,0,sizeX,sizeY);

        }
    }

    public class mouseListner implements MouseListener{
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
            ((JMenu)e.getSource()).doClick(30);
        }

        @Override
        public void mouseExited(MouseEvent e) {

        }

    }


    public void run() {
        try {
            //System.out.println("Enter your name: ");
            /////////********************GUI************************////////////////
            //Tworzenie ramki
            frame = new JFrame("MessageBox9000");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 800);
            frame.setMinimumSize(new Dimension(800, 800));

            //Tworzenie menu i komponentów
            JMenuBar menuBar = new JMenuBar();


            m1 = new JMenu("File"); //Przycisk Plik
            m2 = new JMenu("Server");
            m3 = new JMenu("Help"); // Przycisk pomocy

            m1.addSeparator();
            m1.setPreferredSize(new Dimension(30,30));
            m1.setBorder(new MatteBorder(0,1,0,1,Color.BLACK));
            m1.addMouseListener(new mouseListner());

            m2.addSeparator();
            m2.setPreferredSize(new Dimension(51,30));
            m2.setBorder(new MatteBorder(0,1,0,1,Color.BLACK));
            m2.addMouseListener(new mouseListner());

            m3.addSeparator();
            m3.setPreferredSize(new Dimension(37,30));
            m3.setBorder(new MatteBorder(0,1,0,1,Color.BLACK));
            m3.addMouseListener(new mouseListner());

            menuBar.add(m1);
            menuBar.add(m2);
            menuBar.add(m3);

            m11 = new JMenuItem("Open"); //Pod opcje w Plik
            m12 = new JMenuItem("Save as...");
            m21 = new JMenuItem("Try connect to server");
            m22 = new JMenuItem("Check ping");
            m31 = new JMenuItem("Help");
            m32 = new JMenuItem("Credits");

            m1.add(m11);
            m1.add(m12);
            m2.add(m21);
            m2.add(m22);
            m3.add(m31);
            m3.add(m32);


            //Tworzenie panelu
            JPanel inputPanel = new JPanel(); //Panel
            JPanel userPanel = new JPanel();
            JPanel messagePanel = new JPanel();
            JPanel menuPanel = new JPanel();

            textField.setPreferredSize(new Dimension(700, 25));

            // Głowny obszar tekstowy do wiadomości

            messageScroll = new JScrollPane(messagesField, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            messageScroll.setMinimumSize(new Dimension(250, 400));
            messageScroll.setPreferredSize(new Dimension(250, 400));

            messagesField.setEditable(false);
            messagesField.setLineWrap(true);
            messagesField.setWrapStyleWord(true);
            messagesField.setMargin(new Insets(5, 5, 5, 5));
            // Wiadomoscii.setSize(new Dimension(600,400));
            //messagesField.setPreferredSize(new Dimension(250, 400));

            //Użytkownicy
            users = new JTextArea("Users:");
            users.setMargin(new Insets(2, 2, 5, 5));
            //users.setPreferredSize(new Dimension(100,500));
            users.setEditable(false);
            users.setLineWrap(true);
            users.setBorder(new SoftBevelBorder(SoftBevelBorder.LOWERED));

            JScrollPane usersScrollPane = new JScrollPane(users, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            usersScrollPane.setMinimumSize(new Dimension(200, 475));
            usersScrollPane.setPreferredSize(new Dimension(200, 475));


            userStatus = new JTextArea();
            userStatus.setMinimumSize(new Dimension(200, 195));
            userStatus.setMaximumSize(new Dimension(200, 195));
            userStatus.setPreferredSize(new Dimension(200, 195));
            userStatus.setEditable(false);
            userStatus.setMargin(new Insets(5, 5, 5, 5));
            userStatus.setBorder(new SoftBevelBorder(SoftBevelBorder.LOWERED));

            //Rzeczy do ramki
            frame.getContentPane().add(BorderLayout.SOUTH, inputPanel);
            frame.getContentPane().add(BorderLayout.NORTH, menuPanel);
            frame.getContentPane().add(BorderLayout.CENTER, messagePanel);
            frame.getContentPane().add(BorderLayout.WEST, userPanel);
            // frame.getContentPane().add(BorderLayout.WEST, users);


            userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));
            userPanel.add(userStatus);
            userPanel.add(Box.createRigidArea(new Dimension(200, 5)));
            userPanel.add(usersScrollPane);


            inputPanel.add(textField);
            inputPanel.add(sendButton);

            CustomButton customButton = new CustomButton();

            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            menuPanel.setLayout(new GridBagLayout());
            menuPanel.setPreferredSize(new Dimension(800,30));
            gridBagConstraints.fill = GridBagConstraints.NONE;

            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.weightx =1;
            gridBagConstraints.gridx=0;
            gridBagConstraints.gridy=0;
            gridBagConstraints.ipadx = 0;
            gridBagConstraints.insets = new Insets(0,0,0,0);
            menuPanel.add(menuBar,gridBagConstraints);

            gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
            gridBagConstraints.anchor = GridBagConstraints.EAST;
            menuPanel.add(customButton,gridBagConstraints);




            messagePanel.setLayout(new BorderLayout());
            //messagePanel.add(BorderLayout.CENTER,messagesField);
            messagePanel.add(BorderLayout.CENTER, messageScroll);


            // all colors have to be in hex
            frame.setBackground(Color.decode(bgColor));
            inputPanel.setBackground(Color.decode(bgColor));
            menuPanel.setBackground(Color.decode(bgColor));
            messagePanel.setBackground(Color.decode(bgColor));
            userPanel.setBackground(Color.decode(bgColor));

            messagesField.setForeground(Color.decode(fontColor));
            userStatus.setForeground(Color.decode(fontColor));
            users.setForeground(Color.decode(fontColor));
            textField.setForeground(Color.decode(fontColor));
            messageScroll.setForeground(Color.decode(fontColor));

            messagesField.setBackground(Color.decode(fieldBgColor));
            userStatus.setBackground(Color.decode(fieldBgColor));
            users.setBackground(Color.decode(fieldBgColor));
            textField.setBackground(Color.decode(fieldBgColor));
            messageScroll.setBackground(Color.decode(fieldBgColor));

            //userStatus.setBorder(new EmptyBorder(0,0,5,0));

            userPanel.setBorder(new EmptyBorder(0, 0, 5, 5));
            messagePanel.setBorder(new EmptyBorder(0, 0, 5, 0));
            menuPanel.setBorder(new EmptyBorder(0, 0, 5, 0));
            frame.setVisible(true);

            /////////********************************************////////////////
            while (true) {

                name = JOptionPane.showInputDialog(frame, "Enter your name:", "Set your name", JOptionPane.QUESTION_MESSAGE); /*new BufferedReader(new InputStreamReader(System.in)).readLine()*/
                if (name != null) {
                    if (pattern.matcher(name).matches()) {
                        break;
                    }
                }
                int input = JOptionPane.showConfirmDialog(frame, "Do you want to connect as Anonymous", "Choose action", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (input == 0) {
                    name = "Anonymous";
                    break;
                } else if (input == 2) {
                    System.exit(1);
                }
                JOptionPane.showMessageDialog(null, "Name can only contain letters [A-Z], numbers [0-9] and its size must be larger than 3 characters.",
                        "Popraw", JOptionPane.ERROR_MESSAGE);
                //System.out.println("Name can only contain letters [A-Z], numbers [0-9] and its size must be larger than 3 characters.");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
