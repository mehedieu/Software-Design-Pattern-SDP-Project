/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package registration;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;

/**
 *
 * @author ASUS
 */
public class Handle implements Runnable {

    Thread t;
    int num;
    JButton button;

    public Handle(int num, JButton button) {
        t = new Thread(this);
        this.num = num;
        this.button = button;
        t.start();
    }

    @Override
    public void run() {
        while (true) {
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    LogIn.setGlobalValue(true);
                    LogIn.setFileNoValue(num);
                    return;
                }
            });
        }
    }
}
