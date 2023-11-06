package ru.skypro.homework.secutity;

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;

public class CustomPermissionEvaluator implements PermissionEvaluator {
    @Override
    public boolean hasPermission(
            Authentication auth, Object targetDomainObject, Object permission) {
        if ((auth == null) || (targetDomainObject == null) || !(permission instanceof String)){
            return false;
        }
        Integer id = (Integer)targetDomainObject;
        String typeData=(String) permission;

        return hasPrivilege(auth, id, typeData);
    }
    @Override
    public boolean hasPermission(
            Authentication auth, Serializable targetId, String targetType, Object permission) {
        return false;
    }
    private boolean hasPrivilege(Authentication auth, Integer id, String typeData) {
        MyPrincipal principal = (MyPrincipal)auth.getPrincipal();
        if (typeData.equals("ad")) {
            return principal.getAdsId().contains(id);
        }
        if (typeData.equals("comment")) {
            return principal.getCommentId().contains(id);
        }
        return false;
    }

}
