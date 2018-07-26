/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package registration;

import javax.swing.JButton;

/**
 *
 * @author Asus
 */
public interface Button {

    public JButton getButton();
}

class Submit implements Button {

    @Override
    public JButton getButton() {
        return new JButton("Submit");
    }
}

class LogInButton implements Button {
    @Override
    public JButton getButton() {
        return new JButton("Log in");
    }
}

class Download implements Button {

    @Override
    public JButton getButton() {
        return new JButton("Download/Share");
    }
}

class PostButton implements Button {
    @Override
    public JButton getButton() {
        return new JButton("Post");
    }
}

class OkButton implements Button {
    @Override
    public JButton getButton() {
        return new JButton("OK");
    }
}
