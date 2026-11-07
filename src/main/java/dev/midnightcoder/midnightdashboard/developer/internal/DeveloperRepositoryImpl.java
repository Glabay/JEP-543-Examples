package dev.midnightcoder.midnightdashboard.developer.internal;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Repository
public class DeveloperRepositoryImpl implements DeveloperRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Developer> findAll(String level, String team, String status, String search) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Developer> query = cb.createQuery(Developer.class);
        Root<Developer> root = query.from(Developer.class);
        root.fetch("mentor", JoinType.LEFT);

        List<Predicate> predicates = new ArrayList<>();

        if (level != null && !level.isBlank()) {
            try {
                predicates.add(cb.equal(root.get("level"), DeveloperLevel.valueOf(level.trim().toUpperCase(Locale.ROOT))));
            } catch (IllegalArgumentException ignored) {
            }
        }

        if (team != null && !team.isBlank()) {
            try {
                predicates.add(cb.equal(root.get("team"), DeveloperTeam.valueOf(team.trim().toUpperCase(Locale.ROOT))));
            } catch (IllegalArgumentException ignored) {
            }
        }

        if (status != null && !status.isBlank()) {
            try {
                predicates.add(cb.equal(root.get("status"), DeveloperStatus.valueOf(status.trim().toUpperCase(Locale.ROOT))));
            } catch (IllegalArgumentException ignored) {
            }
        }

        if (search != null && !search.isBlank()) {
            String pattern = "%" + search.trim().toLowerCase(Locale.ROOT) + "%";
            Expression<String> fullName = cb.concat(cb.concat(cb.lower(root.get("firstName")), " "), cb.lower(root.get("lastName")));
            Predicate nameMatch = cb.like(fullName, pattern);
            Predicate emailMatch = cb.like(cb.lower(root.get("email")), pattern);
            Predicate githubMatch = cb.like(cb.lower(root.get("githubUsername")), pattern);
            predicates.add(cb.or(nameMatch, emailMatch, githubMatch));
        }

        if (!predicates.isEmpty()) {
            query.where(predicates.toArray(Predicate[]::new));
        }

        Expression<Integer> levelOrder = cb.<Integer>selectCase()
                .when(cb.equal(root.get("level"), DeveloperLevel.PRINCIPAL), 1)
                .when(cb.equal(root.get("level"), DeveloperLevel.STAFF), 2)
                .when(cb.equal(root.get("level"), DeveloperLevel.SENIOR), 3)
                .when(cb.equal(root.get("level"), DeveloperLevel.MID_LEVEL), 4)
                .when(cb.equal(root.get("level"), DeveloperLevel.JUNIOR), 5)
                .when(cb.equal(root.get("level"), DeveloperLevel.INTERN), 6)
                .otherwise(7);

        query.orderBy(
                cb.asc(levelOrder),
                cb.asc(root.get("lastName")),
                cb.asc(root.get("firstName"))
        );

        return entityManager.createQuery(query).getResultList();
    }
}
