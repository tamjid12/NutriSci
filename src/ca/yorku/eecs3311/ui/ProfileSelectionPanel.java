/*
 * File: MainApp.java
 * Entry point for the NutriSci Tracker application.
 * Sets Nimbus Look & Feel and launches the initial profile panel.
 */
package ca.yorku.eecs3311.ui;

import ca.yorku.eecs3311.profile.ProfileController;
import ca.yorku.eecs3311.profile.UserProfile;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.function.Consumer;

/**
 * Panel for selecting, updating, and deleting user profiles.
 * Displays all profiles in a table with "Update" and "Delete" buttons per row.
 */
public class ProfileSelectionPanel extends JPanel {
    private final Navigator nav;
    private final ProfileController controller = new ProfileController();
    private final DefaultTableModel model;
    private final JTable table;

    public ProfileSelectionPanel(Navigator nav) {
        this.nav = nav;
        setLayout(new BorderLayout(8,8));
        setBorder(new EmptyBorder(10,10,10,10));

        JLabel header = new JLabel("Select Profile");
        header.setFont(header.getFont().deriveFont(Font.BOLD, 18f));
        add(header, BorderLayout.NORTH);

        // Table setup
        model = new DefaultTableModel(new Object[]{"Name","Update","Delete"}, 0) {
            @Override public boolean isCellEditable(int row, int col) {
                // only Update/Delete columns are editable so buttons work
                return col > 0;
            }
        };
        table = new JTable(model);
        table.setRowHeight(28);

        // Renderers & editors for buttons
        table.getColumn("Update").setCellRenderer(new ButtonRenderer());
        table.getColumn("Update").setCellEditor(new ButtonEditor(label -> onUpdate(label)));
        table.getColumn("Delete").setCellRenderer(new ButtonRenderer());
        table.getColumn("Delete").setCellEditor(new ButtonEditor(label -> onDelete(label)));

        add(new JScrollPane(table), BorderLayout.CENTER);

        // Bottom panel for Back & Next
        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(e -> nav.showMainMenu());
        JButton nextBtn = new JButton("Next");
        nextBtn.addActionListener(e -> onSelect());

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER,10,10));
        bottom.setOpaque(false);
        bottom.add(backBtn);
        bottom.add(nextBtn);
        add(bottom, BorderLayout.SOUTH);

        refreshTable();
    }

    /** Reloads all profiles into the table model. */
    private void refreshTable() {
        model.setRowCount(0);
        List<UserProfile> all = controller.getProfiles();
        for (UserProfile p : all) {
            model.addRow(new Object[]{p.getName(), "Update", "Delete"});
        }
    }

    /** Called when the Update button is pressed for a profile. */
    private void onUpdate(String name) {
        UserProfile p = controller.findByName(name);
        if (p != null) nav.showCreateProfile(p);
    }

    /** Called when the Delete button is pressed for a profile. */
    private void onDelete(String name) {
        int ans = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete profile ‘" + name + "’?",
                "Confirm delete",
                JOptionPane.YES_NO_OPTION
        );
        if (ans == JOptionPane.YES_OPTION) {
            controller.deleteProfile(name);
            refreshTable();
        }
    }

    /** Called when Next is pressed to select a profile for meal logging. */
    private void onSelect() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a profile from the list.",
                    "No profile selected",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        String name = (String) model.getValueAt(row, 0);
        nav.showMealLog(name);
    }

    // -------------------
    // ButtonRenderer
    private static class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }
        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column
        ) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    // ButtonEditor with a callback that receives the profile name
    private class ButtonEditor extends AbstractCellEditor
            implements TableCellEditor, ActionListener {
        private final JButton button = new JButton();
        private final java.util.function.Consumer<String> callback;
        private String currentName;

        public ButtonEditor(java.util.function.Consumer<String> callback) {
            this.callback = callback;
            button.addActionListener(this);
        }

        @Override
        public Component getTableCellEditorComponent(
                JTable table, Object value, boolean isSelected, int row, int column
        ) {
            currentName = (String) table.getValueAt(row, 0);
            button.setText((value == null) ? "" : value.toString());
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return button.getText();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            fireEditingStopped();
            callback.accept(currentName);
        }
    }
}
