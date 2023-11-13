package ru.practicum.shareit.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = RentDateConstraintValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RentDate {

    String message() default "{Период аренды указан некорректно}";

    Class<?>[] groups() default {};

    Class<? extends Payload> [] payload() default {};
}
