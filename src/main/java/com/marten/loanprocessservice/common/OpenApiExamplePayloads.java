package com.marten.loanprocessservice.common;

/**
 * Holds Swagger example payloads for the /apply endpoint 201 response.
 * Both are valid responses and therefore should be accessible in Swagger.
 */
public final class OpenApiExamplePayloads {

    public static final String APPLICATION_CREATED_IN_REVIEW_JSON = """
            {
              "id": 1,
              "firstName": "Jane",
              "lastName": "Doe",
              "personalCode": "39912310000",
              "status": "IN_REVIEW",
              "rejectionReason": null
            }
            """;

    public static final String APPLICATION_CREATED_AUTO_REJECTED_JSON = """
            {
              "id": 2,
              "firstName": "John",
              "lastName": "Doe",
              "personalCode": "33801010012",
              "status": "REJECTED",
              "rejectionReason": "CUSTOMER_TOO_OLD"
            }
            """;

    private OpenApiExamplePayloads() {
    }
}
