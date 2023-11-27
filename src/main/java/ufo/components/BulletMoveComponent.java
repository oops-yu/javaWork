package ufo.components;

import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;

import static com.almasb.fxgl.dsl.FXGL.*;
import javafx.geometry.Point2D;

public class BulletMoveComponent extends Component {
    private double speed = 300;// pix/s

    @Override
    public void onUpdate(double tpf) {

        entity.translate(0, speed * tpf);

    }

}
