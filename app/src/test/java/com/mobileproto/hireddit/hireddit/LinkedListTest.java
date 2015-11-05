package com.mobileproto.hireddit.hireddit;

import org.junit.Test;
import java.lang.String;
import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class LinkedListTest {
    @Test
    public void initialize_CorrectValue_ReturnsTrue() {
        final String TEST = "test1";
        LinkedList test_node = new LinkedList(TEST, null);
        assertTrue(test_node.m_value.equals(TEST));
    }

    @Test
    public void add_CorrectNode_ReturnsTrue() {
        final String TEST = "test2";
        LinkedList test_node_head = new LinkedList(TEST, null);
        test_node_head.add(TEST);
        assertTrue(test_node_head.m_next.m_value.equals(TEST));
    }
}