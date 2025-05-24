package utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Component
public class DateFormatter implements Formatter<LocalDateTime> {

    private final MessageSource messageSource;

    @Autowired
    public DateFormatter(MessageSource messageSource) {
        super();
        this.messageSource = messageSource;
    }

    @Override
    public String print(LocalDateTime object, Locale locale) {
        return object.format(formatter(locale));
    }

    @Override
    public LocalDateTime parse(String text, Locale locale) throws ParseException {
        return LocalDateTime.parse(text, formatter(locale));
    }

    private DateTimeFormatter formatter(Locale locale) {
        return DateTimeFormatter.ofPattern(messageSource.getMessage("datetime.format.pattern", null, locale), locale);
    }
}