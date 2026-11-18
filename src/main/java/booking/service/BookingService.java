package booking.service;
import java.util.List;

import booking.model.BookingRequest;
import booking.model.Slot;
import booking.model.Tutor;
import booking.model.Lesson;

public interface BookingService{
    List<Tutor> getTutors();
    Tutor getTutor(int tutorId);
    List<Slot> getSlots(boolean availableOnly);

    Lesson bookLesson(BookingRequest request);

    Lesson getLesson(int lessonId);

    List<Lesson> getLessonsForPupil(int matric);
}