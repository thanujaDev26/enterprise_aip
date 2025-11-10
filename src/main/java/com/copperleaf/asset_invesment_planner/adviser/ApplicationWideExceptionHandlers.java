package com.copperleaf.asset_invesment_planner.adviser;

import com.copperleaf.asset_invesment_planner.exception.AssetNotFoundException;
import com.copperleaf.asset_invesment_planner.exception.AuthenticationException;
import com.copperleaf.asset_invesment_planner.exception.ProjectNotFoundException;
import com.copperleaf.asset_invesment_planner.util.StandardResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApplicationWideExceptionHandlers {

    @ExceptionHandler(AssetNotFoundException.class)
    public ResponseEntity<StandardResponse> assetNotFoundExceptionHandler(RuntimeException ex){
        return new ResponseEntity<>(
                new StandardResponse(
                        404, ex.getMessage(), null
                ), HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(ProjectNotFoundException.class)
    public ResponseEntity<StandardResponse> projectNotFoundExceptionHandler(RuntimeException ex){
        return new ResponseEntity<>(
                new StandardResponse(
                        404, ex.getMessage(), null
                ), HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<StandardResponse> authenticationExceptionHandler(RuntimeException ex){
        return new ResponseEntity<>(
                new StandardResponse(
                        403, ex.getMessage(), null
                ),HttpStatus.FORBIDDEN
        );
    }
}
