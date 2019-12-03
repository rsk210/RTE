/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sudhakar
 */
//Class to read the values from the PDFS data file
public class DataFile {
    //Declaring variables to read data from the file
    ObjectInputStream in;
    FileInputStream fis;
    String filename; 
    //Constructor with filename as parameter
    public DataFile  (String filename){
     this.filename = filename;
     in = null;
}       
    //Creating Food Item variable to store athe values from the file
        FoodItem aFood = null;
        public ArrayList<FoodItem> readfoodData() {
        ArrayList <FoodItem> foodData= new ArrayList<>();
        try
        {
          //Reading and deserializing the file
          fis = new FileInputStream(filename); 
          in = new ObjectInputStream(fis);
          while(true)
          {
           aFood = (FoodItem)in.readObject();
          
             foodData.add(new FoodItem(aFood));
             //System.out.println(bookData.size());
          }
          
        
     }
          catch (EOFException eof) {
        
      }
      catch (IOException | ClassNotFoundException ex) 
     {
       ex.printStackTrace();
       System.exit(-1);
     }
        return foodData;
   }
}


      
    

    
   
 


