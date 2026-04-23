package Zyfara;

public class ContextDoesNotExist extends RuntimeException {
    public ContextDoesNotExist(String message) {
        super(message);
    }
}