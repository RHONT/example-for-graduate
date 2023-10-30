package ru.skypro.homework.supportclasses;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AdCommentIdRepoTest {

    @Test
    void test(){
        AdCommentIdRepo repo=new AdCommentIdRepo();
        repo.setIdAd(1);
        repo.setIdComment(2);
        assertEquals(1,repo.getIdAd());
        assertEquals(2,repo.getIdComment());
        repo.clear();
        assertNull(repo.getIdAd());
        assertNull(repo.getIdComment());

    }

}