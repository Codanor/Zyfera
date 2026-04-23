package Zyfara;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class TestProcessor extends A_Processor {

    @Override
    protected void p_updateValidatedComponentMap(HashMap<Class<? extends A_Component>, A_Component> componentMap) {
        System.out.println(((TestComponent) componentMap.get(TestComponent.class)).data);
    }

    @Override
    protected Collection<Class<? extends A_Component>> p_getRequiredComponents() {
        return List.of(TestComponent.class);
    }

}