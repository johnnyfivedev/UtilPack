package com.johnnyfivedev.utilpack;

import androidx.annotation.Nullable;

import com.google.common.collect.Collections2;

import org.w3c.dom.NodeList;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class CollectionsUtils {

    public static boolean neitherNullNorEmpty(Collection collection) {
        return collection != null && !collection.isEmpty();
    }

    public static boolean neitherNullNorEmpty(NodeList list) {
        return list != null && list.getLength() != 0;
    }

    public static boolean eitherNullOrEmpty(Collection collection) {
        return collection == null || collection.isEmpty();
    }

    @Nullable
    public static <T extends HasId, IdType> List<IdType> collectIds(Collection<T> hasIds) {
        if (eitherNullOrEmpty(hasIds)) {
            return null;
        }
        return new ArrayList<>(Collections2.transform(hasIds, new ToId<IdType>()));
    }

    public static <T> List<T> createEmptyIfNull(List<T> list) {
        if (list == null) {
            return new ArrayList<>();
        } else {
            return list;
        }
    }

    @Nullable
    public static <T> Collection<T> removeAllNullElements(@Nullable Collection<T> collection) {
        if (collection != null) {
            collection.removeAll(Collections.singleton(null));
            return collection;
        } else {
            return null;

        }
    }

    @Nullable
    public static <T> Collection<T> removeIf(@Nullable Collection<T> collection, Predicate<T> predicate) {
        if (eitherNullOrEmpty(collection)) {
            return collection;
        }
        Iterator<T> iterator = collection.iterator();
        while (iterator.hasNext()) {
            T object = iterator.next();
            if (predicate.getPredicate(object)) {
                iterator.remove();
            }
        }

        return collection;
    }

    @Nullable
    public static <T> List<T> collectIf(@Nullable Collection<T> collection, Predicate<T> predicate) {
        if (eitherNullOrEmpty(collection)) {
            return new ArrayList<>();
        }

        List<T> result = new ArrayList<>();
        for (T item : collection) {
            if (predicate.getPredicate(item)) {
                result.add(item);
            }
        }
        return result;
    }

    public static <T> T[] toArray(Class<T> type, List<T> input) {
        @SuppressWarnings("unchecked")
        T[] res = (T[]) Array.newInstance(type, input.size());

        int i = 0;
        for (T t : input) {
            res[i] = t;
            i++;
        }
        return res;
    }

    public interface Predicate<T> {

        boolean getPredicate(T element);
    }

    public static class ToId<IdType> implements com.google.common.base.Function<HasId<IdType>, IdType> {
        public IdType apply(HasId<IdType> hasId) {
            return hasId.getId();
        }
    }
}
