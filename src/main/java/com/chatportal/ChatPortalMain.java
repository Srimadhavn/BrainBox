package com.chatportal;

import com.chatportal.gui.LoginFrame;

import javax.swing.*;

public class ChatPortalMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}