package ru.skypro.homework.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.AdDto;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.dto.CreateOrUpdateAdDto;
import ru.skypro.homework.dto.ExtendedAdDto;
import ru.skypro.homework.entities.AdEntity;
import ru.skypro.homework.entities.CommentEntity;
import ru.skypro.homework.entities.ImageEntity;
import ru.skypro.homework.entities.UserEntity;
import ru.skypro.homework.exceptions.AdNotDeletedException;
import ru.skypro.homework.exceptions.NoAdException;
import ru.skypro.homework.exceptions.UnauthorizedException;
import ru.skypro.homework.mappers.AdsMapper;
import ru.skypro.homework.repository.AdsRepository;
import ru.skypro.homework.repository.ImageRepository;
import ru.skypro.homework.repository.UsersRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdService {
    private final AdsRepository adsRepository;
    private final AdsMapper adsMapper;
    private final UsersRepository usersRepository;
    private final ImageService imageService;
    private final ImageRepository imageRepository;

    /**
     * @param createOrUpdateAdDto - данные о товаре. title, price, decsription
     * @param file                - загружаемая картинка товара
     * @param userDetails         - данные о том, куда класть объявления берутся из spring security
     * @return - Возвращаем DTO AdDto
     * @throws IOException
     */
    @Transactional
    public AdDto adAd(CreateOrUpdateAdDto createOrUpdateAdDto,
                      MultipartFile file,
                      UserDetails userDetails) throws IOException {

        Optional<UserEntity> user = usersRepository.findByUsername(userDetails.getUsername());
        ImageEntity image = imageService.createImageEntity(file);
        AdEntity ad = new AdEntity();
        adsMapper.updateAdDtoToAdEntity(createOrUpdateAdDto, ad);
        ad.setImageEntity(image);
        ad.setAuthor(user.get());
        adsRepository.save(ad);
        image.setFilePath(image.getFilePath() + ad.getImageEntity().getId().toString());
        image.setPathHardStore(image.getPathHardStore() + image.getId()+ image.getExtension());
        imageRepository.save(image);
        imageService.loadImageToHard(image.getId(),file);
        return adsMapper.adEntityToAdDto(ad);
    }

    /**
     * Получение информации об объявлении
     *
     * @param id - идентификатор объявления
     * @return
     */
    public ExtendedAdDto findInfoAboutAd(Integer id) {
        Optional<AdEntity> optionalAd = adsRepository.findById(id);
        if (optionalAd.isPresent()) {
            return adsMapper.adEntityToExAdDto(optionalAd.get());
        } else {
            log.debug("Ad with id = {} not found", id);
            throw new NoAdException("Ad with id =" + id + "not found");
        }
    }

    /**
     * Удаляем выбранное объявление
     *
     * @param id
     */
    public void deleteAdEntity(Integer id,UserDetails userDetails) {
        Optional<AdEntity> optionalAd = adsRepository.findById(id);
        if (optionalAd.isPresent()) {
            checkAuthority(userDetails,optionalAd.get());
            adsRepository.deleteById(id);
            Optional<AdEntity> checkAd = adsRepository.findById(id);

            if (checkAd.isEmpty()) {
                log.info("Ad with id={}, successfully deleted", id);
            } else {
                log.debug("Ad with id={}, cannot be deleted", id);
                throw new AdNotDeletedException("Не удается удалить объявление");
            }
        } else throw new NoAdException("Ad with id =" + id + "not found");


    }

    /**
     * Обновляем информацию об объявлении
     *
     * @param id          - идентификатор объявления
     * @param updateAdDto
     * @return
     */
    public AdDto updateAd(Integer id, CreateOrUpdateAdDto updateAdDto,UserDetails userDetails) {
        Optional<AdEntity> optionalAd = adsRepository.findById(id);
        if (optionalAd.isPresent()) {
            checkAuthority(userDetails,optionalAd.get());
            adsMapper.updateAdDtoToAdEntity(updateAdDto, optionalAd.get());
            adsRepository.save(optionalAd.get());
            return adsMapper.adEntityToAdDto(optionalAd.get());
        } else {
            log.debug("Ad with id={}, cannot be deleted", id);
            throw new AdNotDeletedException("Не удается удалить объявление");
        }
    }

    /**
     * Находим объявления авторизованного пользователя
     *
     * @param userDetails
     * @return
     */
    @Transactional

    public AdsDto findMyAds(UserDetails userDetails) {
        UserEntity user = usersRepository.findByUsername(userDetails.getUsername()).get();
        List<AdDto> listAdsDto = adsMapper.ListAdToListDto(user.getAdEntityList());
        AdsDto myAdsDto = new AdsDto();
        myAdsDto.setResults((ArrayList<AdDto>) listAdsDto);
        myAdsDto.setCount(listAdsDto.size());
        return myAdsDto;
    }

    /**
     * Возвращаем все объявления, что есть в базе
     *
     * @return
     */
    public AdsDto findAllAds() {
        ArrayList<AdEntity> listAds = adsRepository.findAll();
        List<AdDto> listAdsDto = adsMapper.ListAdToListDto(listAds);
        AdsDto adsDto = new AdsDto();
        adsDto.setResults((ArrayList<AdDto>) listAdsDto);
        adsDto.setCount(listAdsDto.size());
        return adsDto;
    }


    /**
     * Проверка является ли комментарий личным
     */
    private boolean itISUserAd(UserDetails userDetails, AdEntity ad) {
        return Objects.equals(userDetails.getUsername(), ad.getAuthor().getUsername());
    }

    /**
     * Если авторизованный пользователь админ, то он имеет доступ на корректировку любого комментария
     * @param userDetails
     * @return
     */
    private boolean userIsAdmin(UserDetails userDetails) {
        return userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    /**
     * Аккумулированный метод использующий userIsAdmin() и itISUserComment(), и если все плохо кидаем
     * исключение и пишем в лог событие
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
