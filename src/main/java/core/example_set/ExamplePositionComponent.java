package core.example_set;

import core.A_Component;

public class ExamplePositionComponent<T extends Number> extends A_Component {

    public ExamplePositionComponent(ACCESS_TYPE accessType, T x, T y) {
        super(accessType);

        _x = x;
        _y = y;
    }

    private T _x, _y;

    public void setX(T x) {
        _x = x;
    }
    public void setY(T y) {
        _y = y;
    }

    public void set(T x, T y) {
        _x = x;
        _y = y;
    }

    public T getX() {
        return _x;
    }
    public T getY() {
        return _y;
    }

}