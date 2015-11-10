package com.mobileproto.hireddit.hireddit;

import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by lwilcox on 11/7/2015.
 */

public class ChooseComment {

    public String pickComment(ArrayList<String> allComments){
//        for (int i = 0; i > allComments.size(); i++){
//            Integer length = allComments.get(i).length();
//            String teh = length.toString();
//            Log.d("length",teh);
//            if (allComments.get(i).length() < 300) {
//                return allComments.get(i);
//            }
//        }
        return allComments.get(0);
        //return allComments.get(0);
        //return "testing!!";
    }

}

