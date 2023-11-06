package ru.skypro.homework.secutity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import ru.skypro.homework.dto.CreateOrUpdateAdDto;
import ru.skypro.homework.dto.UserMinimalDataDto;
import ru.skypro.homework.entities.AdEntity;
import ru.skypro.homework.entities.ImageEntity;
import ru.skypro.homework.entities.UserEntity;
import ru.skypro.homework.repository.AdsRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class CustomMethodSecurityExpressionRoot
        extends SecurityExpressionRoot implements MethodSecurityExpressionOperations {

    private Object filterObject;
    private Object returnObject;

    public CustomMethodSecurityExpressionRoot(Authentication authentication) {
        super(authentication);
    }

    public boolean isMasterAd(Integer idAd, CreateOrUpdateAdDto createOrUpdateAdDto, UserDetails userDetails) {
        UserMinimalDataDto user = ((MyPrincipal) this.getPrincipal()).getUserMinimalDataDto();
        return user.getAds().contains(idAd);
    }

    public boolean isMasterAd(Integer idAd) {
        UserMinimalDataDto user = ((MyPrincipal) this.getPrincipal()).getUserMinimalDataDto();
        return user.getAds().contains(idAd);
    }

    public boolean isMasterComment(Integer idComment) {
        UserMinimalDataDto user = ((MyPrincipal) this.getPrincipal()).getUserMinimalDataDto();
        return user.getComments().contains(idComment);
    }

    @Override
    public Object getFilterObject() {
        return this.filterObject;
    }

    @Override
    public Object getReturnObject() {
        return this.returnObject;
    }

    @Override
    public Object getThis() {
        return this;
    }

    @Override
    public void setFilterObject(Object obj) {
        this.filterObject = obj;
    }

    @Override
    public void setReturnObject(Object obj) {
        this.returnObject = obj;
    }
}
