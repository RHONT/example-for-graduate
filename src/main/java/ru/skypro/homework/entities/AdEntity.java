package ru.skypro.homework.entities;

import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Setter
@Getter
@ToString
@Builder
@NoArgsConstructor
@Table(name = "ads")
@AllArgsConstructor
public class AdEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk")  // id_ad
    private Integer pk;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_image")
    private ImageEntity imageEntity;

    @Column(name = "title")
    private String title;

    @Column(name = "price")
    private Integer price;

    @Column(name = "description")
    private String description;

    @ToString.Exclude
    @OneToMany(mappedBy = "adEntity",cascade = CascadeType.ALL)
    List<CommentEntity> commentEntityList;

    @ManyToOne
    @JoinColumn(name = "id")
    UserEntity author;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        AdEntity adEntity = (AdEntity) o;
        return getPk() != null && Objects.equals(getPk(), adEntity.getPk());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
