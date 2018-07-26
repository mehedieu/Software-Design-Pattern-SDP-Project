/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sdpserver;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.mail.*;
import java.util.Scanner;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author ASUS
 */
public class ClientThread implements Runnable {

    private DataOutputStream os;
    private DataInputStream is;
    private Socket client;
    private String arr[], posting[];
    private File file;
    private boolean pic_flag, shr_flag, notiFlag, fileAlreadyExistsFlag;
    Thread t;
    
    public ClientThread(Socket client, DataInputStream is, DataOutputStream os){
        this.client = client;
        this.is = is;
        this.os = os;
        pic_flag = shr_flag = notiFlag = fileAlreadyExistsFlag = false;
        t = new Thread(this);
        t.start();
    }
    
    public void writePost(String str, String username) throws IOException {
        String s, result[];
        File fin = new File("Input files");
        int c, keep = 0;
        result = fin.list();
        c = result.length;
        for (int i = 0; i < c; i++) {
            if (result[i].equals(username)) {
                keep = i;
                break;
            }
        }
        FileWriter fout = new FileWriter("Input files\\" + result[keep] + "\\Timeline.txt", true);
        synchronized (SDPServer.fout2) {
            synchronized (SDPServer.updated) {
                SDPServer.fout2.write(username + ":");
                SDPServer.updated = SDPServer.updated + username + ":\n";
                SDPServer.fout2.write(System.getProperty("line.separator"));
                SDPServer.fout2.write(str);
                SDPServer.updated = SDPServer.updated + str + "\n\n";
                SDPServer.fout2.write(System.getProperty("line.separator"));
                SDPServer.fout2.write(System.getProperty("line.separator"));
            }
        }
        fout.write(str);
        fout.write(System.getProperty("line.separator"));
        fout.write(System.getProperty("line.separator"));
        fout.close();
    }

    public String encode(String str) {
        int c, x, z, j;
        String s1, s2, s3;
        s2 = new String();
        s3 = new String();
        c = str.length();
        for (int i = 0; i < c; i++) {
            x = (int) str.charAt(i);
            s1 = Integer.toBinaryString(x);
            z = 8 - s1.length();
            for (j = 0; j < z; j++) {
                s2 = s2 + "0";
            }
            s2 = s2 + s1;
            s3 = s3 + s2;
            s2 = new String();
        }

        return s3;
    }

    public void loadPost() throws FileNotFoundException, IOException {
        String s = encode(SDPServer.updated);
        os.writeBytes(s + "\n");
    }

    public void delete(String str, String usr_name) {
        String names[] = str.split(",");
        int c = names.length;
        File f[] = new File[c];
        for (int i = 0; i < c; i++) {
            f[i] = new File("Input files\\" + usr_name + "\\" + names[i]);
            f[i].delete();
        }
    }

    public void download(String str, String str2) throws IOException {
        System.out.println("Accepted connection : " + client);
        File transferFile;
        try {
            if (!notiFlag) {
                transferFile = new File("Input files\\" + str + "\\" + str2);
            } else {
                transferFile = new File("Input files\\" + str + "\\Shared to\\" + str2);
            }
            byte[] bytearray = new byte[(int) transferFile.length()];
            FileInputStream fin = new FileInputStream(transferFile);
            BufferedInputStream bin = new BufferedInputStream(fin);
            os.writeBytes("File found\n");
            bin.read(bytearray, 0, bytearray.length);
            System.out.println("Sending Files...");
            os.writeBytes(bytearray.length + "\n");
            os.write(bytearray, 0, bytearray.length);
            os.flush();
            System.out.println("File transfer complete");
        } catch (FileNotFoundException fnfe) {
            os.writeBytes("File not found\n");
        }
    }

    public void upload(String str, String str2) throws FileNotFoundException, IOException {
        int filesize;
        filesize = Integer.parseInt(is.readLine());
        int bytesRead;
        int currentTot = 0;
        byte[] bytearray = new byte[filesize];
        FileOutputStream fos;
        if (pic_flag == true) {
            str2 = "Profile_Picture.png";
            pic_flag = false;
        }
        if (!shr_flag) {
            fos = new FileOutputStream("Input files\\" + str + "\\" + str2);
        } else {
            fos = new FileOutputStream("Input files\\" + str + "\\Shared to\\" + str2);
        }
        BufferedOutputStream bos = new BufferedOutputStream(fos);

        while (currentTot != filesize) {
            bytesRead = is.read(bytearray, currentTot, (bytearray.length - currentTot));
            currentTot += bytesRead;
        }
        try {
            bos.write(bytearray, 0, currentTot);
        } catch (FileAlreadyExistsException faee) {
            os.writeBytes("File Already Exists!\n");
            fileAlreadyExistsFlag = true;
        }
        bos.flush();
        bos.close();
        if (!fileAlreadyExistsFlag) {
            System.out.println("Done");
            os.writeBytes("Success\n");
        }
    }

    public void sendMail(String target_address, String pass_word, String name) {
        final String username = "farhankanak@gmail.com";
        final String password = "2lmab36ngkz";
        Properties props = new Properties();
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("farhankanak@gmail.com"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(target_address));
            message.setSubject("Get In Touch Account Registration success");
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText("Congratulations! " + name + ", your account has been created successfully. Your Password is: " + pass_word + ". Your provided informations are given as an attachment below:");
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            messageBodyPart = new MimeBodyPart();
            String filename = "Input files\\" + name + "\\form.txt";
            DataSource source = new FileDataSource(filename);
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName("form.txt");
            multipart.addBodyPart(messageBodyPart);
            message.setContent(multipart);
            Transport.send(message);
            System.out.println("Done");
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public String getUsersList(String exceptional) {
        File dir = new File("Input files");
        File list[] = dir.listFiles();
        int c = list.length;
        String ret = new String();
        for (int i = 0; i < c; i++) {
            if (list[i].isDirectory() && (!list[i].getName().equals(exceptional))) {
                ret += "->" + list[i].getName();
            }
        }

        if (ret.isEmpty()) {
            return ret;
        }
        return ret.substring(2);
    }

    public synchronized void writeToShareList(String file_path, String info) throws IOException {
        File dir = new File(file_path);
        if (!dir.exists()) {
            dir.mkdir();
        }
        File doWrite = new File(dir, "Share list.txt");
        FileWriter fw = new FileWriter(doWrite, true);
        fw.write(info);
        fw.write(System.getProperty("line.separator"));
        fw.close();
    }

    public String storeToBeSharedFiles(String usr_name) throws IOException {
        File dir = new File("Input files\\" + usr_name + "\\Shared to");
        dir.mkdir();
        os.writeBytes("Directory created\n");
        shr_flag = true;
        String fName = is.readLine();
        os.writeBytes("Start sending\n");
        upload(usr_name, fName);
        shr_flag = false;
        /*File shr_list = new File(dir, "Share list.txt");
         FileWriter fout = new FileWriter(shr_list);
         String data[] = is.readLine().split("->");
         String usrs[] = data[0].split(",");
         int c = usrs.length;
         for (int i = 0; i < c - 1; i++) {
         fout.write(usrs[i] + "->" + data[1]);
         fout.write(System.getProperty("line.separator"));
         }
         fout.write(usrs[c - 1] + "->" + data[1]);
         fout.close();*/
        /*synchronized (SDPServer.fout3) {
         synchronized (SDPServer.share) {*/
        if (!fileAlreadyExistsFlag) {
            String data[] = is.readLine().split("->");
            String usrs[] = data[1].split(",");
            int c = usrs.length;
            for (int i = 0; i < c; i++) {
                /*SDPServer.fout3.write(data[0] + "->" + usrs[i] + "->" + data[2]);
                 SDPServer.fout3.write(System.getProperty("line.separator"));
                 SDPServer.share += data[0] + "->" + usrs[i] + "->" + data[2] + "\n";*/
                writeToShareList("Input files\\" + usrs[i] + "\\Shared from", data[0] + "->" + data[2]);
            }
            //}
            //}

            return "Done\n";
        }

        return "";
    }

    public synchronized void saveToAccount(String srcFolder, String srcFile, String destFolder, boolean flag) throws IOException {
        boolean success = true;
        Path src;
        if (flag) {
            src = Paths.get("Input files\\" + srcFolder + "\\" + srcFile);
        } else {
            src = Paths.get("Input files\\" + srcFolder + "\\Shared to\\" + srcFile);
        }
        Path dest = Paths.get("Input files\\" + destFolder + "\\Shared from");
        try {
            Files.copy(src, dest.resolve(src.getFileName()));
        } catch (FileAlreadyExistsException faee) {
            os.writeBytes("File Already Exists!\n");
            success = false;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        if (success) {
            os.writeBytes("Saved to your account\n");
        }
    }

    public synchronized void checkForNotification(String username, boolean flag) throws IOException {
        //String check[] = SDPServer.share.split(System.getProperty("line.separator"));
        try {
            FileReader fr = new FileReader("Input files\\" + username + "\\Shared from\\Share list.txt");
            Scanner scanner = new Scanner(fr);
            boolean enter = false;
            ArrayList al = new ArrayList();
            while (scanner.hasNextLine()) {
                String string = scanner.nextLine();
                if (flag) {
                    if (string.substring(string.length() - 4).equals("seen")) {
                        al.add(true);
                        continue;
                    } else {
                        al.add(false);
                    }
                }
                String extract[] = string.split("->");
                os.writeBytes(extract[0] + "->" + extract[1] + "\n");
                String reply = is.readLine();
                enter = true;
                if (reply.equals("Yes")) {
                    if ((extract.length == 4) || (extract.length == 3)) {
                        if (extract[2].equals("true")) {
                            download(extract[0], extract[1]);
                        } else {
                            notiFlag = true;
                            download(extract[0], extract[1]);
                            notiFlag = false;
                        }
                    } else {
                        notiFlag = true;
                        download(extract[0], extract[1]);
                        notiFlag = false;
                    }
                } else if (reply.equals("Save to account")) {
                    if (extract.length == 4) {
                        saveToAccount(extract[0], extract[1], username, true);
                    } else {
                        if (extract.length == 3) {
                            if (extract[2].equals("seen")) {
                                saveToAccount(extract[0], extract[1], username, false);
                            } else {
                                saveToAccount(extract[0], extract[1], username, true);
                            }
                        } else {
                            saveToAccount(extract[0], extract[1], username, false);
                        }
                    }
                }
            }
            /*int c = check.length;
             boolean enter = false;
             for (int i = 0; i < c; i++) {
             if (!check[i].isEmpty()) {
             String extract[] = check[i].split("->");
             if (extract[1].equals(username)) {
             enter = true;*/
                    //os.writeBytes(extract[0] + "->" + extract[2] + "\n");
                    /*String reply = is.readLine();
             if (reply.equals("Yes")) {
             if (extract.length == 4) {
             download(extract[0], extract[2]);
             } else {
             notiFlag = true;
             download(extract[0], extract[2]);
             notiFlag = false;
             }
             }*/
            //}
            // }
            //}
            if (enter) {
                os.writeBytes("No more notification\n");
            }
            if (!notiFlag && !enter) {
                os.writeBytes("No new notification\n");
            }
            fr.close();
            List<String> fileContent = new ArrayList<>(Files.readAllLines(Paths.get("Input files\\" + username + "\\Shared from\\Share list.txt"), StandardCharsets.UTF_8));
            if (flag) {
                for (int i = 0; i < fileContent.size(); i++) {
                    if (!(boolean) al.get(i)) {
                        fileContent.set(i, fileContent.get(i) + "->seen");
                    }
                }
            } else {
                for (int i = 0; i < fileContent.size(); i++) {
                    if (!(fileContent.get(i).substring(fileContent.get(i).length() - 4).equals("seen"))) {
                        fileContent.set(i, fileContent.get(i) + "->seen");
                    }
                }
            }
            Files.write(Paths.get("Input files\\" + username + "\\Shared from\\Share list.txt"), fileContent, StandardCharsets.UTF_8);
        } catch (FileNotFoundException fnfe) {
            os.writeBytes("No new notification\n");
        }
    }

    public void createAccount(String str) throws IOException {
        String info[] = str.split("->");
        File dir = new File("Input files\\" + info[0]);
        dir.mkdirs();
        Path src = Paths.get("Profile_Picture.png");
        Path dest = Paths.get("Input files\\" + info[0]);
        try {
            Files.copy(src, dest.resolve(src.getFileName()));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        file = new File(dir, "form.txt");
        FileWriter fout = new FileWriter(file);
        //FileWriter fout2 = new FileWriter("Input files\\Passwords.txt",true);
        fout.write("Username: " + info[0]);
        fout.write(System.getProperty("line.separator"));
        fout.write("Roll no.: " + info[1]);
        fout.write(System.getProperty("line.separator"));
        fout.write("Address: " + info[2]);
        fout.write(System.getProperty("line.separator"));
        fout.write("Gender: " + info[3]);
        fout.write(System.getProperty("line.separator"));
        fout.write("Hobby: " + info[4]);
        fout.write(System.getProperty("line.separator"));
        fout.write("Password: " + info[5]);
        fout.write(System.getProperty("line.separator"));
        fout.write("Email-address: " + info[6]);
        fout.write(System.getProperty("line.separator"));
        fout.close();
        synchronized (SDPServer.fout) {
            SDPServer.fout.write(info[0] + "->" + info[5] + "->" + info[1]);
            SDPServer.fout.write(System.getProperty("line.separator"));
            //fout2.close();
        }
        //sendMail(info[6],info[5],info[0]);
    }

    synchronized public void deleteAccount(String usr_name) throws FileNotFoundException, IOException {
        File folder = new File("Input files\\" + usr_name);
        String list[] = folder.list();
        int c = list.length;
        File f[] = new File[c];
        for (int i = 0; i < c; i++) {
            f[i] = new File(folder, list[i]);
            if (f[i].isDirectory()) {
                String contents[] = f[i].list();
                int len = contents.length;
                File array[] = new File[len];
                for (int j = 0; j < len; j++) {
                    array[j] = new File(f[i], contents[j]);
                    array[j].delete();
                }
            }
            f[i].delete();
        }
        folder.delete();
        List<String> lines = FileUtils.readLines(SDPServer.passwordFile);
        List<String> updatedLines = lines.stream().filter(s -> !s.contains(usr_name)).collect(Collectors.toList());
        FileUtils.writeLines(SDPServer.passwordFile, updatedLines, false);
    }

    @Override
    public void run() {
        String s, req[], files[], names, down[], up[], del[], folders, dirs[];
        int c, index = 0;
        File folder = null, folder2 = null;
        boolean flag = false, file_touch = false;
        FileReader fin = null;
        while (true) {
            try {
                String line = is.readLine();
                if (line.equals("Sorry")) {
                    continue;
                }
                if (line.contains("Write to share list")) {
                    String extract[] = line.split("->");
                    //synchronized (SDPServer.fout3) {
                    //synchronized (SDPServer.share) {
                    String usrs[] = extract[2].split(",");
                    int x = usrs.length;
                    for (int i = 0; i < x; i++) {
                        /*SDPServer.fout3.write(extract[1] + "->" + usrs[i] + "->" + extract[3] + "->" + extract[4]);
                         SDPServer.fout3.write(System.getProperty("line.separator"));
                         SDPServer.share += extract[1] + "->" + usrs[i] + "->" + extract[3] + "->" + extract[4] + "\n";*/
                        writeToShareList("Input files\\" + usrs[i] + "\\Shared from", extract[1] + "->" + extract[3] + "->" + extract[4]);
                    }
                    //}
                    //}
                    continue;
                }
                if (line.contains("Check for notification")) {
                    String extract[] = line.split("->");
                    checkForNotification(extract[1], Boolean.parseBoolean(extract[2]));
                    continue;
                }
                if (line.contains("Create a directory")) {
                    String usr_name[] = line.split("->");
                    String reply = storeToBeSharedFiles(usr_name[1]);
                    if (!reply.isEmpty()) {
                        os.writeBytes(reply + "\n");
                    }
                    continue;
                }
                if (line.contains("I want to delete files")) {
                    String extract[] = line.split("->");
                    File fold = new File("Input files\\" + extract[1]);
                    String str[] = fold.list();
                    String deletable = new String();
                    int len = str.length;
                    for (int i = 0; i < len; i++) {
                        if (!(str[i].equals("form.txt") || str[i].equals("Profile_Picture.png") || str[i].equals("Timeline.txt") || str[i].equals("Shared to") || str[i].equals("Shared from"))) {
                            deletable = deletable + "," + str[i];
                        } else if (str[i].equals("Shared to")) {
                            File shr_fold = new File("Input files\\" + extract[1] + "\\" + str[i]);
                            String contents[] = shr_fold.list();
                            int length = contents.length;
                            for (int j = 0; j < length; j++) {
                                deletable += "," + str[i] + "\\\\" + contents[j];
                            }
                        } else if (str[i].equals("Shared from")) {
                            File shr_fold = new File("Input files\\" + extract[1] + "\\" + str[i]);
                            String contents[] = shr_fold.list();
                            int length = contents.length;
                            for (int j = 0; j < length; j++) {
                                if (!contents[j].equals("Share list.txt")) {
                                    deletable += "," + str[i] + "\\\\" + contents[j];
                                }
                            }
                        }
                    }
                    if (!deletable.isEmpty()) {
                        os.writeBytes(deletable.substring(1) + "\n");
                    } else {
                        os.writeBytes(deletable + "\n");
                    }
                    continue;
                }
                if (line.equals("Please load the posts")) {
                    loadPost();
                    continue;
                }
                if (line.contains("Writing post")) {
                    posting = line.split("->");
                    writePost(posting[2], posting[1]);
                    continue;
                }
                if (line.contains("Uploading Profile Picture")) {
                    String profile[] = line.split("->");
                    File fi = new File("Input files\\" + profile[1] + "\\Profile_Picture.png");
                    fi.delete();
                    os.writeBytes("start uploading\n");
                    pic_flag = true;
                } else {
                    if (line.contains("Sign up")) {
                        String info[] = line.split(":-");
                        String usr_name[] = info[1].split("->");
                        File dir = new File("Input files");
                        String accs[] = dir.list();
                        int len = accs.length;
                        boolean permission = true;
                        for (int i = 0; i < len; i++) {
                            if (accs[i].contains("\\.") == false) {
                                if (accs[i].equals(usr_name[0])) {
                                    os.writeBytes("Account already exists\n");
                                    permission = false;
                                    break;
                                }
                            }
                        }
                        if ((!usr_name[6].contains("@")) || (!usr_name[6].contains("."))) {
                            permission = false;
                            os.writeBytes("Invalid email-address!\n");
                        }
                        if (permission) {
                            os.writeBytes("Accepted\n");
                            createAccount(info[1]);
                        }
                    } else {
                        if (line.contains("download")) {
                            down = line.split("->");
                            download(down[0], down[1]);
                        } else if (line.contains("upload")) {
                            up = line.split("->");
                            upload(up[0], up[1]);
                        } else if (line.contains("delete files")) {
                            del = line.split("->");
                            delete(del[1], del[2]);
                        } else if (line.contains("Sharing file")) {
                            String usr_shr[] = line.split("->");
                            os.writeBytes(getUsersList(usr_shr[1]) + "\n");
                        } else if (line.contains("Delete account")) {
                            String sep[] = line.split("->");
                            deleteAccount(sep[1]);
                            os.writeBytes("Deleted\n");
                        } else {
                            if (line.equals("Bye")) {
                                break;
                            }
                            folder2 = new File("Input files");
                            dirs = folder2.list();
                            c = dirs.length;
                            req = line.split("->");
                            for (int i = 0; i < c; i++) {
                                if ((dirs[i].equals(req[0])) && (dirs[i].contains("\\.") == false)) {
                                    fin = new FileReader("Input files\\" + dirs[i] + "\\form.txt");
                                    Scanner b = new Scanner(fin);
                                    file_touch = true;//It means at least the username is correct. So, if we enters in any folder, then we will set it's value. As a result, we will then need to close the "form.txt" file.
                                    while (b.hasNextLine()) {
                                        String next = b.nextLine();
                                        if (next.contains("Password")) {
                                            if (next.contains(req[1])) {
                                                os.writeBytes("Accepted\n");
                                                System.out.println("Accepted");
                                                folder = new File("Input files\\" + dirs[i]);
                                                index = i;
                                                flag = true;
                                                fin.close();
                                                break;
                                            }
                                        }
                                    }
                                    break;
                                }
                            }
                            if (flag == false) {
                                os.writeBytes("Invalid Username or Password\n");
                                if (file_touch) {
                                    fin.close();
                                }
                                continue;
                            }
                            files = folder.list();
                            c = files.length;
                            names = files[0];
                            for (int i = 1; i < c; i++) {
                                if (!files[i].equals("Shared to") && !files[i].equals("Shared from")) {
                                    names = names + "," + files[i];
                                } else if (files[i].equals("Shared to") || files[i].equals("Shared from")) {
                                    File shr_fold = new File("Input files\\" + dirs[index] + "\\" + files[i]);
                                    String contents[] = shr_fold.list();
                                    int length = contents.length;
                                    for (int j = 0; j < length; j++) {
                                        names = names + "," + files[i] + "\\\\" + contents[j];
                                    }
                                }
                            }
                            os.writeBytes(names + "\n");
                        }
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            client.close();
            SDPServer.al.remove(this);
        } catch (IOException ex) {
            Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
