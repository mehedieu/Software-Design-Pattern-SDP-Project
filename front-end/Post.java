/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package registration;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;

/**
 *
 * @author ASUS
 */
public class Post {

    private DataInputStream is;
    private DataOutputStream os;
    private String username;

    public Post(DataInputStream is, DataOutputStream os, String username) {
        this.is = is;
        this.os = os;
        this.username = username;
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

    public void writePost() {
        JFrame writeFrame = returnFrame("Write Post");
        writeFrame.setSize(700, 500);
        writeFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                try {
                    os.writeBytes("Sorry\n");
                    writeFrame.dispose();
                } catch (IOException ex) {
                    Logger.getLogger(Registration.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        writeFrame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 3;
        JTextArea ta = new JTextArea(3, 30);
        writeFrame.add(ta, gbc);
        gbc.gridx = 0;
        gbc.gridy += 3;
        JButton post = returnButton("Post button");
        writeFrame.add(post, gbc);
        writeFrame.pack();
        post.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    os.writeBytes("Writing post" + "->" + username + "->" + ta.getText() + "\n");
                    writeFrame.dispose();
                } catch (IOException ex) {
                    Logger.getLogger(Registration.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        writeFrame.setVisible(true);
        writeFrame.setResizable(true);
    }

    public String decode(String to_decode) {
        int i, c, x;
        char o;
        String s, s1;
        s = new String();
        c = to_decode.length();
        for (i = 0; i < c; i += 8) {
            s1 = to_decode.substring(i, i + 8);
            x = Integer.parseInt(s1, 2);
            o = (char) x;
            s = s + o;
        }

        return s;
    }

    public void seePost() throws IOException {
        os.writeBytes("Please load the posts\n");
        String text = is.readLine();
        text = decode(text);
        JFrame seeFrame = returnFrame("All posts");
        seeFrame.setSize(700, 500);
        seeFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JTextArea ta = new JTextArea(10, 30);
        ta.setText(text);
        seeFrame.add(ta);
        seeFrame.pack();
        seeFrame.setVisible(true);
        seeFrame.setResizable(true);
    }
}
