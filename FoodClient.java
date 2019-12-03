/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.awt.SystemColor;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;

/**
 *
 * @author sudhakar
 */
public class FoodClient 
{    
    private static PublicKey publicKey;
    final int serverPort;
     final String host;
     private final Socket clientSocket;
     Scanner input ;
    public FoodClient(int serverPort, String host) throws NoSuchAlgorithmException, UnknownHostException, IOException{
   
       this.serverPort = serverPort;
       this.host = host;
       clientSocket = new Socket(host, serverPort); 
      
    }
    
   public Socket getSocket() {
      return this.clientSocket;
   }
   public  byte [] encrypt( String message) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");  
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);  
        
        byte [] cipherData = cipher.doFinal(message.getBytes("UTF-8"));
        return cipherData;  
    }
    
    
    public static void main(String [] args) throws NoSuchAlgorithmException, InvalidKeySpecException, Exception
    {    
        byte []  bytesPublicKey = null; 
        StringBuilder str = new StringBuilder();
        StringBuilder message = new StringBuilder();
        
        Socket s = null;
        //Setting up the connection with the clent
        try
        {
            int serverport = 7896;
            
            s = new Socket("localhost", serverport);            
            
            DataOutputStream out = new DataOutputStream(s.getOutputStream());
         
            out.writeUTF("start");
            
            DataInputStream inClient = new DataInputStream(s.getInputStream());
            
            System.out.println(inClient.readUTF());
            
            menu(inClient, out); 
           
        }
        catch (IOException ex) 
        {
            Logger.getLogger(FoodClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    //MEthod the display and send the imput values to the server and recieve the output
    public static void menu(DataInputStream in, DataOutputStream out) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, Exception
    {   
        FoodClient foodClient =  new FoodClient(7896, "localhost");
        byte []  bytesPublicKey = null; 
        int pubKeyLength;
        boolean menuOption = true;
        String userOption;
        String name;
        String password;
        String option;
        boolean registration = true;
       
        Scanner input = new Scanner(System.in);
               
    while(registration == true)
    {
        
         System.out.println("\nYour Options are ");
        System.out.println("\n\t1.New User");
        System.out.println("\n\t2.Registered User");
        System.out.println("\nPlease enter a number");
         userOption = input.nextLine();
        switch(userOption)
            {   
               
            
                case "1":
                   
                    //Send the first option through the connection
                    System.out.println("\nEnter your Name");
                    name = input.nextLine();
                    out.writeUTF("first");
                    out.writeUTF(name);
                    System.out.println("\nSending your public key");
                    //read the size PublicKey
                    pubKeyLength = in.readInt();
                    bytesPublicKey = new byte[pubKeyLength];
                    //read the PublicKey in bytes sent from the sever
                    in.readFully(bytesPublicKey, 0, pubKeyLength);                    
                    System.out.println("\nEnter your password");
                    password = input.nextLine();
                    X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(bytesPublicKey);
                    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                    //extract the PublicKey
                    publicKey = keyFactory.generatePublic(pubKeySpec);
                    //ecrypt the password
                    byte[] encodedmessage =  foodClient.encrypt(password);
                    //send the encrypted password length
                     out.writeInt(encodedmessage.length);
                     //send the encrypted password in bytes
                    out.write(encodedmessage, 0, encodedmessage.length);
                    System.out.printf("\nYour UserdID is %s",in.readUTF());
                    System.out.printf("\nHello %s you are registered", name);
                    registration  = false;
                    break;
                    
                case "2":
                    
                    int userID = 0;
                    String Username;
                    String UPassword;
                    String logStatus;
                    System.out.println("\nEnter your UserID");                  
                    userID = input.nextInt();
                    
                    String s = input.nextLine();
                    
                    System.out.println("\nSending your public key");
                    
                    System.out.println("\nEnter your password");
                    UPassword = input.nextLine();
                    out.writeUTF("second");
                    out.writeInt(userID);
                    
                    //read the size PublicKey
                    pubKeyLength = in.readInt();
                    bytesPublicKey = new byte[pubKeyLength];
                    //read the PublicKey in bytes sent from the sever
                    in.readFully(bytesPublicKey, 0, pubKeyLength);  
                    
                    
                    X509EncodedKeySpec pubKeySpecs = new X509EncodedKeySpec(bytesPublicKey);
                    KeyFactory keyfactory = KeyFactory.getInstance("RSA");
                    //extract the PublicKey
                    publicKey = keyfactory.generatePublic(pubKeySpecs);
                    //ecrypt the password
                    byte[] encodedMessage =  foodClient.encrypt(UPassword);
                    //send the encrypted password length
                     out.writeInt(encodedMessage.length);
                     //send the encrypted password in bytes
                    out.write(encodedMessage, 0, encodedMessage.length);
                    //System.out.printf("\nYour UserdID is %s",in.readUTF());
                    String result = in.readUTF();
                    
                    //System.out.printf("the result after the check is %s",result);
                    
                    if(result.equalsIgnoreCase("success"))
                    {
                        
                        System.out.printf("\nHello %s you have successfully logged in",in.readUTF());
                        registration  = false;
                        
                    }
                    
                    else
                    {
                        System.out.println("\nInvalid Username or password. Try again");
                    }
                    
                    
                    break;
        
        }
    }   
        
        System.out.println("\n\nAvailable options");
        System.out.println("\n\t\t1.Display all beverages");
        System.out.println("\n\t\t2.Display all Cereals");
        System.out.println("\n\t\t3.Display high protein cereals");
        System.out.println("\n\t\t4.Display low sugar beverages");
        System.out.println("\n\t\t5.Exit");
       
        
        while(menuOption)
        {
            System.out.println("\nEnter a number to view full listing of processed Food items of your choice:");
            
            option = input.nextLine();
            //Switch case method to send the option and recieve the output from the server
            switch(option)
            {
                case "1":
                    //Send the first option through the connection
                    out.writeUTF(option);
                    //Print the string recieved from the server
                    System.out.print(in.readUTF());
                    
                    break;
                    
                case "2":
                    
                    out.writeUTF(option);
                     
                    System.out.print(in.readUTF());
                    
                    break;
                    
                case "3":
                    
                    out.writeUTF(option);
                     
                    System.out.print(in.readUTF());
                    
                    break;
                    
                case "4":
                    
                    out.writeUTF(option);
                     
                           
                    System.out.print(in.readUTF());
                    
                    break;
                    
                case "5":
                    
                    out.writeUTF(option);
                    
                    System.exit(0);
                    
                    break;
            }
            
          
            

        }

        
        
    }
    
    
       
}

