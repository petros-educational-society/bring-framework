package com.petros.bringframework.core;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Factory for collections that is aware of common Java collection types.
 *
 * <p>Mainly for internal use within the framework.
 *
 * @author Viktor Basanets
 * @Project: bring-framework
 */

public final class CollectionFactory {

    private static final Set<Class<?>> approximableCollectionTypes = Set.of(
            Collection.class,
            List.class,
            Set.class,
            SortedSet.class,
            NavigableSet.class,
            ArrayList.class,
            LinkedList.class,
            HashSet.class,
            LinkedHashSet.class,
            TreeSet.class,
            EnumSet.class);

    private static final Set<Class<?>> approximableMapTypes = Set.of(
            Map.class,
            SortedMap.class,
            NavigableMap.class,
            HashMap.class,
            LinkedHashMap.class,
            TreeMap.class,
            EnumMap.class);


    private CollectionFactory() {
    }

    /**
     * Determine whether the given collection type is an <em>approximable</em> type,
     * @param collectionType the collection type to check
     * @return {@code true} if the type is <em>approximable</em>
     */
    public static boolean isApproximableCollectionType(@Nullable Class<?> collectionType) {
        return (collectionType != null && approximableCollectionTypes.contains(collectionType));
    }

    /**
     * Create the most approximate collection for the given collection.
     * <p><strong>Warning</strong>: Since the parameterized type {@code E} is
     * not bound to the type of elements contained in the supplied
     * {@code collection}, type safety cannot be guaranteed if the supplied
     * {@code collection} is an {@link EnumSet}. In such scenarios, the caller
     * is responsible for ensuring that the element type for the supplied
     * {@code collection} is an enum type matching type {@code E}. As an
     * alternative, the caller may wish to treat the return value as a raw
     * collection or collection of {@link Object}.
     * @param collection the original collection object, potentially {@code null}
     * @param capacity the initial capacity
     * @return a new, empty collection instance
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <E> Collection<E> createApproximateCollection(@Nullable Object collection, int capacity) {
        if (collection instanceof EnumSet enumSet) {
            Collection<E> copy = EnumSet.copyOf(enumSet);
            copy.clear();
            return copy;
        }
        else if (collection instanceof SortedSet sortedSet) {
            return new TreeSet<>(sortedSet.comparator());
        }
        else if (collection instanceof LinkedList) {
            return new LinkedList<>();
        }
        else if (collection instanceof List) {
            return new ArrayList<>(capacity);
        }
        else {
            return new LinkedHashSet<>(capacity);
        }
    }

    /**
     * Determine whether the given map type is an <em>approximable</em> type,
     * @param mapType the map type to check
     * @return {@code true} if the type is <em>approximable</em>
     */
    public static boolean isApproximableMapType(@Nullable Class<?> mapType) {
        return (mapType != null && approximableMapTypes.contains(mapType));
    }

    /**
     * Create the most approximate map for the given map.
     * <p><strong>Warning</strong>: Since the parameterized type {@code K} is
     * not bound to the type of keys contained in the supplied {@code map},
     * type safety cannot be guaranteed if the supplied {@code map} is an
     * {@link EnumMap}. In such scenarios, the caller is responsible for
     * ensuring that the key type in the supplied {@code map} is an enum type
     * matching type {@code K}. As an alternative, the caller may wish to
     * treat the return value as a raw map or map keyed by {@link Object}.
     * @param map the original map object, potentially {@code null}
     * @param capacity the initial capacity
     * @return a new, empty map instance
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <K, V> Map<K, V> createApproximateMap(@Nullable Object map, int capacity) {
        if (map instanceof EnumMap enumMap) {
            EnumMap copy = new EnumMap(enumMap);
            copy.clear();
            return copy;
        }

        if (map instanceof SortedMap sortedMap) {
            return new TreeMap<>(sortedMap.comparator());
        }

        return new LinkedHashMap<>(capacity);
    }
}
