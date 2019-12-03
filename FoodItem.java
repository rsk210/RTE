/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.Serializable;

/**
 *
 * @author sudhakar
 */
public class FoodItem implements Serializable{
    
  //Set the Serial Version according to the file
  private static final long serialVersionUID = 257L;

 
    //Declaring variables of the food class                          
    String foodType;
    String itemName;
    float protein;
    float carbohydrate;
    float sugar;
    int energy;
    //Parametrised Constructor for food class
    public FoodItem(String foodType, String itemName, float protein, float carbohydrate, float sugar, int energy) {
        this.foodType = foodType;
        this.itemName = itemName;
        this.protein = protein;
        this.carbohydrate = carbohydrate;
        this.sugar = sugar;
        this.energy = energy;
    }
    //Constructor
    public FoodItem() {
        
        this.foodType = "not set";
        
    }
    
    public FoodItem(FoodItem another){
          this.foodType = another.foodType;
        this.itemName = another.itemName;
        this.protein = another.protein;
        this.carbohydrate = another.carbohydrate;
        this.sugar = another.sugar;
        this.energy = another.energy;
        
    }

    //Getter and Setter methods for the values of the Food Item variables
    public String getFoodType() {
        return foodType;
    }

    public String getItemName() {
        return itemName;
    }

    public float getProtein() {
        return protein;
    }

    public float getCarbohydrate() {
        return carbohydrate;
    }

    public float getSugar() {
        return sugar;
    }

    public int getEnergy() {
        return energy;
    }

    public void setFoodType(String foodType) {
        this.foodType = foodType;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setProtein(float protein) {
        this.protein = protein;
    }

    public void setCarbohydrate(float carbohydrate) {
        this.carbohydrate = carbohydrate;
    }

    public void setSugar(float sugar) {
        this.sugar = sugar;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }
   //toString method to convert and display the values of the string
    @Override
   public String toString() {
      return String.format("%-9s %-60s %30s \t%30s \t%30s %30s\n", foodType, itemName,  energy, protein, carbohydrate, sugar);
   }
    
    
}
