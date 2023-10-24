package com.quarkussocial.domain.repository;

import com.quarkussocial.domain.model.Follower;
import com.quarkussocial.domain.model.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class FollowerRepository implements PanacheRepository<Follower> {

    public Boolean followers(User follower, User user) {
        var params = Parameters.with("follower", follower)
                .and("user", user).map();
        var result = find("follower=:follower and user=:user", params);
        return result.list().isEmpty();
    }

    public List<Follower> findByUserId(Long userId) {
        return find("user.id", userId).list();
    }

    public void deleteByFollowerANdUser(Long followerId, Long userId) {
        var params = Parameters.with("followerId", followerId)
                .and("userId", userId).map();
        delete("follower.id=:followerId and user.id=:userId", params);
    }

}
