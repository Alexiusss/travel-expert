package com.example.common;

import com.example.common.util.ValidationUtil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;

import static com.example.common.util.ValidationUtil.checkModification;

// https://stackoverflow.com/questions/42781264/multiple-base-repositories-in-spring-data-jpa
@NoRepositoryBean
public interface BaseRepository<T> extends JpaRepository<T, String> {
    //    https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query.spel-expressions
    @Transactional
    @Modifying
    @Query("DELETE FROM #{#entityName} e WHERE e.id=:id")
    int delete(String id);

    default void deleteExisted(String id) {
        checkModification(delete(id), id);
    }

    default T getExisted(String id) {
        return ValidationUtil.checkNotFoundWithId(findById(id), id);
    }
}