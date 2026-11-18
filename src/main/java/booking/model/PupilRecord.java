package booking.model;

import java.util.Map;

public record PupilRecord(int matric, String name, int age, Map<String, InstrumentLevel> instruments){
    
}