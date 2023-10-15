//package ru.skypro.homework.entities;
//
//import lombok.*;
//import org.hibernate.proxy.HibernateProxy;
//
//import javax.persistence.*;
//import java.util.Objects;
//
//@Getter
//@Setter
//@ToString
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//@Entity
//public class Image {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "id_image")
//    private Long id;
//
//    @Column(name = "file_size")
//    private long fileSize;
//    @Column(name = "media_type")
//    private String mediaType;
//    @Column(name = "image")
//    @Lob
//    private byte[] data;
//
//    @Override
//    public final boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null) return false;
//        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
//        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
//        if (thisEffectiveClass != oEffectiveClass) return false;
//        Image image = (Image) o;
//        return getId() != null && Objects.equals(getId(), image.getId());
//    }
//
//    @Override
//    public final int hashCode() {
//        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
//    }
//}
