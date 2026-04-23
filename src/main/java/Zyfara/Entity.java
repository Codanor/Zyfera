package Zyfara;

import java.util.Collection;

public class Entity {

    public static class Stream {

        private Stream(int id, int contextId) {
            _id = id;
            _contextId = contextId;
        }

        private int _id, _contextId;

        public <T extends A_Component> Stream add(T component, boolean override) {
            Entity.addComponentToEntity(_id, _contextId, component, override);

            return this;
        }

        public <T extends A_Component> Stream rmv(T component) {
            Entity.rmvComponentFromEntity(_id, _contextId, component);

            return this;
        }
        public <T extends A_Component> Stream rmv(Class<T> componentClass) {
            Entity.rmvComponentFromEntity(_id, _contextId, componentClass);

            return this;
        }

        public int id() {
            return _id;
        }

    }

    public static Stream entityStream(int id, int contextId) {
        if (!Context.contextContainsEntity(contextId, id)) throw new EntityDoesNotExist("[ENTITY] : Entity with the specified id does not exists : " + id + "!");

        return new Stream(id, contextId);
    }

    public static int createEntity(int contextId) {
        return Context.p_createEntity(contextId);
    }

    public static Collection<Integer> allEntities(int context) {
        return Context.allEntitiesOfContext(context);
    }

    public static <T extends A_Component> boolean addComponentToEntity(int id, int contextId, T component, boolean override) {
        if (component == null) return false;
        if (!Context.contextContainsEntity(contextId, id)) throw new EntityDoesNotExist("[ENTITY] : Entity with the specified id does not exists : " + id + "!");

        if (!Context.p_addEntityComponent(contextId, id, component, override)) return false;

        Context.p_revalidateEntity(contextId, id);

        return true;
    }

    public static <T extends A_Component> boolean rmvComponentFromEntity(int id, int contextId, T component) {
        if (component == null) return false;
        if (!Context.contextContainsEntity(contextId, id)) throw new EntityDoesNotExist("[ENTITY] : Entity with the specified id does not exists : " + id + "!");

        if (!Context.p_rmvEntityComponent(contextId, id, component)) return false;

        Context.p_revalidateEntity(contextId, id);

        return true;
    }
    public static <T extends A_Component> boolean rmvComponentFromEntity(int id, int contextId, Class<T> componentClass) {
        if (componentClass == null) return false;
        if (!Context.contextContainsEntity(contextId, id)) throw new EntityDoesNotExist("[ENTITY] : Entity with the specified id does not exists : " + id + "!");

        if (!Context.p_rmvEntityComponent(contextId, id, componentClass)) return false;

        Context.p_revalidateEntity(contextId, id);

        return true;
    }

    public static <T extends A_Component> T getComponentFromEntity(int id, int contextId, Class<T> componentClass) {
        if (!Context.contextContainsEntity(contextId, id)) throw new EntityDoesNotExist("[ENTITY] : Entity with the specified id does not exists : " + id + "!");

        return Context.p_getEntityComponent(contextId, id, componentClass);
    }

    public static void disposeEntity(int id, int contextId) {
        if (!Context.contextContainsEntity(contextId, id)) throw new EntityDoesNotExist("[ENTITY] : Entity with the specified id does not exists : " + id + "!");

        Context.p_rmvEntity(id, contextId);
    }

}