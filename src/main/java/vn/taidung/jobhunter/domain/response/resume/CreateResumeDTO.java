package vn.taidung.jobhunter.domain.response.resume;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateResumeDTO {
    private long id;
    private Instant createdAt;
    private String createdBy;
}
