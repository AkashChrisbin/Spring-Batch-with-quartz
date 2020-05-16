package com.fingress.batch.writer;

import java.util.List;
import java.util.Map;

import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
public class FgWriter implements ItemWriter<Object>{

	@SuppressWarnings("rawtypes")
	@Override
	public void write(List<? extends Object> items) throws Exception {
		
		if(items.get(0) instanceof List ) {
           List<?>  listItem = (List<?>) items.get(0);
           listItem.forEach(x->{
        	   if(x instanceof Map) {
        		   System.out.println(((Map) x).get("1"));
        	   }
           });
			
		}
	}

}
