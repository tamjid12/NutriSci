package ca.yorku.eecs3311.ui;

import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import ca.yorku.eecs3311.meal.MealEntry;
import ca.yorku.eecs3311.meal.MealEntryDAO;
import ca.yorku.eecs3311.meal.MealItem;

public class MealHistoryPanel extends JPanel {
    private final MealEntryDAO dao = new MealEntryDAO();
    public final String profileName;
    private final Navigator nav;
    private JTable table;
    private DefaultTableModel tableModel;

    public MealHistoryPanel(Navigator nav, String profileName) {
        this.nav = nav;
        this.profileName = profileName;

        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        JLabel title = new JLabel("Meal History for " + profileName, SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(20f));
        add(title, BorderLayout.NORTH);

        // Table setup
        String[] columns = {"ID", "Date", "Time", "Meal Type", "Items", "Delete", "Update"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) { return col >= 5; }
        };
        table = new JTable(tableModel);

        // Hide ID column visually but keep it in the model
        table.removeColumn(table.getColumnModel().getColumn(0));

        table.setRowHeight(28);
        table.setFont(new Font("Arial", Font.PLAIN, 16));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));

        // DELETE button
        table.getColumn("Delete").setCellRenderer(new ButtonRenderer("Delete"));
        table.getColumn("Delete").setCellEditor(new ButtonEditor(new JCheckBox(), tableModel, dao, this, nav, "Delete"));

        // UPDATE button
        table.getColumn("Update").setCellRenderer(new ButtonRenderer("Update"));
        table.getColumn("Update").setCellEditor(new ButtonEditor(new JCheckBox(), tableModel, dao, this, nav, "Update"));

        loadMealEntries();

        JScrollPane scroll = new JScrollPane(table);
        add(scroll, BorderLayout.CENTER);

        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> ((JFrame) SwingUtilities.getWindowAncestor(this)).dispose());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(closeBtn);
        add(btnPanel, BorderLayout.SOUTH);
    }

    public void loadMealEntries() {
        tableModel.setRowCount(0);
        List<MealEntry> meals = dao.findByProfile(profileName);
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter tf = DateTimeFormatter.ofPattern("HH:mm");
        for (MealEntry m : meals) {
            tableModel.addRow(new Object[]{
                    m.getId(),
                    m.getDate().format(df),
                    m.getTime().format(tf),
                    m.getMealType().toString(),
                    m.getItems().size(),
                    "Delete",
                    "Update"
            });
        }
    }
}

// Renderer class for buttons
class ButtonRenderer extends JButton implements TableCellRenderer {
    public ButtonRenderer(String label) {
        setOpaque(true);
        setText(label);
    }
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
        return this;
    }
}

// Editor class for Delete and Update buttons
class ButtonEditor extends DefaultCellEditor {
    private final JButton button;
    private boolean clicked;
    private final DefaultTableModel model;
    private final MealEntryDAO dao;
    private final MealHistoryPanel panel;
    private final Navigator nav;
    private final String actionType;
    private int row;

    public ButtonEditor(JCheckBox checkBox, DefaultTableModel model, MealEntryDAO dao, MealHistoryPanel panel, Navigator nav, String actionType) {
        super(checkBox);
        this.model = model;
        this.dao = dao;
        this.panel = panel;
        this.nav = nav;
        this.actionType = actionType;
        this.button = new JButton(actionType);
        this.button.addActionListener(e -> fireEditingStopped());
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
        this.row = row;
        button.setText(actionType);
        clicked = true;
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        if (clicked) {
            int modelRow = row;
            int mealId = (int) model.getValueAt(modelRow, 0);

            if (actionType.equals("Delete")) {
                int confirm = JOptionPane.showConfirmDialog(button, "Delete this meal?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    boolean ok = dao.deleteMealEntry(mealId);
                    if (ok) {
                        model.removeRow(modelRow);
                        JOptionPane.showMessageDialog(button, "Meal deleted!");
                        panel.loadMealEntries();
                    } else {
                        JOptionPane.showMessageDialog(button, "Failed to delete meal.");
                    }
                }
            } else if (actionType.equals("Update")) {
                List<MealEntry> allMeals = dao.findByProfile(panel.profileName);
                MealEntry selected = allMeals.stream()
                        .filter(m -> m.getId() == mealId)
                        .findFirst().orElse(null);
                if (selected != null && !selected.getItems().isEmpty()) {
                    JFrame top = (JFrame) SwingUtilities.getWindowAncestor(panel);

                    // Prepare old foods/quantities
                    List<String> oldFoods = selected.getItems().stream()
                            .map(MealItem::getFoodName).collect(Collectors.toList());
                    List<String> oldQtys = selected.getItems().stream()
                            .map(i -> String.valueOf(i.getQuantity())).collect(Collectors.toList());

                    // Show the improved update dialog
                    MealSwapDialog dialog = new MealSwapDialog(top, oldFoods, oldQtys);
                    dialog.setVisible(true);

                    if (dialog.isConfirmed()) {
                        List<String> newFoods = dialog.getNewFoods();
                        List<String> newQtys = dialog.getNewQuantities();

                        // Build new items
                        List<MealItem> newItems = new java.util.ArrayList<>();
                        for (int i = 0; i < newFoods.size(); ++i) {
                            String f = newFoods.get(i);
                            String q = newQtys.get(i);
                            try {
                                double qty = Double.parseDouble(q);
                                newItems.add(new MealItem(0, f, qty));
                            } catch (NumberFormatException ex) {
                                // ignore invalid
                            }
                        }
                        if (!newItems.isEmpty()) {
                            // Show confirmation with old/new comparison
                            StringBuilder msg = new StringBuilder("<html>Are you sure you want to update the meal's ingredients?<br><br>");
                            msg.append("<b>Old:</b> ");
                            for (int i = 0; i < oldFoods.size(); ++i) {
                                msg.append(oldFoods.get(i)).append(" (").append(oldQtys.get(i)).append(")");
                                if (i < oldFoods.size() - 1) msg.append(", ");
                            }
                            msg.append("<br><b>New:</b> ");
                            for (int i = 0; i < newFoods.size(); ++i) {
                                msg.append(newFoods.get(i)).append(" (").append(newQtys.get(i)).append(")");
                                if (i < newFoods.size() - 1) msg.append(", ");
                            }
                            msg.append("</html>");
                            int confirm = JOptionPane.showConfirmDialog(panel, msg.toString(), "Confirm Update", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                            if (confirm == JOptionPane.YES_OPTION) {
                                boolean ok = dao.updateMealItems(selected.getId(), newItems);
                                if (ok) {
                                    JOptionPane.showMessageDialog(panel, "Meal updated successfully!");
                                    panel.loadMealEntries();
                                } else {
                                    JOptionPane.showMessageDialog(panel, "Update failed!", "Error", JOptionPane.ERROR_MESSAGE);
                                }
                            }
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(button, "No items found in this meal.");
                }
            }
            clicked = false;
        }
        return actionType;
    }
}
