package booking.service;
import java.util.List;

import booking.model.BookingRequest;
import booking.model.Slot;
import booking.model.Tutor;
import booking.model.Lesson;
import java.time.DayOfWeek;

public interface BookingService{
    List<Tutor> getTutors();
    Tutor getTutor(int tutorId);
    List<Slot> getSlots(boolean availableOnly);

    Lesson bookLesson(BookingRequest request);

    Lesson getLesson(int lessonId);

    List<Lesson> getLessonsForPupil(int matric);

    void cancelLesson (int lessonId, int matric);

    List<Lesson> getLessonsForDay(DayOfWeek day);

    void setSlotBlocked(String slotId, boolean blocked);
    
    List<Lesson> checkLessons();
}