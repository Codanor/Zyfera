package Zyfara;

public class ComponentAlreadyInUse extends RuntimeException {
    public ComponentAlreadyInUse(String message) {
        super(message);
    }
}