package ca.yorku.eecs3311.ui;

import ca.yorku.eecs3311.meal.MealEntry;
import ca.yorku.eecs3311.meal.MealLogController;
import ca.yorku.eecs3311.nutrient.NutrientCalculator;
import ca.yorku.eecs3311.nutrient.NutrientInfo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.List;

public class JournalPanel extends JPanel {
    private final String profileName;
    private final Navigator nav;
    private final MealLogController mealCtrl = new MealLogController();
    private final NutrientCalculator calc   = new NutrientCalculator();

    private final JSpinner dateSpinner;
    private final JTable  table;
    private final DefaultTableModel model;

    public JournalPanel(Navigator nav, String profileName) {
        this.nav = nav;
        this.profileName = profileName;
        setLayout(new BorderLayout(10,10));

        // Top: date picker + load/back
        JPanel top = new JPanel();
        dateSpinner = new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH));
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd"));
        JButton loadBtn = new JButton("Load");
        JButton backBtn = new JButton("Back");
        top.add(new JLabel("Date:"));
        top.add(dateSpinner);
        top.add(loadBtn);
        top.add(backBtn);
        add(top, BorderLayout.NORTH);

        // Table: Type | Time | Calories
        model = new DefaultTableModel(new Object[]{"Type","Time","Calories","ID"}, 0) {
            @Override public boolean isCellEditable(int r,int c){return false;}
        };
        table = new JTable(model);
        // hide the ID column from view:
        table.removeColumn(table.getColumnModel().getColumn(3));
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Listeners
        loadBtn.addActionListener(e -> loadEntries());
        backBtn.addActionListener(e -> nav.showMainMenu());

        table.getSelectionModel().addListSelectionListener((ListSelectionEvent ev) -> {
            if (!ev.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                int row = table.getSelectedRow();
                // We stored the entryId in the hidden 4th column of the model:
                int entryId = (int)model.getValueAt(row, 3);
                showDetailDialog(entryId);
            }
        });
    }

    private void loadEntries() {
        model.setRowCount(0);
        Date d = (Date) dateSpinner.getValue();
        LocalDate date = d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        try {
            List<MealEntry> entries = mealCtrl.getMealsForUserOnDate(profileName, date);
            for (MealEntry e : entries) {
                // Use the new method that returns NutrientInfo (symbol+amount+unit)
                Map<String,NutrientInfo> nuts = calc.calcForEntryWithUnits(e.getId());
                NutrientInfo cal = nuts.get("KCAL");
                double cals = cal == null ? 0.0 : cal.getAmount();
                model.addRow(new Object[]{ e.getMealType(), e.getTime(), cals, e.getId() });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading entries: " + ex.getMessage());
        }
    }

    private void showDetailDialog(int entryId) {
        try {
            Map<String,NutrientInfo> nuts = calc.calcForEntryWithUnits(entryId);
            DefaultTableModel dm = new DefaultTableModel(new Object[]{"Nutrient","Amount","Unit"}, 0);
            nuts.values().forEach(info ->
                    dm.addRow(new Object[]{
                            info.getSymbol(),
                            String.format("%.2f", info.getAmount()),
                            info.getUnit()
                    })
            );
            JTable detail = new JTable(dm);
            JOptionPane.showMessageDialog(
                    this,
                    new JScrollPane(detail),
                    "Nutrient Breakdown",
                    JOptionPane.PLAIN_MESSAGE
            );
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error fetching details: " + ex.getMessage());
        }
    }
}
