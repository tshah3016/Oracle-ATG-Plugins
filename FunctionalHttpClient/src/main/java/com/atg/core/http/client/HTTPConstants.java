package com.atg.core.http.client;

/**
 * @author lahlalna
 */
public final class HTTPConstants {

    /**
     * HTTP Method name GET.
     */
    public static final String GET = "GET";

    /**
     * HTTP Method name POST.
     */
    public static final String POST = "POST";

    /**
     * HTTP Method name GET.
     */
    public static final String PUT = "PUT";

    /**
     * HTTP Return code : 200 - ok.
     */
    public static final int OK = 200;

    /**
     * HTTP Return code : 201 - Created.
     */
    public static final int CREATED = 201;

    /**
     * HTTP Return code : 201 - Created.
     */
    public static final int NO_CONTENT = 204;

    /**
     * HTTP Return code : 404 - not found.
     */
    public static final int NOT_FOUND = 404;

    /**
     * HTTP Return code : 403 - forbidden.
     */
    public static final int FORBIDDEN = 403;

    /**
     * HTTP Return code : 401 - unauthorized.
     */
    public static final int UNAUTHORIZED = 401;

    /**
     * HTTP Return code : 400 - bad request.
     */
    public static final int BAD_REQUEST = 400;

    /**
     * HTTP Return code : 500 - Server internal Error.
     */
    public static final int SERVER_INTERNAL_ERROR = 500;

    /**
     * HTTP Protocol http.
     */
    public static final String HTTP = "http";

    /**
     * HTTP Protocol https.
     */
    public static final String HTTPS = "https";

    /**
     * HTTP Protocol custom sfhttps for specifically use TLS1.2.
     */
    public static final String SFHTTPS = "sfhttps";

    /**
     * JSON Content Type (application/json).
     */
    public static final String JSON_CONTENT_TYPE = "application/json";

    /**
     * JSON Content Type (text/plain;charset=utf-8).
     */
    public static final String PLAIN_CONTENT_TYPE = "text/plain;charset=utf-8";

    /**
     * Content Type Header name.
     */
    public static final String CONTENT_TYPE_HEADER_NAME = "Content-type";

    /**
     * Protocol separator.
     */
    public static final String PROTOCOL_SEP = "://";

    /**
     * security token header parameter name.
     */
    public static final String SECURITY_TOKEN_PROTOCOL_HEADER = "Authorization";

    /**
     * Query parameter separator (&).
     */
    public static final String QUERY_PARAMETER_SEPARATOR = "&";

    /**
     * Query key/value separator (=).
     */
    public static final String QUERY_PARAMETER_KEY_VALUE_SEPARATOR = "=";

    /**
     * Query parameter URI separator (?).
     */
    public static final String QUERY_PARAMETER_URI_SEPARATOR = "?";

    /**
     * HTTP Authorization Header separator (:).
     */
    public static final String AUTH_HEADER_SEPARATOR = ":";

    /**
     * Character encoding type (UTF-8).
     */
    public static final String UTF8 = "UTF-8";

    /**
     * HTTP authorization type (Basic).
     */
    public static final String BASIC_AUTH = "Basic";
    public static final String XML_CONTENT_TYPE = "application/xml";

    /**
     * Default constructor.
     */
    private HTTPConstants() {
        // default constructor for utility classes must be private.
    }

}
