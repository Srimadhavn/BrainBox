package com.chatportal.model;

import java.sql.Timestamp;

public class Query {
    private int id;
    private int userId;
    private String subjectCode;
    private String content;
    private Timestamp timestamp;
    private String teacherResponse;

    public Query(int id, int userId, String subjectCode, String content, Timestamp timestamp, String teacherResponse) {
        this.id = id;
        this.userId = userId;
        this.subjectCode = subjectCode;
        this.content = content;
        this.timestamp = timestamp;
        this.teacherResponse = teacherResponse;

    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public String getContent() {
        return content;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public String getTeacherResponse() {
        return teacherResponse;
    }

    @Override
    public String toString() {
        return subjectCode + ": " + content; // Customize this as needed
    }
}
