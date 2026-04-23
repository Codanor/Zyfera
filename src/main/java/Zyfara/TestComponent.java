package Zyfara;

public class TestComponent extends A_Component {

    public TestComponent(ACCESS accessType, String data) {
        super(accessType);

        this.data = data;
    }

    public String data;

    @Override
    public A_Component copy() {
        return new TestComponent(getAccessType(), data);
    }

}