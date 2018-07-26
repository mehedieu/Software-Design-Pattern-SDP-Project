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
public abstract class AbstractFactory {
    abstract Button getButton(String button);
    abstract MenuItem getMenuItem(String menuItem);
    abstract Frame getFrame(String frame);
}

class ButtonFactory extends AbstractFactory {
    @Override
    public Button getButton(String button) {
        if (button == null) {
            return null;
        } 
        if (button.equalsIgnoreCase("Submit Button")){
            return new Submit();
        } else if (button.equalsIgnoreCase("Download Button")) {
            return new Download();
        } else if (button.equalsIgnoreCase("Post Button")) {
            return new PostButton();
        } else if (button.equalsIgnoreCase("Log in Button")) {
            return new LogInButton();
        } else if (button.equalsIgnoreCase("Ok button")) {
            return new OkButton();
        }
        
        return null;
    }
    
    @Override
    MenuItem getMenuItem(String menuItem) {
        return null;
    }
    
    @Override
    Frame getFrame(String frame) {
        return null;
    }
}

class MenuItemFactory extends AbstractFactory {
    @Override
    Button getButton(String button) {
        return null;
    }
    
    @Override
    public MenuItem getMenuItem(String menuItem) {
        if (menuItem == null) {
            return null;
        } 
        if (menuItem.equalsIgnoreCase("Upload File")){
            return new UploadFile();
        } else if (menuItem.equalsIgnoreCase("Delete File")) {
            return new DeleteFile();
        } else if (menuItem.equalsIgnoreCase("Share File")) {
            return new ShareFile();
        } else if (menuItem.equalsIgnoreCase("Delete Account")) {
            return new DeleteAccount();
        } else if (menuItem.equalsIgnoreCase("Log out")) {
            return new LogOut();
        } else if (menuItem.equalsIgnoreCase("Change Profile Picture")) {
            return new ChangeProPic();
        } else if (menuItem.equalsIgnoreCase("Write Post")) {
            return new WritePost();
        } else if (menuItem.equalsIgnoreCase("See Posts")) {
            return new SeePost();
        } else if (menuItem.equalsIgnoreCase("Show New Notifications")) {
            return new ShowNewNotifications();
        } else if (menuItem.equalsIgnoreCase("Show All Notifications")) {
            return new ShowAllNotifications();
        } else if (menuItem.equalsIgnoreCase("Select All")) {
            return new SelectAll();
        } else if (menuItem.equalsIgnoreCase("Delete")) {
            return new Delete();
        } else if (menuItem.equalsIgnoreCase("Share")) {
            return new Share();
        }
        
        return null;
    }
    
    @Override
    Frame getFrame(String frame) {
        return null;
    }
}

class FrameFactory extends AbstractFactory {
    @Override
    Button getButton(String button) {
        return null;
    }
    
    @Override
    MenuItem getMenuItem(String menuItem) {
        return null;
    }
    
    @Override
    public Frame getFrame(String frame) {
        if (frame == null) {
            return null;
        } 
        if (frame.equalsIgnoreCase("IP Address")){
            return new IPAddress();
        } else if (frame.equalsIgnoreCase("Registration Form")) {
            return new SignUpFrame();
        } else if (frame.equalsIgnoreCase("Log In")) {
            return new LogInFrame();
        } else if (frame.equalsIgnoreCase("Welcome")) {
            return new Welcome();
        } else if (frame.equalsIgnoreCase("Browse File")) {
            return new UploadFrame();
        } else if (frame.equalsIgnoreCase("Delete Files")) {
            return new DeleteFrame();
        } else if (frame.equalsIgnoreCase("Share Files")) {
            return new ShareFileFrame();
        } else if (frame.equalsIgnoreCase("Profile Picture")) {
            return new ChangeProPicFrame();
        } else if (frame.equalsIgnoreCase("Write Post")) {
            return new WritePostFrame();
        } else if (frame.equalsIgnoreCase("All posts")) {
            return new SeePostFrame();
        } 
        
        return null;
    }
}