package ca.yorku.eecs3311.ui;

import ca.yorku.eecs3311.meal.MealEntry;
import ca.yorku.eecs3311.meal.MealItem;
import ca.yorku.eecs3311.meal.MealLogController;
import ca.yorku.eecs3311.meal.MealType;
import ca.yorku.eecs3311.nutrient.NutrientCalculator;
import ca.yorku.eecs3311.nutrient.NutrientInfo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.List;

public class MealEntryPanel extends JPanel {
    private final String profileName;
    private final Navigator nav;
    private final MealLogController controller = new MealLogController();
    private final NutrientCalculator calc = new NutrientCalculator();

    private final JSpinner dateSpinner;
    private final JSpinner timeSpinner;
    private final JComboBox<MealType> mealTypeBox = new JComboBox<>(MealType.values());
    private final JPanel itemsPanel   = new JPanel();
    private final JPanel nutrientPanel = new JPanel(new BorderLayout());
    private final JLabel statusLbl    = new JLabel(" ");

    public MealEntryPanel(Navigator nav, String profileName) {
        this.nav = nav;
        this.profileName = profileName;
        setLayout(new BorderLayout(10,10));

        // --- Header ---
        JLabel header = new JLabel("Log a meal for: " + profileName);
        header.setFont(header.getFont().deriveFont(Font.BOLD, 16f));
        add(header, BorderLayout.NORTH);

        // --- Center form ---
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4,4,4,4);
        gbc.anchor = GridBagConstraints.WEST;
        int y = 0;

        // Date spinner
        dateSpinner = new JSpinner(
                new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH)
        );
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd"));
        gbc.gridx=0; gbc.gridy=y; form.add(new JLabel("Date:"), gbc);
        gbc.gridx=1;           form.add(dateSpinner, gbc);

        // Time spinner
        timeSpinner = new JSpinner(
                new SpinnerDateModel(new Date(), null, null, Calendar.MINUTE)
        );
        timeSpinner.setEditor(new JSpinner.DateEditor(timeSpinner, "HH:mm"));
        gbc.gridy=++y; gbc.gridx=0; form.add(new JLabel("Time:"), gbc);
        gbc.gridx=1;           form.add(timeSpinner, gbc);

        // Meal type
        gbc.gridy=++y; gbc.gridx=0; form.add(new JLabel("Meal Type:"), gbc);
        gbc.gridx=1;           form.add(mealTypeBox, gbc);

        // Ingredients label (note grams)
        gbc.gridy=++y; gbc.gridx=0; gbc.gridwidth=2;
        form.add(new JLabel("Ingredients (Qty in grams):"), gbc);

        // Items panel in scroll pane
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        JScrollPane scroll = new JScrollPane(itemsPanel);
        scroll.setPreferredSize(new Dimension(350,150));
        gbc.gridy=++y; form.add(scroll, gbc);

        // Add‐ingredient button
        JButton addItemBtn = new JButton("Add Ingredient");
        gbc.gridy=++y; form.add(addItemBtn, gbc);

        add(form, BorderLayout.CENTER);

        // --- Nutrient info panel (right) ---
        nutrientPanel.setBorder(BorderFactory.createTitledBorder("Nutrient Info"));
        nutrientPanel.setPreferredSize(new Dimension(300,150));
        add(nutrientPanel, BorderLayout.EAST);

        // --- Bottom controls ---
        JButton showNutBtn = new JButton("Show Nutrients");
        JButton saveBtn    = new JButton("Save Entry");
        JButton backBtn    = new JButton("Back");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.add(showNutBtn);
        buttonPanel.add(saveBtn);
        buttonPanel.add(backBtn);

        JPanel south = new JPanel(new BorderLayout());
        south.add(buttonPanel, BorderLayout.NORTH);
        south.add(statusLbl,    BorderLayout.SOUTH);
        add(south, BorderLayout.SOUTH);

        // --- Listeners ---
        addItemBtn.addActionListener(e -> addIngredientRow());

        showNutBtn.addActionListener(e -> {
            try {
                List<MealItem> items = collectItems();
                if (items.isEmpty()) {
                    statusLbl.setText("Add at least one ingredient");
                    return;
                }
                Map<String,NutrientInfo> all = calc.calcForItemsWithUnits(items);
                List<String> BASIC = List.of("KCAL","PROT","FAT","CARB");

                DefaultTableModel dm = new DefaultTableModel(
                        new Object[]{"Nutrient","Amount","Unit"}, 0
                );
                for (String sym : BASIC) {
                    NutrientInfo info = all.get(sym);
                    double amt  = info==null ? 0.0 : info.getAmount();
                    String unit = info==null ? ""   : info.getUnit();
                    dm.addRow(new Object[]{
                            sym,
                            String.format("%.2f", amt),
                            unit
                    });
                }
                JTable nutTable = new JTable(dm);
                nutrientPanel.removeAll();
                nutrientPanel.add(new JScrollPane(nutTable), BorderLayout.CENTER);
                nutrientPanel.revalidate();
                statusLbl.setText("Basic nutrients displayed");
            } catch (Exception ex) {
                statusLbl.setText("Error: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        saveBtn.addActionListener(e -> {
            try {
                // 1) Read date/time
                Date d = (Date) dateSpinner.getValue();
                LocalDate date = d.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
                Date t = (Date) timeSpinner.getValue();
                LocalTime time = t.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalTime()
                        .withSecond(0).withNano(0);

                // 2) Meal type
                MealType type = (MealType) mealTypeBox.getSelectedItem();

                // 3) Validate one-per-date for non-snacks
                if (type != MealType.SNACK) {
                    List<MealEntry> existing =
                            controller.getMealsForUserOnDate(profileName, date);
                    boolean already = existing.stream()
                            .anyMatch(me -> me.getMealType() == type);
                    if (already) {
                        statusLbl.setText(
                                "You have already entered "
                                        + type.name().toLowerCase()
                                        + " for " + date
                        );
                        return;
                    }
                }

                // 4) Build entry & items
                MealEntry entry = new MealEntry(profileName, type, date, time);
                List<MealItem> items = collectItems();
                if (items.isEmpty()) {
                    statusLbl.setText("Add at least one ingredient");
                    return;
                }
                entry.setItems(items);

                // 5) Persist
                boolean ok = controller.saveMeal(entry);
                statusLbl.setText(ok ? "Entry saved!" : "Save failed");
            } catch (Exception ex) {
                statusLbl.setText("Error: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        backBtn.addActionListener(e -> nav.showSelectProfile());

        // seed initial row
        addIngredientRow();
    }

    private void addIngredientRow() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT,5,0));
        JTextField foodField = new JTextField(12);
        JTextField qtyField  = new JTextField(5);
        qtyField.setToolTipText("Enter quantity in grams");
        JButton removeBtn   = new JButton("–");
        removeBtn.addActionListener(e -> {
            itemsPanel.remove(row);
            itemsPanel.revalidate();
            itemsPanel.repaint();
        });
        row.add(new JLabel("Food:"));
        row.add(foodField);
        row.add(new JLabel("Qty (g):"));
        row.add(qtyField);
        row.add(removeBtn);
        itemsPanel.add(row);
        itemsPanel.revalidate();
    }

    private List<MealItem> collectItems() {
        List<MealItem> items = new ArrayList<>();
        for (Component c : itemsPanel.getComponents()) {
            JPanel row = (JPanel)c;
            String food = ((JTextField)row.getComponent(1)).getText().trim();
            String qtyS = ((JTextField)row.getComponent(3)).getText().trim();
            if (!food.isEmpty() && !qtyS.isEmpty()) {
                double qty = Double.parseDouble(qtyS);
                items.add(new MealItem(0, food, qty));
            }
        }
        return items;
    }
}
