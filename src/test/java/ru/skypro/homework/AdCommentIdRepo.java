package ru.skypro.homework;

import java.util.HashMap;
import java.util.Map;

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
