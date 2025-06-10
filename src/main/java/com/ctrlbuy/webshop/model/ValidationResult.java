package com.ctrlbuy.webshop.model;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * Resultat från validering av betalningsinformation
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ValidationResult {
    private boolean valid;
    private List<String> errors;
    private List<String> warnings;
    private String errorCode;
    private String errorMessage;

    // Builder-kompatibel konstruktor
    public ValidationResult(boolean valid) {
        this();
        this.valid = valid;
    }

    // Säkerställ att listor inte är null
    public List<String> getErrors() {
        if (errors == null) {
            errors = new ArrayList<>();
        }
        return errors;
    }

    public List<String> getWarnings() {
        if (warnings == null) {
            warnings = new ArrayList<>();
        }
        return warnings;
    }

    // Utility methods
    public void addError(String error) {
        getErrors().add(error);
        this.valid = false;
    }

    public void addWarning(String warning) {
        getWarnings().add(warning);
    }

    public boolean hasErrors() {
        return getErrors() != null && !getErrors().isEmpty();
    }

    public boolean hasWarnings() {
        return getWarnings() != null && !getWarnings().isEmpty();
    }

    public String getFirstError() {
        List<String> errorList = getErrors();
        return errorList.isEmpty() ? null : errorList.get(0);
    }

    public static ValidationResult success() {
        return ValidationResult.builder()
                .valid(true)
                .errors(new ArrayList<>())
                .warnings(new ArrayList<>())
                .build();
    }

    public static ValidationResult failure(String error) {
        ValidationResult result = ValidationResult.builder()
                .valid(false)
                .errors(new ArrayList<>())
                .warnings(new ArrayList<>())
                .build();
        result.addError(error);
        return result;
    }
}