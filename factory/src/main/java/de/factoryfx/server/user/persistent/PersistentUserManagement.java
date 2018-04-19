package de.factoryfx.server.user.persistent;

import java.util.List;
import java.util.Optional;

import de.factoryfx.server.user.AuthorizedUser;
import de.factoryfx.server.user.User;
import de.factoryfx.server.user.UserManagement;

public class PersistentUserManagement implements UserManagement {

    private final List<User> users;

    public PersistentUserManagement(List<User> users) {
        this.users = users;
    }

    @Override
    public Optional<AuthorizedUser> authenticate(String user, String password) {
        for (User existingUser: users){
            if (existingUser.matchUser(user, password)){
                return Optional.of(existingUser.toAuthorizedUser());
            }
        }

        return Optional.empty();
    }

    @Override
    public boolean authorisationRequired() {
        return true;
    }
}
