package booking.service;
import java.util.List;
import booking.model.Slot;
import booking.model.Tutor;

public interface BookingService{
    List<Tutor> getTutors();
    Tutor getTutor(int tutorId);
    List<Slot> getSlots(boolean availableOnly);
}