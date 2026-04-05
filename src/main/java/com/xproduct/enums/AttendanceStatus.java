package com.xproduct.enums;

public enum AttendanceStatus {
    CLOCK_IN_PENDING,   // Attendant clocked in, awaiting manager approval
    CLOCKED_IN,         // Manager approved clock-in
    CLOCK_OUT_PENDING,  // Attendant clocked out, awaiting manager approval
    COMPLETED,          // Manager approved clock-out
    REJECTED            // Manager rejected
}
