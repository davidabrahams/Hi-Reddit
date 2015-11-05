package com.mobileproto.hireddit.hireddit;

/**
 * Created by yhuang on 11/5/2015.
 */
public class LinkedList {

    public String m_value;
    public LinkedList m_next;

    public LinkedList(String value, LinkedList next) {
        this.m_value = value;
        this.m_next = next;
    }

    public void add(String value) {
        this.m_next = new LinkedList(value, null);
    }
}
