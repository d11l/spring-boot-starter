package sa.abdulrahman.starter.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class PdfFileValidator implements ConstraintValidator<ValidPdfFile, MultipartFile> {

    private long maxSize;

    @Override
    public void initialize(ValidPdfFile constraintAnnotation) {
        this.maxSize = constraintAnnotation.maxSize();
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("File is required").addConstraintViolation();
            return false;
        }

        if (!"application/pdf".equalsIgnoreCase(file.getContentType())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Only PDF files are allowed").addConstraintViolation();
            return false;
        }

        if (file.getSize() > maxSize) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("PDF size exceeds the maximum limit of " + (maxSize / (1024 * 1024)) + "MB").addConstraintViolation();
            return false;
        }

        return true;
    }
}
