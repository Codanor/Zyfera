import Zyfera.A_Component;
import Zyfera.Context;
import Zyfera.Entity;
import Zyfera.Test.TestComponent;
import Zyfera.Test.TestProcessor;

import static Zyfera.Zyfera.*;

public class Main {

    static void main() {
        Context context, anotherContext;
        Entity entity;

        context = zCreateContext();
        anotherContext = zCreateContext();
        entity = zCreateEntity(context);

        entity.attach(new TestComponent(A_Component.ACCESS_TYPE.UNIQUE), false);
        context.attach(new TestProcessor());

        System.out.println(entity.id());

        entity.switchContext(anotherContext);

        System.out.println(entity.id());

        zUpdate();
    }

}