package nl.miraclethings.platformer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Crate {
	private Sprite sprite;
	private Body body;
	private GameLevel level;
	private float scale;

	public Crate(GameLevel level) {
		scale = level.getScale();
		this.level = level;
		
		//scale *= (0.9 + 0.5 * Math.random());
		scale = 1/256f;
		
		Gdx.app.log("x", "s: " + scale);
		Texture texture = new Texture(Gdx.files.internal("data/crate.png"));
		
		sprite = new Sprite(texture, texture.getWidth(), texture.getHeight());
		
		
//		sprite.setOrigin(1f, 1f);
//		sprite.setOrigin(0, 0);
//		sprite.setScale(2*scale, 2*scale);
		sprite.setScale(scale, scale);
//		sprite.setOrigin(0.5f, 0.5f);//, 0);
//		sprite.setOrigin(-0.5f, -0.5f);
//		Gdx.app.log("x", "s: " + sprite.getWidth());
		
		this.body = WorldUtil.createBox(level.getWorld(), BodyType.DynamicBody, 
				0.5f * sprite.getWidth() * scale, 
				0.5f * sprite.getHeight() * scale, 3);
//		this.body.setFixedRotation(true);
		
	}
	
	public Body getBody() {
		return body;
	}
	
	public Sprite getSprite() {
		return sprite;
	}
	
	public void update() {
		Vector2 v = body.getPosition();
		float w = sprite.getWidth() * scale;
		float h = sprite.getHeight() * scale;
		float a = body.getAngle();
		
		sprite.setPosition(v.x, v.y);
		sprite.setOrigin(0, 0);
		sprite.setRotation(MathUtils.radiansToDegrees * body.getAngle());
		sprite.translateX(-0.5f * MathUtils.cos(a));
		sprite.translateY(-0.5f * MathUtils.sin(a));
		
//		sprite.setOrigin(0f, 0f);
		
	}
	
}
