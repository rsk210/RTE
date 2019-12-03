
import com.sun.org.apache.bcel.internal.generic.F2D;
import java.io.IOException;

import java.util.ArrayList;
import java.net.*;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author sudhakar
 */
public class FoodServer 
{
    private final KeyPairGenerator keyPairGen;
    private final PrivateKey privateKey;
    private final PublicKey publicKey;
    private static int clientCount;
    
     public FoodServer() throws NoSuchAlgorithmException {
      //Generating Keypair by mentioning the RSA algorithm
      keyPairGen =  KeyPairGenerator.getInstance("RSA");
      KeyPair keyPair = keyPairGen.genKeyPair();
      this.privateKey = keyPair.getPrivate();
      this.publicKey = keyPair.getPublic(); 
      
   }
   //Getters
  public PrivateKey getPrivateKey() {
      return this.privateKey;
   }
  
   public PublicKey getPublicKey() {
      return publicKey;
   }
   public KeyPairGenerator getKeyPairGen() {
      return keyPairGen;
   }
  public String decrypt(byte [] encodedMessage) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException{
      Cipher cipher = Cipher.getInstance("RSA");  
      cipher.init(Cipher.DECRYPT_MODE, privateKey, cipher.getParameters());
      return new String(cipher.doFinal(encodedMessage));
   } 
    // Creating arraylist foods for storing object of type food item
    ArrayList<FoodItem> foods = new ArrayList<>();
    // main method    
    public static void main(String[] args) throws NoSuchAlgorithmException {
        
      //method to load the values of data into file  
      
        getList();
        int i=0;
        FoodServer foodServer = new FoodServer();
        PublicKey publicKey =  foodServer.getPublicKey();
        //creating a new connection with the client
        try {
            int serverport = 7896;
            ServerSocket listenSocket = new ServerSocket(serverport);
            //checking for any requests made from client
            while (true) {
                //Accepting connection from client
                Socket clientSocket = listenSocket.accept();
              
                   clientCount++;
                   Connection c = new Connection(clientSocket, i++,clientCount, publicKey, foodServer.getPrivateKey() );
	 
	 

            }
            //Exception to catch if connection is terminated
        } catch (IOException ex) {
            System.out.println("IO exception");
        } 
    }
    //Method to load values from file into an array list
    public static ArrayList<FoodItem> getList() {

        String filename = "C:\\Users\\rohit\\Desktop\\Assignment2\\src\\foodData.ser";
        ArrayList<FoodItem> foodRead;
        DataFile foodData = new DataFile(filename);
        foodRead = foodData.readfoodData();
        return foodRead;
    }
}
//Clas to create thread
class Connection extends Thread {
    
    PublicKey publicKey;
    
    PrivateKey privateKey;
    
    //Using predicate to get the cereals and the beverages seperately from the list
    Predicate <FoodItem> cerealsOnly = f -> (f.getFoodType().equalsIgnoreCase("cereal"));
    Predicate <FoodItem> beveragesOnly = f -> (f.getFoodType().equalsIgnoreCase("beverage"));
    
    //Arraylist to store the values of the list as per the request
    ArrayList<FoodItem> foodConnection = new ArrayList<>();
    
    DataInputStream in;
    DataOutputStream out;
    Socket clientSocket;
    //Connection method to create a thread when requested by the client
    public Connection(Socket aClientSocket, int tn, int client, PublicKey key, PrivateKey privateKey) throws IOException,EOFException, NoSuchAlgorithmException{
        
        publicKey =key;
          this.privateKey = privateKey;
        FoodServer fs = new FoodServer();
        //calling method to load data from the file
        foodConnection = fs.getList();
        try {
            //TCP server input and output connection setup
            clientSocket = aClientSocket;
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
            //Starting the thread once the request is recieved
            this.start();
        } catch (IOException ex) {
            Logger.getLogger(FoodServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public String decrypt(byte [] encodedMessage) throws NoSuchAlgorithmException, NoSuchPaddingException,
                                             InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException,
                                             BadPaddingException{
            Cipher cipher = Cipher.getInstance("RSA");  
            cipher.init(Cipher.DECRYPT_MODE, privateKey, cipher.getParameters());
            return new String(cipher.doFinal(encodedMessage));
      } 
    //Run method for the thread
    public void run() 
    { 
        
        
          String data;
          String name;
          String password;
          int userID;
          String message = null;
          String format = null;
          String encodedKey =null, publicKeyString;
          String messageString =null;
          X509EncodedKeySpec pubKeySpec =null;
          //generate the encoded key
          byte[] bytesPubKey = publicKey.getEncoded();
        
        try 
        {
            while(true)
            {   
                DatabaseUtility dbFunction = new DatabaseUtility();
                
                String userOption = null;
                userOption = in.readUTF();
                
                
                switch (userOption)
                {   
                    
                    case "start":
                        
                        System.out.println("Request recieved from client on");
                        System.out.println("Thread " + Thread.currentThread().getName());
                        System.out.println("Thread " + Thread.currentThread().getId());
                        out.writeUTF("Welcome to Processed Food Data Service");
                        System.out.println("Welcome Message sent to client");
                        
                        break;
                        
                    case "first":
                        
                     
                        //Read the input recieved from the client
                        System.out.println("\nClient response : 1");
                        name = in.readUTF();
                        System.out.printf("\nClient response : name %s",name);
                        
                        //send the keysize;
                        out.writeInt(bytesPubKey.length);
                      
                        //send the key in bytes
                        out.write(bytesPubKey, 0, bytesPubKey.length);
                        
                        int messageLength = in.readInt();
                        byte [] encodedmessage = new byte [messageLength];
                        //read the encryped password sent from client
                        in.read(encodedmessage,0, encodedmessage.length);
                        
                        //decrypt password.                       
                        password = decrypt(encodedmessage);
                        System.out.println("\nPassword recieved");
                        dbFunction.DBtables();
                        dbFunction.insertUser(name, password);
                        System.out.println("\nUser details saved");
                        dbFunction.getUserID(name);
                        out.writeUTF(Integer.toString(dbFunction.getUserID(name)));
                       
                        
                        break;
                        
                    case "second":
                        
                        String UPassword;
                        //Read the input recieved from the client
                        userID = in.readInt();
                        //send the keysize;
                        out.writeInt(bytesPubKey.length);
                        //System.out.println("\nKey size recieved");
                        //send the key in bytes
                        out.write(bytesPubKey, 0, bytesPubKey.length);
                        //System.out.println("\nKey recieved");
                        int messagelength = in.readInt();
                        byte [] encodedMessage = new byte [messagelength];
                        //read the encryped password sent from client
                        in.read(encodedMessage,0, encodedMessage.length);
                        //decrypt password.                       
                       UPassword = decrypt(encodedMessage);
                       dbFunction.DBtables();
                       if(UPassword.equalsIgnoreCase(dbFunction.getPassword(Integer.toString(userID))))
                       {
                           out.writeUTF("success");
                           out.writeUTF(dbFunction.getUsername(Integer.toString(userID)));
                       }
                       else
                           out.writeUTF("failure");
                                                            
                        break;
                
                    
                    case "1":
                        
                        //Returns the beverage list to the client
                        System.out.println("Beverage Item listing request recieved from client");
                        out.writeUTF(formattedString(getBeverages()));
                        System.out.println("Lunch listing sent to client");
                        
                        break;
                        
                    case "2":
                        
                        
                        System.out.println("Cereal Items listing request recieved from client");
                        //Returns the cereal list to the client
                        out.writeUTF(formattedString(getcereals()));
                        System.out.println("Lunch listing sent to client");
                        
                        break;
                        
                    case "3":
                        
                        System.out.println("\nHigh Protein Cereal listing request recieved from client");
                             //Expression to sort the cereal lis
                             List<FoodItem> proteinSorted = getcereals();
                             Collections.sort(proteinSorted, (FoodItem o1, FoodItem o2) -> (int) (o2.getProtein() - o1.getProtein()));
                             
                            //Sending the top 3 values to the client
                            out.writeUTF(formattedString(proteinSorted.subList(0, 3)));
                        
                        System.out.println("High Protein Cereal listing sent to client");
                        
                        break;
                        
                    case "4":
                        
                        System.out.println("Low sugar Bevereages listing request recieved from client");
                            //Sort the beverage list according to Sugar levels
                             List<FoodItem>  lowSugarBeverage = getBeverages();
                             Collections.sort(lowSugarBeverage, (FoodItem f1, FoodItem f2) -> (int) (f1.getSugar() - f2.getSugar()));
                             
                             //Sending the top 3 values from the list
                            List lowSugarBeverageList = lowSugarBeverage.subList(0, 3);
                            Collections.reverse(lowSugarBeverageList);
                            out.writeUTF(formattedString(lowSugarBeverageList));
                       
                        
                        System.out.println("Low sugar Bevereages listing sent to client");
                        
                        break;
                        
                    case "5":
                        //closing the thread and exiting
                        clientSocket.close();
                        
                        break;
                    default:
                        out.writeUTF("Invalid Option");
                }
        
            
        
            }//catch exception 
        }   catch (IOException ex) {
                Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidAlgorithmParameterException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
System.out.print(Thread.currentThread().getName());
            try {
               clientSocket.close();
           } catch (IOException ex) {
                Logger.getLogger(FoodServer.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
        } 
    //Method to return the list of cereals
    public List<FoodItem> getcereals()
            
        {
            return foodConnection.stream().filter(cerealsOnly).collect(Collectors.<FoodItem>toList());

        
        }
    //Method to return the list of beverages
    public List<FoodItem> getBeverages()
            
        {
            return foodConnection.stream().filter(beveragesOnly).collect(Collectors.<FoodItem>toList()); 

        
        }
    //Method to format the string to display
    private String formattedString(List<FoodItem> foodList)
            
    {
        String text = String.format("%s, %s, %80s, %30s, %30s, %30s", "Food Type" , "Item Name", "Energy" , "Protein", "Carb(g)", "Sugar").replace(","," ");
        String data = "";
        String sendString="";
        
        for(FoodItem food : foodList)
        {
            data += food.toString().replace(",","");
        }
        
        sendString = text + "\n" + data;
        
        return sendString;
    }
    
    

        
    
    }


