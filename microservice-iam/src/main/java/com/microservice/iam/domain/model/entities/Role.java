package com.microservice.iam.domain.model.entities;

import com.microservice.iam.domain.model.valueobjects.Roles;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

@Entity
@Data
@NoArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, unique = true)
    private Roles name;

    /**
     * Constructor
     * <p>
     *  Creates a new role with the given name.
     * </p>
     * @param name The name of the role.
     */
    public Role(Roles name) { this.name = name; }

    /**
     * getStringName
     * <p>
     *  Returns the name of the role as a string.
     * </p>
     * @return The name of the role as a string.
     */
    public String getStringName() {
        return name.name();
    }

    /**
     * toRoleFromName
     * <p>
     *  Converts a string to a role.
     * </p>
     * @param name The name of the role.
     * @return The role.
     */
    public static Role toRoleFromName(String name) {
        return new Role(Roles.valueOf(name));
    }

    /**
     * getDefaultRole
     * <p>
     *  Returns the default role for AylluCare/B4U.
     * </p>
     * @return The default role (ROLE_PATIENT).
     */
    public static Role getDefaultRole() {
        return new Role(Roles.ROLE_PATIENT);
    }

    /**
     * validateRoleSet
     * <p>
     *  Validates a set of roles. If the set is null or empty, the default role is returned.
     *  For AylluCare/B4U, valid roles are: ROLE_PATIENT, ROLE_DOCTOR, ROLE_ADMIN.
     * </p>
     * @param roles The set of roles to validate.
     * @return The set of roles.
     */
    public static List<Role> validateRoleSet(List<Role> roles) {
        if (Objects.isNull(roles) || roles.isEmpty()) {
            return List.of(getDefaultRole());
        }
        return roles;
    }
}
