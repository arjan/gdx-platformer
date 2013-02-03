package nl.miraclethings.platformer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class AnimatedObject {
	private Sprite sprite;
	private Body body;
	private GameLevel level;
	private float scale;
	private Animation walkAnimation;
	private float stateTime;

	private static int FRAME_COLS = 8;
	private static int FRAME_ROWS = 8;
	
	public AnimatedObject(GameLevel level) {
		this.level = level;

        Texture walkSheet = new Texture(Gdx.files.internal("data/character.png"));    
        TextureRegion[][] tmp = TextureRegion.split(walkSheet, walkSheet.getWidth() / 
        		FRAME_COLS, walkSheet.getHeight() / FRAME_ROWS); 
        
        TextureRegion[] walkFrames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
        int index = 0;
        for (int i = 0; i < FRAME_ROWS; i++) {
                for (int j = 0; j < FRAME_COLS; j++) {
                        walkFrames[index++] = tmp[i][j];
                }
        }
        walkAnimation = new Animation(1/12f, walkFrames);
        stateTime = 0f;                                                 // #13
        
		sprite = new Sprite(walkAnimation.getKeyFrame(0f));
		Gdx.app.log("X", "S:" + sprite.getWidth() + " " + sprite.getHeight());
		sprite.setOrigin(32, 32);
		
		// Transformeer de grootte van de box naar game units (1x1)
		scale = 2/64.0f; /// 1/(walkSheet.getWidth() / FRAME_COLS);
		
		sprite.setScale(scale, scale);
		Gdx.app.log("X", "S:" + sprite.getWidth() * scale);
		
		this.body = WorldUtil.createBox(level.getWorld(), BodyType.DynamicBody, 
				0.5f * sprite.getWidth() * scale, 
				0.5f * sprite.getHeight() * scale, 3);
	}
	
	public Body getBody() {
		return body;
	}
	
	public Sprite getSprite() {
		return sprite;
	}
	
	/**
	 * Pas de positie en rotatie van de sprite aan aan die van het physics model
	 */
	public void update() {
		Vector2 v = body.getPosition();
		float w = sprite.getWidth();
		float h = sprite.getHeight();
		
		stateTime += Gdx.graphics.getDeltaTime(); 
		
		sprite.setPosition(v.x-0.5f * w, v.y - 0.5f * h);
		sprite.setRotation(MathUtils.radiansToDegrees * body.getAngle());
		sprite.setRegion(walkAnimation.getKeyFrame(stateTime, true));
	}
	
}
