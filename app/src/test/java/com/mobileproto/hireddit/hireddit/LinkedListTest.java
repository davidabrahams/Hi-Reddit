package com.mobileproto.hireddit.hireddit;

import com.mobileproto.hireddit.hireddit.LinkedList;

import org.junit.Test;

import java.lang.String;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class LinkedListTest {
    @Test
    public void initialize_CorrectValue_ReturnsTrue() {
        final String TEST = "test";
        LinkedList test_node = new LinkedList(TEST, null);
        assertTrue(test_node.m_value.equals(TEST));
    }

    @Test
    public void add_CorrectNode_ReturnsTrue() {
        
    }
}