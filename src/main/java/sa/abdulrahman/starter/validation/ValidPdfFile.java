package sa.abdulrahman.starter.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PdfFileValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPdfFile {

    String message() default "Invalid PDF file";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    long maxSize() default 10 * 1024 * 1024; // Default: 10MB
}
