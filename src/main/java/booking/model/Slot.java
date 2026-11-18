package booking.model;

import java.time.DayOfWeek;import java.time.LocalTime;

public class Slot{
    private final String id;
    private final DayOfWeek day;
    private final LocalTime startTime;
    private boolean blocked;

    public Slot(String id, DayOfWeek day, LocalTime startTime){
        this.id = id;
        this.day = day;
        this.startTime = startTime;
        this.blocked = false;
    }

    public String getId() {
        return id;
    }
   public DayOfWeek getDay(){
        return day;
    }
    public LocalTime getStartTime(){
        return startTime;
    }
    public boolean isBlocked(){ 
        return blocked;
    }
    public void setBlocked(boolean blocked){
        this.blocked = blocked;
    }}