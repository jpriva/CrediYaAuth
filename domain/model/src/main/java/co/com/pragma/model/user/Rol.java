package co.com.pragma.model.user;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Rol {
    private Integer rolId;
    private String name;
    private String description;
}
