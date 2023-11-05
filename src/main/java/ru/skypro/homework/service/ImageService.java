package ru.skypro.homework.service;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.entities.AdEntity;
import ru.skypro.homework.entities.ImageEntity;
import ru.skypro.homework.entities.UserEntity;
import ru.skypro.homework.exceptions.NoAdException;
import ru.skypro.homework.exceptions.UnauthorizedException;
import ru.skypro.homework.mappers.ImageMapper;
import ru.skypro.homework.repository.AdsRepository;
import ru.skypro.homework.repository.ImageRepository;
import ru.skypro.homework.repository.UsersRepository;
import ru.skypro.homework.utilclass.JavaFileToMultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImageService {
    @Value("${path.avito.image.folder}")
    private String sourceSaveToHard;

    private final String source = "/users/id-image/";
    private final ImageRepository imageRepository;
    private final ImageMapper imageMapper;
    private final AdsRepository adsRepository;
    private final UsersRepository usersRepository;


    /**
     * Сохраняем картинку в базу. Двойное сохранение требуется для установления пути.
     * Так как оно привязано к id картинки
     * Example: "/id-image/5"
     *
     * @param file
     * @return
     * @throws IOException
     */
    public ImageEntity createImageEntity(MultipartFile file) throws IOException {

        ImageEntity image = new ImageEntity();
        imageMapper.updateImageEntityFromFile(file, image);
        image.setExtension(getExtension(file));
        return image;
    }

    private void updatePathImageEntity(ImageEntity imageEntity, MultipartFile file) {
        imageEntity.setFilePath(source + imageEntity.getId());
        imageEntity.setPathHardStore
                (sourceSaveToHard + imageEntity.getId() + imageEntity.getExtension());
    }

    private void updateAllDataImageEntity(ImageEntity imageEntity, MultipartFile file) throws IOException {
        imageMapper.updateImageEntityFromFile(file, imageEntity);
        imageEntity.setFilePath(source + imageEntity.getId());
        imageEntity.setExtension(getExtension(file));
        imageEntity.setPathHardStore
                (sourceSaveToHard + imageEntity.getId() + imageEntity.getExtension());
    }

    //todo Возможны ли ситуации, когда мы можем создать объявление без картинки?

    /**
     * Обновляем уже существующую картинку объявления
     *
     * @param id   объявления
     * @param file на обновление
     * @return
     */
    public byte[] updateImageAd(Integer id, MultipartFile file, UserDetails userDetails) throws IOException {
        //проверка файла и порезать
        // и его вернуть в конце.
        if (file == null) {
            file = new JavaFileToMultipartFile(new File("src/main/resources/image/test.jpg"));
        }
        ImageEntity image;
        ByteArrayResource resource;
        Optional<AdEntity> ad = adsRepository.findById(id);
        if (ad.isPresent()) {
            checkAuthority(userDetails, ad.get());
            Optional<ImageEntity> optionalImageEntity = Optional.ofNullable(ad.get().getImageEntity());

            if (optionalImageEntity.isPresent()) {
                image = optionalImageEntity.get();
                Files.deleteIfExists(Path.of(image.getPathHardStore()));
                updateAllDataImageEntity(image, file);
                saveImage(file, image);
                resource = new ByteArrayResource(Files.readAllBytes(Path.of(image.getPathHardStore())));
                return resource.getByteArray();
            }
            image = createImageEntity(file);
            ad.get().setImageEntity(image);
            adsRepository.save(ad.get());
            image.setId(ad.get().getImageEntity().getId());
            updatePathImageEntity(image, file);
            saveImage(file, image);
            resource = new ByteArrayResource(Files.readAllBytes(Path.of(image.getPathHardStore())));
            return resource.getByteArray();
        }
        log.debug("Ad with id {}, not found", id);
        throw new NoAdException("Ad with id =" + id + "not found");
    }

    public void updateImageUser(String username, MultipartFile file) throws IOException {
        if (file == null) {
            file = new JavaFileToMultipartFile(new File("src/main/resources/image/test.jpg"));
        }
        ImageEntity image;
        UserEntity userEntity = usersRepository.findByUsername(username).get();
        if (userEntity.getImageEntity() == null) {
            image = createImageEntity(file);
            userEntity.setImageEntity(image);
            usersRepository.save(userEntity);
            image.setId(userEntity.getImageEntity().getId());
            updatePathImageEntity(image, file);
            saveImage(file, image);
            return;
        }
        image = userEntity.getImageEntity();
        Files.deleteIfExists(Path.of(image.getPathHardStore()));
        updateAllDataImageEntity(image, file);
        saveImage(file, image);
    }

    public void loadImageToHard(Integer id, MultipartFile file) throws IOException {
        String extension = getExtension(file);
//        if (!ViewSelect.onlyImage(extension)) {
//            log.warn("Format " + extension + "not supported in ViewSelect.class");
//            throw new IllegalFormatContentException(extension);
//        }
        Path pathFile = Path.of(sourceSaveToHard, id + extension);
        // abc/abc/test.text :: getParent - > abc/abc/
        // createDirectories - создает директории если их нет.
        Files.createDirectories(pathFile.getParent());
        // deleteIfExists -удаляет файл если он уже существует
        Files.deleteIfExists(pathFile);

        log.debug("Path for save Image = " + pathFile);

        try (
                InputStream is = file.getInputStream();
                OutputStream out = Files.newOutputStream(pathFile, CREATE_NEW);
                BufferedInputStream bis = new BufferedInputStream(is, 2048);
                BufferedOutputStream bout = new BufferedOutputStream(out, 2048);
        ) {
            bis.transferTo(bout);
            log.debug("file saved successfully");
        }
    }

    private void saveImage(MultipartFile file, ImageEntity image) {
        try {
            loadImageToHard(image.getId(), file);
            imageRepository.save(image);
        } catch (IOException e) {
            log.debug("Сбой сохранения картинки  id={}", image.getId());
            throw new RuntimeException("При сохранении картинки - непредвиденный сбой");
        }
    }

    private String getExtension(MultipartFile file) {
        String fileName = Objects.requireNonNull(file.getOriginalFilename());
        return fileName.substring(fileName.lastIndexOf("."));
    }

    /**
     * Проверка является ли комментарий личным
     */
    private boolean itISUserAd(UserDetails userDetails, AdEntity ad) {
        return Objects.equals(userDetails.getUsername(), ad.getAuthor().getUsername());
    }

    /**
     * Если авторизованный пользователь админ, то он имеет доступ на корректировку любого комментария
     *
     * @param userDetails
     * @return
     */
    private boolean userIsAdmin(UserDetails userDetails) {
        return userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    /**
     * Аккумулированный метод использующий userIsAdmin() и itISUserComment(), и если все плохо кидаем
     * исключение и пишем в лог событие
     *
     * @param userDetails
     * @param ad
     */
    private void checkAuthority(UserDetails userDetails, AdEntity ad) {
        if (!itISUserAd(userDetails, ad) && !userIsAdmin(userDetails)) {
            log.debug("Attempted unauthorized access id ad={}", ad.getPk());
            throw new UnauthorizedException("Attempted unauthorized access");
        }
    }

}
