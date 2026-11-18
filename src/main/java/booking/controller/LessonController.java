package booking.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import booking.model.BookingRequest;
import booking.model.Lesson;
import booking.service.BookingService;

@RestController 
public class LessonController{
    private final BookingService bookingService;
    public LessonController(BookingService bookingService){
        this.bookingService = bookingService;
    }

    @PostMapping ("/lessons")
    public ResponseEntity<Lesson> bookLesson(@RequestBody BookingRequest request){
        Lesson lesson = bookingService.bookLesson(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{lessonId}")
            .buildAndExpand(lesson.id())
            .toUri();
        return ResponseEntity.created(location).body(lesson);
    }
    @GetMapping ("/lessons/{lessonId}")
    public ResponseEntity<Lesson> getLesson(@PathVariable int lessonId){
        Lesson lesson = bookingService.getLesson(lessonId);
        if(lesson == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No Lesson with this Id");

        }
        return ResponseEntity.ok(lesson);
    }
    @GetMapping ("/lessons")
    public ResponseEntity<List<Lesson>> getLessonsForPupil(@RequestParam int matric){
        return ResponseEntity.ok(bookingService.getLessonsForPupil(matric));
    }
}