package com.stued.StuEd.Model_Classes;

public class SlotBookingClass {
    public String slotPath,razorpayPaymentID;
    public int rating;

    public SlotBookingClass() {
    }

    public SlotBookingClass(String slotPath, String razorpayPaymentID) {
        this.slotPath = slotPath;
        this.razorpayPaymentID = razorpayPaymentID;
        this.rating=-1;
    }
}
