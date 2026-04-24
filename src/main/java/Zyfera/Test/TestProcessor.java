package Zyfera.Test;

import Zyfera.A_Component;
import Zyfera.A_Processor;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestProcessor extends A_Processor {

    @Override
    protected void p_updateValidatedEntity(Map<Class<? extends A_Component>, A_Component> components) {
        System.out.println("Test");
    }

    @Override
    protected Collection<Class<? extends A_Component>> p_getComponents() {
        return List.of(TestComponent.class);
    }

}