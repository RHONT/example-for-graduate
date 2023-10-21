package ru.skypro.homework.entities;

import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Setter
@Getter
@ToString
@Builder
@NoArgsConstructor
@Table(name = "comments")
@AllArgsConstructor
public class CommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Integer commentId;

    @Column(name = "created_at")
    private Long createdAt;

    @Column(name = "text")
    private String text;

    @ManyToOne
    @JoinColumn(name = "pk")
    AdEntity adEntity;

    @ManyToOne
    @JoinColumn(name = "id")
    UserEntity userEntity;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        CommentEntity that = (CommentEntity) o;
        return getCommentId() != null && Objects.equals(getCommentId(), that.getCommentId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
