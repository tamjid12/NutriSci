package ca.yorku.eecs3311.ui;

import ca.yorku.eecs3311.meal.MealEntry;
import ca.yorku.eecs3311.meal.MealEntryDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MealHistoryPanel extends JPanel {
    private final MealEntryDAO dao = new MealEntryDAO();
    private final String profileName;
    private JTable table;
    private DefaultTableModel tableModel;

    public MealHistoryPanel(Navigator nav, String profileName) {
        this.profileName = profileName;
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        JLabel title = new JLabel("Meal History for " + profileName, SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(20f));
        add(title, BorderLayout.NORTH);

        // Table setup
        String[] columns = {"ID", "Date", "Time", "Meal Type", "Items", "Delete"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) { return col == 5; }
        };
        table = new JTable(tableModel);

        // Hide the ID column
        table.removeColumn(table.getColumnModel().getColumn(0));

        // Set fonts and row height for better readability
        table.setRowHeight(28);
        table.setFont(new Font("Arial", Font.PLAIN, 16));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));

        // Renderer/Editor for Delete column (standard button)
        table.getColumn("Delete").setCellRenderer(new ButtonRenderer());
        table.getColumn("Delete").setCellEditor(new ButtonEditor(new JCheckBox(), tableModel, dao, this));
        table.getColumn("Delete").setPreferredWidth(100);

        // Optionally set other columns' widths for better layout
        table.getColumnModel().getColumn(0).setPreferredWidth(100); // Date
        table.getColumnModel().getColumn(1).setPreferredWidth(90); // Time
        table.getColumnModel().getColumn(2).setPreferredWidth(120); // Meal Type
        table.getColumnModel().getColumn(3).setPreferredWidth(60);  // Items

        loadMealEntries();

        JScrollPane scroll = new JScrollPane(table);
        add(scroll, BorderLayout.CENTER);

        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> ((JFrame) SwingUtilities.getWindowAncestor(this)).dispose());
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(closeBtn);
        add(btnPanel, BorderLayout.SOUTH);
    }

    // Loads all meal entries from DB and fills the table
    public void loadMealEntries() {
        tableModel.setRowCount(0);
        List<MealEntry> meals = dao.findByProfile(profileName); // Make sure this method exists!
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter tf = DateTimeFormatter.ofPattern("HH:mm");
        for (MealEntry m : meals) {
            tableModel.addRow(new Object[]{
                    m.getId(),
                    m.getDate().format(df),
                    m.getTime().format(tf),
                    m.getMealType().toString(),
                    m.getItems().size(),
                    "Delete"
            });
        }
    }
}

// ----- Inner classes for ButtonRenderer/Editor -----

class ButtonRenderer extends JButton implements TableCellRenderer {
    public ButtonRenderer() {
        setOpaque(false); // Let the Look & Feel handle background
        // Do NOT set background/foreground/font/margin for standard look
    }
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        setText((value == null) ? "" : value.toString());
        return this;
    }
}

class ButtonEditor extends DefaultCellEditor {
    private JButton button;
    private boolean clicked;
    private DefaultTableModel model;
    private MealEntryDAO dao;
    private MealHistoryPanel historyPanel;
    private int row;

    public ButtonEditor(JCheckBox checkBox, DefaultTableModel model, MealEntryDAO dao, MealHistoryPanel panel) {
        super(checkBox);
        this.model = model;
        this.dao = dao;
        this.historyPanel = panel;
        button = new JButton("Delete");
        // Do NOT set background/foreground/font/margin for standard look
        button.addActionListener(e -> fireEditingStopped());
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        this.row = row;
        button.setText("Delete");
        clicked = true;
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        if (clicked) {
            int mealId = (int) model.getValueAt(row, 0); // Model row always has ID
            int confirm = JOptionPane.showConfirmDialog(button, "Delete this meal?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                boolean ok = dao.deleteMealEntry(mealId);
                if (ok) {
                    model.removeRow(row);
                    JOptionPane.showMessageDialog(button, "Meal deleted!");
                    historyPanel.loadMealEntries(); // Refresh the table
                } else {
                    JOptionPane.showMessageDialog(button, "Failed to delete meal.");
                }
            }
        }
        clicked = false;
        return "Delete";
    }
}
