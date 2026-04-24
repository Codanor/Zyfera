package Zyfera;

import java.util.*;

public class Zyfera {

    private static class ContextData {

        public ContextData(int id, Context context) {
            _maxComponentId = 0;

            _ID = id;
            _CONTEXT = context;

            _PROCESSORS = new ArrayList<>();

            _ENTITY_INDEXER = new Indexer();
            _ENTITIES = new HashMap<>();
            _COMPONENTS = new HashMap<>();
        }

        private int _maxComponentId;

        private final int _ID;
        private final Context _CONTEXT;

        private final ArrayList<A_Processor> _PROCESSORS;

        private final Indexer _ENTITY_INDEXER;
        private final HashMap<Integer, Entity> _ENTITIES;
        private final HashMap<Class<? extends A_Component>, A_Component[]> _COMPONENTS;

        public int createEntity() {
            int id;

            id = _ENTITY_INDEXER.get();

            if (id > _maxComponentId) {
                _maxComponentId = id;

                _COMPONENTS.replaceAll((c, v) -> Arrays.copyOf(_COMPONENTS.get(c), _maxComponentId + 1));
            }
            _ENTITIES.put(id, new Entity(id, _ID));

            for (A_Processor processor : _PROCESSORS) processor.p_validateEntity(_ID, id);

            return id;
        }

        public Entity getEntity(int id) {
            Entity entity;

            entity = _ENTITIES.get(id);
            if (entity == null) throw new NoEntityException(
                    "[CONTEXT ERROR] : An entity with the specified id does not exist in the specified context!" + '\n'
                    + "|-> Context Id : " + _ID + '\n'
                    + "|-> Entity Id  : " +  id + '\n'
            );

            return entity;
        }
        public HashMap<Class<? extends A_Component>, A_Component> getEntityComponents(int id) {
            HashMap<Class<? extends A_Component>, A_Component> components;

            if (_ENTITIES.get(id) == null) throw new NoEntityException(
                    "[CONTEXT ERROR] : An entity with the specified id does not exist in the specified context!" + '\n'
                            + "|-> Context Id : " + _ID + '\n'
                            + "|-> Entity Id  : " +  id + '\n'
            );

            components = new HashMap<>();

            for (Class<? extends A_Component> componentClass : _COMPONENTS.keySet()) {
                A_Component component;

                component = _COMPONENTS.get(componentClass)[id];
                if (component == null) continue;

                components.put(componentClass, component);
            }

            return components;
        }

        public int addEntity(HashMap<Class<? extends A_Component>, A_Component> components, Entity entity) {
            int id;

            id = _ENTITY_INDEXER.get();
            entity.p_onContextSwitch(id, _ID);

            if (id >= _maxComponentId) {
                _maxComponentId = id;

                _COMPONENTS.replaceAll((c, v) -> Arrays.copyOf(_COMPONENTS.get(c), _maxComponentId + 1));
            }
            _ENTITIES.put(id, entity);

            for (Class<? extends A_Component> componentClass : components.keySet()) {
                A_Component component;

                _COMPONENTS.computeIfAbsent(componentClass, (cc) -> new A_Component[_maxComponentId + 1])[id] = (component = components.get(componentClass));
                component.use();
            }

            for (A_Processor processor : _PROCESSORS) processor.p_validateEntity(_ID, id);

            return id;
        }

        public void rmvEntity(int id) {
            if (_ENTITIES.remove(id) == null) throw new NoEntityException(
                    "[CONTEXT ERROR] : An entity with the specified id does not exist in the specified context!" + '\n'
                            + "|-> Context Id : " + _ID + '\n'
                            + "|-> Entity Id  : " +  id + '\n'
            );

            _ENTITY_INDEXER.free(id);
            for (A_Processor processor : _PROCESSORS) processor.p_removeEntity(_ID, id);

            for (Class<? extends A_Component> componentClass : _COMPONENTS.keySet()) {
                A_Component component;

                component = _COMPONENTS.get(componentClass)[id];
                if (component != null) component.unuse();

                _COMPONENTS.get(componentClass)[id] = null;
            }

            if (id == _maxComponentId) {
                _COMPONENTS.replaceAll((c, v) -> Arrays.copyOf(_COMPONENTS.get(c), _maxComponentId));
                _maxComponentId--;
            }
        }
        public void rmvEntities() {
            HashSet<Integer> entities;

            entities = new HashSet<>(_ENTITIES.keySet());

            entities.forEach(this::rmvEntity);
        }

        public <T extends A_Component> boolean addComponent(int entityId, T component, boolean override) {
            A_Component[] componentList;
            A_Component currentComponent;

            if (_ENTITIES.get(entityId) == null) throw new NoEntityException(
                    "[CONTEXT ERROR] : An entity with the specified id does not exist in the specified context!" + '\n'
                            + "|-> Context Id : " + _ID      + '\n'
                            + "|-> Entity Id  : " + entityId + '\n'
            );

            currentComponent = (componentList = _COMPONENTS.computeIfAbsent(component.getClass(), (componentClass) -> new A_Component[_maxComponentId + 1]))[entityId];
            if (currentComponent != null) {
                if (!override) return false;
                else currentComponent.unuse();
            }

            componentList[entityId] = component;
            component.use();

            for (A_Processor processor : _PROCESSORS) processor.p_validateEntity(_ID, entityId);

            return true;
        }
        public <T extends A_Component> boolean rmvComponent(int entityId, Class<T> componentClass) {
            A_Component[] componentList;
            A_Component currentComponent;

            if (_ENTITIES.get(entityId) == null) throw new NoEntityException(
                    "[CONTEXT ERROR] : An entity with the specified id does not exist in the specified context!" + '\n'
                            + "|-> Context Id : " + _ID      + '\n'
                            + "|-> Entity Id  : " + entityId + '\n'
            );

            componentList = _COMPONENTS.get(componentClass);
            if (componentList == null) return false;

            currentComponent = componentList[entityId];
            if (currentComponent == null) return false;

            componentList[entityId] = null;
            currentComponent.unuse();

            for (A_Processor processor : _PROCESSORS) processor.p_validateEntity(_ID, entityId);

            return true;
        }

        public boolean attachProcessor(A_Processor processor) {
            if (_PROCESSORS.contains(processor)) return false;

            processor.p_init(_ID);
            _PROCESSORS.add(processor);
            _ENTITIES.keySet().forEach((entityId) -> processor.p_validateEntity(_ID, entityId));

            return true;
        }
        public boolean detachProcessor(A_Processor processor) {
            if (!_PROCESSORS.remove(processor)) return false;

            processor.p_removeContext(_ID);

            return true;
        }

        public void update() {
            for (A_Processor processor : _PROCESSORS) processor.p_updateEntities(_ID, _ENTITIES.keySet());
        }

    }

    private static final Indexer s_CONTEXT_INDEXER = new Indexer();
    private static final HashMap<Integer, ContextData> s_CONTEXTS = new HashMap<>();

    /**
     * Creates a new {@link Context} and returns it's id.
     *
     * @return Context's id
     *
     * @author Tim Kloepper
     */
    public static int zCreateContextId() {
        int id;
        ContextData contextData;

        id = s_CONTEXT_INDEXER.get();
        contextData = new ContextData(id, new Context(id));

        s_CONTEXTS.put(id, contextData);

        return id;
    }
    /**
     * Creates a new {@link Context} and returns it.
     *
     * @return A new context.
     *
     * @author Tim Kloepper
     */
    public static Context zCreateContext() {
        int id;

        id = zCreateContextId();

        return _getContextData(id)._CONTEXT;
    }

    /**
     * Returns the {@link Context} object, based on its id.
     *
     * @param contextId The id of the context.
     *
     * @return The context with the specified id.
     *
     * @author Tim Kloepper
     */
    public static Context zGetContext(int contextId) {
        return _getContextData(contextId)._CONTEXT;
    }

    /**
     * Returns the ids of all {@link Context} objects.
     *
     * @return All context ids.
     *
     * @author Tim Kloepper
     */
    public static Collection<Integer> zGetContextIds() {
        return new HashSet<>(s_CONTEXTS.keySet());
    }
    /**
     * Returns all {@link Context} objects.
     *
     * @return All contexts.
     *
     * @author Tim Kloepper
     */
    public static Collection<Context> zGetContexts() {
        ArrayList<Context> contexts;

        contexts = new ArrayList<>();

        for (ContextData contextData : s_CONTEXTS.values()) contexts.add(contextData._CONTEXT);

        return contexts;
    }

    /**
     * Deletes the {@link Context} with the specified id. <br>
     * This will also delete all {@link Entity} objects contained by this context.
     *
     * @param id The id of the context.
     *
     * @author Tim Kloepper
     */
    public static void zDisposeContext(int id) {
        ContextData contextData;

        if ((contextData = s_CONTEXTS.remove(id)) == null) return;

        contextData.rmvEntities();

        s_CONTEXT_INDEXER.free(id);
    }
    /**
     * Deletes the specified {@link Context}. <br>
     * This will also delete all {@link Entity} objects contained by this context.
     *
     * @param context The context which is to be deleted.
     *
     * @author Tim Kloepper
     */
    public static void zDisposeContext(Context context) {
        zDisposeContext(context.id());
    }

    /**
     * Creates a new {@link Entity} in the {@link Context} with the specified id and returns the entities id.
     *
     * @param contextId The id of the context, the entity should be created in.
     *
     * @return The id of the new entity in the specified context.
     *
     * @author Tim Kloepper
     */
    public static int zCreateEntityId(int contextId) {
        ContextData contextData;

        contextData = _getContextData(contextId);

        return contextData.createEntity();
    }
    /**
     * Creates a new {@link Entity} in the specified {@link Context} and returns the entities id.
     *
     * @param context The context, the entity should be created in.
     *
     * @return The id of the new entity in the specified context.
     *
     * @author Tim Kloepper
     */
    public static int zCreateEntityId(Context context) {
        return zCreateEntityId(context.id());
    }

    /**
     * Creates a new {@link Entity} in the {@link Context} and returns the entity.
     *
     * @param contextId The id of the context, the entity should be created in.
     *
     * @return The new entity in the specified context.
     *
     * @author Tim Kloepper
     */
    public static Entity zCreateEntity(int contextId) {
        int id;

        id = zCreateEntityId(contextId);

        return zGetEntity(id, contextId);
    }
    /**
     * Creates a new {@link Entity} in the specified {@link Context} and returns the new entity.
     *
     * @param context The context, the entity should be created in.
     *
     * @return The new entity in the specified context.
     *
     * @author Tim Kloepper
     */
    public static Entity zCreateEntity(Context context) {
        return zCreateEntity(context.id());
    }

    /**
     * Returns the ids of all the {@link Entity} objects inside the {@link Context} with the specified id.
     *
     * @param contextId The id of the context of the entities.
     *
     * @return All the entity ids in the specified context.
     *
     * @author Tim Kloepper
     */
    public static Collection<Integer> zGetEntityIds(int contextId) {
        return new HashSet<>(_getContextData(contextId)._ENTITIES.keySet());
    }
    /**
     * Returns the ids of all the {@link Entity} objects inside the specified {@link Context}.
     *
     * @param context The context of the entities.
     *
     * @return All the entity ids in the specified context.
     *
     * @author Tim Kloepper
     */
    public static Collection<Integer> zGetEntityIds(Context context) {
        return zGetEntityIds(context.id());
    }

    /**
     * Returns all the {@link Entity} objects in the {@link Context} with the specified id.
     *
     * @param contextId The id of the context of the entities.
     *
     * @return All the entities in the specified context.
     *
     * @author Tim Kloepper
     */
    public static Collection<Entity> zGetEntities(int contextId) {
        ArrayList<Entity> entities;

        entities = new ArrayList<>(_getContextData(contextId)._ENTITIES.values());

        return entities;
    }
    /**
     * Returns all the {@link Entity} objects in the specified {@link Context}.
     *
     * @param context The context of the ids.
     *
     * @return All the entities in the specified context.
     *
     * @author Tim Kloepper
     */
    public static Collection<Entity> zGetEntities(Context context) {
        return zGetEntities(context.id());
    }

    /**
     * Returns the {@link Entity} object of the specified entity id in the {@link Context} of the specified id.
     *
     * @param entityId The id of the entity.
     * @param contextId The id of the context.
     *
     * @return The entity of the specified entity id.
     *
     * @author Tim Kloepper
     */
    public static Entity zGetEntity(int entityId, int contextId) {
        return _getContextData(contextId).getEntity(entityId);
    }
    /**
     * Returns the {@link Entity} object of the specified entity id in the specified {@link Context}.
     *
     * @param entityId The id of the entity.
     * @param context The context.
     *
     * @return The entity of the specified entity id.
     *
     * @author Tim Kloepper
     */
    public static Entity zGetEntity(int entityId, Context context) {
        return _getContextData(context.id()).getEntity(entityId);
    }

    /**
     * Deletes the {@link Entity} object of the specified entity id in the {@link Context} of the specified id.
     *
     * @param contextId The id of the context;
     * @param entityId The id of the entity.
     *
     * @author Tim Kloepper
     */
    public static void zDisposeEntity(int contextId, int entityId) {
        _getContextData(contextId).rmvEntity(entityId);
    }
    /**
     * Deletes the {@link Entity} object of the specified entity in the specified {@link Context}.
     *
     * @param context The context;
     * @param entityId The id of the entity.
     *
     * @author Tim Kloepper
     */
    public static void zDisposeEntity(Context context, int entityId) {
        zDisposeEntity(context.id(), entityId);
    }
    /**
     * Deletes the specified {@link Entity} object in the {@link Context} of the specified id.
     *
     * @param contextId The id of the context;
     * @param entity The entity.
     *
     * @author Tim Kloepper
     */
    public static void zDisposeEntity(int contextId, Entity entity) {
        zDisposeEntity(contextId, entity.id());
    }
    /**
     * Deletes the specified {@link Entity}.
     *
     * @param entity The id of the entity.
     *
     * @author Tim Kloepper
     */
    public static void zDisposeEntity(Entity entity) {
        zDisposeEntity(entity.contextId(), entity.id());
    }

    /**
     * Attaches the {@link A_Processor} to the {@link Context} of the specified id. <br>
     * Every processor can only ever be in a context once at a time.
     *
     * @param contextId The id of the context.
     * @param processor The processor.
     *
     * @return Returns {@code true} on success and {@code false} on failure.
     *
     * @author Tim Kloepper
     */
    public static boolean zAttachProcessor(int contextId, A_Processor processor) {
        return _getContextData(contextId).attachProcessor(processor);
    }
    /**
     * Attaches the {@link A_Processor} to the specified {@link Context}. <br>
     * Every processor can only ever be in a context once at a time.
     *
     * @param context The context.
     * @param processor The processor.
     *
     * @return Returns {@code true} on success and {@code false} on failure.
     *
     * @author Tim Kloepper
     */
    public static boolean zAttachProcessor(Context context, A_Processor processor) {
        return zAttachProcessor(context.id(), processor);
    }

    /**
     * Detaches the {@link A_Processor} from the {@link Context} of the specified id. <br>
     *
     * @param contextId The id of the context.
     * @param processor The processor.
     *
     * @return Returns {@code true} on success and {@code false} on failure.
     *
     * @author Tim Kloepper
     */
    public static boolean zDetachProcessor(int contextId, A_Processor processor) {
        return _getContextData(contextId).detachProcessor(processor);
    }
    /**
     * Detaches the {@link A_Processor} from the specified {@link Context}. <br>
     *
     * @param context The context.
     * @param processor The processor.
     *
     * @return Returns {@code true} on success and {@code false} on failure.
     *
     * @author Tim Kloepper
     */
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
        return _getContextData(contextId).rmvComponent(entityId, component.getClass());
    }
    public static <T extends A_Component> boolean zRmvComponent(Context context, int entityId, T component) {
        return _getContextData(context.id()).rmvComponent(entityId, component.getClass());
    }
    public static <T extends A_Component> boolean zRmvComponent(Entity entity, T component) {
        return _getContextData(entity.contextId()).rmvComponent(entity.id(), component.getClass());
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
        return _getContextData(contextId)._ENTITIES.containsKey(entityId);
    }
    public static boolean zContainsEntity(Context context, int entityId) {
        return zContainsEntity(context.id(), entityId);
    }
    public static boolean zContainsEntity(int contextId, Entity entity) {
        return zContainsEntity(contextId, entity.id());
    }
    public static boolean zContainsEntity(Context context, Entity entity) {
        return zContainsEntity(context.id(), entity.id());
    }

    public static void zUpdate() {
        for (ContextData contextData : s_CONTEXTS.values()) contextData.update();
    }

    public static int zSwitchContext(int entityId, int contextId, int toContextId) {
        ContextData from, to;
        HashMap<Class<? extends A_Component>, A_Component> components;
        Entity entity;

        from = _getContextData(contextId);
        to = _getContextData(toContextId);

        components = from.getEntityComponents(entityId);
        entity = from.getEntity(entityId);

        from.rmvEntity(entityId);

        return to.addEntity(components, entity);
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
        return Collections.unmodifiableMap(_getContextData(contextId).getEntityComponents(entityId));
    }

    private static ContextData _getContextData(int id) {
        ContextData contextData;

        contextData = s_CONTEXTS.get(id);
        if (contextData == null) throw new NoContextException(
                "[CONTEXT ERROR] : No context with such an id!" + '\n'
                + "|-> Context Id : " + id + '\n'
        );

        return contextData;
    }

}