package ca.yorku.eecs3311.ui;

import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
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

        String[] columns = {"ID", "Date", "Time", "Meal Type", "Items", "Delete", "Swap"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) {
                return col >= 5;
            }
        };
        table = new JTable(tableModel);
        table.removeColumn(table.getColumnModel().getColumn(0));
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

        JButton saveSwapBtn = new JButton("Save Swap");
        saveSwapBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select a row to swap.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int modelRow = table.convertRowIndexToModel(row);
            int mealId = (int) tableModel.getValueAt(modelRow, 0); // internal model contains ID
            List<MealEntry> meals = dao.findByProfile(profileName);
            MealEntry selected = meals.stream().filter(m -> m.getId() == mealId).findFirst().orElse(null);

            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Meal not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String[] foodOptions = selected.getItems().stream().map(MealItem::getFoodName).toArray(String[]::new);
            String foodToSwap = (String) JOptionPane.showInputDialog(
                    this, "Select food to swap:", "Swap Selection",
                    JOptionPane.PLAIN_MESSAGE, null, foodOptions, foodOptions[0]
            );

            if (foodToSwap == null) return;

            MealItem itemToReplace = selected.getItems().stream()
                    .filter(i -> i.getFoodName().equals(foodToSwap))
                    .findFirst().orElse(null);

            if (itemToReplace == null) {
                JOptionPane.showMessageDialog(this, "Item not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String goal = "Reduce Calories"; // Default goal
            List<String> alternatives = new ca.yorku.eecs3311.foodswap.FoodSwapDAO()
                    .suggestSwap(itemToReplace.getFoodName(), goal);

            if (alternatives.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No swap found.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            String newFood = alternatives.get(0);
            boolean ok = dao.updateMealItem(itemToReplace.getId(), newFood, itemToReplace.getQuantity());

            if (ok) {
                JOptionPane.showMessageDialog(this, "Swap saved! " + foodToSwap + " ➔ " + newFood);
                loadMealEntries();
            } else {
                JOptionPane.showMessageDialog(this, "Swap failed.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> ((JFrame) SwingUtilities.getWindowAncestor(this)).dispose());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(saveSwapBtn);
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
