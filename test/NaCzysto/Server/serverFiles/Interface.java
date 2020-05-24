package serverFiles;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class Interface implements Runnable {

    JTextField tf = new JTextField(); // Pole do pisania wiadomości
    JButton send = new JButton("Wyślij"); //Przycisk
    JTextArea wiadomosci = new JTextArea(); //Pole textowe
    JTextArea userss = new JTextArea("Użytkownicy:"); //Pole tekstowe (uzytkownicy)
    JTextArea logii = new JTextArea(); //Pole tekstowe (logi)


    public void run() {

        /////////********************GUI************************////////////////

        //Obramowanie
        Border border = BorderFactory.createLineBorder(Color.BLACK);

        //Tworzenie ramki
        JFrame frame = new JFrame("Serwer"); //Tytul
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Zamknij jak klikniesz X
        frame.setSize(1000, 500); //Rozmiary "ramki"


        //Tworzenie menu i komponentów
        JMenuBar mb = new JMenuBar(); //Opcje
        JMenu m2 = new JMenu("Pomoc"); // Przycisk pomocy
        JMenu m3 = new JMenu("Start/Stop"); // Przycisk Włączania serwera
        mb.add(m2);//Dodawanie podrzędnych
        mb.add(m3);


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
        wiadomosci.setEditable(false); //Brak edycji
        wiadomosci.setLineWrap(true); //Zawijanie
        wiadomosci.setWrapStyleWord(true);
        // Wiadomoscii.setSize(new Dimension(600,400));
        wiadomosci.setSize(new Dimension(250,400));
        wiadomosci.setBackground(new Color(245, 245, 245)); //Kolor tła

        //Użytkownicy

        userss.setEditable(false);
        JScrollPane Users = new JScrollPane(userss);
        Users.setPreferredSize(new Dimension(100, 500));

        //Logi

        JScrollPane Logi = new JScrollPane(logii);
        logii.setEditable(false);
        logii.setSize(new Dimension(450, 100));
        logii.setLineWrap(true);
        logii.setWrapStyleWord(true);

        //Rzeczy do ramki
        frame.getContentPane().add(BorderLayout.SOUTH, panel);
        frame.getContentPane().add(BorderLayout.NORTH, mb);
        frame.getContentPane().add(BorderLayout.CENTER, Wiadomosci);
        frame.getContentPane().add(BorderLayout.WEST, Users);
        frame.getContentPane().add(BorderLayout.EAST, Logi);
        frame.setVisible(true);
    }
}
