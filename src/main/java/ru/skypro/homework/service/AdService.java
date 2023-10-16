package ru.skypro.homework.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.AdDto;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.dto.CreateOrUpdateAdDto;
import ru.skypro.homework.dto.ExtendedAdDto;
import ru.skypro.homework.entities.AdEntity;
import ru.skypro.homework.entities.UserEntity;
import ru.skypro.homework.mappers.AdsMapper;
import ru.skypro.homework.repository.AdsRepository;
import ru.skypro.homework.repository.UsersRepository;

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

    public AdDto createOrUpdateAd(CreateOrUpdateAdDto createOrUpdateAdDto, String urlImage) {
        AdEntity ad = new AdEntity();
        ad = adsMapper.crOrUpdAdDtoToAdEntity(createOrUpdateAdDto);
        adsRepository.save(ad);
        ad.setImage(urlImage);
        return adsMapper.adEntityToAdDto(ad);
    }

    public ExtendedAdDto findInfoAboutAd (Integer id){
        Optional<AdEntity> optionalAd = adsRepository.findById(id.longValue());
        AdEntity ad = optionalAd.get();
        return adsMapper.adEntityToExAdDto(ad);
    }

    public void deleteAdEntity(Integer id) {
        adsRepository.deleteById(id.longValue());
    }

    public AdDto updateAd(Integer id, CreateOrUpdateAdDto updateAdDto) {
        Optional<AdEntity> optionalAd = adsRepository.findById(id.longValue());
        AdEntity ad = optionalAd.get();
        ad = adsMapper.crOrUpdAdDtoToAdEntity(updateAdDto);
        adsRepository.save(ad);
        return adsMapper.adEntityToAdDto(ad);
    }

    public AdsDto findMyAds(Authentication authentication) {
//        String username = authentication.getName();
//        UserEntity user = usersRepository.findByUsername(username).orElse(null);
//        Optional<AdEntity> adsList = adsRepository.findAllByAuthor(user.getId());
//
//        List<AdDto> ads2 = new ArrayList<>();
////        adsList.forEach(adEntity -> ads2.add(adsMapper.adEntityToAdDto(adEntity)));
        AdsDto myAds = new AdsDto();
//        myAds.setResults((ArrayList<AdDto>) ads2);
//        myAds.setCount(ads2.size());
        return myAds;
//
    }
}
