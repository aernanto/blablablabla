package apap.ti._5.tour_package_2306165963_be.restservice;

import apap.ti._5.tour_package_2306165963_be.dto.statistics.RevenueMonthlyDetailResponseDTO;
import apap.ti._5.tour_package_2306165963_be.dto.statistics.RevenueResponseDTO;
import apap.ti._5.tour_package_2306165963_be.dto.statistics.RevenueYearlyResponseDTO;

import java.util.List;

/**
 * Service for handling statistics and revenue calculations
 * Based on fulfilled OrderedQuantities from packages
 */
public interface StatisticsRestService {
    
    /**
     * Get revenue statistics
     * - If month is null: returns revenue per month in the year
     * - If month is provided: returns detailed revenue for that month with breakdown per activityType
     * 
     * @param year The year to get statistics for (required)
     * @param month The month to get statistics for (optional, 1-12)
     * @return Revenue response DTO containing period, total revenue, and breakdown
     */
    RevenueResponseDTO getRevenue(Integer year, Integer month);
    
    /**
     * Get yearly revenue statistics (Jan-Dec breakdown)
     * Returns monthly revenue for all 12 months in the specified year
     * 
     * @param year The year to get statistics for
     * @return List of monthly revenue DTOs (12 months)
     */
    List<RevenueYearlyResponseDTO> getYearlyRevenue(Integer year);
    
    /**
     * Get monthly revenue detail with activityType breakdown
     * Returns detailed revenue breakdown by activity type (Flight, Accommodation, Vehicle Rental)
     * 
     * @param year The year
     * @param month The month (1-12)
     * @return Monthly detail response DTO with breakdown per activity type
     */
    RevenueMonthlyDetailResponseDTO getMonthlyRevenue(Integer year, Integer month);
}