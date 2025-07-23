package ca.yorku.eecs3311.ui;

import ca.yorku.eecs3311.meal.MealEntry;
import ca.yorku.eecs3311.meal.MealLogController;
import ca.yorku.eecs3311.nutrient.NutrientCalculator;
import ca.yorku.eecs3311.nutrient.NutrientInfo;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class CalorieIntakePanel extends JPanel {
    private final Navigator nav;
    private final String profileName;

    public CalorieIntakePanel(Navigator nav, String profileName) {
        this.nav = nav;
        this.profileName = profileName;
        setLayout(new BorderLayout());

        // Use JSpinner for start and end date
        JSpinner startDateSpinner = new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH));
        JSpinner endDateSpinner = new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH));
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

        showButton.addActionListener(e -> {
            try {
                LocalDate startDate = ((Date) startDateSpinner.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate endDate = ((Date) endDateSpinner.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                MealLogController ctrl = new MealLogController();
                NutrientCalculator calc = new NutrientCalculator();

                List<MealEntry> entries = ctrl.getMealsForUserBetweenDates(profileName, startDate, endDate);

                double totalCalories = 0;
                for (MealEntry entry : entries) {
                    NutrientInfo cal = calc.calcForEntryWithUnits(entry.getId()).get("KCAL");
                    if (cal != null) totalCalories += cal.getAmount();
                }
                    //Optional line below to show total calories intake
              JOptionPane.showMessageDialog(this, "Total Calories: " + totalCalories);

                // Bar Chart Section

                Map<LocalDate, Double> perDay = new LinkedHashMap<>();
                for (MealEntry entry : entries) {
                    LocalDate day = entry.getDate();
                    double cals   = 0.0;
                    NutrientInfo calInfo = calc.calcForEntryWithUnits(entry.getId()).get("KCAL");
                    if (calInfo != null) cals = calInfo.getAmount();

                    perDay.merge(day, cals, Double::sum);     // accumulate if multiple meals per day
                }

                // Create a dataset (row-key = “Calories”, column-key = date string)
                DefaultCategoryDataset dataset = new DefaultCategoryDataset();
                for (Map.Entry<LocalDate, Double> d : perDay.entrySet()) {
                    dataset.addValue(d.getValue(), "Calories", d.getKey().toString());
                }

                // Build the bar chart
                JFreeChart chart = ChartFactory.createBarChart(
                        "Daily Calorie Intake (" + startDate + " – " + endDate + ")",
                        "Date",
                        "Calories (kcal)",
                        dataset,
                        PlotOrientation.VERTICAL,
                        false,   // legend: false (only one series)
                        true,    // tool-tips
                        false    // URLs
                );


                ChartPanel chartPanel = new ChartPanel(chart);
                chartPanel.setPreferredSize(new Dimension(700, 400));

                JOptionPane.showMessageDialog(
                        this,
                        chartPanel,
                        "Calorie Bar Chart",
                        JOptionPane.PLAIN_MESSAGE);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        backButton.addActionListener(e -> nav.showMainMenu());
    }
}
