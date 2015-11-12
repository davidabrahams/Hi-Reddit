package com.mobileproto.hireddit.hireddit;

/**
 * A simple LinkedList class implementation
 */
public class LinkedList
{

    public String m_value; // String value inside each node
    public LinkedList m_next; // pointer to the next node

    /**
     * LinkedList constructor creates a LinkedList object by assigning
     * a string and another LinkedList object
     */
    public LinkedList(String value, LinkedList next)
    {
        this.m_value = value;
        this.m_next = next;
    }

    /**
     * LinkedList add() method add a new LinkedList object to the current
     * node by assigning a string to the new node
     */
    public void add(String value)
    {
        this.m_next = new LinkedList(value, null);
    }
}
