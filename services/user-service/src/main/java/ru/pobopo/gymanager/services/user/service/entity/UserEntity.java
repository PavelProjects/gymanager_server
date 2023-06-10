package ru.pobopo.gymanager.services.user.service.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Data
@Table(name = "gm_user")
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserEntity {
    @Id
    @GenericGenerator(name = "entity_id", strategy = "ru.pobopo.services.user.service.entity.EntityIdGenerator")
    @GeneratedValue(generator = "entity_id")
    @Column(name = "_id")
    private String id;

    @Column(name = "_login", length = 32, nullable = false, unique = true)
    private String login;

    @Column(name = "_password", length = 128, nullable = false)
    private String password;

    @Column(name = "_name", length = 128, nullable = false)
    private String name;

    @Column(name = "_phone", length = 15)
    private String phone;

    @Column(name = "_email", length = 64)
    private String email;

    @Column(name = "_creation_date")
    private LocalDateTime creationDate;

    @Column(name = "_active")
    private boolean active = true;

    @Column(name = "_auth_attempts")
    private int authAttempts;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "_type", referencedColumnName = "_id")
    private UserTypeEntity type;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "gm_user_roles",
        joinColumns = @JoinColumn(name = "_user_id", referencedColumnName = "_id"),
        inverseJoinColumns = @JoinColumn(name = "_role_id", referencedColumnName = "_id")
    )
    private List<Role> roles = new ArrayList<>();
}
