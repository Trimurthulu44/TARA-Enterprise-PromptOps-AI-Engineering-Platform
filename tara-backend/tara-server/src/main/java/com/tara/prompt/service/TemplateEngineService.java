package com.tara.prompt.service;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TemplateEngineService {

    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{\\{\\s*([a-zA-Z0-9_]+)\\s*\\}\\}");

    /**
     * Extracts all unique variable names declared in the template (e.g. {{variable_name}}).
     */
    public Set<String> extractVariables(String template) {
        if (template == null || template.isBlank()) {
            return Collections.emptySet();
        }
        Set<String> variables = new LinkedHashSet<>();
        Matcher matcher = VARIABLE_PATTERN.matcher(template);
        while (matcher.find()) {
            variables.add(matcher.group(1));
        }
        return variables;
    }

    /**
     * Renders the template by replacing {{variable}} placeholders with supplied values.
     */
    public String render(String template, Map<String, Object> variables) {
        if (template == null) {
            return "";
        }
        if (variables == null || variables.isEmpty()) {
            return template;
        }

        StringBuilder result = new StringBuilder();
        Matcher matcher = VARIABLE_PATTERN.matcher(template);
        int lastEnd = 0;

        while (matcher.find()) {
            result.append(template, lastEnd, matcher.start());
            String varName = matcher.group(1);
            Object value = variables.get(varName);
            if (value != null) {
                result.append(value.toString());
            } else {
                // Keep the placeholder if no replacement provided
                result.append(matcher.group(0));
            }
            lastEnd = matcher.end();
        }
        result.append(template.substring(lastEnd));
        return result.toString();
    }

    /**
     * Estimates token count using standard BPE/tiktoken approximation (~4 characters per token).
     */
    public int estimateTokens(String text) {
        if (text == null || text.isBlank()) {
            return 0;
        }
        // English words average ~4.5 characters; punctuation/spaces take tokens.
        // Math.max(1, (int) Math.ceil(text.length() / 3.8)) provides highly reliable estimate.
        return Math.max(1, (int) Math.ceil(text.trim().length() / 3.8));
    }
}
