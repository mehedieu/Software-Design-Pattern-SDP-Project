/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package registration;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import javax.swing.*;

public class Registration {

    /**
     * @param args the command line arguments
     */
    private static boolean connection = false, connection2 = false, bool = false;
    private static String s;

    public static void setConnectionValue(Boolean val) {
        connection = val;
    }

    public static void setConnectionTwoValue(Boolean val) {
        connection2 = val;
    }
    
    public static JButton returnButton (String text) {
        AbstractFactory buttonFactory = FactoryProducer.getFactory("Button");
        Button button1 = buttonFactory.getButton(text);
        
        return button1.getButton();
    }
    
    public static JFrame returnFrame (String text) {
        AbstractFactory frameFactory = FactoryProducer.getFactory("Frame");
        Frame frame = frameFactory.getFrame(text);
        
        return frame.getFrame();
    }

    public static void main(String[] args) throws IOException {
        JFrame frame = returnFrame("IP Address");
        frame.setSize(700, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        JLabel label = new JLabel("Enter IP Address: ");
        JTextField tf = new JTextField(20);
        JButton okButton = returnButton("OK button");
        gbc.gridx = 0;
        gbc.gridy = 0;
        frame.add(label, gbc);
        gbc.gridx++;
        frame.add(tf, gbc);
        gbc.gridx++;
        frame.add(okButton, gbc);
        frame.pack();
        frame.setResizable(true);
        frame.setVisible(true);
        okButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                s = tf.getText();
                bool = true;
            }
        });
        while (!bool);
        try {
            Socket client = new Socket(s, 1234);
            DataOutputStream os = new DataOutputStream(client.getOutputStream());
            DataInputStream is = new DataInputStream(client.getInputStream());
            Object[] options = {"Log in", "Sign up"};
            Toolkit.getDefaultToolkit().beep();
            frame.dispose();
            int n = JOptionPane.showOptionDialog(null, "What do you want to do?", "Log in Or Sign up", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
            if (n == JOptionPane.NO_OPTION) {
                new SignUp(client, os, is).registration();
            } else if (n == JOptionPane.YES_OPTION) {
                LogIn l = LogIn.getInstance();
                l.setValues(is, os, client);
                if (l.logIn()) {
                    try {
                        String line = is.readLine();
                        l.pass(line);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            if (!client.isClosed()) {
                os.writeBytes("Bye\n");
                client.close();
            }
        } catch (IOException e) {
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(null, "Sorry! Server is closed. Please try again later", "Error", JOptionPane.ERROR_MESSAGE);
            frame.dispose();
        }
    }

}
