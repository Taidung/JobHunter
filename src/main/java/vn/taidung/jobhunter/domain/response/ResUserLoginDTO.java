package vn.taidung.jobhunter.domain.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.taidung.jobhunter.domain.Role;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResUserLoginDTO {
    private long id;
    private String email;
    private String name;
    private Role role;
}
