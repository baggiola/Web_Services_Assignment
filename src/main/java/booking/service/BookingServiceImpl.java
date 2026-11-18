package booking.service;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import booking.model.GradeRange;
import booking.model.Slot;
import booking.model.Tutor;

@Component
public class BookingServiceImpl implements BookingService{

    private final Tutor tutor;
    private final Map<String, Slot> slots = new LinkedHashMap<>();

    public BookingServiceImpl(){
        tutor = new Tutor(1, "Benjamin Buttons", Map.of(
            "piano", new GradeRange(3,7),
            "guitar", new GradeRange(1,8),
            "bass", new GradeRange(1,5)
        ));

        DateTimeFormatter idTime = DateTimeFormatter.ofPattern("HHmm");
        LocalTime closing = LocalTime.of(20,0);

        for(DayOfWeek day : DayOfWeek.values()){
            if(day == DayOfWeek.SUNDAY){
                continue;
            }
            LocalTime start = LocalTime.of(16,0);
            if (day == DayOfWeek.SATURDAY){
                start = LocalTime.of(9,0);
            }
            while (start.isBefore(closing)){
                String id = day.name().substring(0,3) + "-" + start.format(idTime);
                slots.put(id, new Slot(id,day,start));
                start = start.plusMinutes(30);
            }
        }
    }
    @Override 
    public List<Tutor> getTutors(){
        return List.of(tutor);
    }
    @Override 
    public Tutor getTutor(int tutorId){
        if(tutorId == tutor.id()){
            return tutor;
    }
    return null;

}
@Override 
public List<Slot> getSlots(boolean availableOnly){
    List<Slot> result = new ArrayList<>();
    for (Slot slot : slots.values()){
        if(!availableOnly || !slot.isBlocked()){
            result.add(slot);
        }
    }
    return result;
}
}
