package clientFiles;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DialogBox implements Runnable {
    String boxName;
    JFrame frame;
    String message = "Dupa";
    JPanel panel;
    JTextField textField;
    Interface anInterface;
    String bgColor;
    String buttonColor;
    String fontColor;
    String borderColor;

    public class CustomSquare extends JPanel{
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

        ;

        //designated size square
        CustomSquare(int x, int y) {
            sizeX = x;
            sizeY = y;
        }

        ;

        //designated size and color square
        CustomSquare(int x, int y, String bgColor) {
            sizeX = x;
            sizeY = y;
            this.bgColor = bgColor;
        }

        ;

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

        };


        protected void paintComponent(Graphics g) {

            //Wymuś rozmiar bo się pierdoli
            super.setMaximumSize(new Dimension(sizeX, sizeY));
            super.setMinimumSize(new Dimension(sizeX, sizeY));
            super.setPreferredSize(new Dimension(sizeX, sizeY));
            super.setSize(new Dimension(sizeX, sizeY));
            super.setBackground(Color.decode(bgColor));
            super.setBorder(new EmptyBorder(2, 2, 2, 2));
            super.paintComponent(g);


            g.setColor(Color.decode(bgColor));
            g.fillRect(0, 0, sizeX, sizeY);

            g.setColor(Color.decode(borderColor));
            g.drawRect(0,0,sizeX-1,sizeY-1);
            g.drawRect(1,1,sizeX-2,sizeY-2);

            if (!label.equals("")) {
                g.setFont(new Font("TimesRoman", Font.PLAIN, fSize));
                g.setColor(Color.decode(fColor));
                g.drawString(label, fPosX, fPosY);
            }

        }

    }

    DialogBox(String boxName,Interface anInterface,String bgColor,String buttonColor, String fontColor, String borderColor){
        this.boxName=boxName;
        this.anInterface = anInterface;
        this.bgColor =bgColor;
        this.buttonColor = buttonColor;
        this.fontColor = fontColor;
        this.borderColor =borderColor;
    }


    @Override
    public void run() {
        frame = new JFrame(boxName);
        frame.setSize(new Dimension(300,100));
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocation((anInterface.frame.getX()+(anInterface.frame.getWidth()-frame.getWidth())/2),(anInterface.frame.getY()+(anInterface.frame.getHeight()-frame.getHeight())/2));
        frame.setAutoRequestFocus(true);
        frame.setVisible(true);


        panel = new JPanel();
        frame.getContentPane().add(BorderLayout.CENTER,panel);

        textField = new JTextField();
        textField.setPreferredSize(new Dimension(200,20));

        CustomSquare confirmButton = new CustomSquare(30,20,buttonColor,"Ok",12,5,15,fontColor);

        panel.setLayout(new GridBagLayout());
        panel.setBackground(Color.decode(bgColor));
        GridBagConstraints bagConstraints = new GridBagConstraints();
        panel.add(textField,bagConstraints);
        panel.add(confirmButton,bagConstraints);


    }
}
