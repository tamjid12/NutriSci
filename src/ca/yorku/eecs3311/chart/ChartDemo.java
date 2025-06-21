package ca.yorku.eecs3311.chart;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

public class ChartDemo {
    public static void main(String[] args) {
        // 1) Create your dataset
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(250, "Calories", "2025-06-18");
        dataset.addValue(300, "Calories", "2025-06-19");
        dataset.addValue(200, "Calories", "2025-06-20");

        // 2) Build the chart
        JFreeChart chart = ChartFactory.createBarChart(
                "Daily Calories",     // chart title
                "Date",               // domain axis label
                "Calories (kcal)",    // range axis label
                dataset               // your data
        );

        // 3) Show it in a window
        ChartFrame frame = new ChartFrame("Calorie Chart", chart);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
