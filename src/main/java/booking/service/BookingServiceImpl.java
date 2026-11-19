package booking.service;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import booking.model.GradeRange;
import booking.model.Slot;
import booking.model.Tutor;
import booking.model.BookingRequest;
import booking.model.InstrumentLevel;
import booking.model.Lesson;
import booking.model.PupilRecord;


@Component
public class BookingServiceImpl implements BookingService{

    private final PupilTrackerClient trackerClient;
    private static final LocalTime UNDER_16_CUTOFF = LocalTime.of(18,0);

    private final Tutor tutor;
    private final Map<String, Slot> slots = new LinkedHashMap<>();
    private final Map<Integer, Lesson> lessons = new LinkedHashMap<>();
    private int nextLessonId = 1;

    public BookingServiceImpl(PupilTrackerClient trackerClient){
        this.trackerClient = trackerClient;

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
public synchronized List<Slot> getSlots(boolean availableOnly){
    List<Slot> result = new ArrayList<>();
    for (Slot slot : slots.values()){
        if(!availableOnly || (!slot.isBlocked() && !isBooked(slot.getId()))){
            result.add(slot);
        }
    }
    return result;
}
@Override 
public synchronized Lesson getLesson(int lessonId){
    return lessons.get(lessonId);
}
@Override 
public synchronized List<Lesson> getLessonsForPupil(int matric){
    List<Lesson> result = new ArrayList<>();
    for (Lesson lesson : lessons.values()){
        if(lesson.matric() == matric){
            result.add(lesson);
        }
    }
    return result;
}
@Override 
public synchronized Lesson bookLesson(BookingRequest request){
    if (request.name() == null || request.instrument() == null || request.slotId() == null){
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"matric, name, instrument and slotId are all required to Book");
    }
    Slot slot = slots.get(request.slotId());
    if (slot == null){
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,"no Slot with id" + request.slotId() );
        
    }
    if (slot.isBlocked()){
        throw new ResponseStatusException(HttpStatus.CONFLICT,"The tutor has blocked this slot");
    }
    if (isBooked(slot.getId())){
        throw new ResponseStatusException(HttpStatus.CONFLICT,"This slot is already booked");
    }
    GradeRange range = tutor.instruments().get(request.instrument());
    if (range == null){
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The tutor does not teach this instrument");

    }
    PupilRecord pupil = trackerClient.getPupil(request.matric());
    if(pupil == null){
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "there is no pupil with this matriculation number. Please check the number you have entered");
    }
    if (!pupil.name().equalsIgnoreCase(request.name().trim())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "The name of the pupil does not match the matriculation number you have entered");

        }

    
    InstrumentLevel level = pupil.instruments().get(request.instrument());
    if (level == null){
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The pupil does not play" + request.instrument());
    }
    if (!range.includes(level.grade())){
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
            "The tutor teaches " + request.instrument() + "at grades " + range.min() + "-" +range.max()+ "only");
    }
    if(pupil.age() < 16 && !slot.getStartTime().isBefore(UNDER_16_CUTOFF)){
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pupils that are under 16 can't book lesson after 6pm");
    }
    if(hasLessonOnDay(pupil.matric(),slot.getDay())){
        throw new ResponseStatusException(HttpStatus.CONFLICT, "The pupil alread has a lesson booked on "+ slot.getDay());
    }

    Lesson lesson = new Lesson(nextLessonId, pupil.matric(), pupil.name(),request.instrument(), slot.getId());
    lessons.put(lesson.id(), lesson);
    nextLessonId++;
    return lesson;
}
@Override 
    public synchronized void cancelLesson(int lessonId, int matric){
        Lesson lesson = lessons.get(lessonId);
        if (lesson == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"There is no lesson with this Id" + lessonId);
        }
        if (lesson.matric()!=matric){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"This lesson belongs to another pupil");

        }
        lessons.remove(lessonId);
    }
private boolean isBooked(String slotId){
    for(Lesson lesson : lessons.values()){
        if(lesson.slotId().equals(slotId)){
            return true;
        }
    }return false;
}

private boolean hasLessonOnDay(int matric, DayOfWeek day){
    for(Lesson lesson:lessons.values()){
        if(lesson.matric() == matric && slots.get(lesson.slotId()).getDay()==day){
            return true;
        }
    }return false;
}

}
