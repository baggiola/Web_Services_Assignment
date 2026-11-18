package booking.model;

public record GradeRange( int min, int max) {
    public boolean includes (int grade){
        return grade >= min && grade <= max;
    }
}