package booking.controller;

import java.time.DayOfWeek;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;

import booking.model.Lesson;
import booking.model.Slot;
import booking.model.Tutor;
import booking.service.BookingService;

@RestController
public class TutorController {

    private final BookingService bookingService;
    private final String tutorApiKey;

    public TutorController(BookingService bookingService, @Value("${tutor.api-key}") String tutorApiKey) {
        this.bookingService = bookingService;
        this.tutorApiKey = tutorApiKey;
    }

    @GetMapping("/tutors")
    public ResponseEntity<List<Tutor>> getTutors() {
        return ResponseEntity.ok(bookingService.getTutors());
    }

    @GetMapping("/tutors/{tutorId}")
    public ResponseEntity<Tutor> getTutor(@PathVariable int tutorId) {
        return ResponseEntity.ok(findTutor(tutorId));
    }

    @GetMapping("/tutors/{tutorId}/slots")
    public ResponseEntity<List<Slot>> getSlots(@PathVariable int tutorId,
            @RequestParam(defaultValue = "false") boolean available) {
        findTutor(tutorId);
        return ResponseEntity.ok(bookingService.getSlots(available));
    }

    @GetMapping("/tutors/{tutorId}/lessons")
    public ResponseEntity<List<Lesson>> getLessonsForDay(@PathVariable int tutorId,
            @RequestParam DayOfWeek day,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        checkTutorKey(authorization);
        findTutor(tutorId);
        return ResponseEntity.ok(bookingService.getLessonsForDay(day));
    }
    @PutMapping("/tutors/{tutorId}/slots/{slotId}/block")
    public ResponseEntity<Void> blockSlot(@PathVariable int tutorId, @PathVariable String slotId,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        checkTutorKey(authorization);
        findTutor(tutorId);
        bookingService.setSlotBlocked(slotId, true);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/tutors/{tutorId}/slots/{slotId}/block")
    public ResponseEntity<Void> unblockSlot(@PathVariable int tutorId, @PathVariable String slotId,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        checkTutorKey(authorization);
        findTutor(tutorId);
        bookingService.setSlotBlocked(slotId, false);
        return ResponseEntity.noContent().build();
    }

    private void checkTutorKey(String authorization) {
        if (authorization == null || !authorization.equals("Bearer " + tutorApiKey)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "A valid tutor API key is required");
        }
    }

    private Tutor findTutor(int tutorId) {
        Tutor tutor = bookingService.getTutor(tutorId);
        if (tutor == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No tutor with id " + tutorId);
        }
        return tutor;
    }
        @PostMapping("/tutors/{tutorId}/lesson-checks")
    public ResponseEntity<List<Lesson>> checkLessons(@PathVariable int tutorId,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        checkTutorKey(authorization);
        findTutor(tutorId);
        return ResponseEntity.ok(bookingService.checkLessons());
    }
}

