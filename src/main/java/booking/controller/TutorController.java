package booking.controller;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import booking.model.Slot;
import booking.model.Tutor;
import booking.service.BookingService;

@RestController 
public class TutorController {
    private final BookingService bookingService;

    public TutorController(BookingService bookingService){
        this.bookingService = bookingService;
    }
    @GetMapping("/tutors")
    public ResponseEntity<List<Tutor>> getTutors(){
        return ResponseEntity.ok(bookingService.getTutors());
    }
    @GetMapping("/tutors/{tutorId}")
    public ResponseEntity<Tutor> getTutor(@PathVariable int tutorId){
        return ResponseEntity.ok(findTutor(tutorId));
    }
    @GetMapping ("/tutors/{tutorId}/slots")
    public ResponseEntity<List<Slot>> getSlots(@PathVariable int tutorId,
        @RequestParam(defaultValue = "false") boolean available){
    findTutor(tutorId);
    return ResponseEntity.ok(bookingService.getSlots(available));
        }
    private Tutor findTutor(int tutorId){
        Tutor tutor = bookingService.getTutor(tutorId);
        if (tutor == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No Tutor with id "+ tutorId);
        }
        return tutor;
    }
    }


