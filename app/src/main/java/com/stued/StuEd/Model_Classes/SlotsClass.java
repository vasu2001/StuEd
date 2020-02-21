package com.stued.StuEd.Model_Classes;

public class SlotsClass {
    public String date,time,fees,venue1,venue2,genderPreference;
    public int maxStudents,currentStudents;
    public boolean slotStatus;
    public  SlotsClass()
    {
    }

    public SlotsClass(String date, String time, String fees, int maxStudents,String venue1,String venue2, String genderPreference) {
        this.date = date;
        this.time = time;
        this.fees = fees;
        this.maxStudents=maxStudents;
        this.currentStudents=0;
        this.venue1=venue1;
        this.venue2=venue2;
        this.genderPreference=genderPreference;
        this.slotStatus=true;
    }
}
