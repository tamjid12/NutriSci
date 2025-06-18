package ca.yorku.eecs3311.ui;

import ca.yorku.eecs3311.profile.ProfileController;
import ca.yorku.eecs3311.profile.UserProfile;
import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Panel for selecting an existing profile.
 */
public class ProfileSelectionPanel extends JPanel {
    public ProfileSelectionPanel(Navigator nav) {
        setBorder(BorderFactory.createTitledBorder("Select Profile"));
        setLayout(new BorderLayout(10,10));

        List<UserProfile> profiles = new ProfileController().getProfiles();
        DefaultListModel<String> model = new DefaultListModel<>();
        profiles.forEach(p -> model.addElement(p.getName()));

        JList<String> list = new JList<>(model);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JButton back = new JButton("Back");
        back.addActionListener(e -> nav.showMainMenu());

        JButton next = new JButton("Next");
        next.addActionListener(e -> {
            String sel = list.getSelectedValue();
            if (sel != null) nav.showMealLog(sel);
            else            JOptionPane.showMessageDialog(this, "Select a profile first");
        });

        JPanel btns = new JPanel();
        btns.add(back);
        btns.add(next);

        add(new JScrollPane(list), BorderLayout.CENTER);
        add(btns, BorderLayout.SOUTH);
    }
}
