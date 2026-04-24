package Zyfera;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Zyfera {

    private record ContextData(
            int id,
            Context context,

            ArrayList<A_Processor> processors,

            Indexer entityIndexer,
            HashMap<Integer, EntityData> entities,

            HashMap<Class<? extends A_Component>, A_Component[]> components
    ) {

        int createEntity() {
            int entityId;

            entities.put((entityId = entityIndexer.get()), new EntityData(new Entity(entityId, id), new HashMap<>()));

            for (A_Processor processor : processors) processor.p_validateEntity(entityId);

            return entityId;
        }

        void disposeEntity(int entityId) {
            rmvEntity(entityId);
        }
        void disposeEntities() {
            ArrayList<Integer> entities;

            entities = new ArrayList<>(entities().keySet());

            entities.forEach(this::disposeEntity);
            for (A_Processor processor : processors) processor.p_removeContext(id);
        }

        int addEntity(EntityData data) {
            int entityId;

            entities.put((entityId = entityIndexer.get()), data);
            data.entity.p_onContextSwitch(entityId, this.id);

            for (A_Processor processor : processors) processor.p_validateEntity(entityId);
            for (A_Component component : data.components.values()) component.use();

            return entityId;
        }
        void rmvEntity(int entityId) {
            EntityData data;

            if ((data = entities.remove(entityId)) == null) return;

            entityIndexer.free(entityId);

            for (A_Component component : data.components.values()) component.unuse();
            for (A_Processor processor : processors) processor.p_removeEntity(entityId);
        }

        boolean addProcessor(A_Processor processor) {
            if (processors.contains(processor)) return false;

            processor.p_init(id);

            processors.add(processor);

            entities.keySet().forEach(processor::p_validateEntity);

            return true;
        }
        boolean rmvProcessor(A_Processor processor) {
            return processors.remove(processor);
        }

        <T extends A_Component> boolean addComponent(int entityId, T component, boolean override) {
            EntityData entityData;

            entityData = getEntity(entityId);

            if (!entityData.addComponent(component, override)) return false;

            for (A_Processor processor : processors) processor.p_validateEntity(entityId);

            return true;
        }

        boolean rmvComponent(int entityId, A_Component component) {
            EntityData entityData;

            entityData = getEntity(entityId);

            if (!entityData.rmvComponent(component)) return false;

            for (A_Processor processor : processors) processor.p_validateEntity(entityId);

            return true;
        }
        <T extends A_Component> boolean rmvComponent(int entityId, Class<T> componentClass) {
            EntityData entityData;

            entityData = getEntity(entityId);

            if (!entityData.rmvComponent(componentClass)) return false;

            for (A_Processor processor : processors) processor.p_validateEntity(entityId);

            return true;
        }

        EntityData getEntity(int id) {
            return entities.get(id);
        }

        void update() {
            for (A_Processor processor : processors) processor.p_updateEntities(entities.keySet());
        }

    }
    private record EntityData(
            Entity entity,

            A_Component[] components
    ) {

        <T extends A_Component> boolean addComponent(T component, boolean override) {
            Class<? extends A_Component> componentClass;
            A_Component oldComponent;

            componentClass = component.getClass();

            oldComponent = components.get(componentClass);
            if (oldComponent != null) {
                if (override) oldComponent.unuse();
                else return false;
            }

            component.use();
            components().put(componentClass, component);

            return true;
        }

        boolean rmvComponent(A_Component component) {
            if (!components.remove(component.getClass(), component)) return false;

            component.unuse();

            return true;
        }
        <T extends A_Component> boolean rmvComponent(Class<T> componentClass) {
            A_Component component;

            component = components().remove(componentClass);
            if (component == null) return false;

            component.unuse();

            return true;
        }

        HashMap<Class<? extends A_Component>, A_Component> getComponents() {
            return components;
        }

    }

    private static final Indexer s_CONTEXT_INDEXER = new Indexer();
    private static final HashMap<Integer, ContextData> s_CONTEXTS = new HashMap<>();

    public static int zCreateContextId() {
        int id;
        ContextData contextData;

        id = s_CONTEXT_INDEXER.get();
        contextData = new ContextData(id, new Context(id), new ArrayList<>(), new Indexer(), new HashMap<>());

        s_CONTEXTS.put(id, contextData);

        return id;
    }

    public static Context zCreateContext() {
        int id;

        id = zCreateContextId();

        return _getContextData(id).context;
    }

    public static Context zGetContext(int contextId) {
        return _getContextData(contextId).context;
    }
    public static Context zGetContext(Context context) {
        return zGetContext(context.id());
    }

    public static void zDisposeContext(int id) {
        ContextData contextData;

        if ((contextData = s_CONTEXTS.remove(id)) == null) return;

        contextData.disposeEntities();

        s_CONTEXT_INDEXER.free(id);
    }
    public static void zDisposeContext(Context context) {
        zDisposeContext(context.id());
    }

    public static int zCreateEntityId(int contextId) {
        ContextData contextData;

        contextData = _getContextData(contextId);

        return contextData.createEntity();
    }
    public static int zCreateEntityId(Context context) {
        return zCreateEntityId(context.id());
    }

    public static Entity zCreateEntity(int contextId) {
        int id;

        id = zCreateEntityId(contextId);

        return _getContextData(contextId).getEntity(id).entity;
    }
    public static Entity zCreateEntity(Context context) {
        return zCreateEntity(context.id());
    }

    public static Entity zGetEntity(int entityId, int contextId) {
        return _getContextData(contextId).getEntity(entityId).entity;
    }
    public static Entity zGetEntity(Entity entity) {
        return _getContextData(entity.contextId()).getEntity(entity.id()).entity;
    }
    public static Entity zGetEntity(int entityId, Context context) {
        return _getContextData(context.id()).getEntity(entityId).entity;
    }

    public static void zDisposeEntity(int contextId, int entityId) {
        _getContextData(contextId).disposeEntity(entityId);
    }
    public static void zDisposeEntity(Context context, int entityId) {
        zDisposeEntity(context.id(), entityId);
    }
    public static void zDisposeEntity(int contextId, Entity entity) {
        zDisposeEntity(contextId, entity.id());
    }
    public static void zDisposeEntity(Context context, Entity entity) {
        zDisposeEntity(context.id(), entity.id());
    }

    public static boolean zAttachProcessor(int contextId, A_Processor processor) {
        return _getContextData(contextId).addProcessor(processor);
    }
    public static boolean zAttachProcessor(Context context, A_Processor processor) {
        return zAttachProcessor(context.id(), processor);
    }

    public static boolean zDetachProcessor(int contextId, A_Processor processor) {
        return _getContextData(contextId).rmvProcessor(processor);
    }
    public static boolean zDetachProcessor(Context context, A_Processor processor) {
        return zDetachProcessor(context.id(), processor);
    }

    public static <T extends A_Component> boolean zAddComponent(int contextId, int entityId, T component, boolean override) {
        return _getContextData(contextId).addComponent(entityId, component, override);
    }
    public static <T extends A_Component> boolean zAddComponent(Context context, int entityId, T component, boolean override) {
        return _getContextData(context.id()).addComponent(entityId, component, override);
    }
    public static <T extends A_Component> boolean zAddComponent(Entity entity, T component, boolean override) {
        return _getContextData(entity.contextId()).addComponent(entity.id(), component, override);
    }

    public static <T extends A_Component> boolean zRmvComponent(int contextId, int entityId, T component) {
        return _getContextData(contextId).rmvComponent(entityId, component);
    }
    public static <T extends A_Component> boolean zRmvComponent(Context context, int entityId, T component) {
        return _getContextData(context.id()).rmvComponent(entityId, component);
    }
    public static <T extends A_Component> boolean zRmvComponent(Entity entity, T component) {
        return _getContextData(entity.contextId()).rmvComponent(entity.id(), component);
    }

    public static <T extends A_Component> boolean zRmvComponent(int contextId, int entityId, Class<T> componentClass) {
        return _getContextData(contextId).rmvComponent(entityId, componentClass);
    }
    public static <T extends A_Component> boolean zRmvComponent(Context context, int entityId, Class<T> componentClass) {
        return _getContextData(context.id()).rmvComponent(entityId, componentClass);
    }
    public static <T extends A_Component> boolean zRmvComponent(int contextId, Entity entity, Class<T> componentClass) {
        return _getContextData(contextId).rmvComponent(entity.id(), componentClass);
    }
    public static <T extends A_Component> boolean zRmvComponent(Context context, Entity entity, Class<T> componentClass) {
        return _getContextData(context.id()).rmvComponent(entity.id(), componentClass);
    }

    public static boolean zContainsEntity(int contextId, int entityId) {
        return _getContextData(contextId).getEntity(entityId) != null;
    }
    public static boolean zContainsEntity(Context context, int entityId) {
        return _getContextData(context.id()).getEntity(entityId) != null;
    }
    public static boolean zContainsEntity(int contextId, Entity entity) {
        return _getContextData(contextId).getEntity(entity.id()) != null;
    }
    public static boolean zContainsEntity(Context context, Entity entity) {
        return _getContextData(context.id()).getEntity(entity.id()) != null;
    }

    public static void zUpdate() {
        for (ContextData contextData : s_CONTEXTS.values()) contextData.update();
    }

    public static int zSwitchContext(int entityId, int contextId, int toContextId) {
        ContextData from, to;
        EntityData entityData;

        from = _getContextData(contextId);
        to = _getContextData(toContextId);

        entityData = from.getEntity(entityId);

        from.rmvEntity(entityId);

        return to.addEntity(entityData);
    }
    public static int zSwitchContext(int entityId, int contextId, Context toContext) {
        return zSwitchContext(entityId, contextId, toContext.id());
    }
    public static int zSwitchContext(Entity entity, int toContextId) {
        return zSwitchContext(entity.id(), entity.contextId(), toContextId);
    }
    public static int zSwitchContext(Entity entity, Context toContext) {
        return zSwitchContext(entity.id(), entity.contextId(), toContext.id());
    }

    protected static Map<Class<? extends A_Component>, A_Component> p_getEntityComponents(int contextId, int entityId) {
        return Collections.unmodifiableMap(_getContextData(contextId).getEntity(entityId).getComponents());
    }

    private static ContextData _getContextData(int id) {
        ContextData contextData;

        contextData = s_CONTEXTS.get(id);

        if (contextData == null) throw new RuntimeException();

        return contextData;
    }

}