package com.sdiawara.voicextt;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import com.sdiawara.voicextt.node.Block;

public class FormItemTest {

	@Test
	public void getName() {
		Block block1 = mock(Block.class);
		block1.idFormItem = 1;
		when(block1.getName()).thenCallRealMethod();
		when(block1.getNodeName()).thenReturn("block");

		Block block2 = mock(Block.class);
		block2.idFormItem = 2;
		when(block2.getName()).thenCallRealMethod();
		when(block2.getNodeName()).thenReturn("block");

		assertEquals("block$1", ((FormItem) block1).getName());
		assertEquals("block$2", ((FormItem) block2).getName());
	}

}
