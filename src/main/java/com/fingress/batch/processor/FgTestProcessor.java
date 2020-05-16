package com.fingress.batch.processor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
@Component
public class FgTestProcessor implements ItemProcessor<Object,Object>{

	@Override
	public Object process(Object item) throws Exception {
		return item;
	}

}
