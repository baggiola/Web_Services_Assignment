package booking.model;

import java.util.Map;

public record Tutor(int id, String name, Map<String, GradeRange> instruments) {

    public record GradeRange(int min, int max) {

        public boolean includes(int grade) {
            return grade >= min && grade <= max;
        }
    }
}