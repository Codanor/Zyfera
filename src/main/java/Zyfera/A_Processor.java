package Zyfera;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class A_Processor {

    public A_Processor() {
        _contextId = -1;

        _CACHE = new HashMap<>();
    }

    private int _contextId;

    private final HashMap<Integer, Boolean> _CACHE;

    protected void p_init(int contextId) {
        if (_contextId != -1) throw new RuntimeException();

        _contextId = contextId;
    }
    protected void p_removeContext(int contextId) {
        if (contextId != _contextId) return;

        _contextId = -1;
    }

    protected void p_validateEntity(int id) {
        Map<Class<? extends A_Component>, A_Component> componentMap;

        componentMap = Zyfera.p_getEntityComponents(_contextId, id);

        for (Class<? extends A_Component> componentClass : p_getComponents()) {
            if (!componentMap.containsKey(componentClass)) {
                _CACHE.put(id, false);

                return;
            }
        }

        _CACHE.put(id, true);
    }

    protected void p_removeEntity(int id) {
        _CACHE.remove(id);
    }

    protected void p_updateEntities(Collection<Integer> entityIds) {
        for (int entityId : entityIds) {
            if (!Boolean.TRUE.equals(_CACHE.get(entityId))) continue;

            p_updateValidatedEntity(Zyfera.p_getEntityComponents(_contextId, entityId));
        }
    }
    protected abstract void p_updateValidatedEntity(Map<Class<? extends A_Component>, A_Component> components);

    protected abstract Collection<Class<? extends A_Component>> p_getComponents();

}