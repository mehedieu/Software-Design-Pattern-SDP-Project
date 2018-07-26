/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package registration;

import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 *
 * @author ASUS
 */
public class LogIn {

    private DataInputStream is;
    private DataOutputStream os;
    private boolean closing, connection, response2, flag2, response;
    private static boolean global, shared;//static so that this array can be accessed from outside without instantiating an object of this class.
    private static int file_no;
    private int variable;
    private Socket client;
    private String username, arr[], path;
    private static String sharedFileFromMenuBar;
    private static ArrayList<JButton> button;
    private ArrayList<String> arr_list;
    private ArrayList<JLabel> label;
    private ArrayList<Handle> handle;
    private GridBagConstraints gbcons;
    private File file2;
    private JFrame up_frame, passFrame;
    
    private static LogIn instance = new LogIn();
    
    private LogIn(){
        
    }

    public void setValues(DataInputStream is, DataOutputStream os, Socket client) {
        this.is = is;
        this.os = os;
        closing = flag2 = response = connection = response2 = global = shared = false;
        path = new String();
        sharedFileFromMenuBar = new String();
        this.client = client;
    }
    
    public static LogIn getInstance(){
        return instance;
    }
    
    public JButton returnButton (String text) {
        AbstractFactory buttonFactory = FactoryProducer.getFactory("Button");
        Button button1 = buttonFactory.getButton(text);
        
        return button1.getButton();
    }
    
    public JMenuItem returnMenuItem (String text) {
        AbstractFactory menuItemFactory = FactoryProducer.getFactory("Menu Item");
        MenuItem menuItem = menuItemFactory.getMenuItem(text);
        
        return menuItem.getMenuItem();
    }
    
    public JFrame returnFrame (String text) {
        AbstractFactory frameFactory = FactoryProducer.getFactory("Frame");
        Frame frame = frameFactory.getFrame(text);
        
        return frame.getFrame();
    }

    public boolean logIn() {
        JFrame frame = returnFrame("Log In");
        frame.setSize(700, 500);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                try {
                    if (!closing) {
                        os.writeBytes("Bye\n");
                        client.close();
                    }
                    System.exit(0);
                } catch (IOException ex) {
                    Logger.getLogger(Registration.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        frame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel label1 = new JLabel("Username: ");
        frame.add(label1, gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        JTextField tf1 = new JTextField(20);
        frame.add(tf1, gbc);
        gbc.gridy++;
        gbc.gridx = 0;
        JLabel label2 = new JLabel("Password: ");
        frame.add(label2, gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        JPasswordField pf2 = new JPasswordField(20);
        frame.add(pf2, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        JButton logIn = returnButton("Log in Button");
        frame.add(logIn, gbc);
        frame.pack();
        frame.setResizable(true);
        frame.setVisible(true);
        logIn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String string = String.valueOf(pf2.getPassword());
                    username = tf1.getText(); //Username is stored because it is required to write post after logging in to the account.
                    os.writeBytes(username + "->" + string + "\n");
                    String line = is.readLine();
                    if (line.equals("Accepted") == true) {
                        flag2 = true;
                        response = true;
                    } else {
                        Toolkit.getDefaultToolkit().beep();
                        JOptionPane.showMessageDialog(frame, "Invalid Username or Password", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(Registration.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        while (true) {
            if (response == true) {
                frame.dispose();
                break;
            }
        }

        return flag2;
    }

    public static void setGlobalValue(boolean flag) {
        global = flag;
    }

    public static void setFileNoValue(int x) {
        file_no = x;
    }

    public static void setSharedValue(boolean val) {
        shared = val;
    }

    public static void setStringValue(String val) {
        sharedFileFromMenuBar = val;
    }

    public void checkNotification(Load load) throws IOException {
        os.writeBytes("Check for notification->" + username + "->true\n");
        while (true) {
            String reply = is.readLine();
            if (reply.equals("No more notification")) {
                break;
            } else if (reply.equals("No new notification")) {
                break;
            }
            String noti[] = reply.split("->");
            Object[] options = {"Yes", "No"};
            Toolkit.getDefaultToolkit().beep();
            int n = JOptionPane.showOptionDialog(null, noti[0] + " wants to share " + noti[1] + " with you.\nWill you download?", "Notification", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
            if (n == JOptionPane.YES_OPTION) {
                Object[] choices = {"Download", "Save to Account"};
                Toolkit.getDefaultToolkit().beep();
                int h = JOptionPane.showOptionDialog(null, "What do you want to do?", "Choose operation", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, choices, choices[0]);
                if (h == JOptionPane.YES_OPTION) {
                    load.setNotificationFlag(true);
                    load.download(noti[1]);
                    load.setNotificationFlag(false);
                } else if (h == JOptionPane.NO_OPTION) {
                    os.writeBytes("Save to account\n");
                    String answer = is.readLine();
                    if (answer.equals("File Already Exists!")) {
                        Toolkit.getDefaultToolkit().beep();
                        JOptionPane.showMessageDialog(null, answer, "Warning", JOptionPane.WARNING_MESSAGE);
                        continue;
                    }
                    Toolkit.getDefaultToolkit().beep();
                    JOptionPane.showMessageDialog(null, "Saved to your account successfully!", "Congratulations", JOptionPane.INFORMATION_MESSAGE);
                    updateFrame("Shared from\\\\" + noti[1]);
                } else {
                    os.writeBytes("No\n");
                }
            } else {
                os.writeBytes("No\n");
            }
        }
    }

    public void updateFrame(String file_name) {
        button.add(returnButton("Download Button"));
        label.add(new JLabel(file_name));
        gbcons.insets = new Insets(10, 0, 0, 0);
        gbcons.ipadx = 20;
        passFrame.add(label.get(variable), gbcons);
        gbcons.insets = new Insets(10, 0, 0, 0);
        gbcons.gridx++;
        passFrame.add(button.get(button.size() - 1), gbcons);
        handle.add(new Handle(variable, button.get(button.size() - 1)));
        arr_list.add(file_name);
        gbcons.gridx = 0;
        gbcons.gridy++;
        passFrame.validate();
        passFrame.pack();
        variable++;
    }

    public void pass(String line) throws FileNotFoundException, IOException {
        JFileChooser fc = new JFileChooser();
        arr = line.split(",");
        int c = arr.length;
        arr_list = new ArrayList<>();
        for (int i = 0; i < c; i++) {
            arr_list.add(arr[i]);
        }
        handle = new ArrayList<>();
        passFrame = returnFrame("Welcome");
        passFrame.setSize(700, 500);
        JMenuBar menubar = new JMenuBar();
        passFrame.setJMenuBar(menubar);
        JMenu account = new JMenu("Account");
        JMenu post = new JMenu("Post");
        JMenu notification = new JMenu("Notifications");
        menubar.add(account);
        menubar.add(post);
        menubar.add(notification);
        JMenuItem up_file = returnMenuItem("Upload File");
        JMenuItem del_file = returnMenuItem("Delete File");
        JMenuItem shr_file = returnMenuItem("Share File");
        JMenuItem del_acc = returnMenuItem("Delete Account");
        JMenuItem logout = returnMenuItem("Log out");
        JMenuItem change_pro_pic = returnMenuItem("Change Profile Picture");
        JMenuItem write_post = returnMenuItem("Write Post");
        JMenuItem see_post = returnMenuItem("See posts");
        JMenuItem show_noti = returnMenuItem("Show New Notifications");
        JMenuItem show_all_noti = returnMenuItem("Show All Notifications");
        account.add(up_file);
        account.add(del_file);
        account.add(shr_file);
        account.add(change_pro_pic);
        account.add(logout);
        account.add(del_acc);
        post.add(write_post);
        post.add(see_post);
        notification.add(show_noti);
        notification.add(show_all_noti);
        passFrame.setLayout(new GridBagLayout());
        gbcons = new GridBagConstraints();
        variable = c;
        button = new ArrayList<>();
        label = new ArrayList<>();
        gbcons.gridx = 0;
        gbcons.gridy = 0;
        for (int i = 0; i < c; i++) {
            label.add(new JLabel(arr[i]));
            gbcons.insets = new Insets(10, 0, 0, 0);
            gbcons.ipadx = 20;
            passFrame.add(label.get(i), gbcons);
            gbcons.insets = new Insets(10, 0, 0, 0);
            gbcons.gridx++;
            button.add(returnButton("Download button"));
            passFrame.add(button.get(i), gbcons);
            handle.add(new Handle(i, button.get(i)));
            gbcons.gridx = 0;
            gbcons.gridy++;
        }
        passFrame.pack();
        JButton ok;
        JTextField tf = new JTextField(15);
        ok = returnButton("OK button");
        Post pst = new Post(is, os, username);
        Load load = new Load(os, is, client, username);
        checkNotification(load);
        passFrame.setResizable(true);
        passFrame.setVisible(true);
        write_post.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pst.writePost();
            }
        });
        see_post.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    pst.seePost();
                } catch (IOException ex) {
                    Logger.getLogger(Registration.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        shr_file.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    os.writeBytes("Sharing file->" + username + "\n");
                    String line = is.readLine();
                    if (line.isEmpty()) {
                        Toolkit.getDefaultToolkit().beep();
                        JOptionPane.showMessageDialog(null, "There is no user currently exists in the database", "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        load.share(line);
                        if (shared) {
                            if (!sharedFileFromMenuBar.isEmpty()) {
                                updateFrame(sharedFileFromMenuBar);
                            }
                        }
                    }
                } catch (IOException ex) {
                    Logger.getLogger(LogIn.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        change_pro_pic.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal = fc.showOpenDialog(passFrame);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    file2 = fc.getSelectedFile();
                    path = file2.getAbsolutePath();
                    up_frame = returnFrame("Profile Picture");
                    up_frame.setSize(300, 300);
                    up_frame.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent windowEvent) {
                            up_frame.dispose();
                        }
                    });
                    up_frame.setLayout(new GridBagLayout());
                    GridBagConstraints gbc = new GridBagConstraints();
                    gbc.gridx = 0;
                    gbc.gridy = 0;
                    ImageIcon image = new ImageIcon(path);
                    Image img = image.getImage();
                    BufferedImage bi = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
                    Graphics g = bi.createGraphics();
                    g.drawImage(img, 0, 0, 1000, 750, null);
                    ImageIcon newIcon = new ImageIcon(bi);
                    Image img2 = newIcon.getImage();
                    Image newImg = img2.getScaledInstance(up_frame.getWidth(), up_frame.getHeight(), Image.SCALE_SMOOTH);
                    newIcon = new ImageIcon(newImg);
                    JLabel label = new JLabel(newIcon);
                    up_frame.add(label, gbc);
                    up_frame.setVisible(true);
                    up_frame.setResizable(true);
                    label.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            try {
                                os.writeBytes("Uploading Profile Picture" + "->" + username + "\n");
                                is.readLine();
                                load.upload(file2, path);
                                up_frame.dispose();
                            } catch (IOException ex) {
                                Logger.getLogger(Registration.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    });
                }
            }
        });
        show_noti.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    os.writeBytes("Check for notification->" + username + "->true\n");
                    while (true) {
                        String reply = is.readLine();
                        if (reply.equals("No more notification")) {
                            break;
                        } else if (reply.equals("No new notification")) {
                            Toolkit.getDefaultToolkit().beep();
                            JOptionPane.showMessageDialog(null, reply, "Notification", JOptionPane.INFORMATION_MESSAGE);
                            break;
                        }
                        String noti[] = reply.split("->");
                        Object[] options = {"Yes", "No"};
                        Toolkit.getDefaultToolkit().beep();
                        int n = JOptionPane.showOptionDialog(null, noti[0] + " wants to share " + noti[1] + " with you.\nWill you download?", "Notification", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
                        if (n == JOptionPane.YES_OPTION) {
                            Object[] choices = {"Download", "Save to Account"};
                            Toolkit.getDefaultToolkit().beep();
                            int h = JOptionPane.showOptionDialog(null, "What do you want to do?", "Choose operation", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, choices, choices[0]);
                            if (h == JOptionPane.YES_OPTION) {
                                load.setNotificationFlag(true);
                                load.download(noti[1]);
                                load.setNotificationFlag(false);
                            } else if (h == JOptionPane.NO_OPTION) {
                                os.writeBytes("Save to account\n");
                                String answer = is.readLine();
                                if (answer.equals("File Already Exists!")) {
                                    Toolkit.getDefaultToolkit().beep();
                                    JOptionPane.showMessageDialog(null, answer, "Warning", JOptionPane.WARNING_MESSAGE);
                                    continue;
                                }
                                Toolkit.getDefaultToolkit().beep();
                                JOptionPane.showMessageDialog(null, "Saved to your account successfully!", "Congratulations", JOptionPane.INFORMATION_MESSAGE);
                                updateFrame("Shared from\\\\" + noti[1]);
                            } else {
                                os.writeBytes("No\n");
                            }
                        } else {
                            os.writeBytes("No\n");
                        }
                    }
                } catch (IOException ex) {
                    Logger.getLogger(LogIn.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        show_all_noti.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    os.writeBytes("Check for notification->" + username + "->false\n");
                    while (true) {
                        String reply = is.readLine();
                        if (reply.equals("No more notification")) {
                            break;
                        } else if (reply.equals("No new notification")) {
                            Toolkit.getDefaultToolkit().beep();
                            JOptionPane.showMessageDialog(null, "No notification", "Notification", JOptionPane.INFORMATION_MESSAGE);
                            break;
                        }
                        String noti[] = reply.split("->");
                        Object[] options = {"Yes", "No"};
                        Toolkit.getDefaultToolkit().beep();
                        int n = JOptionPane.showOptionDialog(null, noti[0] + " wants to share " + noti[1] + " with you.\nWill you download?", "Notification", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
                        if (n == JOptionPane.YES_OPTION) {
                            Object[] choices = {"Download", "Save to Account"};
                            Toolkit.getDefaultToolkit().beep();
                            int h = JOptionPane.showOptionDialog(null, "What do you want to do?", "Choose operation", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, choices, choices[0]);
                            if (h == JOptionPane.YES_OPTION) {
                                load.setNotificationFlag(true);
                                load.download(noti[1]);
                                load.setNotificationFlag(false);
                            } else if (h == JOptionPane.NO_OPTION) {
                                os.writeBytes("Save to account\n");
                                String answer = is.readLine();
                                if (answer.equals("File Already Exists!")) {
                                    Toolkit.getDefaultToolkit().beep();
                                    JOptionPane.showMessageDialog(null, answer, "Warning", JOptionPane.WARNING_MESSAGE);
                                    continue;
                                }
                                Toolkit.getDefaultToolkit().beep();
                                JOptionPane.showMessageDialog(null, "Saved to your account successfully!", "Congratulations", JOptionPane.INFORMATION_MESSAGE);
                                updateFrame("Shared from\\\\" + noti[1]);
                            } else {
                                os.writeBytes("No\n");
                            }
                        } else {
                            os.writeBytes("No\n");
                        }
                    }
                } catch (IOException ex) {
                    Logger.getLogger(LogIn.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        up_file.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal = fc.showOpenDialog(passFrame);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    file2 = fc.getSelectedFile();
                    path = file2.getAbsolutePath();
                    up_frame = returnFrame("Browse File");
                    up_frame.setSize(700, 500);
                    up_frame.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent windowEvent) {
                            up_frame.dispose();
                        }
                    });
                    up_frame.setLayout(new GridBagLayout());
                    GridBagConstraints gbc = new GridBagConstraints();
                    gbc.gridx = 0;
                    gbc.gridy = 0;
                    gbc.gridwidth = 3;
                    up_frame.add(tf, gbc);
                    gbc.gridx += 3;
                    up_frame.add(ok, gbc);
                    up_frame.pack();
                    tf.setText(path);
                    up_frame.setResizable(true);
                    up_frame.setVisible(true);
                }
            }
        });
        ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    load.upload(file2, path);
                    up_frame.dispose();
                    updateFrame(file2.getName());
                } catch (IOException ex) {
                    Logger.getLogger(Registration.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        del_file.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    os.writeBytes("I want to delete files->" + username + "\n");
                    String reply = is.readLine();
                    if (reply.isEmpty()) {
                        Toolkit.getDefaultToolkit().beep();
                        JOptionPane.showMessageDialog(null, "There is no file to delete", "Warning", JOptionPane.WARNING_MESSAGE);
                    } else {
                        load.delete(reply, passFrame, handle, button, label, arr_list);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(LogIn.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        passFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                try {
                    os.writeBytes("Bye\n");
                    connection = true;
                    response2 = true;
                    client.close();
                    closing = true;
                    Registration.setConnectionValue(true);
                    Registration.setConnectionTwoValue(true);
                    passFrame.dispose();
                    System.exit(0);
                } catch (IOException ex) {
                    Logger.getLogger(Registration.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        del_acc.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Object[] options = {"Yes", "No"};
                    Toolkit.getDefaultToolkit().beep();
                    int n = JOptionPane.showOptionDialog(null, "Are you sure?", "Delete Account", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                    if (n == JOptionPane.YES_OPTION) {
                        os.writeBytes("Delete account->" + username + "\n");
                        is.readLine();
                        os.writeBytes("Bye\n");
                        connection = true;
                        response2 = true;
                        client.close();
                        closing = true;
                        Registration.setConnectionValue(true);
                        Registration.setConnectionTwoValue(true);
                        passFrame.dispose();
                        System.exit(0);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(LogIn.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        logout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    os.writeBytes("Bye\n");
                    connection = true;
                    response2 = true;
                    client.close();
                    closing = true;
                    Registration.setConnectionValue(true);
                    Registration.setConnectionTwoValue(true);
                    passFrame.dispose();
                    System.exit(0);
                } catch (IOException ex) {
                    Logger.getLogger(Registration.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        while (true) {
            if (global) {
                Object[] options = {"Download", "Share"};
                Toolkit.getDefaultToolkit().beep();
                int n = JOptionPane.showOptionDialog(null, "What do you want to do?", "Download or Share", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                if (n == JOptionPane.YES_OPTION) {
                    System.out.println(file_no + " " + arr_list.get(file_no));
                    load.download(arr_list.get(file_no));
                    System.out.println("Done");
                    global = false;
                } else if (n == JOptionPane.NO_OPTION) {
                    os.writeBytes("Sharing file->" + username + "\n");
                    String reply = is.readLine();
                    if (reply.isEmpty()) {
                        Toolkit.getDefaultToolkit().beep();
                        JOptionPane.showMessageDialog(null, "There is no user currently exists in the database", "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        load.share(reply, arr_list.get(file_no), true);
                    }
                    global = false;
                } else {
                    global = false;
                }
            }
            if (response2) {
                break;
            }
        }
        while (true) {
            if (response2 == true) {
                break;
            }
        }
    }
}
