package vn.taidung.jobhunter.domain.response.resume;

import java.time.Instant;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.taidung.jobhunter.util.constant.ResumeStateEnum;

@Getter
@Setter
public class ResResumeDTO {
    private Long id;
    private String email;
    private String url;
    @Enumerated(EnumType.STRING)
    private ResumeStateEnum status;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;
    private String companyName;
    private ResResumeDTO.User user;
    private ResResumeDTO.Job job;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class User {
        private long id;
        private String name;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Job {
        private long id;
        private String name;
    }
}
