package sa.abdulrahman.starter.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

public class ImageFileValidator implements ConstraintValidator<ValidImageFile, MultipartFile> {

    private long maxSize;

    @Override
    public void initialize(ValidImageFile constraintAnnotation) {
        this.maxSize = constraintAnnotation.maxSize();
    }

    @Override
    public boolean isValid(MultipartFile imageFile, ConstraintValidatorContext context) {
        if (imageFile == null || imageFile.isEmpty()) {
            context.buildConstraintViolationWithTemplate("No image file selected").addConstraintViolation();
            return false;
        }

        // Validate MIME type
        String contentType = imageFile.getContentType();
        if (contentType == null || !(contentType.equalsIgnoreCase("image/png") || contentType.equalsIgnoreCase("image/jpeg"))) {
            context.buildConstraintViolationWithTemplate("Invalid file type. Only PNG and JPG images are allowed").addConstraintViolation();
            return false;
        }

        // Validate file extension
        String originalFilename = imageFile.getOriginalFilename();
        if (originalFilename == null || !originalFilename.matches("^.+\\.(png|jpg|jpeg)$")) {
            context.buildConstraintViolationWithTemplate("Invalid file extension. Only PNG and JPG images are allowed").addConstraintViolation();
            return false;
        }

        // Validate file size
        if (imageFile.getSize() > maxSize) {
            context.buildConstraintViolationWithTemplate("Image size exceeds the maximum limit of " + (maxSize / (1024 * 1024)) + "MB").addConstraintViolation();
            return false;
        }

        // Validate image content (Magic bytes check)
        if (!isValidImageHeader(imageFile)) {
            context.buildConstraintViolationWithTemplate("Invalid image content").addConstraintViolation();
            return false;
        }

        return true; // Validation passed
    }

    private boolean isValidImageHeader(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            byte[] header = new byte[8];
            if (inputStream.read(header) != 8) {
                return false;
            }
            // PNG signature: 89 50 4E 47 0D 0A 1A 0A
            if (header[0] == (byte) 0x89 && header[1] == 0x50 && header[2] == 0x4E && header[3] == 0x47) {
                return true;
            }
            // JPEG signature: FF D8 FF
            return header[0] == (byte) 0xFF && header[1] == (byte) 0xD8 && header[2] == (byte) 0xFF;
        } catch (IOException e) {
            return false;
        }
    }
}
