package Zyfara;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class Context {

    private static final Indexer _CONTEXT_INDEXER = new Indexer();
    private static final HashMap<Integer, ContextRecord> _CONTEXTS = new HashMap<>();

    private record ContextRecord(
            Indexer _ENTITY_INDEXER,
            HashMap<Integer, HashMap<Class<? extends A_Component>, A_Component>> entities,
            HashSet<A_Processor> _PROCESSORS,
            int id
    ) {
        public boolean addProcessor(A_Processor processor) {
            return _PROCESSORS.add(processor);
        }
        public boolean rmvProcessor(A_Processor processor) {
            return _PROCESSORS.remove(processor);
        }

        <T extends A_Component> T getEntityComponent(int id, Class<T> componentClass) {
            return (T) entities.get(id).get(componentClass);
        }

        <T extends A_Component> boolean addEntityComponent(int id, T component, boolean override) {
            HashMap<Class<? extends A_Component>, A_Component> componentMap;
            A_Component oldComponent;

            componentMap = entities.get(id);
            oldComponent = componentMap.get(component.getClass());

            if (oldComponent != null && !override) return false;

            if (oldComponent != null) oldComponent.unuse();
            component.use();

            componentMap.put(component.getClass(), component);

            return true;
        }

        <T extends A_Component> boolean rmvEntityComponent(int id, T component) {
            HashMap<Class<? extends A_Component>, A_Component> componentMap;

            componentMap = entities.get(id);

            if (!componentMap.remove(component.getClass(), component)) return false;
            component.unuse();

            return true;
        }
        <T extends A_Component> boolean rmvEntityComponent(int id, Class<T> componentClass) {
            HashMap<Class<? extends A_Component>, A_Component> componentMap;
            A_Component component;

            componentMap = entities.get(id);

            component = componentMap.remove(componentClass);
            if (component == null) return false;

            component.unuse();

            return true;
        }

        Collection<Integer> getEntities() {
            return entities.keySet();
        }

        int createEntity() {
            int id;

            entities.put((id = _ENTITY_INDEXER.get()), new HashMap<>());

            return id;
        }

        boolean rmvEntity(int id) {
            return entities.remove(id) != null;
        }

        boolean containsEntity(int id) {
            return entities.containsKey(id);
        }

        void update() {
            for (A_Processor processor : _PROCESSORS) for (int entity : entities.keySet()) processor.p_updateEntity(id, entity);
        }
    }

    public static class ContextStream {

        private ContextStream(int id) {
            if (!_CONTEXTS.containsKey(id)) throw new ContextDoesNotExist("[CONTEXT STREAM] : Context of the specified id does not exists : " + id + "!");

            _id = id;
        }

        private int _id;

        public ContextStream addProcessor(A_Processor processor) {
            _CONTEXTS.get(_id).addProcessor(processor);

            return this;
        }
        public ContextStream rmvProcessor(A_Processor processor) {
            _CONTEXTS.get(_id).rmvProcessor(processor);

            return this;
        }

        public <T extends A_Component> ContextStream addComponentToEntity(int entityId, T component, boolean override) {
            ContextRecord contextRecord;

            contextRecord = _CONTEXTS.get(_id);

            if (!contextRecord.containsEntity(entityId)) throw new EntityDoesNotExist("[CONTEXT STREAM] : An entity with this id does not exist in this context : " + entityId + "!");

            contextRecord.addEntityComponent(entityId, component, override);
            for (A_Processor processor : contextRecord._PROCESSORS) processor.p_validateEntity(_id, entityId);

            return this;
        }

        public <T extends A_Component> ContextStream rmvComponentFromEntity(int entityId, T component) {
            _CONTEXTS.get(_id).rmvEntityComponent(entityId, component);

            return this;
        }
        public <T extends A_Component> ContextStream rmvComponentFromEntity(int entityId, Class<T> componentClass) {
            _CONTEXTS.get(_id).rmvEntityComponent(entityId, componentClass);

            return this;
        }

        public int id() {
            return _CONTEXTS.get(_id).id;
        }

    }

    public static ContextStream contextStream(int id) {
        return new ContextStream(id);
    }

    public static int createContext() {
        int id;

        _CONTEXTS.put((id = _CONTEXT_INDEXER.get()), new ContextRecord(new Indexer(), new HashMap<>(), new HashSet<>(), id));

        return id;
    }
    public static void disposeContext(int id) {
        int oldId;

        if (_CONTEXTS.get(id) == null) throw new ContextDoesNotExist("[CONTEXT] : Context does not exist with this id : " + id + "!");

        oldId = _CONTEXTS.get(id).id;

        _CONTEXTS.remove(oldId);
        _CONTEXT_INDEXER.free(oldId);
    }

    public static Collection<Integer> allContexts() {
        return _CONTEXTS.keySet();
    }
    public static Collection<Integer> allEntitiesOfContext(int id) {
        ContextRecord contextRecord;

        contextRecord = _CONTEXTS.get(id);

        if (contextRecord == null) throw new ContextDoesNotExist("[CONTEXT] : Context does not exist with this id : " + id + "!");

        return new HashSet<>(contextRecord.entities.keySet());
    }

    public static boolean addProcessorToContext(int id, A_Processor processor) {
        if (_CONTEXTS.get(id) == null) throw new ContextDoesNotExist("[CONTEXT] : Context does not exist with this id : " + id + "!");

        return _CONTEXTS.get(id).addProcessor(processor);
    }
    public static boolean rmvProcessorFromContext(int id, A_Processor processor) {
        if (_CONTEXTS.get(id) == null) throw new ContextDoesNotExist("[CONTEXT] : Context does not exist with this id : " + id + "!");

        return _CONTEXTS.get(id).rmvProcessor(processor);
    }

    public static void updateContext(int id) {
        if (_CONTEXTS.get(id) == null) throw new ContextDoesNotExist("[CONTEXT] : Context does not exist with this id : " + id + "!");

        _CONTEXTS.get(id).update();
    }
    public static void updateAllContexts() {
        for (int id :  _CONTEXTS.keySet()) updateContext(id);
    }

    protected static void p_rmvEntity(int id, int entityId) {
        ContextRecord contextRecord;

        contextRecord = _CONTEXTS.get(id);

        if (contextRecord == null) throw new ContextDoesNotExist("[CONTEXT] : Context does not exist with this id : " + id + "!");

        for (A_Processor processor : contextRecord._PROCESSORS) processor.p_removeEntityFromValidationCache(entityId);
        for (A_Component component : contextRecord.entities.get(entityId).values()) component.unuse();

        contextRecord.rmvEntity(entityId);
    }

    protected static <T extends A_Component> boolean p_addEntityComponent(int id, int entityId, T component, boolean override) {
        if (_CONTEXTS.get(id) == null) throw new ContextDoesNotExist("[CONTEXT] : Context does not exist with this id : " + id + "!");

        return _CONTEXTS.get(id).addEntityComponent(entityId, component, override);
    }

    protected static <T extends A_Component> boolean p_rmvEntityComponent(int id, int entityId, T component) {
        if (_CONTEXTS.get(id) == null) throw new ContextDoesNotExist("[CONTEXT] : Context does not exist with this id : " + id + "!");

        return _CONTEXTS.get(id).rmvEntityComponent(entityId, component);
    }
    protected static <T extends A_Component> boolean p_rmvEntityComponent(int id, int entityId, Class<T> componentClass) {
        if (_CONTEXTS.get(id) == null) throw new ContextDoesNotExist("[CONTEXT] : Context does not exist with this id : " + id + "!");

        return _CONTEXTS.get(id).rmvEntityComponent(entityId, componentClass);
    }

    protected static <T extends A_Component> T p_getEntityComponent(int id, int entityId, Class<T> componentClass) {
        if (_CONTEXTS.get(id) == null) throw new ContextDoesNotExist("[CONTEXT] : Context does not exist with this id : " + id + "!");

        return _CONTEXTS.get(id).getEntityComponent(entityId, componentClass);
    }

    protected static int p_createEntity(int id) {
        if (_CONTEXTS.get(id) == null) throw new ContextDoesNotExist("[CONTEXT] : Context does not exist with this id : " + id + "!");

        return _CONTEXTS.get(id).createEntity();
    }

    protected static void p_revalidateEntity(int id, int entityId) {
        for (A_Processor processor : _CONTEXTS.get(id)._PROCESSORS) processor.p_validateEntity(id, entityId);
    }

    public static boolean contextContainsEntity(int id, int entityId) {
        if (_CONTEXTS.get(id) == null) throw new ContextDoesNotExist("[CONTEXT] : Context does not exist with this id : " + id + "!");

        return _CONTEXTS.get(id).containsEntity(entityId);
    }

}