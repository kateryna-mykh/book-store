package lib;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EnumMatchStringValidator implements ConstraintValidator<EnumMatchString, String> {
    private Set<String> values;

    @Override
    public void initialize(EnumMatchString constraintAnnotation) {
        values = Stream.of(constraintAnnotation.enumClass().getEnumConstants()).map(Enum::name)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return values.contains(value);
    }
}
