package ufo.components;

import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;

import static com.almasb.fxgl.dsl.FXGL.*;
import javafx.util.Duration;

import javafx.geometry.Point2D;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.time.*;

public class Ufo1ShootComponent extends Component {
    private double attackInterval = 6.0; //单位 num/s
    private TimerAction autoAttack;
    @Override
    public void onAdded(){
        autoAttack = getGameTimer().runAtInterval(() -> {
            this.attack();
        },Duration.seconds(attackInterval));
    }
    @Override
    public void onRemoved(){
        autoAttack.expire();
    }
    public void  attack() {

        Point2D pos = entity.getPosition();
        getGameWorld().spawn("BULLET",pos.getX()+15,pos.getY()+30);
    }
}
