package hu.kristof.nagy.hikebookserver.api;

import hu.kristof.nagy.hikebookserver.model.ResponseResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class IllegalArgumentAdvice {

    @ResponseBody
    @ExceptionHandler(IllegalArgumentException.class)
    ResponseResult<?> illegalArgumentHandler(IllegalArgumentException ex) {
        return ResponseResult.fail(ex.getMessage());
    }
}
