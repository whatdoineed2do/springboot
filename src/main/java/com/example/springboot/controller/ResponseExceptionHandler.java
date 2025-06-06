package com.example.springboot.controller;

// see https://www.baeldung.com/global-error-handler-in-a-spring-rest-api

import com.example.springboot.controller.exception.BarException;
import com.example.springboot.controller.exception.FooException;
import com.example.springboot.controller.exception.SvcException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@ControllerAdvice
public class ResponseExceptionHandler extends ResponseEntityExceptionHandler
{
    // 400
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex,
            final HttpHeaders headers,
            final HttpStatusCode status,
            final WebRequest request)
    {
        log.info("error: " + ex.getClass().getName());
        //
        final List<String> errors = new ArrayList<String>();
        for (final FieldError error : ex.getBindingResult().getFieldErrors())
        {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }
        for (final ObjectError error : ex.getBindingResult().getGlobalErrors())
        {
            errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
        }
        final ApiError apiError = new ApiError(request, HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), errors);
        return handleExceptionInternal(ex, apiError, headers, apiError.getHttpStatus(), request);
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(final TypeMismatchException ex,
            final HttpHeaders headers,
            final HttpStatusCode status,
            final WebRequest request)
    {
        log.info("error: " + ex.getClass().getName());
        //
        final String   error    = ex.getValue() + " value for " + ex.getPropertyName() + " should be of type "
                + ex.getRequiredType();

        final ApiError apiError = new ApiError(request, HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), error);
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getHttpStatus());
    }

    /*
     * @Override protected ResponseEntity<Object>
     * handleMissingServletRequestPart(final MissingServletRequestPartException ex,
     * final HttpHeaders headers, final HttpStatusCode status, final WebRequest
     * request) { log.info("error: " + ex.getClass().getName()); // final String
     * error = ex.getRequestPartName() + " part is missing"; final ApiError apiError
     * = new ApiError(request, HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(),
     * error); return new ResponseEntity<Object>(request, apiError, new
     * HttpHeaders(), apiError.getHttpStatus()); }
     */

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            final MissingServletRequestParameterException ex,
            final HttpHeaders headers,
            final HttpStatusCode status,
            final WebRequest request)
    {
        log.info("error: " + ex.getClass().getName());
        //
        final String   error    = ex.getParameterName() + " parameter is missing";
        final ApiError apiError = new ApiError(request, HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), error);
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getHttpStatus());
    }

    //

    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(final MethodArgumentTypeMismatchException ex,
            final WebRequest request)
    {
        log.info("error: " + ex.getClass().getName());
        //
        final String   error    = ex.getName() + " should be of type " + ex.getRequiredType().getName();

        final ApiError apiError = new ApiError(request, HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), error);
        return new ResponseEntity<Object>(apiError, apiError.getHttpStatus());
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<Object> handleConstraintViolation(final ConstraintViolationException ex,
            final WebRequest request)
    {
        log.info("error: " + ex.getClass().getName());
        //
        final List<String> errors = new ArrayList<String>();
        for (final ConstraintViolation<?> violation : ex.getConstraintViolations())
        {
            errors
                    .add(violation.getRootBeanClass().getName() + " " + violation.getPropertyPath() + ": "
                            + violation.getMessage());
        }

        final ApiError apiError = new ApiError(request, HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), errors);
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getHttpStatus());
    }

    // 404

    // this one doesnt seem to get fired for spring boot 2.6.x
    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(final NoHandlerFoundException ex,
            final HttpHeaders headers,
            final HttpStatusCode status,
            final WebRequest request)
    {
        log.info("error: " + ex.getClass().getName());
        //
        final String   error    = "No handler found for " + ex.getHttpMethod() + " " + ex.getRequestURL();

        final ApiError apiError = new ApiError(request, HttpStatus.NOT_FOUND, ex.getLocalizedMessage(), error);
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getHttpStatus());
    }

    // 405

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            final HttpRequestMethodNotSupportedException ex,
            final HttpHeaders headers,
            final HttpStatusCode status,
            final WebRequest request)
    {
        log.info("error: " + ex.getClass().getName());
        //
        final StringBuilder builder = new StringBuilder();
        builder.append(ex.getMethod());
        builder.append(" method is not supported for this request. Supported methods are ");
        ex.getSupportedHttpMethods().forEach(t -> builder.append(t + " "));

        final ApiError apiError = new ApiError(request, HttpStatus.METHOD_NOT_ALLOWED, ex.getLocalizedMessage(),
                builder.toString());
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getHttpStatus());
    }

    // 415

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(final HttpMediaTypeNotSupportedException ex,
            final HttpHeaders headers,
            final HttpStatusCode status,
            final WebRequest request)
    {
        log.info("error: " + ex.getClass().getName());
        //
        final StringBuilder builder = new StringBuilder();
        builder.append(ex.getContentType());
        builder.append(" media type is not supported. Supported media types are ");
        ex.getSupportedMediaTypes().forEach(t -> builder.append(t + " "));

        final ApiError apiError = new ApiError(request, HttpStatus.UNSUPPORTED_MEDIA_TYPE, ex.getLocalizedMessage(),
                builder.substring(0, builder.length() - 2));
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getHttpStatus());
    }

    @ExceptionHandler({IndexOutOfBoundsException.class})
    public ResponseEntity<Object> handleIdxOoB(final Exception ex, final WebRequest request)
    {
        log.info("error: " + ex.getClass().getName() + " custom handling for IndexOutOfBounds: " + ex.getMessage(), ex);

        final ApiError apiError = new ApiError(request, HttpStatus.I_AM_A_TEAPOT, ex.getLocalizedMessage(),
                ex.getMessage());
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getHttpStatus());
    }

    @ExceptionHandler({UnsupportedOperationException.class, IllegalStateException.class})
    public ResponseEntity<Object> handlePost(final Exception ex, final WebRequest request)
    {
        log.info("error NOT_IMPL from {}", ex.getClass().getName());

        final ApiError apiError = new ApiError(request, HttpStatus.NOT_IMPLEMENTED, ex.getLocalizedMessage(),
                ex.getMessage());
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getHttpStatus());
    }

    // 500

    @ExceptionHandler({ArithmeticException.class})
    public ResponseEntity<Object> handleTransient(final Exception ex, final WebRequest request)
    {
        log.info("temp error {}", ex.getClass().getName(), ex);

        final ApiError apiError = new ApiError(request, HttpStatus.TEMPORARY_REDIRECT, ex.getLocalizedMessage(),
                "temp error");
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getHttpStatus());
    }

    @ExceptionHandler({Exception.class, SvcException.class})
    public ResponseEntity<Object> handleAll(final Exception ex, final WebRequest request)
    {
        log.info("error unhandled exception {}", ex.getClass().getName(), ex);
        //
        final ApiError apiError = new ApiError(request, HttpStatus.INTERNAL_SERVER_ERROR, ex.getLocalizedMessage(),
                "error occurred");
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getHttpStatus());
    }

    private ResponseEntity<Object> returnRespEntity(final SvcException ex, ApiError error)
    {
        return new ResponseEntity<Object>(error, error.getHttpStatus());
    }

    @ExceptionHandler({FooException.class})
    public ResponseEntity<Object> handleFoo(final SvcException ex,
            HttpServletRequest request,
            HandlerMethod handlerMethod)
    {
        log.info("error: {} - {}", request.getRequestURI(), ex.getMessage());
        //
        return returnRespEntity(ex,
                new ApiError(request, HttpStatus.BAD_REQUEST, ex.getMessage(), "Foo Exception -> BAD_REQUEST"));
    }

    @ExceptionHandler({BarException.class})
    public ResponseEntity<Object> handleBar(final SvcException ex, final WebRequest request)
    {
        log.info("error: {} - {} - {}", request.getContextPath(), ex.getClass().getName(), ex);
        //
        return returnRespEntity(ex,
                new ApiError(request, HttpStatus.I_AM_A_TEAPOT, ex.getMessage(), "Bar Exception -> TEAPOT"));
    }
}
