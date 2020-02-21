package com.stued.StuEd.Model_Classes;

public class Users {
    public String Username,Email,PhoneNo,TeacherAc;
    public int rating,noOfRating;


    public Users()
    {

    }

    public Users(String username, String email, String phoneNo, String teacherAc) {
        Username = username;
        Email = email;
        PhoneNo = phoneNo;
        TeacherAc = teacherAc;
        rating=0;
        noOfRating=0;
    }
}
