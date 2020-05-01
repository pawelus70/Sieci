package Client;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.regex.Pattern;

public class Interface implements Runnable {
    JTextField textField = new JTextField(); // Pole do pisania wiadomości
    JButton send = new JButton("Send");
    JTextArea wiadomosci = new JTextArea();
    Pattern pattern = Pattern.compile("^[a-zA-Z0-9]+$");
    String name;
    JTextArea users;
    JTextArea userStatus;
    JMenu m1, m2, m3;
    JMenuItem m11, m12, m21, m22, m31, m32;


    public void run() {
        try {

            //System.out.println("Enter your name: ");


            /////////********************GUI************************////////////////

            //Obramowanie
            Border border = BorderFactory.createLineBorder(Color.BLACK);

            //Tworzenie ramki
            JFrame frame = new JFrame("MessageBox9000");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 500);


            //Tworzenie menu i komponentów
            JMenuBar menuBar = new JMenuBar();
            m1 = new JMenu("File"); //Przycisk Plik
            m2 = new JMenu("Server");
            m3 = new JMenu("Help"); // Przycisk pomocy
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
            JPanel panel = new JPanel(); //Panel
            JLabel label = new JLabel("Type something");

            textField.setPreferredSize(new Dimension(500, 50));
            textField.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(10, 10, 10, 10)));


            // panel.add(Users);
            panel.add(label); //Dodawanie wszystko we flowLayout
            panel.add(textField);
            panel.add(send);

            // Głowny obszar tekstowy do wiadomości

            JScrollPane Wiadomosci = new JScrollPane(wiadomosci);
            wiadomosci.setEditable(false);
            wiadomosci.setLineWrap(true);
            wiadomosci.setWrapStyleWord(true);
            wiadomosci.setMargin(new Insets(5, 5, 5, 5));
            // Wiadomoscii.setSize(new Dimension(600,400));
            wiadomosci.setSize(new Dimension(250, 400));
            wiadomosci.setBackground(new Color(245, 245, 245));

            //Użytkownicy
            users = new JTextArea("Users:");
            users.setMargin(new Insets(2, 2, 5, 5));
            users.setEditable(false);
            JScrollPane usersScrollPane = new JScrollPane(users);
            usersScrollPane.setPreferredSize(new Dimension(100, 500));


            //Rzeczy do ramki
            frame.getContentPane().add(BorderLayout.SOUTH, panel);
            frame.getContentPane().add(BorderLayout.NORTH, menuBar);
            frame.getContentPane().add(BorderLayout.CENTER, Wiadomosci);
            frame.getContentPane().add(BorderLayout.WEST, users);
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
                JOptionPane.showMessageDialog(null, "Name can only contain letters [A-Z], numbers [0-9] and its size must be larger than 3 characters.", "Popraw", JOptionPane.ERROR_MESSAGE);
                //System.out.println("Name can only contain letters [A-Z], numbers [0-9] and its size must be larger than 3 characters.");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
