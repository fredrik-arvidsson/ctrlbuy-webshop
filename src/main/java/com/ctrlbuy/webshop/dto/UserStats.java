package com.ctrlbuy.webshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStats {

    private long totalUsers;
    private long activeUsers;
    private long inactiveUsers;
    private long adminUsers;
    private long regularUsers;
    private long verifiedUsers;
    private long unverifiedUsers;
    private long recentRegistrations; // Last 30 days

    /**
     * Beräkna aktivitetsgrad i procent
     */
    public double getActivityPercentage() {
        if (totalUsers == 0) {
            return 0.0;
        }
        return (double) activeUsers / totalUsers * 100.0;
    }

    /**
     * Beräkna verifieringsgrad i procent
     */
    public double getVerificationPercentage() {
        if (totalUsers == 0) {
            return 0.0;
        }
        return (double) verifiedUsers / totalUsers * 100.0;
    }

    /**
     * Beräkna admin-andel i procent
     */
    public double getAdminPercentage() {
        if (totalUsers == 0) {
            return 0.0;
        }
        return (double) adminUsers / totalUsers * 100.0;
    }

    /**
     * Factory method för enkel skapning
     */
    public static UserStats empty() {
        return new UserStats(0, 0, 0, 0, 0, 0, 0, 0);
    }

    /**
     * Factory method med grundläggande data
     */
    public static UserStats basic(long total, long active) {
        return new UserStats(total, active, total - active, 0, total, 0, 0, 0);
    }
}