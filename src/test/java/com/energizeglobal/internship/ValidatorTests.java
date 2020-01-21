package com.energizeglobal.internship;

import com.energizeglobal.internship.model.LoginRequest;
import org.junit.Assert;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

public class ValidatorTests {
    @Test
    public void test_LoginRequestTest_success() {
        final LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(null);
        loginRequest.setPassword(null);
        final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        final Validator validator = validatorFactory.getValidator();
        final Set<ConstraintViolation<LoginRequest>> validated = validator.validate(loginRequest);
        for (ConstraintViolation<LoginRequest> cv : validated) {
            System.out.println(cv.getPropertyPath() + ": " + cv.getMessage());
        }
        Assert.assertFalse(validated.isEmpty());
    }
}
