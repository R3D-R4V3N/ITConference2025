package com.hogent.ewdj.itconference.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import validator.BeamerCheckValidator;
import validator.ConferenceDateValidator;
import validator.EventConstraintsValidator;
import validator.SpeakerListValidator;

@Configuration // definieert validator beans
public class ValidationConfig {

    @Bean
    public BeamerCheckValidator beamerCheckValidator() {
        return new BeamerCheckValidator();
    }

    @Bean
    public ConferenceDateValidator conferenceDateValidator(MessageSource messageSource) {
        ConferenceDateValidator validator = new ConferenceDateValidator();
        validator.setMessageSource(messageSource);
        return validator;
    }

    @Bean
    public EventConstraintsValidator eventConstraintsValidator() {
        return new EventConstraintsValidator();
    }

    @Bean
    public SpeakerListValidator speakerListValidator() {
        return new SpeakerListValidator();
    }

    @Bean
    public jakarta.validation.Validator validator(MessageSource messageSource) {
        LocalValidatorFactoryBean factory = new LocalValidatorFactoryBean();
        factory.setValidationMessageSource(messageSource);
        return factory;
    }
}