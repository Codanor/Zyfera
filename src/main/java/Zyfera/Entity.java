package Zyfera;

public class Entity {

    protected Entity(int entityId, int contextId) {
        _contextId = contextId;

        _id = entityId;
    }

    public class Stream {

        public Stream(Entity entity) {
            _ENTITY = entity;
        }

        private final Entity _ENTITY;

        public <T extends A_Component> Stream attach(T component, boolean override) {
            Zyfera.zAddComponent(_ENTITY.contextId(), _ENTITY.id(), component, override);

            return this;
        }

        public <T extends A_Component> Stream detach(T component) {
            Zyfera.zRmvComponent(_ENTITY.contextId(), _ENTITY.id(), component);

            return this;
        }
        public <T extends A_Component> Stream detach(Class<T> componentClass) {
            Zyfera.zRmvComponent(_ENTITY.contextId(), _ENTITY.id(), componentClass);

            return this;
        }

        public Stream switchContext(int contextId) {
            _ENTITY.switchContext(contextId);

            return this;
        }
        public Stream switchContext(Context context) {
            _ENTITY.switchContext(context);

            return this;
        }

        public int id() {
            return _ENTITY.id();
        }
        public Entity entity() {
            return _ENTITY;
        }

    }

    private int _id, _contextId;

    private Stream _stream;

    public <T extends A_Component> boolean attach(T component, boolean override) {
        return Zyfera.zAddComponent(_contextId, _id, component, override);
    }

    public <T extends A_Component> boolean detach(T component) {
        return Zyfera.zRmvComponent(_contextId, _id, component);
    }
    public <T extends A_Component> boolean detach(Class<T> componentClass) {
        return Zyfera.zRmvComponent(_contextId, _id, componentClass);
    }

    public int switchContext(int toId) {
        return Zyfera.zSwitchContext(this, toId);
    }
    public int switchContext(Context to) {
        return Zyfera.zSwitchContext(this, to);
    }

    public int id() {
        return _id;
    }
    public Stream stream() {
        if (_stream == null) _stream = new Stream(this);

        return _stream;
    }

    public int contextId() {
        return _contextId;
    }
    public Context context() {
        return Zyfera.zGetContext(_contextId);
    }

    protected void p_onContextSwitch(int newId, int newContextId) {
        _id = newId;
        _contextId = newContextId;
    }

}