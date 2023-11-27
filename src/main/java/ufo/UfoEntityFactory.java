package ufo;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Ellipse;
import static com.almasb.fxgl.dsl.FXGL.*;
import com.almasb.fxgl.physics.*;
import static ufo.EntityType.*;
import ufo.components.*;
import com.almasb.fxgl.entity.component.Component;
import java.util.List;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.components.*;
import javafx.util.Duration;

public class UfoEntityFactory implements EntityFactory {
    @Spawns("UFO")
    public Entity createRandomUfo(SpawnData data) {

        int level = FXGLMath.random(1, 3);
        int x = FXGLMath.random(0, getAppWidth() - 30);
        int y = FXGLMath.random(0, getAppHeight() / 2);
        EntityType type = UFO1;
        if (level == 2)
            type = UFO2;
        if (level == 3)
            type = UFO3;
        return createUfo(type, x, y);

    }

    private Entity createUfo(EntityType type, int x, int y) {

        String image = "";
        Component moveComp;
        Component shootComp;
        switch (type) {
            case UFO1:
                image = "Ufo1.gif";
                moveComp = new Ufo1MoveComponent();
                shootComp = new Ufo1ShootComponent();
                break;
            case UFO2:
                image = "Ufo2.gif";
                moveComp = new Ufo2MoveComponent();
                shootComp = new Ufo2ShootComponent();
                break;
            case UFO3:
                image = "Ufo3.gif";
                moveComp = new Ufo3MoveComponent();
                shootComp = new Ufo3ShootComponent();
                break;

            default:
                return null;

        }

        Entity entity = FXGL.entityBuilder()
                .at(x, y)
                .type(type)
                .view(texture(image))
                .with(moveComp)
                .with(shootComp)
                .build();
        return entity;

    }

    @Spawns("CITY")
    public Entity createCity(SpawnData data) {

        return FXGL.entityBuilder()
                .at(data.getX(), data.getY())
                .type(CITY)
                .viewWithBBox(texture("City.gif"))
                .collidable()
                .build();

    }

    @Spawns("CITYBURN")
    public Entity createBurnCity(SpawnData data) {

        return FXGL.entityBuilder()
                .at(data.getX(), data.getY())
                .type(CITYBURN)
                .view(texture("CityBurn.gif"))
                .build();

    }

    @Spawns("STAR")
    public Entity createRandomStar(SpawnData data) {

        int x = FXGLMath.random(0, getAppWidth());
        int y = FXGLMath.random(0, getAppHeight() * 9 / 10 - 30);
        Rectangle rect = new Rectangle(getAppWidth() * 2 / 300, getAppHeight() / 300, Color.WHITE);
        return FXGL.entityBuilder()
                .at(x, y)
                .type(STAR)
                .view(rect)
                .build();

    }

    @Spawns("BULLET")
    public Entity createBullet(SpawnData data) {

        Ellipse e = new Ellipse();
        e.setFill(Color.RED);
        e.setRadiusX(3);
        e.setRadiusY(6);

        return FXGL.entityBuilder()
                .at(data.getX(), data.getY())
                .viewWithBBox(e)
                .type(BULLET)
                .with(new BulletMoveComponent())
                .with(new OffscreenCleanComponent())
                .with(new ExpireCleanComponent(Duration.seconds(2)))
                .collidable()
                .build();
                
    }
}
