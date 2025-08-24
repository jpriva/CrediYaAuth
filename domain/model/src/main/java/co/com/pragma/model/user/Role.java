package co.com.pragma.model.user;

import lombok.*;

import java.util.StringJoiner;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Role {
    private Integer rolId;
    private String name;
    private String description;

    @Override
    public String toString() {
        StringJoiner joiner = new java.util.StringJoiner(", ", "Role{", "}");
        joiner.add("rolId=" + rolId);
        joiner.add("name=" + name);
        joiner.add("description=" + description);
        return joiner.toString();
    }
}
