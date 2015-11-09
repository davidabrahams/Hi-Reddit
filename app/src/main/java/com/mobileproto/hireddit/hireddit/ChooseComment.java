package com.mobileproto.hireddit.hireddit;

import android.os.Bundle;

import java.util.ArrayList;

/**
 * Created by lwilcox on 11/7/2015.
 */

public class ChooseComment {

    public String pickComment(ArrayList<String> allComments){
        for (int i = 0; i > allComments.size(); i++){
            if (allComments.get(i).length() < 1000) {
                return allComments.get(i);
            }
        }
        return "not short enough";
        //return allComments.get(0);
        //return "testing!!";
    }

}

