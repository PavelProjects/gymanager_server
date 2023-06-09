package ru.pobopo.services.user.service.entity;


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
@Table(name = "gm_user_type")
@AllArgsConstructor
@NoArgsConstructor
public class UserTypeEntity {
    @Id
    @GenericGenerator(name = "entity_id", strategy = "ru.pobopo.services.user.service.entity.EntityIdGenerator")
    @GeneratedValue(generator = "entity_id")
    @Column(name = "_id")
    private String id;

    @Column(name = "_name", length = 64, nullable = false)
    private String name;

    @Column(name = "_caption", length = 128, nullable = false)
    private String caption;
}
