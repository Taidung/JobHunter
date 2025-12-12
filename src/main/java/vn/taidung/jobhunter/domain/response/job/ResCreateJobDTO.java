package vn.taidung.jobhunter.domain.response.job;

import java.time.Instant;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ResCreateJobDTO {
    private Long id;
    private String name;
    private String location;
    private Double salary;
    private Integer quantity;
    private String level;
    private Instant startDate;
    private Instant endDate;
    private List<String> skills;
    private Instant createdAt;
    private String createdBy;
    private Boolean active;
}
