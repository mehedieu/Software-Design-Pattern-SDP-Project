/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package registration;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author ASUS
 */
public class SignUp {

    private DataOutputStream os;
    private DataInputStream is;
    private boolean connection, flag;
    private Socket client;
    private String str, restr;

    public SignUp(Socket client, DataOutputStream os, DataInputStream is) {
        this.client = client;
        this.os = os;
        this.is = is;
        connection = false;
        str = "Sign up:-";
        restr = new String();
    }
    
    public JButton returnButton (String text) {
        AbstractFactory buttonFactory = FactoryProducer.getFactory("Button");
        Button button1 = buttonFactory.getButton(text);
        
        return button1.getButton();
    }
    
    public JFrame returnFrame (String text) {
        AbstractFactory frameFactory = FactoryProducer.getFactory("Frame");
        Frame frame = frameFactory.getFrame(text);
        
        return frame.getFrame();
    }
    
    //Registration form
    public String registration() {
        JFrame frame = returnFrame("Registration Form");
        JPanel p = new JPanel();
        frame.setSize(700, 500);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                try {
                    if (connection == false) {
                        os.writeBytes("Bye\n");//Clicking the close button of the window these things will happen.
                        client.close();
                    }
                    System.exit(0);//Instruct JVM to terminate this program.
                } catch (IOException ex) {
                    Logger.getLogger(Registration.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        GridBagConstraints gbc = new GridBagConstraints();
        GridLayout g = new GridLayout(1, 3);
        JLabel label1, label2, label3, label4, label5, label6, label7;
        label1 = new JLabel("Username: ");
        JTextField tf1, tf2, tf3;
        JPasswordField pf;
        JTextArea ta;
        tf1 = new JTextField(25);
        label2 = new JLabel("Roll: ");
        tf2 = new JTextField(10);
        label3 = new JLabel("Address: ");
        ta = new JTextArea(5, 30);
        p.setLayout(new GridBagLayout());
        gbc.gridx = 0;
        gbc.gridy = 0;
        p.add(label1, gbc);
        gbc.gridx = 1;
        p.add(tf1, gbc);
        gbc.gridx = 2;
        gbc.insets = new Insets(0, 1, 0, 0);
        p.add(label2, gbc);
        gbc.gridx = 3;
        p.add(tf2, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.ipady = 30;
        gbc.anchor = GridBagConstraints.BASELINE_LEADING;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        p.add(label3, gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.ipadx = 10;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.BOTH;
        p.add(ta, gbc);
        label4 = new JLabel("Gender:");
        JCheckBox cb1 = new JCheckBox("Male");
        JCheckBox cb2 = new JCheckBox("Female");
        gbc.gridx = 0;
        gbc.gridy = 2;
        p.add(label4, gbc);
        gbc.gridx++;
        p.add(cb1, gbc);
        gbc.gridx += 3;
        p.add(cb2, gbc);
        label5 = new JLabel("Hobby");
        String s[] = {"Playing games", "Watching movies", "Solving problems"};
        JComboBox<String> cox = new JComboBox<>(s);
        gbc.gridx = 0;
        gbc.gridy++;
        p.add(label5, gbc);
        gbc.gridx++;
        gbc.gridwidth = 3;
        p.add(cox, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        label6 = new JLabel("Password: ");
        p.add(label6, gbc);
        gbc.gridx++;
        gbc.insets = new Insets(0, 14, 0, 0);
        pf = new JPasswordField(20);
        p.add(pf, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        label7 = new JLabel("Email-id: ");
        p.add(label7, gbc);
        gbc.gridx++;
        gbc.insets = new Insets(0, 14, 0, 0);
        tf3 = new JTextField(20);
        p.add(tf3, gbc);
        gbc.gridy++;
        JButton bn = returnButton("Submit button");
        gbc.insets = new Insets(0, 180, 0, 180);
        p.add(bn, gbc);
        frame.add(p);
        frame.pack();
        bn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                str = str + tf1.getText() + "->" + tf2.getText() + "->" + ta.getText() + "->";
                flag = cb1.isSelected();
                if (flag == true) {
                    str = str + "Male->";
                } else {
                    str = str + "Female->";
                }
                str = str + (String) cox.getSelectedItem() + "->";
                String string = String.valueOf(pf.getPassword());
                str = str + string + "->";
                String email = tf3.getText().toString();//Symbol @ cannot be extracted by getText().
                str = str + email;
                try {
                    os.writeBytes(str + "\n");
                    String s = is.readLine();
                    if (s.equals("Account already exists")) {
                        Toolkit.getDefaultToolkit().beep();
                        JOptionPane.showMessageDialog(frame, "Account already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                        str = "Sign up:-";
                    } else if (s.equals("Invalid email-address!")) {
                        Toolkit.getDefaultToolkit().beep();
                        JOptionPane.showMessageDialog(frame, "Invalid email-address!", "Error", JOptionPane.ERROR_MESSAGE);
                        str = "Sign up:-";
                    } else {
                        connection = true;
                        Registration.setConnectionValue(true);
                        Registration.setConnectionTwoValue(true);
                        frame.dispose();
                    }
                } catch (IOException ex) {
                    Logger.getLogger(SignUp.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        frame.setVisible(true);
        frame.setResizable(true);
        while (true) {
            if (connection) {
                break;
            }
        }

        return str;
    }
}
