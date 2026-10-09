package com.ssps.stockprediction.ui.prediction

/**
 * Demand prediction utility for Sprint 1 (US6).
 *
 * This is a simple, testable prediction function that:
 * 1. Calculates average daily demand from historical sales data.
 * 2. Estimates the number of remaining days before stock runs out.
 *
 * Formula:
 *   averageDailyDemand = totalQuantitySold / numberOfDays
 *   estimatedDaysRemaining = currentStock / averageDailyDemand
 *
 * Edge cases:
 * - If there are no sales (totalQuantitySold == 0), demand is 0 → days remaining is infinite (Double.MAX_VALUE).
 * - If numberOfDays <= 0, returns 0.0 demand and infinite days.
 * - If currentStock <= 0, returns 0.0 days remaining.
 */
object DemandPredictor {

    /**
     * Result of a demand prediction calculation.
     *
     * @param averageDailyDemand The calculated average units sold per day.
     * @param estimatedDaysRemaining The estimated number of days until stock is depleted.
     *                                Double.MAX_VALUE if demand is zero.
     */
    data class PredictionResult(
        val averageDailyDemand: Double,
        val estimatedDaysRemaining: Double
    )

    /**
     * Calculates demand prediction.
     *
     * @param totalQuantitySold Total units sold in the observed period.
     * @param numberOfDays Number of days in the observed period.
     * @param currentStock Current stock quantity on hand.
     * @return PredictionResult with average daily demand and estimated days remaining.
     */
    fun calculatePrediction(
        totalQuantitySold: Int,
        numberOfDays: Int,
        currentStock: Int
    ): PredictionResult {
        // Handle edge cases
        if (totalQuantitySold <= 0 || numberOfDays <= 0) {
            return PredictionResult(
                averageDailyDemand = 0.0,
                estimatedDaysRemaining = if (currentStock > 0) Double.MAX_VALUE else 0.0
            )
        }

        val averageDailyDemand = totalQuantitySold.toDouble() / numberOfDays.toDouble()

        val estimatedDaysRemaining = if (averageDailyDemand > 0.0) {
            currentStock.toDouble() / averageDailyDemand
        } else {
            if (currentStock > 0) Double.MAX_VALUE else 0.0
        }

        return PredictionResult(
            averageDailyDemand = averageDailyDemand,
            estimatedDaysRemaining = estimatedDaysRemaining
        )
    }
}
