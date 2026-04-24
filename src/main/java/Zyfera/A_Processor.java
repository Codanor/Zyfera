package Zyfera;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class A_Processor {

    public A_Processor() {
        _CACHE = new HashMap<>();
    }

    private final HashMap<Integer, HashMap<Integer, Boolean>> _CACHE;

    protected void p_init(int contextId) {
        _CACHE.put(contextId, new HashMap<>());
    }
    protected void p_removeContext(int contextId) {
        _CACHE.remove(contextId);
    }

    protected void p_validateEntity(int contextId, int id) {
        Map<Class<? extends A_Component>, A_Component> componentMap;

        componentMap = Zyfera.p_getEntityComponents(contextId, id);

        for (Class<? extends A_Component> componentClass : p_getComponents()) {
            if (!componentMap.containsKey(componentClass)) {
                _CACHE.get(contextId).put(id, false);

                return;
            }
        }

        _CACHE.get(contextId).put(id, true);
    }

    protected void p_removeEntity(int contextId, int id) {
        _CACHE.get(contextId).remove(id);
    }

    protected void p_updateEntities(int contextId, Collection<Integer> entityIds) {
        for (int entityId : entityIds) {
            if (!Boolean.TRUE.equals(_CACHE.get(contextId).get(entityId))) continue;

            p_updateValidatedEntity(Zyfera.p_getEntityComponents(contextId, entityId));
        }
    }
    protected abstract void p_updateValidatedEntity(Map<Class<? extends A_Component>, A_Component> components);

    protected abstract Collection<Class<? extends A_Component>> p_getComponents();

}