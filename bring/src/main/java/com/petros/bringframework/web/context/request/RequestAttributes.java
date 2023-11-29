package com.petros.bringframework.web.context.request;

import javax.annotation.Nullable;

/**
 * Abstraction for accessing attribute objects associated with a request.
 * Supports access to request-scoped attributes as well as to session-scoped
 * attributes, with the optional notion of a "global session".
 *
 * @author Viktor Basanets
 * @Project: bring-framework
 */
public interface RequestAttributes {


    /**
     * Constant that indicates request scope.
     */
    int SCOPE_REQUEST = 0;

    /**
     * Constant that indicates session scope.
     * <p>This preferably refers to a locally isolated session, if such
     * a distinction is available.
     * Else, it simply refers to the common session.
     */
    int SCOPE_SESSION = 1;


    /**
     * Name of the standard reference to the request object: "request".
     */
    String REFERENCE_REQUEST = "request";

    /**
     * Name of the standard reference to the session object: "session".
     */
    String REFERENCE_SESSION = "session";


    /**
     * Return the value for the scoped attribute of the given name, if any.
     * @param name the name of the attribute
     * @param scope the scope identifier
     */
    @Nullable
    Object getAttribute(String name, int scope);

    /**
     * Set the value for the scoped attribute of the given name,
     * replacing an existing value (if any).
     * @param name the name of the attribute
     * @param scope the scope identifier
     * @param value the value for the attribute
     */
    void setAttribute(String name, Object value, int scope);

    /**
     * Remove the scoped attribute of the given name, if it exists.
     * @param name the name of the attribute
     * @param scope the scope identifier
     */
    void removeAttribute(String name, int scope);

    /**
     * Retrieve the names of all attributes in the scope.
     * @param scope the scope identifier
     * @return the attribute names as String array
     */
    String[] getAttributeNames(int scope);

    /**
     * Resolve the contextual reference for the given key, if any.
     * <p>At a minimum: the HttpServletRequest reference for key "request", and
     * the HttpSession reference for key "session".
     * @param key the contextual key
     * @return the corresponding object, or {@code null} if none found
     */
    @Nullable
    Object resolveReference(String key);

    /**
     * Return an id for the current underlying session.
     * @return the session id as String (never {@code null})
     */
    String getSessionId();
}
