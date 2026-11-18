package booking.model;
import java.util.Map;

public record Tutor(int id, String name, Map<String, GradeRange> instruments){
    
}