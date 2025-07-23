package ca.yorku.eecs3311.ui;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import ca.yorku.eecs3311.foodswap.FoodSwapDAO;
import ca.yorku.eecs3311.meal.*;

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
        String[] columns = {"ID", "Date", "Time", "Meal Type", "Items", "Delete", "Swap"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) { return col >= 5; }
        };
        table = new JTable(tableModel);
        table.removeColumn(table.getColumnModel().getColumn(0)); // hide ID

        table.setRowHeight(28);
        table.setFont(new Font("Arial", Font.PLAIN, 16));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));

        table.getColumn("Delete").setCellRenderer(new ButtonRenderer("Delete"));
        table.getColumn("Delete").setCellEditor(new ButtonEditor(new JCheckBox(), tableModel, dao, this, nav, "Delete"));

        table.getColumn("Swap").setCellRenderer(new ButtonRenderer("Swap"));
        table.getColumn("Swap").setCellEditor(new ButtonEditor(new JCheckBox(), tableModel, dao, this, nav, "Swap"));

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
                    "Swap"
            });
        }
    }
}

// ButtonRenderer
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

// ButtonEditor
class ButtonEditor extends DefaultCellEditor {
    private JButton button;
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
            int mealId = (int) model.getValueAt(modelRow, 0); // ID is hidden but still in model

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
            } else if (actionType.equals("Swap")) {
                List<MealEntry> allMeals = dao.findByProfile(panel.profileName);
                MealEntry selected = allMeals.stream()
                        .filter(m -> m.getId() == mealId)
                        .findFirst().orElse(null);

                if (selected != null && !selected.getItems().isEmpty()) {
                    MealItem item = selected.getItems().get(0); // prompt user in future
                    String goal = "Reduce Calories"; // default for now
                    List<String> swaps = new FoodSwapDAO().suggestSwap(item.getFoodName(), goal);
                    if (!swaps.isEmpty()) {
                        String swap = swaps.get(0);
                        boolean ok = dao.updateMealItem(item.getId(), swap, item.getQuantity());
                        if (ok) {
                            JOptionPane.showMessageDialog(button, "Swapped: " + item.getFoodName() + " ➔ " + swap);
                            panel.loadMealEntries();
                        }
                    } else {
                        JOptionPane.showMessageDialog(button, "No suitable swap found.");
                    }
                } else {
                    JOptionPane.showMessageDialog(button, "No items found in this meal.");
                }
            }
        }
        clicked = false;
        return actionType;
    }
}
