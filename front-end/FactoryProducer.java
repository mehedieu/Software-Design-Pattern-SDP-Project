/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package registration;

/**
 *
 * @author Asus
 */
public class FactoryProducer {    
    public static AbstractFactory getFactory(String choice) {
        if(choice.equalsIgnoreCase("Button")) {
            return new ButtonFactory();
        } else if (choice.equalsIgnoreCase("Menu Item")) {
            return new MenuItemFactory();
        } else if (choice.equalsIgnoreCase("Frame")) {
            return new FrameFactory();
        }
        
        return null;
    }
}
