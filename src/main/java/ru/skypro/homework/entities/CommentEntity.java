package ru.skypro.homework.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Setter
@Getter
@ToString
@NoArgsConstructor
@Table(name = "comments")
public class CommentEntity {
    @Id
    @Column(name = "commentId")
    private Integer commentId;
    @Column(name = "author")
    private Integer author;
    @Column(name = "authorImage")
    private String authorImage;
    @Column(name = "authorFirstName")
    private String authorFirstName;
    @Column(name = "createdAt")
    private Integer createdAt;
    @Column(name = "pk")
    private Integer pk;
    @Column(name = "text")
    private String text;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommentEntity)) return false;
        CommentEntity that = (CommentEntity) o;
        return Objects.equals(commentId, that.commentId) && Objects.equals(author, that.author) && Objects.equals(authorImage, that.authorImage) && Objects.equals(authorFirstName, that.authorFirstName) && Objects.equals(createdAt, that.createdAt) && Objects.equals(pk, that.pk) && Objects.equals(text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(commentId, author, authorImage, authorFirstName, createdAt, pk, text);
    }
}
