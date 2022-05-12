package com.example.restaurant_advisor_react.util.validation;

import com.example.restaurant_advisor_react.HasId;
import com.example.restaurant_advisor_react.error.IllegalRequestDataException;
import lombok.experimental.UtilityClass;

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
}