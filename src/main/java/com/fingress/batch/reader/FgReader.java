package com.fingress.batch.reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.stereotype.Component;


@Component
public class FgReader implements ItemReader<Object>{

	private int count =0;
	
	@Override
	public Object read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
	    if(count ==0) {
		Map<String,Object> data = new HashMap<>();
		data.put("1", "111");
		count ++;
		List<Map<String,Object>>  object= new ArrayList<>();
		object.add(data);
		return object;
	    }
	    else {
	    	return null;
	    }
	}

}
