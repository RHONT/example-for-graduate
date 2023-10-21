package ru.skypro.homework.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.AdDto;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.dto.CreateOrUpdateAdDto;
import ru.skypro.homework.dto.ExtendedAdDto;
import ru.skypro.homework.entities.AdEntity;
import ru.skypro.homework.entities.ImageEntity;
import ru.skypro.homework.entities.UserEntity;
import ru.skypro.homework.exceptions.AdNotDeletedException;
import ru.skypro.homework.exceptions.NoAdException;
import ru.skypro.homework.mappers.AdsMapper;
import ru.skypro.homework.repository.AdsRepository;
import ru.skypro.homework.repository.ImageRepository;
import ru.skypro.homework.repository.UsersRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
     * @param properties  - данные о товаре. DTO CreateOrUpdateAdDto - title, price, decsription
     * @param file - загружаемая картинка товара
     * @param userDetails - данные о том, куда класть объявления берутся из spring security
     * @return - Возвращаем DTO AdDto
     * @throws IOException
     */
    public AdDto adAd(CreateOrUpdateAdDto createOrUpdateAdDto,
                      MultipartFile file,
                      UserDetails userDetails) throws IOException {

        Optional<UserEntity> user = usersRepository.findByUsername(userDetails.getUsername());
        ImageEntity image = imageService.goImageToBD(file);
        AdEntity ad = adsMapper.updateAdDtoToAdEntity(createOrUpdateAdDto);
        ad.setImageEntity(image);
        ad.setAuthor(user.get());
        adsRepository.save(ad);

        return adsMapper.adEntityToAdDto(ad);
    }

    /**
     * Получение информации об объявлении
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
     * @param id
     */
    public void deleteAdEntity(Integer id) {
        Optional<AdEntity> ad=adsRepository.findById(id);
        if (ad.isPresent()) {

            adsRepository.deleteById(id);
            Optional<AdEntity> checkAd=adsRepository.findById(id);

            if (checkAd.isEmpty()) {
                log.info("Ad with id={}, successfully deleted", id);
            } else {
                log.debug("Ad with id={}, cannot be deleted", id);
                throw new AdNotDeletedException("Не удается удалить объявление");
            }
        } else throw new NoAdException("Ad with id =" + id + "not found");


    }

    public AdDto updateAd(Integer id, CreateOrUpdateAdDto updateAdDto) {
        Optional<AdEntity> optionalAd = adsRepository.findById(id);
        AdEntity ad = optionalAd.get();
        ad = adsMapper.updateAdDtoToAdEntity(updateAdDto);
        adsRepository.save(ad);
        return adsMapper.adEntityToAdDto(ad);
    }

    public AdsDto findMyAds(Authentication authentication) {
        String username = authentication.getName();
        UserEntity user = usersRepository.findByUsername(username).orElse(null);
        ArrayList<AdEntity> myAds = adsRepository.findAdEntitiesByAuthor(user.getId().intValue());
        ArrayList<AdDto> myTempAdsDto = new ArrayList<>();
        myAds.forEach(adEntity -> {
            AdDto adDto = adsMapper.adEntityToAdDto(adEntity);
            myTempAdsDto.add(adDto);
        });
        AdsDto myAdsDto = new AdsDto();
        myAdsDto.setResults(myTempAdsDto);
        myAdsDto.setCount(myTempAdsDto.size());
        return myAdsDto;

    }

    /**
     * Возвращаем все объявления, что есть в базе
     * @return
     */
    public AdsDto findAllAds() {
        ArrayList<AdEntity> ads = adsRepository.findAll();
        List<AdDto> ads2 = adsMapper.ListAdToListDto(ads);
        AdsDto adsDto = new AdsDto();
        adsDto.setResults((ArrayList<AdDto>) ads2);
        adsDto.setCount(ads2.size());
        return adsDto;
    }

}
