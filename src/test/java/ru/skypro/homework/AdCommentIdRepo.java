package ru.skypro.homework;

import java.util.HashMap;
import java.util.Map;

/**
 * В тестах невозможно изменять переменный объявленные в классе тестов, они постоянно обнуляются.
 * А хардком их не задашь, ведь id постоянно скачут.
 * А id знать хочется другим тестам.
 * Поэтому была создана обертка Map<>, чтобы служить транспортом данных id объявлений и комментариев в тестах
 */
public class AdCommentIdRepo {

    private static final Map<String,Integer> idRepositories = new HashMap<>();

    {
        idRepositories.put("idAd",null);
        idRepositories.put("idComment",null);
    }

    public void setIdAd(Integer idAd){
        idRepositories.put("idAd",idAd);
    }

    public void setIdComment(Integer idComment){
        idRepositories.put("idComment",idComment);
    }

    public Integer getIdAd(){
        return idRepositories.get("idAd");
    }

    public Integer getIdComment(){
        return idRepositories.get("idComment");
    }

    public void clear(){
        idRepositories.values().removeIf(e->e>0);
    }




}
