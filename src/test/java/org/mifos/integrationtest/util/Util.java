package org.mifos.integrationtest.util;

public final class Util {

    private Util() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static String getFormattedEndpoint(String formattedTemplate, String tobeReplaced, String replacedWith) {
        return formattedTemplate.replace(tobeReplaced, replacedWith);
    }
}
