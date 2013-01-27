package nl.miraclethings.platformer;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
 
public class PlatformerGame extends InputAdapter implements ApplicationListener {
 
	World world;
	Player player;

	OrthographicCamera cam;
	Box2DDebugRenderer renderer;
	Array<MovingPlatform> platforms = new Array<MovingPlatform>();

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
		float y1 = 1; //(float)Math.random() * 0.1f + 1;
		float y2 = y1;
		for(int i = 0; i < 50; i++) {
			Body ground = createEdge(BodyType.StaticBody, -50 + i * 2, y1, -50 + i * 2 + 2, y2, 0);			
			y1 = y2;
			y2 = (float)Math.random() + 1;
		}			
 */
		/*
		Body box = createBox(BodyType.StaticBody, 1, 1, 0);
		box.setTransform(30, 3, 0);
		box = createBox(BodyType.StaticBody, 1.2f, 1.2f, 0);
		box.setTransform(5, 2.4f, 0);
		*/
		
		player = new Player(world);
	
//		playerObject = player.getPlayerBody();
		
 /*
		for(int i = 0; i < 10; i++) {
			box = createBox(BodyType.DynamicBody, (float)Math.random(), (float)Math.random(), 3);
			box.setTransform((float)Math.random() * 10f - (float)Math.random() * 10f, (float)Math.random() * 10 + 6, (float)(Math.random() * 2 * Math.PI));
		}
 
		for(int i = 0; i < 10; i++) {
			Body circle = createCircle(BodyType.DynamicBody, (float)Math.random() * 0.5f, 3);
			circle.setTransform((float)Math.random() * 10f - (float)Math.random() * 10f, (float)Math.random() * 10 + 6, (float)(Math.random() * 2 * Math.PI));
		}
 */
		platforms.add(new MovingPlatform(world, -2, 3, 2, 0.5f, 2, 0, 4));
		platforms.add(new MovingPlatform(world, 17, 3, 5, 0.5f, 0, 2, 5));		
		platforms.add(new MovingPlatform(world, -7, 5, 2, 0.5f, -2, 2, 8));		
		platforms.add(new MovingPlatform(world, 40, 3, 20, 0.5f, 0, 2, 5));
		
	}
 
 
	@Override
	public void resume() {
 
	}
 
	@Override
	public void render() {
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		// Update the camera
		cam.position.set(player.getPosition().x, player.getPosition().y, 0);
		cam.update();
		cam.apply(Gdx.gl10);
		renderer.render(world, cam.combined);
 
		// Update the player position, etc
		player.update();
 
		// update platforms
		for(int i = 0; i < platforms.size; i++) {
			MovingPlatform platform = platforms.get(i);
			platform.update(Math.max(1/30.0f, Gdx.graphics.getDeltaTime()));
		}
 
		// le step...			
		world.step(Gdx.graphics.getDeltaTime(), 4, 4);
				
 
//		cam.project(point.set(pos.x, pos.y, 0));
//		batch.begin();
//		font.drawMultiLine(batch, "friction: " + playerPhysicsFixture.getFriction() + "\ngrounded: " + grounded, point.x+20, point.y);
//		batch.end();
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
