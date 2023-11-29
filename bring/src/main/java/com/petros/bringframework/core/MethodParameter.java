package com.petros.bringframework.core;

import com.petros.bringframework.core.type.ResolvableType;
import com.petros.bringframework.util.ObjectUtils;
import org.apache.commons.lang3.ClassUtils;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Helper class that encapsulates the specification of a method parameter, i.e. a {@link Method}
 * or {@link Constructor} plus a parameter index and a nested type index for a declared generic
 * type. Useful as a specification object to pass along.
 *
 * @author Viktor Basanets
 * @Project: bring-framework
 */
public class MethodParameter {

    private static final Annotation[] EMPTY_ANNOTATION_ARRAY = new Annotation[0];

    private final int parameterIndex;

    private final Executable executable;

    @Nullable
    private volatile Parameter parameter;

    private int nestingLevel;

    /** Map from Integer level to Integer type index. */
    @Nullable
    Map<Integer, Integer> typeIndexesPerLevel;

    /** The containing class. Could also be supplied by overriding {@link #getContainingClass()} */
    @Nullable
    private volatile Class<?> containingClass;

    @Nullable
    private volatile Class<?> parameterType;

    @Nullable
    private volatile Type genericParameterType;

    @Nullable
    private volatile Annotation[] parameterAnnotations;

    @Nullable
    private volatile String parameterName;

    @Nullable
    private volatile MethodParameter nestedMethodParameter;


    /**
     * Create a new {@code MethodParameter} for the given method, with nesting level 1.
     * @param method the Method to specify a parameter for
     * @param parameterIndex the index of the parameter: -1 for the method
     * return type; 0 for the first method parameter; 1 for the second method
     * parameter, etc.
     */
    public MethodParameter(Method method, int parameterIndex) {
        this(method, parameterIndex, 1);
    }

    /**
     * Create a new {@code MethodParameter} for the given method.
     * @param method the Method to specify a parameter for
     * @param parameterIndex the index of the parameter: -1 for the method
     * return type; 0 for the first method parameter; 1 for the second method
     * parameter, etc.
     * @param nestingLevel the nesting level of the target type
     * (typically 1; e.g. in case of a List of Lists, 1 would indicate the
     * nested List, whereas 2 would indicate the element of the nested List)
     */
    public MethodParameter(Method method, int parameterIndex, int nestingLevel) {
        AssertUtils.notNull(method, "Method must not be null");
        this.executable = method;
        this.parameterIndex = validateIndex(method, parameterIndex);
        this.nestingLevel = nestingLevel;
    }

    /**
     * Create a new MethodParameter for the given constructor, with nesting level 1.
     * @param constructor the Constructor to specify a parameter for
     * @param parameterIndex the index of the parameter
     */
    public MethodParameter(Constructor<?> constructor, int parameterIndex) {
        this(constructor, parameterIndex, 1);
    }

    /**
     * Create a new MethodParameter for the given constructor.
     * @param constructor the Constructor to specify a parameter for
     * @param parameterIndex the index of the parameter
     * @param nestingLevel the nesting level of the target type
     * (typically 1; e.g. in case of a List of Lists, 1 would indicate the
     * nested List, whereas 2 would indicate the element of the nested List)
     */
    public MethodParameter(Constructor<?> constructor, int parameterIndex, int nestingLevel) {
        AssertUtils.notNull(constructor, "Constructor must not be null");
        this.executable = constructor;
        this.parameterIndex = validateIndex(constructor, parameterIndex);
        this.nestingLevel = nestingLevel;
    }

    /**
     * Internal constructor used to create a {@link MethodParameter} with a
     * containing class already set.
     * @param executable the Executable to specify a parameter for
     * @param parameterIndex the index of the parameter
     * @param containingClass the containing class
     */
    MethodParameter(Executable executable, int parameterIndex, @Nullable Class<?> containingClass) {
        AssertUtils.notNull(executable, "Executable must not be null");
        this.executable = executable;
        this.parameterIndex = validateIndex(executable, parameterIndex);
        this.nestingLevel = 1;
        this.containingClass = containingClass;
    }

    /**
     * Copy constructor, resulting in an independent MethodParameter object
     * based on the same metadata and cache state that the original object was in.
     * @param original the original MethodParameter object to copy from
     */
    public MethodParameter(MethodParameter original) {
        AssertUtils.notNull(original, "Original must not be null");
        this.executable = original.executable;
        this.parameterIndex = original.parameterIndex;
        this.parameter = original.parameter;
        this.nestingLevel = original.nestingLevel;
        this.typeIndexesPerLevel = original.typeIndexesPerLevel;
        this.containingClass = original.containingClass;
        this.parameterType = original.parameterType;
        this.genericParameterType = original.genericParameterType;
        this.parameterAnnotations = original.parameterAnnotations;
        this.parameterName = original.parameterName;
    }


    /**
     * Return the wrapped Method, if any.
     * <p>Note: Either Method or Constructor is available.
     * @return the Method, or {@code null} if none
     */
    @Nullable
    public Method getMethod() {
        return (this.executable instanceof Method ? (Method) this.executable : null);
    }

    /**
     * Return the wrapped Constructor, if any.
     * <p>Note: Either Method or Constructor is available.
     * @return the Constructor, or {@code null} if none
     */
    @Nullable
    public Constructor<?> getConstructor() {
        return (this.executable instanceof Constructor ? (Constructor<?>) this.executable : null);
    }

    /**
     * Return the class that declares the underlying Method or Constructor.
     */
    public Class<?> getDeclaringClass() {
        return this.executable.getDeclaringClass();
    }

    /**
     * Return the wrapped member.
     * @return the Method or Constructor as Member
     */
    public Member getMember() {
        return this.executable;
    }

    /**
     * Return the wrapped annotated element.
     * <p>Note: This method exposes the annotations declared on the method/constructor
     * itself (i.e. at the method/constructor level, not at the parameter level).
     * @return the Method or Constructor as AnnotatedElement
     */
    public AnnotatedElement getAnnotatedElement() {
        return this.executable;
    }

    /**
     * Return the wrapped executable.
     * @return the Method or Constructor as Executable
     */
    public Executable getExecutable() {
        return this.executable;
    }

    /**
     * Return the {@link Parameter} descriptor for method/constructor parameter.
     */
    public Parameter getParameter() {
        if (this.parameterIndex < 0) {
            throw new IllegalStateException("Cannot retrieve Parameter descriptor for method return type");
        }
        Parameter parameter = this.parameter;
        if (parameter == null) {
            parameter = getExecutable().getParameters()[this.parameterIndex];
            this.parameter = parameter;
        }
        return parameter;
    }

    /**
     * Return the index of the method/constructor parameter.
     * @return the parameter index (-1 in case of the return type)
     */
    public int getParameterIndex() {
        return this.parameterIndex;
    }

    /**
     * Increase this parameter's nesting level.
     */
    public void increaseNestingLevel() {
        this.nestingLevel++;
    }

    /**
     * Decrease this parameter's nesting level.
     * using {@link #nested(Integer)} if nesting is required
     */
    public void decreaseNestingLevel() {
        getTypeIndexesPerLevel().remove(this.nestingLevel);
        this.nestingLevel--;
    }

    /**
     * Return the nesting level of the target type
     * (typically 1; e.g. in case of a List of Lists, 1 would indicate the
     * nested List, whereas 2 would indicate the element of the nested List).
     */
    public int getNestingLevel() {
        return this.nestingLevel;
    }

    /**
     * Return a variant of this {@code MethodParameter} with the type
     * for the current level set to the specified value.
     * @param typeIndex the new type index
     */
    public MethodParameter withTypeIndex(int typeIndex) {
        return nested(this.nestingLevel, typeIndex);
    }


    /**
     * Return the type index for the current nesting level.
     * @return the corresponding type index, or {@code null}
     * if none specified (indicating the default type index)
     * @see #getNestingLevel()
     */
    @Nullable
    public Integer getTypeIndexForCurrentLevel() {
        return getTypeIndexForLevel(this.nestingLevel);
    }

    /**
     * Return the type index for the specified nesting level.
     * @param nestingLevel the nesting level to check
     * @return the corresponding type index, or {@code null}
     * if none specified (indicating the default type index)
     */
    @Nullable
    public Integer getTypeIndexForLevel(int nestingLevel) {
        return getTypeIndexesPerLevel().get(nestingLevel);
    }

    /**
     * Obtain the (lazily constructed) type-indexes-per-level Map.
     */
    private Map<Integer, Integer> getTypeIndexesPerLevel() {
        if (this.typeIndexesPerLevel == null) {
            this.typeIndexesPerLevel = new HashMap<>(4);
        }
        return this.typeIndexesPerLevel;
    }

    /**
     * Return a variant of this {@code MethodParameter} which points to the
     * same parameter but one nesting level deeper.
     */
    public MethodParameter nested() {
        return nested(null);
    }

    /**
     * Return a variant of this {@code MethodParameter} which points to the
     * same parameter but one nesting level deeper.
     * @param typeIndex the type index for the new nesting level
     */
    public MethodParameter nested(@Nullable Integer typeIndex) {
        MethodParameter nestedParam = this.nestedMethodParameter;
        if (nestedParam != null && typeIndex == null) {
            return nestedParam;
        }
        nestedParam = nested(this.nestingLevel + 1, typeIndex);
        if (typeIndex == null) {
            this.nestedMethodParameter = nestedParam;
        }
        return nestedParam;
    }

    private MethodParameter nested(int nestingLevel, @Nullable Integer typeIndex) {
        MethodParameter copy = clone();
        copy.nestingLevel = nestingLevel;
        if (this.typeIndexesPerLevel != null) {
            copy.typeIndexesPerLevel = new HashMap<>(this.typeIndexesPerLevel);
        }
        if (typeIndex != null) {
            copy.getTypeIndexesPerLevel().put(copy.nestingLevel, typeIndex);
        }
        copy.parameterType = null;
        copy.genericParameterType = null;
        return copy;
    }

    /**
     * Return whether this method indicates a parameter which is not required:
     * either in the form of Java 8's {@link java.util.Optional}, any variant
     * of a parameter-level {@code Nullable} annotation
     */
    public boolean isOptional() {
        return getParameterType() == Optional.class || hasNullableAnnotation();
    }

    /**
     * Return the type of the method/constructor parameter.
     * @return the parameter type (never {@code null})
     */
    public Class<?> getParameterType() {
        Class<?> paramType = this.parameterType;
        if (paramType != null) {
            return paramType;
        }

        paramType = computeParameterType();
        this.parameterType = paramType;

        return paramType;
    }

    /**
     * Check whether this method parameter is annotated with any variant of a
     * {@code Nullable} annotation, e.g. {@code javax.annotation.Nullable} or
     * {@code edu.umd.cs.findbugs.annotations.Nullable}.
     */
    private boolean hasNullableAnnotation() {
        for (Annotation ann : getParameterAnnotations()) {
            if ("Nullable".equals(ann.annotationType().getSimpleName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return a variant of this {@code MethodParameter} which refers to the
     * given containing class.
     * @param containingClass a specific containing class (potentially a
     * subclass of the declaring class, e.g. substituting a type variable)
     */
    public MethodParameter withContainingClass(@Nullable Class<?> containingClass) {
        MethodParameter result = clone();
        result.containingClass = containingClass;
        result.parameterType = null;
        return result;
    }

    /**
     * Set a containing class to resolve the parameter type against.
     */
    @Deprecated
    void setContainingClass(Class<?> containingClass) {
        this.containingClass = containingClass;
        this.parameterType = null;
    }

    /**
     * Return the containing class for this method parameter.
     * @return a specific containing class (potentially a subclass of the
     * declaring class), or otherwise simply the declaring class itself
     * @see #getDeclaringClass()
     */
    public Class<?> getContainingClass() {
        Class<?> containingClass = this.containingClass;
        return (containingClass != null ? containingClass : getDeclaringClass());
    }

    /**
     * Set a resolved (generic) parameter type.
     */
    @Deprecated
    void setParameterType(@Nullable Class<?> parameterType) {
        this.parameterType = parameterType;
    }


    /**
     * Return the generic type of the method/constructor parameter.
     * @return the parameter type (never {@code null})
     */
    public Type getGenericParameterType() {
        var paramType = this.genericParameterType;
        if (paramType == null) {
            if (this.parameterIndex < 0) {
                var method = getMethod();
                paramType = method != null ? method.getGenericReturnType() : void.class;
            }
            else {
                var genericParameterTypes = this.executable.getGenericParameterTypes();
                int index = this.parameterIndex;
                if (this.executable instanceof Constructor &&
                        ClassUtils.isInnerClass(this.executable.getDeclaringClass()) &&
                        genericParameterTypes.length == this.executable.getParameterCount() - 1) {
                    index = this.parameterIndex - 1;
                }
                paramType = (index >= 0 && index < genericParameterTypes.length ?
                        genericParameterTypes[index] : computeParameterType());
            }
            this.genericParameterType = paramType;
        }
        return paramType;
    }

    private Class<?> computeParameterType() {
        if (this.parameterIndex < 0) {
            Method method = getMethod();
            if (method == null) {
                return void.class;
            }
            return method.getReturnType();
        }
        return this.executable.getParameterTypes()[this.parameterIndex];
    }

    /**
     * Return the nested generic type of the method/constructor parameter.
     * @return the parameter type (never {@code null})
     */
    public Type getNestedGenericParameterType() {
        if (this.nestingLevel > 1) {
            Type type = getGenericParameterType();
            for (int i = 2; i <= this.nestingLevel; i++) {
                if (type instanceof ParameterizedType) {
                    Type[] args = ((ParameterizedType) type).getActualTypeArguments();
                    Integer index = getTypeIndexForLevel(i);
                    type = args[index != null ? index : args.length - 1];
                }
            }
            return type;
        }
        else {
            return getGenericParameterType();
        }
    }

    /**
     * Return the annotations associated with the target method/constructor itself.
     */
    public Annotation[] getMethodAnnotations() {
        return adaptAnnotationArray(getAnnotatedElement().getAnnotations());
    }

    /**
     * Return the method/constructor annotation of the given type, if available.
     * @param annotationType the annotation type to look for
     * @return the annotation object, or {@code null} if not found
     */
    @Nullable
    public <A extends Annotation> A getMethodAnnotation(Class<A> annotationType) {
        A annotation = getAnnotatedElement().getAnnotation(annotationType);
        return (annotation != null ? adaptAnnotation(annotation) : null);
    }

    /**
     * Return whether the method/constructor is annotated with the given type.
     * @param annotationType the annotation type to look for
     */
    public <A extends Annotation> boolean hasMethodAnnotation(Class<A> annotationType) {
        return getAnnotatedElement().isAnnotationPresent(annotationType);
    }

    /**
     * Return the annotations associated with the specific method/constructor parameter.
     */
    public Annotation[] getParameterAnnotations() {
        Annotation[] paramAnns = this.parameterAnnotations;
        if (paramAnns == null) {
            Annotation[][] annotationArray = this.executable.getParameterAnnotations();
            int index = this.parameterIndex;
            if (this.executable instanceof Constructor &&
                    ClassUtils.isInnerClass(this.executable.getDeclaringClass()) &&
                    annotationArray.length == this.executable.getParameterCount() - 1) {
                // Bug in javac in JDK <9: annotation array excludes enclosing instance parameter
                // for inner classes, so access it with the actual parameter index lowered by 1
                index = this.parameterIndex - 1;
            }
            paramAnns = (index >= 0 && index < annotationArray.length ?
                    adaptAnnotationArray(annotationArray[index]) : EMPTY_ANNOTATION_ARRAY);
            this.parameterAnnotations = paramAnns;
        }
        return paramAnns;
    }

    /**
     * Return {@code true} if the parameter has at least one annotation,
     * {@code false} if it has none.
     */
    public boolean hasParameterAnnotations() {
        return (getParameterAnnotations().length != 0);
    }

    /**
     * Return the parameter annotation of the given type, if available.
     * @param annotationType the annotation type to look for
     * @return the annotation object, or {@code null} if not found
     */
    @SuppressWarnings("unchecked")
    @Nullable
    public <A extends Annotation> A getParameterAnnotation(Class<A> annotationType) {
        var annotations = getParameterAnnotations();
        for (var annotation : annotations) {
            if (annotationType.isInstance(annotation)) {
                return (A) annotation;
            }
        }
        return null;
    }

    /**
     * Return whether the parameter is declared with the given annotation type.
     * @param annotationType the annotation type to look for
     * @see #getParameterAnnotation(Class)
     */
    public <A extends Annotation> boolean hasParameterAnnotation(Class<A> annotationType) {
        return (getParameterAnnotation(annotationType) != null);
    }

    /**
     * A template method to post-process a given annotation instance before
     * returning it to the caller.
     * <p>The default implementation simply returns the given annotation as-is.
     * @param annotation the annotation about to be returned
     * @return the post-processed annotation (or simply the original one)
     */
    protected <A extends Annotation> A adaptAnnotation(A annotation) {
        return annotation;
    }

    /**
     * A template method to post-process a given annotation array before
     * returning it to the caller.
     * <p>The default implementation simply returns the given annotation array as-is.
     * @param annotations the annotation array about to be returned
     * @return the post-processed annotation array (or simply the original one)
     */
    protected Annotation[] adaptAnnotationArray(Annotation[] annotations) {
        return annotations;
    }


    @Override
    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof MethodParameter otherParam)) {
            return false;
        }
        return getContainingClass() == otherParam.getContainingClass() &&
                ObjectUtils.nullSafeEquals(this.typeIndexesPerLevel, otherParam.typeIndexesPerLevel) &&
                this.nestingLevel == otherParam.nestingLevel &&
                this.parameterIndex == otherParam.parameterIndex &&
                this.executable.equals(otherParam.executable);
    }

    @Override
    public int hashCode() {
        return (31 * this.executable.hashCode() + this.parameterIndex);
    }

    @Override
    public String toString() {
        Method method = getMethod();
        return (method != null ? "method '" + method.getName() + "'" : "constructor") +
                " parameter " + this.parameterIndex;
    }

    @Override
    public MethodParameter clone() {
        return new MethodParameter(this);
    }

    /**
     * Create a new MethodParameter for the given method or constructor.
     * <p>This is a convenience factory method for scenarios where a
     * Method or Constructor reference is treated in a generic fashion.
     * @param methodOrConstructor the Method or Constructor to specify a parameter for
     * @param parameterIndex the index of the parameter
     * @return the corresponding MethodParameter instance
     */
    public static MethodParameter forMethodOrConstructor(Object methodOrConstructor, int parameterIndex) {
        if (!(methodOrConstructor instanceof Executable)) {
            throw new IllegalArgumentException(
                    "Given object [" + methodOrConstructor + "] is neither a Method nor a Constructor");
        }
        return forExecutable((Executable) methodOrConstructor, parameterIndex);
    }

    /**
     * Create a new MethodParameter for the given method or constructor.
     * <p>This is a convenience factory method for scenarios where a
     * Method or Constructor reference is treated in a generic fashion.
     * @param executable the Method or Constructor to specify a parameter for
     * @param parameterIndex the index of the parameter
     * @return the corresponding MethodParameter instance
     */
    public static MethodParameter forExecutable(Executable executable, int parameterIndex) {
        if (executable instanceof Method) {
            return new MethodParameter((Method) executable, parameterIndex);
        }
        else if (executable instanceof Constructor) {
            return new MethodParameter((Constructor<?>) executable, parameterIndex);
        }
        else {
            throw new IllegalArgumentException("Not a Method/Constructor: " + executable);
        }
    }

    /**
     * Create a new MethodParameter for the given parameter descriptor.
     * <p>This is a convenience factory method for scenarios where a
     * Java 8 {@link Parameter} descriptor is already available.
     * @param parameter the parameter descriptor
     * @return the corresponding MethodParameter instance
     */
    public static MethodParameter forParameter(Parameter parameter) {
        return forExecutable(parameter.getDeclaringExecutable(), findParameterIndex(parameter));
    }

    protected static int findParameterIndex(Parameter parameter) {
        var executable = parameter.getDeclaringExecutable();
        var allParams = executable.getParameters();

        for (int i = 0; i < allParams.length; i++) {
            if (parameter == allParams[i]) {
                return i;
            }
        }

        for (int i = 0; i < allParams.length; i++) {
            if (parameter.equals(allParams[i])) {
                return i;
            }
        }

        throw new IllegalArgumentException("Given parameter [" + parameter +
                "] does not match any parameter in the declaring executable");
    }

    private static int validateIndex(Executable executable, int parameterIndex) {
        int count = executable.getParameterCount();
        AssertUtils.isTrue(parameterIndex >= -1 && parameterIndex < count,
                "Parameter index needs to be between -1 and " + (count - 1));
        return parameterIndex;
    }
}
