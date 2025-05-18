package com.hogent.ewdj.itconference.config; // Pas dit package aan

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import validator.BeamerCheckValidator; // Importeer je validators
import validator.ConferenceDateValidator;
import validator.EventConstraintsValidator;
import validator.SpeakerListValidator;

@Configuration
public class ValidationConfig {

    // Definieer je custom validators als Beans
    @Bean
    public BeamerCheckValidator beamerCheckValidator() {
        return new BeamerCheckValidator();
    }

    @Bean
    public ConferenceDateValidator conferenceDateValidator() {
        return new ConferenceDateValidator();
    }

    @Bean
    public EventConstraintsValidator eventConstraintsValidator() {
        return new EventConstraintsValidator();
    }

    @Bean
    public SpeakerListValidator speakerListValidator() {
        return new SpeakerListValidator();
    }

    // Definieer de globale Validator bean en configureer deze
    @Bean
    public jakarta.validation.Validator validator() {
        LocalValidatorFactoryBean factory = new LocalValidatorFactoryBean();
        // Spring zal nu automatisch de custom validator beans die hierboven zijn gedefinieerd
        // en die @Autowired dependencies hebben, correct bedraden (injecteren).
        // De @Component op de validators is nog steeds nodig zodat Spring ze vindt voor de @Bean methodes hier.
        return factory;
    }
}