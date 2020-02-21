package com.stued.StuEd.Model_Classes;

public class TopicClass {
    public String topicName,topicDescription,estimatedMarks;
    public SlotsClass slotsClass;
    public TopicClass()
    {
    }

    public TopicClass(String topicName, String topicDescription, String estimatedMarks, SlotsClass slotsClass) {
        //this.uniquekey = uniquekey;
        this.topicName = topicName;
        this.topicDescription = topicDescription;
        this.estimatedMarks = estimatedMarks;
        this.slotsClass = slotsClass;
    }
}
