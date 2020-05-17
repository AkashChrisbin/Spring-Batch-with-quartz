package com.fingress.batch.context;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class AppContext {
	
	  @Autowired
	    private ApplicationContext applicationContext;
	  
	  private static AppContext instance;
	  
	  
	  @PostConstruct 
	    public void init() {
		instance = this;
	    }
	  
	  
	  public static <T> T getBean(Class<T> clazz) {
		  return instance.applicationContext.getBean(clazz);
	  }
}
