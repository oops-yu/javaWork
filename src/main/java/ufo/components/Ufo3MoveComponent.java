package ufo.components;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;

import static com.almasb.fxgl.dsl.FXGL.*;
import javafx.util.Duration;

import javafx.geometry.Point2D;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.time.*;

public class Ufo3MoveComponent extends Component {
    private double moveSpeed = 90.0;      //单位pix/s
    private double moveInterval = 1.0;   //单位 s
    private TimerAction autoMove;
    private int moveDirectionX;     // -1代表负方向，0代表不移动，1代表正方向
    private int moveDirectionY;     // 同上
    @Override
    public void onUpdate(double tpf){

        entity.translate(tpf * moveSpeed * moveDirectionX,tpf * moveSpeed * moveDirectionY);
        //对活动范围进行限定
        Point2D pos = entity.getPosition();
        if(pos.getX() < 0){
            
            moveDirectionX = 1;
        }else if(pos.getX() > getAppWidth() - 30){
            
            moveDirectionX = -1;
        }else if(pos.getY() > getAppHeight() / 2){
            
            moveDirectionY = -1;
        }else if(pos.getY() <0){
           
            moveDirectionY = 1;
        }

    }
    @Override
    public void onAdded(){

        //刚开始需要在左右两个方向挑一个进行运动，而不能不动
        moveDirectionX = FXGLMath.random(0,1) == 0 ? -1:1;
        
        autoMove = getGameTimer().runAtInterval(()  -> {
            int randomDirectionX = FXGLMath.random(0,1000);
            int randomDirectionY = FXGLMath.random(0,1000);

            if(randomDirectionX < 450){
             moveDirectionX = -1;
            }else if(randomDirectionX < 550){
             moveDirectionX =  0;
            }else{
             moveDirectionX = 1;
            }
 
            if(randomDirectionY < 450){
             moveDirectionY = -1;
            }else if(randomDirectionY < 550){
             moveDirectionY =  0;
            }else{
             moveDirectionY = 1;
            }
 
        },Duration.seconds(moveInterval));
    }
    @Override
    public void onRemoved(){

        autoMove.expire();
        
    }
}
