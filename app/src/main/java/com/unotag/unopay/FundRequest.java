package com.unotag.unopay;

public class FundRequest {
    private String to_upi_id;
    private String amount;
    private String time_date;
    private String status;

    // Getters and Setters
    public String getTo_upi_id() {
        return to_upi_id;
    }

    public void setTo_upi_id(String to_upi_id) {
        this.to_upi_id = to_upi_id;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTime_date() {
        return time_date;
    }

    public void setTime_date(String time_date) {
        this.time_date = time_date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
