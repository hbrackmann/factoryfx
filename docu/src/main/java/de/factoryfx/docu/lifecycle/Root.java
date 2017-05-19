package de.factoryfx.docu.lifecycle;

import de.factoryfx.docu.dependency.Dependency;

public class Root {

    public Root() {
        System.out.println("create");
    }

    public void start() {
        System.out.println("start");

    }

    public void destroy() {
        System.out.println("destroy");
    }
}