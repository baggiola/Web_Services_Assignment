package booking.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;
import booking.model.PupilRecord;
import booking.model.TrackerResponse;

@Component 
public class PupilTrackerClient{
    private final RestClient restClient = RestClient.create("https://pmaier.eu.pythonanywhere.com/plt");

    public PupilRecord getPupil(int matric){
        try{
            TrackerResponse response = restClient.get()
            .uri("/pupil/{matric}", matric)
            .retrieve()
            .body(TrackerResponse.class);
            return response.pupil();
        } catch (HttpClientErrorException.NotFound e){
            return null;
        } catch(RestClientException e){
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,"Pupil level tracker is unavailable");
        }
    }
}