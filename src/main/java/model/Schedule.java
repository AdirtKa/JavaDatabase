package model;

public class Schedule {
    private int id;
    private String dayOfWeek;
    private String time;
    private String classroom;

    public Schedule(int id, String dayOfWeek, String time, String classroom) {
        this.id = id;
        this.dayOfWeek = dayOfWeek;
        this.time = time;
        this.classroom = classroom;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getClassroom() {
        return classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }
}
