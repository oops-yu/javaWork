package ufo;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;

import javafx.scene.Group;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.geometry.Point2D;
import java.util.Map;
import java.util.List;
import static com.almasb.fxgl.dsl.FXGL.*;
import static ufo.EntityType.*;
import ufo.components.*;
import com.almasb.fxgl.time.*;

/**
 * @author oops-yu@github.com (473211890@qq.com)
 */
public class UfoGame extends GameApplication {
    TimerAction autoGenUfo;

    @Override
    protected void initSettings(GameSettings settings) {

        settings.setWidth(300);
        settings.setHeight(300);
        settings.setTitle("UFO");
        settings.setVersion("1.1");
    }

    @Override
    protected void initInput() {

        getInput().addAction(new UserAction("Click") {

            @Override
            protected void onActionEnd() {

                List<Entity> entities = getGameWorld().getEntities();
                Entity tmpEntity = null;
                EntityType type = UFO1;
                for (Entity e : entities) {
                    if (e.isType(UFO1) || e.isType(UFO2) || e.isType(UFO3)) {
                        // 进一步地，如果鼠标点击的位置在UFO上则退出循环，代表已找到鼠标点击的UFO
                        Point2D mousePos = getInput().getMousePositionWorld();
                        Point2D targetPos = e.getPosition();
                        if (mousePos.getX() > targetPos.getX()
                                && mousePos.getX() < targetPos.getX() + 30
                                && mousePos.getY() > targetPos.getY()
                                && mousePos.getY() < targetPos.getY() + 30) {
                            tmpEntity = e;
                            break;
                        }

                    }
                }

                if (tmpEntity != null) {
                    type = (EntityType) tmpEntity.getType();
                    tmpEntity.removeFromWorld();
                    int scoreAdd = 0;
                    switch (type) {
                        case UFO1:
                            scoreAdd = 1;
                            break;
                        case UFO2:
                            scoreAdd = 2;
                            break;
                        case UFO3:
                            scoreAdd = 3;
                            break;
                        default:
                            break;
                    }
                    inc("score", scoreAdd);
                    // 减少存活的ufo数量
                    inc("aliveUfoesLeft", -1);
                }
                // 减少子弹数量
                inc("shotsLeft", -1);

            }
        }, MouseButton.PRIMARY);
        // for test

    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {

        vars.put("score", 0);
        vars.put("shotsLeft", 30);
        vars.put("citiesLeft", 10);
        vars.put("shieldsLeft", 5);
        vars.put("ufoesLeft", 20);
        vars.put("aliveUfoesLeft", 0);
        vars.put("city1Health", 5);
        vars.put("city2Health", 5);
    }

    @Override
    protected void onPreInit() {
    }

    @Override
    protected void initGame() {

        getGameWorld().addEntityFactory(new UfoEntityFactory());
        initBackground();
        initCities();
        getGameWorld().spawn("UFO");
        autoGenUfo = getGameTimer().runAtInterval(() -> {
            getGameWorld().spawn("UFO");
            inc("ufoesLeft", -1);
            inc("aliveUfoesLeft", +1);
        }, Duration.seconds(3 * 2));

    }

    @Override
    protected void initPhysics() {
        // 当子弹打到城市的时候
        onCollisionBegin(BULLET, CITY, (bullet, city) -> {

            bullet.removeFromWorld();

            if (geti("shieldsLeft") > 0) { // 护盾值大于0时，扣除护盾

                inc("shieldsLeft", -1);

            } else { // 否则扣除血量
                String targetCityHealth = "city1Health";
                if (city.getPosition().getX() > getAppWidth() / 2)
                    targetCityHealth = "city2Health";// 在1/2屏幕右边的city为第二个城市

                // 扣除血量
                inc(targetCityHealth, -1);
                inc("citiesLeft", -1);

                // 当某个city的血量见底时，替换为城市被摧毁的纹理
                if (geti(targetCityHealth) == 0) {
                    getGameWorld().spawn("CITYBURN", city.getX(), city.getY());
                    city.removeFromWorld();
                }
            }

        });
        // 子弹碰到地板则清除子弹
        onCollisionBegin(BULLET, GROUND, (bullet, ground) -> {
            bullet.removeFromWorld();
        });
    }

    @Override
    protected void initUI() {
        // ============== score =================//
        Text scoreText = new Text("Score:");
        scoreText.setTranslateX(20);
        scoreText.setTranslateY(20);
        scoreText.setFill(Color.rgb(0, 255, 0));

        Text uiScore = new Text("");
        uiScore.setFont(Font.font(12));
        uiScore.setFill(Color.rgb(0, 255, 0));
        uiScore.setTranslateX(80);
        uiScore.setTranslateY(20);
        uiScore.textProperty().bind(getip("score").asString());

        addUINode(scoreText);
        addUINode(uiScore);
        // ============= score ===============//

        // ============= Shots left ==========//
        Text shotsLeftText = new Text("Shots left:");
        shotsLeftText.setTranslateX(20);
        shotsLeftText.setTranslateY(40);
        shotsLeftText.setFill(Color.rgb(0, 255, 0));

        Text uiShotsLeft = new Text("");
        uiShotsLeft.setFont(Font.font(12));
        uiShotsLeft.setFill(Color.rgb(0, 255, 0));
        uiShotsLeft.setTranslateX(80);
        uiShotsLeft.setTranslateY(40);
        uiShotsLeft.textProperty().bind(getip("shotsLeft").asString());

        addUINode(shotsLeftText);
        addUINode(uiShotsLeft);
        // ============= Shots left ==========//

        // ============= Cities left ==========//
        Text citiesLeftText = new Text("Cities left:");
        citiesLeftText.setTranslateX(getAppWidth() - 100);
        citiesLeftText.setTranslateY(20);
        citiesLeftText.setFill(Color.rgb(0, 255, 0));

        Text uiCitiesLeft = new Text("");
        uiCitiesLeft.setFont(Font.font(12));
        uiCitiesLeft.setFill(Color.rgb(0, 255, 0));
        uiCitiesLeft.setTranslateX(getAppWidth() - 40);
        uiCitiesLeft.setTranslateY(20);
        uiCitiesLeft.textProperty().bind(getip("citiesLeft").asString());

        addUINode(citiesLeftText);
        addUINode(uiCitiesLeft);
        // ============= Cities left ==========//

        // ============= Shields left ==========//
        Text shieldsLeftText = new Text("Shields left:");
        shieldsLeftText.setTranslateX(getAppWidth() - 100);
        shieldsLeftText.setTranslateY(40);
        shieldsLeftText.setFill(Color.rgb(0, 255, 0));

        Text uiShieldsLeft = new Text("");
        uiShieldsLeft.setFont(Font.font(12));
        uiShieldsLeft.setFill(Color.rgb(0, 255, 0));
        uiShieldsLeft.setTranslateX(getAppWidth() - 30);
        uiShieldsLeft.setTranslateY(40);
        uiShieldsLeft.textProperty().bind(getip("shieldsLeft").asString());

        addUINode(shieldsLeftText);
        addUINode(uiShieldsLeft);
        // ============= Shields left ==========//

    }

    @Override
    protected void onUpdate(double tpf) {

        if (geti("shotsLeft") == 0 && geti("aliveUfoesLeft") + geti("ufoesLeft") > 0 || geti("citiesLeft") == 0) {
            showGameOver();
        }

        if (geti("ufoesLeft") == 0) {
            autoGenUfo.pause();
            if (geti("aliveUfoesLeft") == 0) {
                showGoodGame();
            }
        }

    }

    private void initBackground() {
        // 黑色背景
        Rectangle rect = new Rectangle(getAppWidth(), getAppHeight() * 9 / 10, Color.BLACK);
        Rectangle bottom = new Rectangle(getAppWidth(), getAppHeight() / 10, Color.rgb(0, 255, 0));
        Entity bg = entityBuilder()
                .view(rect)
                .with("rect", rect)
                .buildAndAttach();
        // 绿色地板
        Entity bg_bottom = entityBuilder()
                .type(GROUND)
                .at(0, getAppHeight() * 9 / 10)
                .viewWithBBox(bottom)
                .with("rect", bottom)
                .collidable()
                .buildAndAttach();

        // 生成50颗星星
        for (int i = 0; i < 50; i++) {
            getGameWorld().spawn("STAR");
        }
    }

    private void initCities() {

        getGameWorld().spawn("CITY", getAppWidth() / 3 - 20, getAppHeight() * 9 / 10 - 30);
        getGameWorld().spawn("CITY", getAppWidth() * 2 / 3 - 20, getAppHeight() * 9 / 10 - 30);

    }

    private void showGameOver() {

        String msg = String.format("保卫失败!score:%d", geti("score"));
        showMessage(msg, getGameController()::exit);

    }

    private void showGoodGame() {

        String msg = String.format("真厉害!score:%d", geti("score"));
        showMessage(msg, getGameController()::exit);
        
    }

    public static void main(String[] args) {
        launch(args);
    }
}
