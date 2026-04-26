package core.example_set;

import core.A_Component;
import core.A_Processor;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ExamplePositionProcessor extends A_Processor {

    @Override
    protected void p_updateValidatedEntity(Map<Class<? extends A_Component>, A_Component> components) {
        ExamplePositionComponent<? extends Number> component;

        component = (ExamplePositionComponent<? extends Number>) components.get(ExamplePositionComponent.class);

        System.out.println("Example component " + component + " with x (" + component.getX() + ") and y (" + component.getY() + ")!");
    }

    @Override
    protected Collection<Class<? extends A_Component>> p_getComponents() {
        return List.of(ExamplePositionComponent.class);
    }

}