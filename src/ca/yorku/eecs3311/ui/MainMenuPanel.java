package ca.yorku.eecs3311.ui;

import javax.swing.*;
import java.awt.*;

/**
 * First screen: choose to create or select a profile.
 */
public class MainMenuPanel extends JPanel {
    public MainMenuPanel(Navigator nav) {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);

        JButton createBtn = new JButton("Create Profile");
        createBtn.addActionListener(e -> nav.showCreateProfile());

        JButton selectBtn = new JButton("Select Profile");
        selectBtn.addActionListener(e -> nav.showSelectProfile());

        gbc.gridx = 0; gbc.gridy = 0;
        add(createBtn, gbc);
        gbc.gridy = 1;
        add(selectBtn, gbc);
    }
}
