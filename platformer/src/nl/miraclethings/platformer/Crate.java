package nl.miraclethings.platformer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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

		// Laad texture in en maak er een sprite van
		Texture texture = new Texture(Gdx.files.internal("data/crate.png"));
		TextureRegion region = new TextureRegion(texture, 0, 0, texture.getWidth(), texture.getHeight());
		sprite = new Sprite(region);

		// Transformeer de grootte van de box naar game units (1x1)
		scale = 1/sprite.getWidth();
		// Varieer een beetje, voor de gein
		scale *= (0.4 + 1.4 * Math.random());
		
		sprite.setScale(scale, scale);
		
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
		
		sprite.setPosition(v.x-0.5f * w, v.y - 0.5f * h);
		sprite.setRotation(MathUtils.radiansToDegrees * body.getAngle());
	}
	
}
