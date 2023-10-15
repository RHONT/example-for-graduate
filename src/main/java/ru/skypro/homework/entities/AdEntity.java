package ru.skypro.homework.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Setter
@Getter
@ToString
@NoArgsConstructor
@Table(name = "ads")
public class AdEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk")
    private Integer pk;
    @Column(name = "authorFirstName")
    private String authorFirstName;
    @Column(name = "authorLastName")
    private String authorLastName;
    @Column(name = "description")
    private String description;
    @Column(name = "email")
    private String email;
    @Column(name = "image")
    private String image;
    @Column(name = "phone")
    private String phone;
    @Column(name = "price")
    private Integer price;
    @Column(name = "title")
    private String title;
    @Column(name = "authorId")
    private Integer author;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AdEntity)) return false;
        AdEntity adEntity = (AdEntity) o;
        return Objects.equals(pk, adEntity.pk) && Objects.equals(authorFirstName, adEntity.authorFirstName) && Objects.equals(authorLastName, adEntity.authorLastName) && Objects.equals(description, adEntity.description) && Objects.equals(email, adEntity.email) && Objects.equals(image, adEntity.image) && Objects.equals(phone, adEntity.phone) && Objects.equals(price, adEntity.price) && Objects.equals(title, adEntity.title) && Objects.equals(userId, adEntity.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pk, authorFirstName, authorLastName, description, email, image, phone, price, title, author);
    }
}
