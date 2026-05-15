package main.java.com.smartcity.gui;

import javax.swing.SwingUtilities;

public class SmartCityApp {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            SmartCityGUI gui = new SmartCityGUI();
            gui.setVisible(true);

        });
    }
}
