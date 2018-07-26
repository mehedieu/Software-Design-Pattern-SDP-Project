/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package registration;

import javax.swing.JMenuItem;

/**
 *
 * @author Asus
 */
public interface MenuItem {
    public JMenuItem getMenuItem();
}

class UploadFile implements MenuItem {
    @Override
    public JMenuItem getMenuItem() {
        return new JMenuItem("Upload File");
    }
}

class DeleteFile implements MenuItem {
    @Override
    public JMenuItem getMenuItem() {
        return new JMenuItem("Delete File");
    }
}

class ShareFile implements MenuItem {
    @Override
    public JMenuItem getMenuItem() {
        return new JMenuItem("Share File");
    }
}

class DeleteAccount implements MenuItem {
    @Override
    public JMenuItem getMenuItem() {
        return new JMenuItem("Delete Account");
    }
}

class LogOut implements MenuItem {
    @Override
    public JMenuItem getMenuItem() {
        return new JMenuItem("Log out");
    }
}

class ChangeProPic implements MenuItem {
    @Override
    public JMenuItem getMenuItem() {
        return new JMenuItem("Change Profile Picture");
    }
}

class WritePost implements MenuItem {
    @Override
    public JMenuItem getMenuItem() {
        return new JMenuItem("Write Post");
    }
}

class SeePost implements MenuItem {
    @Override
    public JMenuItem getMenuItem() {
        return new JMenuItem("See posts");
    }
}

class ShowNewNotifications implements MenuItem {
    @Override
    public JMenuItem getMenuItem() {
        return new JMenuItem("Show New Notifications");
    }
}

class ShowAllNotifications implements MenuItem {
    @Override
    public JMenuItem getMenuItem() {
        return new JMenuItem("Show All Notifications");
    }
}

class SelectAll implements MenuItem {
    @Override
    public JMenuItem getMenuItem() {
        return new JMenuItem("Select All");
    }
}

class Delete implements MenuItem {
    @Override
    public JMenuItem getMenuItem() {
        return new JMenuItem("Delete");
    }
}

class Share implements MenuItem {
    @Override
    public JMenuItem getMenuItem() {
        return new JMenuItem("Share");
    }
}