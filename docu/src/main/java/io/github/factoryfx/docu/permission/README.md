# Permissions (attribute based)

FactoryFX supports setting attribute permissions to limit write access.

The check is secure because it is done on the server side yet also more complex because a user management is needed.
Alternatively, the editing can also be deactivated in the GUI only.
```java
...
    public final StringAttribute attribute = new StringAttribute().userReadOnly();
...
```

[**code**](https://github.com/factoryfx/factoryfx/tree/master/docu/src/main/java/io/github/factoryfx/docu/permission)