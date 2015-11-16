package com.mobileproto.hireddit.hireddit;

import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by lwilcox on 11/7/2015.
 */

public class ChooseComment {

    public String pickComment(ArrayList<String> allComments){
        for (int i = 0; i < allComments.size(); i++) {
            if (allComments.get(i).length() < 200) {
                if (!allComments.get(i).toLowerCase().contains("http") && !allComments.get(i).toLowerCase().contains("?")) {
                    return allComments.get(i);
                }
            }
        }
        return allComments.get(0);
    }

}

