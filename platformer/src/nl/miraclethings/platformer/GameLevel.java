package nl.miraclethings.platformer;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class GameLevel {

	public World world;
	private String levelname;
	Array<MovingPlatform> platforms;
	Array<Crate> crates;

	int width;
	int height;
	private SpriteBatch batch;
	private Sprite background;
	private Vector2 playerOrigin;
	private Sprite testSprite;
	private Body testBody;

	static float SCALE = 0.02f;
	static float SCALE_INV = 50f;

	public GameLevel(String levelname) {

		this.world = new World(new Vector2(0, -20), true);

		this.levelname = levelname;
		this.platforms = new Array<MovingPlatform>();

		loadGeometry();

		platforms.add(new MovingPlatform(world, -2, 3, 2, 0.5f, 2, 0, 4));
		platforms.add(new MovingPlatform(world, 17, 3, 5, 0.5f, 0, 2, 5));
		platforms.add(new MovingPlatform(world, -7, 5, 2, 0.5f, -2, 2, 8));
		platforms.add(new MovingPlatform(world, 40, 3, 20, 0.5f, 0, 2, 5));

		batch = new SpriteBatch();
		
		this.crates = new Array<Crate>();
		for(int i = 0; i < 40; i++) {
			Crate crate = new Crate(this);
			crate.getBody().setTransform((float)Math.random() * 100f, (float)Math.random() * 10 + 6, 0);//(float)(Math.random() * 2 * Math.PI));
			this.crates.add(crate);
		}		

		loadBitmaps();
	}

	public float getScale() {
		return SCALE;
	}
	private void loadBitmaps() {

		// De background texture
		Texture texture = new Texture(Gdx.files.internal(levelname + ".png"));
		background = new Sprite(texture, width, height);
		background.setOrigin(0, 0);
		background.setScale(SCALE, SCALE);
	}

	protected void loadGeometry() {
		FileHandle handle = Gdx.files.internal(levelname + ".svg");

		Document doc = null;
		try {
			doc = Jsoup.parse(handle.read(), "utf-8", "", Parser.xmlParser());
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		// Get the world dimensions
		Element svg = doc.child(0);
		width = Integer.parseInt(svg.attr("width"));
		height = Integer.parseInt(svg.attr("height"));

		Gdx.app.log("level", "width: " + width);

		float x, y, width, height;

		// get all objects
		Element objectLayer = doc.select("#objects").first();

		Vector2 baseTransform = parseSVGTransform(objectLayer.attr("transform"));
		Gdx.app.log("WorldLoader", "v: " + baseTransform.toString());

		for (Element rect : objectLayer.select("rect")) {
			x = Float.parseFloat(rect.attr("x"));
			y = Float.parseFloat(rect.attr("y"));
			width = Float.parseFloat(rect.attr("width"));
			height = Float.parseFloat(rect.attr("height"));

			// transform from top-left to bottom-left origin
			y = this.height - y - height;

			// get svg transform offset
			Vector2 tr = parseSVGTransform(rect.attr("transform"));

			Body box = WorldUtil.createBox(world, BodyType.StaticBody, 0.5f
					* width * SCALE, 0.5f * height * SCALE, 0f);
			box.setTransform((baseTransform.x - tr.x + x + width * 0.5f)
					* SCALE, (baseTransform.y - tr.y + y + height * 0.5f)
					* SCALE, 0f);

		}

		// Load the player origin
		Element originRect = doc.select("#player-origin rect").first();
		playerOrigin = new Vector2(
				Float.parseFloat(originRect.attr("x"))* SCALE, 
				Float.parseFloat(originRect.attr("y")) * SCALE
			);
 	}

	public World getWorld() {
		return this.world;
	}

	public void update(OrthographicCamera cam) {

		batch.setProjectionMatrix(cam.combined);
		batch.begin();
		background.draw(batch);

		// update platforms
		for (int i = 0; i < platforms.size; i++) {
			MovingPlatform platform = platforms.get(i);
			platform.update(Math.max(1 / 30.0f, Gdx.graphics.getDeltaTime()));
		}

		// update the crates
		for (Crate c : crates) {
			c.update();
			c.getSprite().draw(batch);
		}

		batch.end();
		
		// step the world
		world.step(Gdx.graphics.getDeltaTime(), 4, 4);
	}

	// "translate(0,-28.362183)"
	private Vector2 parseSVGTransform(String transform) {
		transform = transform.trim();
		if (transform.equals("")) {
			return new Vector2(0f, 0f);
		}

		if (!transform.startsWith("translate(") || !transform.endsWith(")")) {
			return null;
		}
		transform = transform.substring("translate(".length(),
				transform.length() - 1);
		String parts[] = transform.split(",");
		if (parts.length != 2)
			return null;

		return new Vector2(Float.parseFloat(parts[0].trim()),
				Float.parseFloat(parts[1].trim()));
	}

	public Vector2 getPlayerOrigin() {
		return playerOrigin;
	}

}
