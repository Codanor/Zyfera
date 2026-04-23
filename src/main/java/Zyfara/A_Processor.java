package Zyfara;

import java.util.Collection;
import java.util.HashMap;

public abstract class A_Processor {

    public A_Processor() {
        _VALIDATION_CACHE = new HashMap<>();
    }

    private final HashMap<Integer, Boolean> _VALIDATION_CACHE;

    protected boolean p_validateEntity(int contextId, int entityId) {
        for (Class<? extends A_Component> componentClass : p_getRequiredComponents()) if (Entity.getComponentFromEntity(entityId, contextId, componentClass) == null) {
            _VALIDATION_CACHE.put(entityId, false);

            return false;
        }

        _VALIDATION_CACHE.put(entityId, true);

        return true;
    }
    protected void p_removeEntityFromValidationCache(int id) {
        _VALIDATION_CACHE.remove(id);
    }

    protected void p_updateEntity(int contextId, int entityId) {
        Boolean cachedValidation;
        HashMap<Class<? extends A_Component>, A_Component> componentMap;

        cachedValidation = _VALIDATION_CACHE.get(entityId);
        if (cachedValidation == null) _VALIDATION_CACHE.put(entityId, cachedValidation = p_validateEntity(contextId, entityId));
        if (!cachedValidation) return;

        componentMap = new HashMap<>();

        for (Class<? extends A_Component> componentClass : p_getRequiredComponents()) componentMap.put(componentClass, Entity.getComponentFromEntity(entityId, contextId, componentClass));

        p_updateValidatedComponentMap(componentMap);
    }
    protected abstract void p_updateValidatedComponentMap(HashMap<Class<? extends A_Component>, A_Component> componentMap);

    protected abstract Collection<Class<? extends A_Component>> p_getRequiredComponents();

}