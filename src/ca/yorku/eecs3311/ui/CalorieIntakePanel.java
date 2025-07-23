package ca.yorku.eecs3311.ui;

import ca.yorku.eecs3311.meal.MealEntry;
import ca.yorku.eecs3311.meal.MealLogController;
import ca.yorku.eecs3311.nutrient.NutrientCalculator;
import ca.yorku.eecs3311.nutrient.NutrientInfo;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.List;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class CalorieIntakePanel extends JPanel {

    private final Navigator nav;
    private final String profileName;

    // UI components updated by nutrient calculator
    private final JPanel chartContainer = new JPanel(new BorderLayout());
    private final JLabel totalLabel     = new JLabel("Total Calories: 0");

    // date pickers
    private final JSpinner startDateSpinner =
            new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH));
    private final JSpinner endDateSpinner   =
            new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH));

    public CalorieIntakePanel(Navigator nav, String profileName) {
        this.nav = nav;
        this.profileName = profileName;

        setLayout(new BorderLayout());

        // format spinners
        startDateSpinner.setEditor(new JSpinner.DateEditor(startDateSpinner, "yyyy-MM-dd"));
        endDateSpinner.setEditor(new JSpinner.DateEditor(endDateSpinner, "yyyy-MM-dd"));

        JButton showButton = new JButton("Show Total Calories");
        JButton backButton = new JButton("Back");

        JPanel top = new JPanel();
        top.add(new JLabel("Start Date:"));
        top.add(startDateSpinner);
        top.add(new JLabel("End Date:"));
        top.add(endDateSpinner);
        top.add(showButton);
        top.add(backButton);

        add(top, BorderLayout.NORTH);
        add(chartContainer, BorderLayout.CENTER);
        add(totalLabel, BorderLayout.SOUTH);

        // listeners
        //loads data and go backs to already selected profile/previous page
        showButton.addActionListener(e -> loadAndDisplayData());
        backButton.addActionListener(e -> nav.showMealLog(profileName));
    }
// Displays barchart and total cals.
    private void loadAndDisplayData() {
        try {
            LocalDate startDate = ((Date) startDateSpinner.getValue())
                    .toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate endDate = ((Date) endDateSpinner.getValue())
                    .toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            MealLogController ctrl = new MealLogController();
            NutrientCalculator calc = new NutrientCalculator();

            List<MealEntry> entries = ctrl.getMealsForUserBetweenDates(profileName, startDate, endDate);

            if (entries.isEmpty()) {
                totalLabel.setText("Total Calories: 0 (no entries)");
                chartContainer.removeAll();
                chartContainer.add(new JLabel("No meals found for the selected range.",
                        SwingConstants.CENTER), BorderLayout.CENTER);
                chartContainer.revalidate();
                chartContainer.repaint();
                return;
            }

            double totalCalories = 0.0;
            Map<LocalDate, Double> perDay = new LinkedHashMap<>();

            for (MealEntry entry : entries) {
                Map<String, NutrientInfo> nutMap = calc.calcForEntryWithUnits(entry.getId());
                NutrientInfo calInfo = (nutMap != null) ? nutMap.get("KCAL") : null;
                double cals = (calInfo != null) ? calInfo.getAmount() : 0.0;

                totalCalories += cals;
                perDay.merge(entry.getDate(), cals, Double::sum);
            }

            totalLabel.setText(String.format("Total Calories: %.0f", totalCalories));

            // dataset
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            for (Map.Entry<LocalDate, Double> d : perDay.entrySet()) {
                dataset.addValue(d.getValue(), "Calories", d.getKey().toString());
            }

            // chart
            JFreeChart chart = ChartFactory.createBarChart(
                    "Daily Calorie Intake (" + startDate + " – " + endDate + ")",
                    "Date",
                    "Calories (kcal)",
                    dataset,
                    PlotOrientation.VERTICAL,
                    false,
                    true,
                    false
            );

            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(new Dimension(700, 400));

            chartContainer.removeAll();
            chartContainer.add(chartPanel, BorderLayout.CENTER);
            chartContainer.revalidate();
            chartContainer.repaint();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
