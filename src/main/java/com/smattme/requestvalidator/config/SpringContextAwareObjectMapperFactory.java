package com.smattme.requestvalidator.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringContextAwareObjectMapperFactory implements ApplicationContextAware {

    private static ApplicationContext context;
    private static ObjectMapper objectMapper;
    private static final Logger logger = LoggerFactory.getLogger(SpringContextAwareObjectMapperFactory.class);

    /**
     * This method will first try to find a Spring managed ObjectMapper bean
     * if not found, then it will create a new instance and return it
     * @return ObjectMapper
     */
    public static ObjectMapper getObjectMapper() {

        if(objectMapper != null) return objectMapper;

        objectMapper = getObjectMapperFromSpringContext();
        if(objectMapper == null) objectMapper = createObjectMapper();

        return objectMapper;
    }

    protected static ObjectMapper getObjectMapperFromSpringContext() {
        if(context == null) return null;
        try {
            return context.getBean(ObjectMapper.class);
        } catch (BeansException e) {
          //we're most likely not in a Spring Framework context
          return null;
        }
    }

    protected static ObjectMapper createObjectMapper() {
        return new ObjectMapper();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }
}
