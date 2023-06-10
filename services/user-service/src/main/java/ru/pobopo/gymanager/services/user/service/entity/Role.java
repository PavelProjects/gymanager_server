package ru.pobopo.gymanager.services.user.service.entity;


import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Data
@Table(name = "gm_role")
@AllArgsConstructor
@NoArgsConstructor
public class Role {
    @Id
    @GenericGenerator(name = "entity_id", strategy = "ru.pobopo.services.user.service.entity.EntityIdGenerator")
    @GeneratedValue(generator = "entity_id")
    @Column(name = "_id")
    private String id;

    @Column(name = "_name", length = 64, nullable = false)
    private String name;

    @Column(name = "_caption", length = 128, nullable = false)
    private String caption;

    @Column(name = "_creation_date")
    private LocalDateTime creationDate;
}
