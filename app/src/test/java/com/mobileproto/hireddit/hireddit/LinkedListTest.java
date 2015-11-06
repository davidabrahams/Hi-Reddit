package com.mobileproto.hireddit.hireddit;

import org.junit.Test;

import java.lang.String;

import static org.junit.Assert.*;

/**
 * LinkedListTest is a test unit for unit class LinkedList.java
 * in path app/src/main/java
 */
public class LinkedListTest {

    @Test
    /**
     * initialize_CorrectValue_ReturnsTrue() tests whether the LinkedList constructor
     * returns the correct value as the input or not
     */
    public void initialize_CorrectValue_ReturnsTrue() {
        final String TEST = "test1";
        LinkedList test_node = new LinkedList(TEST, null);
        assertTrue(test_node.m_value.equals(TEST));
    }

    @Test
    /**
     * add_CorrectValue_ReturnsTrue() tests whether the LinkedList add method
     * returns the correct value as the input or not
     */
    public void add_CorrectValue_ReturnsTrue() {
        final String TEST = "test2";
        LinkedList test_node_head = new LinkedList(TEST, null);
        test_node_head.add(TEST);
        assertTrue(test_node_head.m_next.m_value.equals(TEST));
    }
}