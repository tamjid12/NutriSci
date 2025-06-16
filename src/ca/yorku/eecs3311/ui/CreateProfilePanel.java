package ca.yorku.eecs3311.ui;

import ca.yorku.eecs3311.profile.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class CreateProfilePanel extends JPanel {
    private JTextField   nameField   = new JTextField(20);
    private JComboBox<String> sexBox = new JComboBox<>(new String[]{"Male","Female"});
    private JTextField   dobField    = new JTextField("YYYY-MM-DD", 10);
    private JTextField   heightField = new JTextField(5);
    private JTextField   weightField = new JTextField(5);
    private JComboBox<UnitSystem> unitBox =
            new JComboBox<>(UnitSystem.values());
    private JButton      saveBtn     = new JButton("Save Profile");
    private JLabel       statusLabel = new JLabel(" ");

    private ProfileController controller = new ProfileController();

    public CreateProfilePanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4,4,4,4);
        gbc.anchor = GridBagConstraints.WEST;

        int y = 0;
        // Name
        gbc.gridx=0; gbc.gridy=y; add(new JLabel("Name:"), gbc);
        gbc.gridx=1;           add(nameField, gbc);
        // Sex
        gbc.gridy=++y; gbc.gridx=0; add(new JLabel("Sex:"), gbc);
        gbc.gridx=1; add(sexBox, gbc);
        // DOB
        gbc.gridy=++y; gbc.gridx=0; add(new JLabel("DOB:"), gbc);
        gbc.gridx=1; add(dobField, gbc);
        // Height
        gbc.gridy=++y; gbc.gridx=0; add(new JLabel("Height:"), gbc);
        gbc.gridx=1; add(heightField, gbc);
        // Weight
        gbc.gridy=++y; gbc.gridx=0; add(new JLabel("Weight:"), gbc);
        gbc.gridx=1; add(weightField, gbc);
        // Units
        gbc.gridy=++y; gbc.gridx=0; add(new JLabel("Units:"), gbc);
        gbc.gridx=1; add(unitBox, gbc);
        // Button
        gbc.gridy=++y; gbc.gridx=0; gbc.gridwidth=2;
        gbc.anchor=GridBagConstraints.CENTER;
        add(saveBtn, gbc);
        // Status
        gbc.gridy=++y; add(statusLabel, gbc);

        saveBtn.addActionListener(this::onSave);
    }

    private void onSave(ActionEvent ev) {
        try {
            String name = nameField.getText().trim();
            LocalDate dob = LocalDate.parse(dobField.getText().trim());
            double height = Double.parseDouble(heightField.getText().trim());
            double weight = Double.parseDouble(weightField.getText().trim());
            String sex = (String) sexBox.getSelectedItem();
            UnitSystem units = (UnitSystem) unitBox.getSelectedItem();

            UserProfile p = UserProfileFactory.createUserProfile(
                    name, sex, dob, height, weight, units
            );
            boolean ok = controller.saveProfile(p);
            statusLabel.setText(ok ? "Saved!" : "Save failed");
        } catch (DateTimeParseException x) {
            statusLabel.setText("DOB format YYYY-MM-DD");
        } catch (NumberFormatException x) {
            statusLabel.setText("Height/Weight must be numbers");
        } catch (Exception x) {
            statusLabel.setText("Error: " + x.getMessage());
        }
    }
}
