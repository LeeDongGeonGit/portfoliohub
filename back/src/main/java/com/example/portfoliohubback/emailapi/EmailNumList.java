package com.example.portfoliohubback.emailapi;
import java.time.LocalDateTime;
import java.util.*;
public  class EmailNumList {
    public static List<EmailNumEntity> list = new ArrayList<EmailNumEntity>();
    public static void addNum(String email,String num){
        expiration();
        EmailNumEntity entity = new EmailNumEntity(email, num);
        list.add(entity);
    }
    public static boolean sameNum(String email,String num){
        expiration();
        for (EmailNumEntity item : list) {
            if (item.getEmail().equals(email) && item.getNum().equals(num)) {
                // 일치하는 값이 있을 때 해당 요소를 삭제
                list.remove(item);
                return true;
            }
        }
        return false;

    }
    public static boolean sameNumNoDelete(String email,String num){
        expiration();
        for (EmailNumEntity item : list) {
            if (item.getEmail().equals(email) && item.getNum().equals(num)) {
                return true;
            }
        }
        return false;

    }
    public static void expiration(){
        for (EmailNumEntity item : list) {
            if (item.getEndTime().isBefore(LocalDateTime.now())) {
                list.remove(item);
            }
        }
    }

}
