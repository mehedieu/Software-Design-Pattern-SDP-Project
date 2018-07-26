/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package registration;

import javax.swing.JFrame;

/**
 *
 * @author Asus
 */
public interface Frame {
    public JFrame getFrame();
}

class IPAddress implements Frame {
    @Override
    public JFrame getFrame() {
        return new JFrame("IP Address");
    }
}

class SignUpFrame implements Frame {
    @Override
    public JFrame getFrame() {
        return new JFrame("Registration Form");
    }
}

class LogInFrame implements Frame {
    @Override
    public JFrame getFrame() {
        return new JFrame("Log In");
    }
}

class Welcome implements Frame {
    @Override
    public JFrame getFrame() {
        return new JFrame("Welcome");
    }
}

class UploadFrame implements Frame {
    @Override
    public JFrame getFrame() {
        return new JFrame("Browse File");
    }
}

class DeleteFrame implements Frame {
    @Override
    public JFrame getFrame() {
        return new JFrame("Delete Files");
    }
}

class ChangeProPicFrame implements Frame {
    @Override
    public JFrame getFrame() {
        return new JFrame("Profile Picture");
    }
}

class ShareFileFrame implements Frame {
    @Override
    public JFrame getFrame() {
        return new JFrame("Share Files");
    }
}

class WritePostFrame implements Frame {
    @Override
    public JFrame getFrame() {
        return new JFrame("Write Post");
    }
}

class SeePostFrame implements Frame {
    @Override
    public JFrame getFrame() {
        return new JFrame("All posts");
    }
}