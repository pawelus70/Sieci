package Client;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.regex.Pattern;

public class Interface implements Runnable{
    JTextField tf = new JTextField(); // Pole do pisania wiadomości
    JButton send = new JButton("Wyślij");
    JTextArea wiadomosci = new JTextArea();
    Pattern pattern =Pattern.compile("^[a-zA-Z0-9]+$");
    String name;

    public void run() {
        try {

            //System.out.println("Enter your name: ");


            /////////********************GUI************************////////////////

            //Obramowanie
            Border border = BorderFactory.createLineBorder(Color.BLACK);

            //Tworzenie ramki
            JFrame frame = new JFrame("Komunikator");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 500);


            //Tworzenie menu i komponentów
            JMenuBar mb = new JMenuBar();
            JMenu m1 = new JMenu("Plik"); //Przycisk Plik
            JMenu m2 = new JMenu("Pomoc"); // Przycisk pomocy
            mb.add(m1);
            mb.add(m2);
            JMenuItem m11 = new JMenuItem("Otwórz"); //Pod opcje w Plik
            JMenuItem m22 = new JMenuItem("Zapisz jako...");
            m1.add(m11);
            m1.add(m22);

            //Tworzenie panelu
            JPanel panel = new JPanel(); //Panel
            JLabel label = new JLabel("Napisz coś!");

            tf.setPreferredSize(new Dimension(500,50));
            tf.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(10,10,10,10)));



            // panel.add(Users);
            panel.add(label); //Dodawanie wszystko we flowLayout
            panel.add(tf);
            panel.add(send);

            // Głowny obszar tekstowy do wiadomości

            JScrollPane Wiadomosci = new JScrollPane(wiadomosci);
            wiadomosci.setEditable(false);
            wiadomosci.setLineWrap(true);
            wiadomosci.setWrapStyleWord(true);
            // Wiadomoscii.setSize(new Dimension(600,400));
            wiadomosci.setSize(new Dimension(250,400));
            wiadomosci.setBackground(new Color(245, 245, 245));

            //Użytkownicy
            JTextArea Userss = new JTextArea("Użytkownicy:");
            Userss.setEditable(false);
            JScrollPane Users = new JScrollPane(Userss);
            Users.setPreferredSize(new Dimension(100, 500));


            //Rzeczy do ramki
            frame.getContentPane().add(BorderLayout.SOUTH, panel);
            frame.getContentPane().add(BorderLayout.NORTH, mb);
            frame.getContentPane().add(BorderLayout.CENTER, Wiadomosci);
            frame.getContentPane().add(BorderLayout.WEST, Users);
            frame.setVisible(true);

            /////////********************************************////////////////
            while(true) {

                    name = JOptionPane.showInputDialog(frame, "Wprowadź swój pseudonim:", "Wprowadzenie", JOptionPane.QUESTION_MESSAGE); /*new BufferedReader(new InputStreamReader(System.in)).readLine()*/
                    if(name !=null){
                        if(pattern.matcher(name).matches()){
                            break;
                        }
                    }
                    int input = JOptionPane.showConfirmDialog(frame,"Do you want to connect as Anonymous","Choose action",JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE);
                    if(input == 0){
                        name="Anonymous";
                        break;
                    }else if(input ==2){
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
