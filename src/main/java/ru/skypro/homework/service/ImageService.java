package ru.skypro.homework.service;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.image.BufferedImage;
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

    private final ImageRepository imageRepository;
    private final ImageMapper imageMapper;
    private final AdsRepository adsRepository;
    private final UsersRepository usersRepository;

    /**
     * Обновляем уже существующую картинку объявления
     * @param idAd   объявления
     * @param file на обновление
     * @return
     */
    public byte[] updateImageAd(Integer idAd, MultipartFile file, UserDetails userDetails) throws IOException {
        if (file == null) {
            file = new JavaFileToMultipartFile(new File("src/main/resources/image/test.jpg"));
        }
        ImageEntity imageEntity;
        ByteArrayResource resource;
        Optional<AdEntity> ad = adsRepository.findById(idAd);
        if (ad.isPresent()) {
            checkAuthority(userDetails, ad.get());
            Optional<ImageEntity> optionalImageEntity = Optional.ofNullable(ad.get().getImageEntity());

            if (optionalImageEntity.isPresent()) {
                imageEntity = optionalImageEntity.get();
                Files.deleteIfExists(Path.of(imageEntity.getPathHardStore()));
                imageEntity=saveImage(file, imageEntity);
                resource = new ByteArrayResource(Files.readAllBytes(Path.of(imageEntity.getPathHardStore())));
                return resource.getByteArray();
            } else {
                imageEntity = imageRepository.save(new ImageEntity());
                imageEntity.setMediaType(file.getContentType());
                imageEntity=saveImage(file, imageEntity);
                ad.get().setImageEntity(imageEntity);
                adsRepository.save(ad.get());
                resource = new ByteArrayResource(Files.readAllBytes(Path.of(imageEntity.getPathHardStore())));
                return resource.getByteArray();
            }

        }
        log.debug("Ad with id {}, not found", idAd);
        throw new NoAdException("Ad with id =" + idAd + "not found");

    }

    /**
     * Обновление аватара пользователя
     * @param username
     * @param file
     * @throws Exception
     */
    public void updateImageUser(String username, MultipartFile file) throws Exception {
        if (file == null) {
            file = new JavaFileToMultipartFile(new File("src/main/resources/image/test.jpg"));
        }
        ImageEntity imageEntity;
        UserEntity userEntity = usersRepository.findByUsername(username).get();
        if (userEntity.getImageEntity() == null) {
            imageEntity = imageRepository.save(new ImageEntity());
            imageEntity.setMediaType(file.getContentType());
            imageEntity=saveImage(file, imageEntity);
            userEntity.setImageEntity(imageEntity);
            usersRepository.save(userEntity);
            return;
        }
        imageEntity = userEntity.getImageEntity();
        Files.deleteIfExists(Path.of(imageEntity.getPathHardStore()));
        saveImage(file, imageEntity);
    }

    /**
     * Загрузка картинки на сервер
     * @param imageEntity - сущность из базы. Она приходит сюда пустой. Заполняться
     *                    будет в методе {@link ImageService#saveImage(MultipartFile, ImageEntity)}
     * @param file - входящий на контроллер файл
     * @return
     * @throws IOException
     */
    public File loadImageToHard(ImageEntity imageEntity, MultipartFile file) throws IOException {
        String extension = getExtension(file);
        imageEntity.setExtension(extension);
//        if (!ViewSelect.onlyImage(extension)) {
//            log.warn("Format " + extension + "not supported in ViewSelect.class");
//            throw new IllegalFormatContentException(extension);
//        }
        Path pathFile = Path.of(sourceSaveToHard, imageEntity.getId() + extension);
        Files.createDirectories(pathFile.getParent());
        log.debug("Path for save Image = " + pathFile);
        File saveImageTo;
        BufferedImage bufferedImage;
        try(InputStream is=file.getInputStream();
            BufferedInputStream bis=new BufferedInputStream(is,4200);
            OutputStream out = Files.newOutputStream(pathFile,CREATE_NEW);
            BufferedOutputStream bout = new BufferedOutputStream(out, 4200);
                ) {
            saveImageTo = new File(String.valueOf(pathFile));
            if (file.getSize() > 100288) {
                bufferedImage = ImageIO.read(bis);
                bufferedImage = simpleResizeImage(bufferedImage, 600);
                ImageIO.write(bufferedImage, extension.substring(1), saveImageTo);
                bufferedImage.flush();
            } else {
                bis.transferTo(bout);
            }

        } catch (Exception e) {
            throw new RuntimeException("Что-то пошло не так при сохранении файла");
        }
        return saveImageTo;
    }

    private ImageEntity saveImage(MultipartFile file, ImageEntity imageEntity) {
        try {
            File fileSaved = loadImageToHard(imageEntity, file);
            imageMapper.updateImageEntityFromFile(fileSaved, imageEntity);
            return imageRepository.save(imageEntity);
        } catch (IOException e) {
            log.debug("Сбой сохранения картинки  id={}", imageEntity.getId());
            throw new RuntimeException("При сохранении картинки - непредвиденный сбой");
        }
    }

    /**
     * Вовзращет расширение файла с точкой
     * Пример: .jpg
     * @param file
     * @return
     */
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

    /**
     * Уменьшение картинки при помощи библиоткеи ImgScalar
     * @param originalImage - картинка оригинальная
     * @param targetWidth - изменение ширины, высота уменьшаеться по пропорциям
     * @return
     */
    private BufferedImage simpleResizeImage(BufferedImage originalImage, int targetWidth){
        return Scalr.resize(originalImage, targetWidth);
    }

}
