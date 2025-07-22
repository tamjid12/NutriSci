package ca.yorku.eecs3311.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingConstants;
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

/**
 * This panel allows the user to log a meal by selecting a date, time, and meal type,
 * and entering food ingredients with quantity.
 * It supports live nutrient preview (including calories, macros, and key vitamins)
 * and a visual calorie bar chart (before and after entry).
 * Users can save the entry to the database.
 * (Updated: Meal History button opens MealHistoryPanel with Delete support)
 */
public class MealEntryPanel extends JPanel {
    private final String profileName;
    private final Navigator nav;
    private final MealLogController controller = new MealLogController();
    private final NutrientCalculator calc       = new NutrientCalculator();

    private JSpinner dateSpinner;
    private JSpinner timeSpinner;
    private final JComboBox<MealType> mealTypeBox = new JComboBox<>(MealType.values());
    private final JPanel itemsPanel   = new JPanel();
    private final JPanel nutrientPanel = new JPanel(new BorderLayout(5,5));
    private final JLabel statusLbl    = new JLabel(" ");

    public MealEntryPanel(Navigator nav, String profileName) {
        this.nav = nav;
        this.profileName = profileName;

        setLayout(new BorderLayout(10,10));
        setBorder(new EmptyBorder(10,10,10,10));
        setBackground(Color.WHITE);

        // Title
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        JLabel title    = new JLabel("Meal Logger", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(24f).deriveFont(Font.BOLD));
        JLabel subtitle = new JLabel("Log a meal for: " + profileName, SwingConstants.CENTER);
        subtitle.setFont(subtitle.getFont().deriveFont(16f));
        titlePanel.add(title,    BorderLayout.NORTH);
        titlePanel.add(subtitle, BorderLayout.SOUTH);
        add(titlePanel, BorderLayout.NORTH);

        // Form
        add(buildFormPanel(), BorderLayout.WEST);

        // Nutrient info
        nutrientPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                "Nutrient Info"
        ));
        nutrientPanel.setPreferredSize(new Dimension(320,300));
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
        form.setBorder(new EmptyBorder(0,0,10,0));
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

        // Ingredients label
        gbc.gridy=++y; gbc.gridx=0; gbc.gridwidth=2;
        form.add(new JLabel("Ingredients:"), gbc);

        // Items panel
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.setBackground(Color.WHITE);
        JScrollPane scroll = new JScrollPane(itemsPanel);
        scroll.setPreferredSize(new Dimension(350,140));
        scroll.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        gbc.gridy=++y; form.add(scroll, gbc);

        // Add ingredient button
        JButton addBtn = makeButton("Add Ingredient");
        gbc.gridy=++y; gbc.gridwidth=2;
        form.add(addBtn, gbc);
        addBtn.addActionListener(e -> addIngredientRow());

        return form;
    }

    private JPanel buildButtonPanel() {
        JButton showNutBtn = makeButton("Show Nutrients");
        JButton saveBtn    = makeButton("Save Entry");
        JButton historyBtn = makeButton("Meal History");
        JButton swapBtn    = makeButton("Food Swap");  // New button
        JButton backBtn    = makeButton("Back");
        JButton showCaloriesButton = makeButton("Show Calorie Intake");
        showNutBtn.addActionListener(e -> showNutrients());
        saveBtn   .addActionListener(e -> saveEntry());
        backBtn   .addActionListener(e -> nav.showSelectProfile());
        showCaloriesButton .addActionListener(e ->  nav.showCalorieIntakePanel(profileName));;

        // NEW: Open Meal History window
        historyBtn.addActionListener(e -> {
            MealHistoryPanel historyPanel = new MealHistoryPanel(nav, profileName);
            JFrame frame = new JFrame("Meal History");
            frame.setContentPane(historyPanel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });

        // Swap action
        swapBtn.addActionListener(e -> {
            List<MealItem> items = collectItems();
            if (items.isEmpty()) {
                statusLbl.setText("Add ingredients before swapping.");
                return;
            }
            nav.showFoodSwapPanel(items); // Navigator handles panel switch
        });

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER,10,0));
        btns.setOpaque(false);
        btns.add(showNutBtn);
        btns.add(saveBtn);
        btns.add(historyBtn);
        btns.add(swapBtn);   // Add the button here
        btns.add(showCaloriesButton);
        btns.add(backBtn);

        statusLbl.setFont(statusLbl.getFont().deriveFont(Font.BOLD,14f));
        statusLbl.setBorder(new EmptyBorder(8,8,8,8));
        statusLbl.setForeground(new Color(0,120,0));

        JPanel south = new JPanel(new BorderLayout());
        south.setOpaque(false);
        south.add(btns,      BorderLayout.NORTH);
        south.add(statusLbl, BorderLayout.SOUTH);
        return south;
    }


    private JButton makeButton(String text) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setBackground(new Color(70,130,180));
        b.setForeground(Color.WHITE);
        b.setBorder(new EmptyBorder(6,12,6,12));
        b.setFont(b.getFont().deriveFont(13f));
        return b;
    }

    //Reads and validates ingredients, then shows the nutrient table and calorie intake chart side by side.
    private void showNutrients() {
        try {
            List<MealItem> items = collectItems();
            if (items.isEmpty()) {
                statusLbl.setText("Add at least one ingredient");
                return;
            }

            Map<String,NutrientInfo> all = calc.calcForItemsWithUnits(items);

            // Show Nutrients
            List<String> BASIC = List.of(
                    "KCAL","PROT","FAT","CARB",
                    "VITC","D-IU","B6","B12"
            );
            DefaultTableModel dm = new DefaultTableModel(
                    new Object[]{"Nutrient","Amount","Unit"}, 0
            );
            for (String sym : BASIC) {
                NutrientInfo info = all.get(sym);
                double amt  = (info == null ? 0 : info.getAmount());
                String unit = (info == null ? "" : info.getUnit());
                dm.addRow(new Object[]{
                        displayName(sym),
                        String.format("%.2f", amt),
                        unit
                });
            }
            JTable nutTable = new JTable(dm);
            JScrollPane tableScroll = new JScrollPane(nutTable);
            tableScroll.setPreferredSize(new Dimension(280,160));

            // calculate before/after calories
            LocalDate date = getSelectedDate();
            double before = 0;
            for (MealEntry me : controller.getMealsForUserOnDate(profileName, date)) {
                NutrientInfo kc = calc.calcForEntryWithUnits(me.getId()).get("KCAL");
                before += (kc == null ? 0 : kc.getAmount());
            }
            double added = all.getOrDefault("KCAL", new NutrientInfo("KCAL",0,"")).getAmount();
            double after = before + added;

            DefaultCategoryDataset ds = new DefaultCategoryDataset();
            ds.addValue(before, "Calories", "Before Entry");
            ds.addValue(after,  "Calories", "After Entry");
            JFreeChart chart = ChartFactory.createBarChart(
                    "Calories Intake Today","", "kCal", ds
            );
            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(new Dimension(280,120));

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

    // Reads form data and saves a new meal entry after validation.
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

            MealEntry entry = new MealEntry(profileName, type, date, time);
            List<MealItem> items = collectItems();
            if (items.isEmpty()) {
                statusLbl.setText("Add at least one ingredient");
                return;
            }
            entry.setItems(items);
            boolean ok = controller.saveMeal(entry);
            statusLbl.setText(ok ? "Entry saved!" : "Save failed");
        } catch (Exception ex) {
            statusLbl.setText("Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // Maps nutrient symbols to user friendly names
    private String displayName(String sym) {
        return switch (sym) {
            case "KCAL"   -> "Calories";
            case "PROT"   -> "Protein";
            case "FAT"    -> "Fat";
            case "CARB"   -> "Carbohydrates";
            case "VITC"   -> "Vitamin C";
            case "D-IU"   -> "Vitamin D";
            case "B6"     -> "Vitamin B-6";
            case "B12"    -> "Vitamin B-12";
            default       -> sym;
        };
    }

    // returns date
    private LocalDate getSelectedDate() {
        Date d = (Date) dateSpinner.getValue();
        return d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    // Adds a new row in the ingredient entry list.
    private void addIngredientRow() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT,5,0));
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(2,0,2,0));  // tighter vertical gap

        JTextField foodField = new JTextField(12);
        JTextField qtyField  = new JTextField(5);
        qtyField.setToolTipText("Enter quantity in grams");
        JButton removeBtn   = new JButton("–");
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
    private List<MealItem> collectItems() {
        List<MealItem> list = new ArrayList<>();
        for (Component c : itemsPanel.getComponents()) {
            if (!(c instanceof JPanel)) continue;
            JPanel row = (JPanel) c;
            String food = ((JTextField)row.getComponent(1)).getText().trim();
            String qtyS = ((JTextField)row.getComponent(3)).getText().trim();
            if (!food.isEmpty() && !qtyS.isEmpty()) {
                list.add(new MealItem(0, food, Double.parseDouble(qtyS)));
            }
        }
        return list;
    }
}
