package ca.yorku.eecs3311.ui;

import ca.yorku.eecs3311.profile.ProfileController;
import ca.yorku.eecs3311.profile.UnitSystem;
import ca.yorku.eecs3311.profile.UserProfile;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

/**
 * A Swing panel for creating or editing user profiles.
 * In "create mode", this panel collects user input to create a new profile.
 * In "edit mode", it loads an existing profile into editable fields.
 * This panel handles profile input, validation, and saving logic via ProfileController.
 */
public class CreateProfilePanel extends JPanel {
    private final Navigator nav;
    private final ProfileController controller = new ProfileController();
    private final boolean editMode;
    private final UserProfile original;  // non-null in edit mode

    // Form fields
    private JTextField nameField;
    private JComboBox<String> sexBox;
    private JSpinner dobSpinner;
    private JTextField heightField;
    private JTextField weightField;
    private JComboBox<UnitSystem> unitBox;
    private JButton saveBtn;
    private JButton backBtn;
    private JLabel statusLbl;

    /** Constructor for creating a new profile. */
    public CreateProfilePanel(Navigator nav) {
        this(nav, null);
    }

    /** Constructor for editing an existing profile. */
    public CreateProfilePanel(Navigator nav, UserProfile toEdit) {
        this.nav = nav;
        this.original = toEdit;
        this.editMode = (toEdit != null);
        initUI();
        if (editMode) {
            populateFields(toEdit);
        }
    }
    /**
     * Initializes the UI layout and form fields.
     */
    private void initUI() {
        setLayout(new GridBagLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;
        int y = 0;

        // Title
        JLabel title = new JLabel(editMode ? "Update Profile" : "Create Profile");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2;
        add(title, gbc);

        // Name
        y++; gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = y;
        add(new JLabel("Name:"), gbc);
        nameField = new JTextField(15);
        gbc.gridx = 1;
        add(nameField, gbc);

        // Sex
        y++;
        gbc.gridx = 0; gbc.gridy = y;
        add(new JLabel("Sex:"), gbc);
        sexBox = new JComboBox<>(new String[]{"Male", "Female"});
        gbc.gridx = 1;
        add(sexBox, gbc);

        // Date of Birth
        y++;
        gbc.gridx = 0; gbc.gridy = y;
        add(new JLabel("Date of Birth:"), gbc);
        dobSpinner = new JSpinner(
                new SpinnerDateModel(
                        Date.from(LocalDate.now()
                                .minusYears(20)
                                .atStartOfDay(ZoneId.systemDefault())
                                .toInstant()),
                        null, null, Calendar.DAY_OF_MONTH
                )
        );
        dobSpinner.setEditor(new JSpinner.DateEditor(dobSpinner, "yyyy-MM-dd"));
        gbc.gridx = 1;
        add(dobSpinner, gbc);

        // Height
        y++;
        gbc.gridx = 0; gbc.gridy = y;
        add(new JLabel("Height:"), gbc);
        heightField = new JTextField(8);
        gbc.gridx = 1;
        add(heightField, gbc);

        // Weight
        y++;
        gbc.gridx = 0; gbc.gridy = y;
        add(new JLabel("Weight:"), gbc);
        weightField = new JTextField(8);
        gbc.gridx = 1;
        add(weightField, gbc);

        // Unit system
        y++;
        gbc.gridx = 0; gbc.gridy = y;
        add(new JLabel("Units:"), gbc);
        unitBox = new JComboBox<>(UnitSystem.values());
        gbc.gridx = 1;
        add(unitBox, gbc);

        // Buttons
        y++;
        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnPanel.setOpaque(false);
        saveBtn = new JButton(editMode ? "Update Profile" : "Create Profile");
        backBtn = new JButton("Back");
        btnPanel.add(saveBtn);
        btnPanel.add(backBtn);
        add(btnPanel, gbc);

        // Status label
        y++;
        statusLbl = new JLabel(" ");
        statusLbl.setFont(statusLbl.getFont().deriveFont(Font.BOLD, 12f));
        statusLbl.setForeground(new Color(0, 120, 0));
        gbc.gridy = y;
        add(statusLbl, gbc);

        // Listeners
        saveBtn.addActionListener(e -> onSave());
        backBtn.addActionListener(e -> {
            if (editMode) nav.showSelectProfile();
            else         nav.showMainMenu();
        });
    }

    /**  Prefills the form fields with an existing user profile when in edit mode.*/
    private void populateFields(UserProfile p) {
        nameField.setText(p.getName());
        nameField.setEnabled(false);
        sexBox.setSelectedItem(p.getSex());
        dobSpinner.setValue(
                Date.from(p.getDob().atStartOfDay(ZoneId.systemDefault()).toInstant())
        );
        heightField.setText(String.valueOf(p.getHeight()));
        weightField.setText(String.valueOf(p.getWeight()));
        unitBox.setSelectedItem(p.getUnits());
    }

    /** Called when the Create/Update button is pressed */
    private void onSave() {
        try {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                throw new IllegalArgumentException("Name is required");
            }
            String sex = (String) sexBox.getSelectedItem();
            Date d = (Date) dobSpinner.getValue();
            LocalDate dob = d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            double height = Double.parseDouble(heightField.getText().trim());
            double weight = Double.parseDouble(weightField.getText().trim());
            UnitSystem units = (UnitSystem) unitBox.getSelectedItem();

            UserProfile p = new UserProfile(name, sex, dob, height, weight, units);
            boolean ok;
            if (editMode) {
                ok = controller.updateProfile(p);
                statusLbl.setText(ok ? "Profile updated!" : "Update failed.");
            } else {
                ok = controller.saveProfile(p);
                statusLbl.setText(ok ? "Profile created!" : "Creation failed.");
            }

            // NOTE: timer removed—no automatic navigation
        } catch (Exception ex) {
            statusLbl.setForeground(Color.RED);
            statusLbl.setText("Error: " + ex.getMessage());
        }
    }
}
