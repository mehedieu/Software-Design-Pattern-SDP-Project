/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sdpserver;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SDPServer {

    /**
     * @param args the command line arguments
     */
    static ServerSocket server;
    static Socket client;
    static DataInputStream is;
    static DataOutputStream os;
    public static FileWriter fout,fout2;
    public static File passwordFile;
    static ArrayList<ClientThread> al;
    static String updated;
    static boolean closingFlag = false;
    
    static class Inner implements Runnable
    {
        Thread t;
        
        public Inner()
        {
            t = new Thread(this);
            t.start();
        }
        
        @Override
        public void run()
        {
            while(true)
            {
                try {   
                    client = server.accept();
                    is = new DataInputStream(client.getInputStream());
                    os = new DataOutputStream(client.getOutputStream());   
                    al.add(new ClientThread(client, is, os));
                } catch (SocketException ex) {
                    break;
                } catch (IOException ex) {
                    Logger.getLogger(SDPServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }        
    }
    
    public static void main(String[] args)throws IOException, InterruptedException {
        server = new ServerSocket(1234);
        ClientThread ct[] = null;
        //flag = false;
        //Scanner b = new Scanner(System.in);
        al = new ArrayList<>();
        File dir = new File("Input files");
        dir.mkdirs();
        File file = new File(dir, "Posts.txt");
        if(!file.exists())
            file.createNewFile();
        FileReader fin = new FileReader(file);
        Scanner b = new Scanner(fin);
        updated = new String();
        int i;
        char ch;
        while ((i = fin.read()) != -1) {
            ch = (char) i;
            updated = updated + ch;
        }
        fin.close();
        passwordFile = new File("Input files\\Passwords.txt");
        fout = new FileWriter(passwordFile,true);
        fout2 = new FileWriter("Input files\\Posts.txt",true);
        /*shareFile = new File("Input files\\Share list.txt");
        if(!shareFile.exists()){
            shareFile.createNewFile();
        }
        fin = new FileReader(shareFile);
        share = new String();
        while ((i = fin.read()) != -1) {
            ch = (char) i;
            share += ch;
        }
        fin.close();*/
        Inner inner = new Inner();
        /*JFrame frame = new JFrame("Whatever");
        frame.setSize(100,100);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);*/
        //System.out.print("Enter Closing Command: ");
        //b.nextLine();
        //flag = true;
        while (true) {            
            if(al.isEmpty()){
                Thread.sleep(60000);
                if(al.isEmpty()){
                    server.close();
                    break;
                }
            }
        }
        inner.t.join();
        //while(!al.isEmpty());
        fout.close();
        fout2.close();
    }
    
}

