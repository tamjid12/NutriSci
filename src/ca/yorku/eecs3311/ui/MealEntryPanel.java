package ca.yorku.eecs3311.ui;

import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import ca.yorku.eecs3311.meal.MealEntry;
import ca.yorku.eecs3311.meal.MealItem;
import ca.yorku.eecs3311.meal.MealLogController;
import ca.yorku.eecs3311.meal.MealType;
import ca.yorku.eecs3311.nutrient.NutrientCalculator;
import ca.yorku.eecs3311.nutrient.NutrientInfo;

public class MealEntryPanel extends JPanel {
    private final String profileName;
    private final Navigator nav;
    private final MealLogController controller = new MealLogController();
    private final NutrientCalculator calc = new NutrientCalculator();

    private JSpinner dateSpinner;
    private JSpinner timeSpinner;
    private final JComboBox<MealType> mealTypeBox = new JComboBox<>(MealType.values());
    private final JPanel itemsPanel = new JPanel();
    private final JPanel nutrientPanel = new JPanel(new BorderLayout(5, 5));
    private final JLabel statusLbl = new JLabel(" ");

    public MealEntryPanel(Navigator nav, String profileName) {
        this.nav = nav;
        this.profileName = profileName;

        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(Color.WHITE);

        // Title
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        JLabel title = new JLabel("Meal Logger", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(24f).deriveFont(Font.BOLD));
        JLabel subtitle = new JLabel("Log a meal for: " + profileName, SwingConstants.CENTER);
        subtitle.setFont(subtitle.getFont().deriveFont(16f));
        titlePanel.add(title, BorderLayout.NORTH);
        titlePanel.add(subtitle, BorderLayout.SOUTH);
        add(titlePanel, BorderLayout.NORTH);

        // Form
        add(buildFormPanel(), BorderLayout.WEST);

        // Nutrient info
        nutrientPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                "Nutrient Info"
        ));
        nutrientPanel.setPreferredSize(new Dimension(320, 300));
        nutrientPanel.setBackground(Color.WHITE);
        add(nutrientPanel, BorderLayout.EAST);

        // Buttons & status
        add(buildButtonPanel(), BorderLayout.SOUTH);

        // seed first ingredient row
        addIngredientRow();
    }

    private JPanel buildFormPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(new EmptyBorder(0, 0, 10, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;
        int y = 0;

        // Date spinner
        dateSpinner = new JSpinner(
                new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH)
        );
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd"));
        gbc.gridx = 0; gbc.gridy = y; form.add(new JLabel("Date:"), gbc);
        gbc.gridx = 1; form.add(dateSpinner, gbc);

        // Time spinner
        timeSpinner = new JSpinner(
                new SpinnerDateModel(new Date(), null, null, Calendar.MINUTE)
        );
        timeSpinner.setEditor(new JSpinner.DateEditor(timeSpinner, "HH:mm"));
        gbc.gridy = ++y; gbc.gridx = 0; form.add(new JLabel("Time:"), gbc);
        gbc.gridx = 1; form.add(timeSpinner, gbc);

        // Meal type
        gbc.gridy = ++y; gbc.gridx = 0; form.add(new JLabel("Meal Type:"), gbc);
        gbc.gridx = 1; form.add(mealTypeBox, gbc);

        // Ingredients label
        gbc.gridy = ++y; gbc.gridx = 0; gbc.gridwidth = 2;
        form.add(new JLabel("Ingredients:"), gbc);

        // Items panel
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.setBackground(Color.WHITE);
        JScrollPane scroll = new JScrollPane(itemsPanel);
        scroll.setPreferredSize(new Dimension(350, 140));
        scroll.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        gbc.gridy = ++y; form.add(scroll, gbc);

        // Add ingredient button
        JButton addBtn = makeButton("Add Ingredient");
        gbc.gridy = ++y; gbc.gridwidth = 2;
        form.add(addBtn, gbc);
        addBtn.addActionListener(e -> addIngredientRow());

        return form;
    }

    private JPanel buildButtonPanel() {
        JButton showNutBtn = makeButton("Show Nutrients");
        JButton saveBtn = makeButton("Save Entry");
        JButton historyBtn = makeButton("Meal History");
        JButton swapBtn = makeButton("Food Swap");
        JButton backBtn = makeButton("Back");
        JButton showCaloriesButton = makeButton("Show Calorie Intake");

        showNutBtn.addActionListener(e -> showNutrients());
        saveBtn.addActionListener(e -> saveEntry());
        backBtn.addActionListener(e -> nav.showSelectProfile());
        showCaloriesButton.addActionListener(e -> nav.showCalorieIntakePanel(profileName));

        historyBtn.addActionListener(e -> {
            MealHistoryPanel historyPanel = new MealHistoryPanel(nav, profileName);
            JFrame frame = new JFrame("Meal History");
            frame.setContentPane(historyPanel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });

        swapBtn.addActionListener(e -> {
            List<MealItem> items = collectItems(false);
            if (items.isEmpty()) {
                statusLbl.setText("Add ingredients before swapping.");
                return;
            }
            nav.showFoodSwapPanel(items);
        });

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btns.setOpaque(false);
        btns.add(showNutBtn);
        btns.add(saveBtn);
        btns.add(historyBtn);
        btns.add(swapBtn);
        btns.add(showCaloriesButton);
        btns.add(backBtn);

        statusLbl.setFont(statusLbl.getFont().deriveFont(Font.BOLD, 14f));
        statusLbl.setBorder(new EmptyBorder(8, 8, 8, 8));
        statusLbl.setForeground(new Color(0, 120, 0));

        JPanel south = new JPanel(new BorderLayout());
        south.setOpaque(false);
        south.add(btns, BorderLayout.NORTH);
        south.add(statusLbl, BorderLayout.SOUTH);
        return south;
    }

    private JButton makeButton(String text) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setBackground(new Color(70, 130, 180));
        b.setForeground(Color.WHITE);
        b.setBorder(new EmptyBorder(6, 12, 6, 12));
        b.setFont(b.getFont().deriveFont(13f));
        return b;
    }

    // Reads and validates ingredients, then shows the nutrient table and calorie intake chart side by side.
    private void showNutrients() {
        try {
            List<MealItem> items = collectItems(true);
            if (items.isEmpty()) {
                statusLbl.setText("Add at least one valid ingredient");
                return;
            }

            Map<String, NutrientInfo> all = calc.calcForItemsWithUnits(items);

            List<String> BASIC = List.of(
                    "KCAL", "PROT", "FAT", "CARB",
                    "VITC", "D-IU", "B6", "B12"
            );
            DefaultTableModel dm = new DefaultTableModel(
                    new Object[]{"Nutrient", "Amount", "Unit"}, 0
            );
            for (String sym : BASIC) {
                NutrientInfo info = all.get(sym);
                double amt = (info == null ? 0 : info.getAmount());
                String unit = (info == null ? "" : info.getUnit());
                dm.addRow(new Object[]{
                        displayName(sym),
                        String.format("%.2f", amt),
                        unit
                });
            }
            JTable nutTable = new JTable(dm);
            JScrollPane tableScroll = new JScrollPane(nutTable);
            tableScroll.setPreferredSize(new Dimension(280, 160));

            // calculate before/after calories
            LocalDate date = getSelectedDate();
            double before = 0;
            for (MealEntry me : controller.getMealsForUserOnDate(profileName, date)) {
                NutrientInfo kc = calc.calcForEntryWithUnits(me.getId()).get("KCAL");
                before += (kc == null ? 0 : kc.getAmount());
            }
            double added = all.getOrDefault("KCAL", new NutrientInfo("KCAL", 0, "")).getAmount();
            double after = before + added;

            DefaultCategoryDataset ds = new DefaultCategoryDataset();
            ds.addValue(before, "Calories", "Before Entry");
            ds.addValue(after, "Calories", "After Entry");
            JFreeChart chart = ChartFactory.createBarChart(
                    "Calories Intake Today", "", "kCal", ds
            );
            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(new Dimension(280, 120));

            JSplitPane split = new JSplitPane(
                    JSplitPane.VERTICAL_SPLIT,
                    tableScroll, chartPanel
            );
            split.setResizeWeight(0.33);
            split.setBorder(null);

            nutrientPanel.removeAll();
            nutrientPanel.add(split, BorderLayout.CENTER);
            nutrientPanel.revalidate();
            nutrientPanel.repaint();

            statusLbl.setText("Nutrients + calorie chart displayed");
        } catch (Exception ex) {
            statusLbl.setText("Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // Reads form data and saves a new meal entry after validation, with all validations and confirmation.
    private void saveEntry() {
        try {
            Date d = (Date) dateSpinner.getValue();
            LocalDate date = d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            Date t = (Date) timeSpinner.getValue();
            LocalTime time = t.toInstant().atZone(ZoneId.systemDefault())
                    .toLocalTime().withSecond(0).withNano(0);

            MealType type = (MealType) mealTypeBox.getSelectedItem();
            if (type != MealType.SNACK) {
                boolean dup = controller.getMealsForUserOnDate(profileName, date)
                        .stream().anyMatch(me -> me.getMealType() == type);
                if (dup) {
                    statusLbl.setText(type.name().toLowerCase()
                            + " already entered for " + date);
                    return;
                }
            }

            List<MealItem> items = collectItems(true); // true = show error dialogs
            if (items.isEmpty()) {
                statusLbl.setText("Add at least one valid ingredient");
                return;
            }

            // Check ingredient names against DB
            List<String> invalidFoods = new ArrayList<>();
            for (MealItem item : items) {
                if (!foodExistsInDB(item.getFoodName())) {
                    invalidFoods.add(item.getFoodName());
                }
            }
            if (!invalidFoods.isEmpty()) {
                StringBuilder sb = new StringBuilder("<html><b>The following ingredient(s) do not exist in the database:</b><ul>");
                for (String s : invalidFoods) sb.append("<li>").append(s).append("</li>");
                sb.append("</ul>Meal entry NOT saved. Please check ingredient names.</html>");
                JOptionPane.showMessageDialog(this, sb.toString(), "Invalid Ingredient(s)", JOptionPane.ERROR_MESSAGE);
                statusLbl.setText("Invalid ingredient(s): " + invalidFoods);
                return;
            }

            MealEntry entry = new MealEntry(profileName, type, date, time);
            entry.setItems(items);

            // Show the custom confirmation dialog
            Map<String, NutrientInfo> nutrients = calc.calcForItemsWithUnits(items);
            boolean confirm = showNutrientConfirmDialog(items, nutrients, date, type);
            if (!confirm) {
                statusLbl.setText("Save cancelled by user.");
                return;
            }

            boolean ok = controller.saveMeal(entry);
            statusLbl.setText(ok ? "Entry saved!" : "Save failed");
        } catch (Exception ex) {
            statusLbl.setText("Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // Utility for checking if a food exists in the database (column: FoodDescription)
    private boolean foodExistsInDB(String foodName) {
        String sql = "SELECT COUNT(*) FROM food_name WHERE FoodDescription = ?";
        try (java.sql.Connection conn = java.sql.DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/nutriscidb", "root", "Tamjid01711!");
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, foodName);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    // Custom confirmation dialog with centered buttons and no empty space
    private boolean showNutrientConfirmDialog(List<MealItem> items, Map<String, NutrientInfo> nutrients, LocalDate date, MealType type) {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Review Nutrients and Ingredients - Save Entry?", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(12, 12));
        panel.setBorder(new EmptyBorder(10, 20, 10, 20));

        // Top: Date, Meal Type, Ingredients
        StringBuilder sb = new StringBuilder("<html>");
        sb.append("<b>Date:</b> ").append(date).append("<br>");
        sb.append("<b>Meal Type:</b> ").append(type).append("<br><br>");
        sb.append("<b>Ingredients:</b><br>");
        for (MealItem mi : items) {
            sb.append(mi.getFoodName()).append(": ").append(mi.getQuantity()).append(" g<br>");
        }
        sb.append("</html>");
        JLabel headerLabel = new JLabel(sb.toString());
        panel.add(headerLabel, BorderLayout.NORTH);

        // Center: Nutrient Table (compact)
        String[] colNames = {"Nutrient", "Amount", "Unit"};
        Object[][] rowData = {
                {"Calories", fmt(nutrients.get("KCAL")), unit(nutrients.get("KCAL"))},
                {"Protein", fmt(nutrients.get("PROT")), unit(nutrients.get("PROT"))},
                {"Fat", fmt(nutrients.get("FAT")), unit(nutrients.get("FAT"))},
                {"Carbs", fmt(nutrients.get("CARB")), unit(nutrients.get("CARB"))},
                {"Vitamin C", fmt(nutrients.get("VITC")), unit(nutrients.get("VITC"))},
                {"Vitamin D", fmt(nutrients.get("D-IU")), unit(nutrients.get("D-IU"))},
                {"Vitamin B-6", fmt(nutrients.get("B6")), unit(nutrients.get("B6"))},
                {"Vitamin B-12", fmt(nutrients.get("B12")), unit(nutrients.get("B12"))},
        };
        JTable table = new JTable(rowData, colNames);
        table.setEnabled(false);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        table.setRowHeight(22);
        table.setPreferredScrollableViewportSize(table.getPreferredSize());
        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setPreferredSize(new Dimension(340, table.getPreferredSize().height + 2));
        panel.add(tableScroll, BorderLayout.CENTER);

        // Bottom: confirmation text and buttons
        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));
        JLabel msg = new JLabel("<html><center><b>Please review the above information carefully.<br>Are you sure you want to save this meal entry?</b></center></html>", SwingConstants.CENTER);
        msg.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 4));
        JButton yesBtn = new JButton("Yes");
        JButton noBtn = new JButton("No");
        yesBtn.setPreferredSize(new Dimension(70, 30));
        noBtn.setPreferredSize(new Dimension(70, 30));
        btnPanel.add(yesBtn);
        btnPanel.add(noBtn);

        southPanel.add(Box.createVerticalStrut(10));
        southPanel.add(msg);
        southPanel.add(Box.createVerticalStrut(12));
        southPanel.add(btnPanel);
        panel.add(southPanel, BorderLayout.SOUTH);

        final boolean[] result = new boolean[1];
        yesBtn.addActionListener(e -> {
            result[0] = true;
            dialog.dispose();
        });
        noBtn.addActionListener(e -> {
            result[0] = false;
            dialog.dispose();
        });

        dialog.setContentPane(panel);
        dialog.pack();
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        return result[0];
    }

    // Utility formatters
    private String fmt(NutrientInfo ni) {
        if (ni == null) return "-";
        return String.format("%.2f", ni.getAmount());
    }
    private String unit(NutrientInfo ni) {
        if (ni == null) return "";
        return ni.getUnit();
    }

    private String displayName(String sym) {
        return switch (sym) {
            case "KCAL" -> "Calories";
            case "PROT" -> "Protein";
            case "FAT" -> "Fat";
            case "CARB" -> "Carbohydrates";
            case "VITC" -> "Vitamin C";
            case "D-IU" -> "Vitamin D";
            case "B6" -> "Vitamin B-6";
            case "B12" -> "Vitamin B-12";
            default -> sym;
        };
    }

    private LocalDate getSelectedDate() {
        Date d = (Date) dateSpinner.getValue();
        return d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private void addIngredientRow() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(2, 0, 2, 0));

        JTextField foodField = new JTextField(12);
        JTextField qtyField = new JTextField(5);
        qtyField.setToolTipText("Enter quantity in grams");
        JButton removeBtn = new JButton("–");
        removeBtn.setFocusPainted(false);
        removeBtn.setFont(removeBtn.getFont().deriveFont(12f));
        removeBtn.addActionListener(e -> {
            itemsPanel.remove(row);
            itemsPanel.revalidate();
            itemsPanel.repaint();
        });

        row.add(new JLabel("Food:"));
        row.add(foodField);
        row.add(new JLabel("Quantity:"));
        row.add(qtyField);
        row.add(removeBtn);

        itemsPanel.add(row);
        itemsPanel.revalidate();
    }

    // Collects all entered ingredients and quantities into MealItem objects.
    // The 'showErrorDialogs' argument determines whether to show error dialogs for invalid input (true) or just silently skip invalid rows (false, for Food Swap etc).
    private List<MealItem> collectItems(boolean showErrorDialogs) {
        List<MealItem> list = new ArrayList<>();
        for (Component c : itemsPanel.getComponents()) {
            if (!(c instanceof JPanel)) continue;
            JPanel row = (JPanel) c;
            String food = ((JTextField) row.getComponent(1)).getText().trim();
            String qtyS = ((JTextField) row.getComponent(3)).getText().trim();
            if (!food.isEmpty() && !qtyS.isEmpty()) {
                try {
                    double qty = Double.parseDouble(qtyS);
                    list.add(new MealItem(0, food, qty));
                } catch (NumberFormatException nfe) {
                    if (showErrorDialogs) {
                        JOptionPane.showMessageDialog(this,
                                "Invalid quantity for '" + food + "': " + qtyS + "\nPlease enter a valid number.",
                                "Invalid Quantity",
                                JOptionPane.ERROR_MESSAGE);
                    }
                    // return empty so validation fails and statusLbl is set
                    return new ArrayList<>();
                }
            }
        }
        return list;
    }
}
