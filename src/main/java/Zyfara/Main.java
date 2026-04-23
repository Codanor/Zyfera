package Zyfara;

import static Zyfara.Context.*;
import static Zyfara.Entity.*;

public class Main {

    static void main() {
        int contextId, entityId, anotherEntityId;
        TestComponent testComponent;

        contextId = createContext();
        entityId = createEntity(contextId);
        anotherEntityId = createEntity(contextId);

        testComponent = new TestComponent(A_Component.ACCESS.SHARED, "Test");

        contextStream(contextId).
                addProcessor(new TestProcessor()).
                addComponentToEntity(entityId, testComponent, false).
                addComponentToEntity(anotherEntityId, testComponent, false);

        disposeEntity(entityId, contextId);

        updateAllContexts();
    }

}