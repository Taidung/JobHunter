package vn.taidung.jobhunter.util.error;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import vn.taidung.jobhunter.domain.response.RestResponse;

@RestControllerAdvice
public class GlobalException {

    // handle all exception
    @ExceptionHandler(Exception.class)
    public ResponseEntity<RestResponse<Object>> handleAllException(Exception ex) {
        RestResponse<Object> res = new RestResponse<Object>();
        res.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        res.setMessage(ex.getMessage());
        res.setError("Internal Server Error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
    }

    @ExceptionHandler({
            UsernameNotFoundException.class,
            BadCredentialsException.class,
            ResourceNotFoundException.class })
    public ResponseEntity<RestResponse<Object>> handleException(Exception ex) {
        RestResponse<Object> rest = new RestResponse<>();
        rest.setStatusCode(HttpStatus.BAD_REQUEST.value());
        rest.setError("Exception occurs...");
        rest.setMessage(ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rest);
    }

    @ExceptionHandler(value = {
            NoResourceFoundException.class
    })
    public ResponseEntity<RestResponse<Object>> handleNotFoundException(Exception ex) {
        RestResponse<Object> res = new RestResponse<Object>();
        res.setStatusCode(HttpStatus.NOT_FOUND.value());
        res.setError("404 Not Found. URL may not exist...");
        res.setMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RestResponse<Object>> handleValidation(MethodArgumentNotValidException ex) {
        RestResponse<Object> rest = new RestResponse<>();
        rest.setStatusCode(HttpStatus.BAD_REQUEST.value());

        List<String> errors = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }

        rest.setError(ex.getBody().getDetail());
        rest.setMessage(errors.size() > 1 ? errors : errors.get(0).toString());

        return ResponseEntity.badRequest().body(rest);
    }

    @ExceptionHandler({
            StorageException.class })
    public ResponseEntity<RestResponse<Object>> handleFileUploadException(Exception ex) {
        RestResponse<Object> rest = new RestResponse<>();
        rest.setStatusCode(HttpStatus.BAD_REQUEST.value());

        rest.setError("Exception upload file...");
        rest.setMessage(ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rest);
    }

    @ExceptionHandler({
            PermissionException.class })
    public ResponseEntity<RestResponse<Object>> handlePermissionException(Exception ex) {
        RestResponse<Object> rest = new RestResponse<>();
        rest.setStatusCode(HttpStatus.FORBIDDEN.value());
        rest.setError("Forbitdden");
        rest.setMessage(ex.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(rest);
    }
}
