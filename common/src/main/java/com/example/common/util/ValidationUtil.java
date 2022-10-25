package com.example.common.util;

import com.example.common.HasId;
import com.example.common.error.IllegalRequestDataException;
import com.example.common.error.NotFoundException;
import lombok.experimental.UtilityClass;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.lang.NonNull;

import java.util.Optional;

@UtilityClass
public class ValidationUtil {

    public static void checkNew(HasId bean) {
        if(!bean.isNew()) {
            throw new IllegalRequestDataException(bean.getClass().getSimpleName() + " must be new (id=null)");
        }
    }

    //http://stackoverflow.com/a/32728226/548473
    public static void assureIdConsistent(HasId bean, String id) {
        if (bean.isNew()) {
            bean.setId(id);
        } else if (!bean.id().equals(id)) {
            throw new IllegalRequestDataException(bean.getClass().getSimpleName() + " must has id=" + id);
        }
    }

    public static void checkModification(int count, String id) {
        if (count == 0) {
            throw new IllegalRequestDataException("Entity with id=" + id + " not found");
        }
    }

    //  https://stackoverflow.com/a/65442410/548473
    @NonNull
    public static Throwable getRootCause(@NonNull Throwable t) {
        Throwable rootCause = NestedExceptionUtils.getRootCause(t);
        return rootCause != null ? rootCause : t;
    }

    public static <T> T checkNotFoundWithId(String msg, Optional<T> optional) {
        return optional.orElseThrow(() -> new NotFoundException(msg));
    }

    public static <T> T checkNotFoundWithId(Optional<T> optional, String id) {
        return checkNotFoundWithId("Entity with id=" + id +" not found", optional);
    }

    public static <T> T checkNotFoundWithName(Optional<T> optional, String id) {
        return checkNotFoundWithId("Entity with name=" + id +" not found", optional);
    }
}