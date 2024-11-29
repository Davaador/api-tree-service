package com.api.family.apitreeservice.spec;

import com.api.family.apitreeservice.model.postgres.User;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class UserSpecs {
    public Specification<User> containsEnabled(boolean enabled) {
        return (root, query, builder) -> builder.equal(root.get("enabled"), enabled);
    }
}
