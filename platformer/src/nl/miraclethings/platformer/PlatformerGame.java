package nl.miraclethings.platformer;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
 
public class PlatformerGame extends InputAdapter implements ApplicationListener {
 
	World world;
	Player player;

	OrthographicCamera cam;
	Box2DDebugRenderer renderer;

	SpriteBatch batch;
	BitmapFont font;
	private GameLevel level;
 
	@Override
	public void create() {
		renderer = new Box2DDebugRenderer();
		cam = new OrthographicCamera(28, 20);
		createWorld();
		Gdx.input.setInputProcessor(this);
		batch = new SpriteBatch();
		font = new BitmapFont();
	}
 
	private void createWorld() {
		
		level = new GameLevel("data/world");
		world = level.getWorld();
		
		/*
		Body box = createBox(BodyType.StaticBody, 1, 1, 0);
		box.setTransform(30, 3, 0);
		box = createBox(BodyType.StaticBody, 1.2f, 1.2f, 0);
		box.setTransform(5, 2.4f, 0);
		*/
		
		player = new Player(level);
	
		
		Body box;
		for(int i = 0; i < 40; i++) {
			box = WorldUtil.createBox(world, BodyType.DynamicBody, (float)Math.random(), (float)Math.random(), 3);
			box.setTransform((float)Math.random() * 100f - (float)Math.random() * 10f, (float)Math.random() * 10 + 6, (float)(Math.random() * 2 * Math.PI));
		}
/* 
		for(int i = 0; i < 10; i++) {
			Body circle = createCircle(BodyType.DynamicBody, (float)Math.random() * 0.5f, 3);
			circle.setTransform((float)Math.random() * 10f - (float)Math.random() * 10f, (float)Math.random() * 10 + 6, (float)(Math.random() * 2 * Math.PI));
		}
 */
		
	}
 
	@Override
	public void resume() {
 
	}
 
	@Override
	public void render() {
		Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		Vector2 pos = player.getPosition();
		// Update the camera
		cam.position.set(pos.x, pos.y, 0);
		cam.zoom = 1;
		cam.update();
		cam.apply(Gdx.gl10);
		 
		// Update the player position, etc
		player.update();
		
		level.update(cam);

		renderer.render(world, cam.combined);
	}	
 
	@Override
	public void resize(int width, int height) {
 
	}
 
	@Override
	public void pause() {
 
	}
 
	@Override
	public void dispose() {
 
	}
 
	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Input.Keys.ESCAPE) 
			Gdx.app.exit();
		
		player.keyDown(keycode);
		return false;
	}
 
	@Override
	public boolean keyUp(int keycode) {
		player.keyUp(keycode);
		return false;
	}
 
	Vector2 last = null;
	Vector3 point = new Vector3();

	/*
	@Override
	public boolean touchDown(int x, int y, int pointerId, int button) {
		cam.unproject(point.set(x, y, 0));
 
		if(button == Input.Buttons.LEFT) {
			if(last == null) {
				last = new Vector2(point.x, point.y);
			} else {
				createEdge(BodyType.StaticBody, last.x, last.y, point.x, point.y, 0);
				last.set(point.x, point.y);
			}
		} else {
			last = null;
		}
 
		return false;
	}
	*/
	
}
