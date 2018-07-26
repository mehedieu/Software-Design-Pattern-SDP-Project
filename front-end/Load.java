/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package registration;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

/**
 *
 * @author ASUS
 */
public class Load {

    private DataOutputStream os;
    private DataInputStream is;
    private Socket client;
    private String username, str, string, users[];
    private boolean shrFlag, notiFlag, fileExists;
    private JFileChooser fc;

    public Load(DataOutputStream os, DataInputStream is, Socket client, String username) {
        this.os = os;
        this.is = is;
        this.client = client;
        this.username = username;
        str = new String();
        shrFlag = notiFlag = fileExists = false;
    }
    
    public JFrame returnFrame (String text) {
        AbstractFactory frameFactory = FactoryProducer.getFactory("Frame");
        Frame frame = frameFactory.getFrame(text);
        
        return frame.getFrame();
    }

    public void setNotificationFlag(boolean val) {
        notiFlag = val;
    }
    
    public JMenuItem returnMenuItem (String text) {
        AbstractFactory menuItemFactory = FactoryProducer.getFactory("Menu Item");
        MenuItem menuItem = menuItemFactory.getMenuItem(text);
        
        return menuItem.getMenuItem();
    }

    public void download(String str) {
        try {
            String path;
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new File(str));
            int retval = fileChooser.showSaveDialog(null);
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (retval == JFileChooser.APPROVE_OPTION) {
                path = fileChooser.getSelectedFile().getAbsolutePath();
                if (!notiFlag) {
                    os.writeBytes(username + "->" + str + "->download\n");
                } else {
                    os.writeBytes("Yes\n");
                }
                String reply = is.readLine();
                if (reply.equals("File not found")) {
                    Toolkit.getDefaultToolkit().beep();
                    JOptionPane.showMessageDialog(null, "Sorry! File not found! Sender has removed the file.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int filesize;
                filesize = Integer.parseInt(is.readLine());
                int bytesRead;
                int currentTot = 0;
                byte[] bytearray = new byte[filesize];
                FileOutputStream fos = new FileOutputStream(path);
                BufferedOutputStream bos = new BufferedOutputStream(fos);

                while (currentTot != filesize) {
                    bytesRead = is.read(bytearray, currentTot, (bytearray.length - currentTot));
                    currentTot += bytesRead;
                }

                bos.write(bytearray, 0, currentTot);
                bos.flush();
                bos.close();
            } else {
                os.writeBytes("No\n");
            }
        } catch (IOException ex) {
            Logger.getLogger(Registration.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void delete(String file_names, JFrame parentFrame, ArrayList<Handle> handle, ArrayList<JButton> button, ArrayList<JLabel> labels, ArrayList<String> arr_list) {
        String arr[] = file_names.split(",");
        str = "";
        JFrame frame = returnFrame("Delete Files");
        frame.setSize(700, 500);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);
        JMenu opt = new JMenu("Options");
        menuBar.add(opt);
        JMenuItem select_all = returnMenuItem("Select All");
        JMenuItem delOption = returnMenuItem("Delete");
        opt.add(select_all);
        opt.add(delOption);
        int c = arr.length;
        JLabel label[] = new JLabel[c];
        JCheckBox cb[] = new JCheckBox[c];
        gbc.gridx = 0;
        gbc.gridy = 0;
        for (int i = 0; i < c; i++) {
            label[i] = new JLabel(arr[i]);
            gbc.insets = new Insets(10, 0, 0, 0);
            frame.add(label[i], gbc);
            gbc.gridx++;
            cb[i] = new JCheckBox();
            gbc.insets = new Insets(10, 0, 0, 0);
            frame.add(cb[i], gbc);
            gbc.gridx = 0;
            gbc.gridy++;
        }
        frame.pack();
        frame.setResizable(true);
        frame.setVisible(true);
        delOption.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Object[] options = {"Yes", "No"};
                Toolkit.getDefaultToolkit().beep();
                int n = JOptionPane.showOptionDialog(null, "Are you sure?", "Delete Files", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                if (n == JOptionPane.YES_OPTION) {
                    for (int i = 0; i < c; i++) {
                        if (cb[i].isSelected()) {
                            for (int j = 0; j < labels.size(); j++) {
                                if (labels.get(j).getText().equals(label[i].getText())) {
                                    labels.get(j).setVisible(false);
                                    button.get(j).setVisible(false);
                                    handle.remove(j);
                                    arr_list.remove(j);
                                    labels.remove(j);
                                    button.remove(j);
                                    break;
                                }
                            }
                            str = str + "," + arr[i];
                        }
                    }
                    if (str.isEmpty()) {
                        Toolkit.getDefaultToolkit().beep();
                        JOptionPane.showMessageDialog(null, "First select the files and then delete please", "Warning", JOptionPane.WARNING_MESSAGE);
                    } else {
                        try {
                            os.writeBytes("delete files->" + str + "->" + username + "\n");
                            frame.dispose();
                        } catch (IOException ex) {
                            Logger.getLogger(Load.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        });
        select_all.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < c; i++) {
                    cb[i].setSelected(true);
                }
            }
        });
    }

    public void upload(File file_to_upload, String file_path) throws IOException {
        String line = file_to_upload.getName();
        if (!shrFlag) {
            os.writeBytes(username + "->" + line + "->upload\n");
        }
        System.out.println("Accepted connection : " + client);
        File transferFile = new File(file_path);
        byte[] bytearray = new byte[(int) transferFile.length()];
        FileInputStream fin = new FileInputStream(transferFile);
        BufferedInputStream bin = new BufferedInputStream(fin);
        bin.read(bytearray, 0, bytearray.length);
        System.out.println("Sending Files...");
        os.writeBytes(bytearray.length + "\n");
        os.write(bytearray, 0, bytearray.length);
        os.flush();
        String answer = is.readLine();
        if (answer.equals("File Already Exists!")) {
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(null, answer, "Warning", JOptionPane.WARNING_MESSAGE);
            fileExists = true;
        } else {
            System.out.println("File transfer complete");
        }
    }

    public void share(String s, String file_name, boolean exist) {
        users = s.split("->");
        string = "";
        int c = users.length;
        JCheckBox cb[] = new JCheckBox[c];
        JLabel label[] = new JLabel[c];
        JFrame frame = returnFrame("Share Files");
        frame.setSize(700, 500);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);
        JMenu opt = new JMenu("Options");
        menuBar.add(opt);
        JMenuItem select_all = returnMenuItem("Select All");
        JMenuItem shrOption = returnMenuItem("Share");
        opt.add(select_all);
        opt.add(shrOption);
        gbc.gridx = 0;
        gbc.gridy = 0;
        for (int i = 0; i < c; i++) {
            label[i] = new JLabel(users[i]);
            gbc.insets = new Insets(10, 0, 0, 0);
            frame.add(label[i], gbc);
            gbc.gridx++;
            cb[i] = new JCheckBox();
            gbc.insets = new Insets(10, 0, 0, 0);
            frame.add(cb[i], gbc);
            gbc.gridx = 0;
            gbc.gridy++;
        }
        frame.pack();
        frame.setResizable(true);
        frame.setVisible(true);
        shrOption.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < c; i++) {
                    if (cb[i].isSelected()) {
                        string = string + "," + users[i];
                    }
                }
                if (string.isEmpty()) {
                    Toolkit.getDefaultToolkit().beep();
                    JOptionPane.showMessageDialog(null, "First select users and then share the file please", "Warning", JOptionPane.WARNING_MESSAGE);
                } else {
                    try {
                        os.writeBytes("Write to share list->" + username + "->" + string.substring(1) + "->" + file_name + "->" + String.valueOf(exist) + "\n");
                        frame.dispose();
                    } catch (IOException ex) {
                        Logger.getLogger(Load.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        select_all.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < c; i++) {
                    cb[i].setSelected(true);
                }
            }
        });
    }

    public void share(String s) {
        users = s.split("->");
        string = "";
        int c = users.length;
        JCheckBox cb[] = new JCheckBox[c];
        JLabel label[] = new JLabel[c];
        JFrame frame = returnFrame("Share Files");
        frame.setSize(700, 500);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);
        JMenu opt = new JMenu("Options");
        menuBar.add(opt);
        JMenuItem select_all = returnMenuItem("Select All");
        JMenuItem shrOption = returnMenuItem("Share");
        opt.add(select_all);
        opt.add(shrOption);
        gbc.gridx = 0;
        gbc.gridy = 0;
        for (int i = 0; i < c; i++) {
            label[i] = new JLabel(users[i]);
            gbc.insets = new Insets(10, 0, 0, 0);
            frame.add(label[i], gbc);
            gbc.gridx++;
            cb[i] = new JCheckBox();
            gbc.insets = new Insets(10, 0, 0, 0);
            frame.add(cb[i], gbc);
            gbc.gridx = 0;
            gbc.gridy++;
        }
        frame.pack();
        frame.setResizable(true);
        frame.setVisible(true);
        shrOption.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                fc = new JFileChooser();
                int retval;
                while (true) {
                    retval = fc.showOpenDialog(frame);
                    if (retval == JFileChooser.APPROVE_OPTION) {
                        for (int i = 0; i < c; i++) {
                            if (cb[i].isSelected()) {
                                string = string + "," + users[i];
                            }
                        }
                        if (string.isEmpty()) {
                            Toolkit.getDefaultToolkit().beep();
                            JOptionPane.showMessageDialog(null, "First select users and then select the file please", "Warning", JOptionPane.WARNING_MESSAGE);
                            continue;
                        } else {
                            break;
                        }
                    } else {
                        break;
                    }
                }
                try {
                    if (retval == JFileChooser.APPROVE_OPTION) {
                        frame.dispose();
                        os.writeBytes("Create a directory->" + username + "\n");
                        is.readLine();
                        os.writeBytes(fc.getSelectedFile().getName() + "\n");
                        is.readLine();
                        shrFlag = true;
                        upload(fc.getSelectedFile(), fc.getSelectedFile().getAbsolutePath());
                        shrFlag = false;
                        if (!fileExists) {
                            os.writeBytes(username + "->" + string.substring(1) + "->" + fc.getSelectedFile().getName() + "\n");
                            LogIn.setSharedValue(true);
                            LogIn.setStringValue(fc.getSelectedFile().getName());
                            is.readLine();
                        } else {
                            LogIn.setSharedValue(true);
                            LogIn.setStringValue("");
                        }
                    }
                } catch (IOException ex) {
                    Logger.getLogger(Load.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        select_all.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < c; i++) {
                    cb[i].setSelected(true);
                }
            }
        });
    }
}
