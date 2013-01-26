package nl.miraclethings.platformer;

import java.util.List;

import javax.swing.Box;
 
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.WorldManifold;
import com.badlogic.gdx.utils.Array;
 
public class PlatformerGame extends InputAdapter implements ApplicationListener {
 
	final static float MAX_VELOCITY = 7f;		
	boolean jump = false;	
	World world;
	Body player;
	Fixture playerPhysicsFixture;
	Fixture playerSensorFixture;
	OrthographicCamera cam;
	Box2DDebugRenderer renderer;
	Array<MovingPlatform> platforms = new Array<MovingPlatform>();
	MovingPlatform groundedPlatform = null;
	float stillTime = 0;
	long lastGroundTime = 0;
	SpriteBatch batch;
	BitmapFont font;
 
	@Override
	public void create() {
		world = new World(new Vector2(0, -20), true);		
		renderer = new Box2DDebugRenderer();
		cam = new OrthographicCamera(28, 20);
		createWorld();
		Gdx.input.setInputProcessor(this);
		batch = new SpriteBatch();
		font = new BitmapFont();
	}
 
	private void createWorld() {
		
		WorldLoader loader = new WorldLoader("data/world.svg");
		float scale = 0.02f;
		for (Rectangle rect : loader.rectangles) {
			Body box = createBox(BodyType.StaticBody, 0.5f*rect.width*scale, 0.5f*rect.height*scale, 0f);
			box.setTransform((rect.x+rect.width*0.5f)*scale, (1000+rect.y+rect.height*0.5f)*scale, 0f);
		} 
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
		
		player = createPlayer();
		player.setTransform(10.0f, 4.0f, 0);
		player.setFixedRotation(true);						
 /*
		for(int i = 0; i < 10; i++) {
			box = createBox(BodyType.DynamicBody, (float)Math.random(), (float)Math.random(), 3);
			box.setTransform((float)Math.random() * 10f - (float)Math.random() * 10f, (float)Math.random() * 10 + 6, (float)(Math.random() * 2 * Math.PI));
		}
 
		for(int i = 0; i < 10; i++) {
			Body circle = createCircle(BodyType.DynamicBody, (float)Math.random() * 0.5f, 3);
			circle.setTransform((float)Math.random() * 10f - (float)Math.random() * 10f, (float)Math.random() * 10 + 6, (float)(Math.random() * 2 * Math.PI));
		}
 
		platforms.add(new MovingPlatform(-2, 3, 2, 0.5f, 2, 0, 4));
		platforms.add(new MovingPlatform(17, 3, 5, 0.5f, 0, 2, 5));		
		platforms.add(new MovingPlatform(-7, 5, 2, 0.5f, -2, 2, 8));		
		platforms.add(new MovingPlatform(40, 3, 20, 0.5f, 0, 2, 5));
		*/
	}
 
	private Body createBox(BodyType type, float width, float height, float density) {
		BodyDef def = new BodyDef();
		def.type = type;
		Body box = world.createBody(def);
 
		PolygonShape poly = new PolygonShape();
		poly.setAsBox(width, height);
		box.createFixture(poly, density);
		poly.dispose();
 
		return box;
	}	
 
	private Body createEdge(BodyType type, float x1, float y1, float x2, float y2, float density) {
		BodyDef def = new BodyDef();
		def.type = type;
		Body box = world.createBody(def);
 
		EdgeShape edge = new EdgeShape();
		edge.set(new Vector2(0, 0), new Vector2(x2 - x1, y2 - y1));
		box.createFixture(edge, density);
		box.setTransform(x1, y1, 0);
		edge.dispose();
 
		return box;
	}
 
	private Body createCircle(BodyType type, float radius, float density) {
		BodyDef def = new BodyDef();
		def.type = type;
		Body box = world.createBody(def);
 
		CircleShape poly = new CircleShape();
		poly.setRadius(radius);
		box.createFixture(poly, density);
		poly.dispose();
 
		return box;
	}	
 
	private Body createPlayer() {
		BodyDef def = new BodyDef();
		def.type = BodyType.DynamicBody;
		Body box = world.createBody(def);
 
		PolygonShape poly = new PolygonShape();		
		poly.setAsBox(0.45f, 1.4f);
		playerPhysicsFixture = box.createFixture(poly, 1);
		poly.dispose();			
 
		CircleShape circle = new CircleShape();		
		circle.setRadius(0.45f);
		circle.setPosition(new Vector2(0, -1.4f));
		playerSensorFixture = box.createFixture(circle, 0);		
		circle.dispose();		
 
		box.setBullet(true);
 
		return box;
	}
 
	@Override
	public void resume() {
 
	}
 
	@Override
	public void render() {
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		cam.position.set(player.getPosition().x, player.getPosition().y, 0);
		cam.update();
		cam.apply(Gdx.gl10);
		renderer.render(world, cam.combined);
 
		Vector2 vel = player.getLinearVelocity();
		Vector2 pos = player.getPosition();		
		boolean grounded = isPlayerGrounded(Gdx.graphics.getDeltaTime());
		if(grounded) {
			lastGroundTime = System.nanoTime();
		} else {
			if(System.nanoTime() - lastGroundTime < 100000000) {
				grounded = true;
			}
		}
 
		// cap max velocity on x		
		if(Math.abs(vel.x) > MAX_VELOCITY) {			
			vel.x = Math.signum(vel.x) * MAX_VELOCITY;
			player.setLinearVelocity(vel.x, vel.y);
		}
 
		// calculate stilltime & damp
		if(!Gdx.input.isKeyPressed(Keys.A) && !Gdx.input.isKeyPressed(Keys.D)) {			
			stillTime += Gdx.graphics.getDeltaTime();
			player.setLinearVelocity(vel.x * 0.9f, vel.y);
		}
		else { 
			stillTime = 0;
		}			
 
		// disable friction while jumping
		if(!grounded) {			
			playerPhysicsFixture.setFriction(0f);
			playerSensorFixture.setFriction(0f);			
		} else {
			if(!Gdx.input.isKeyPressed(Keys.A) && !Gdx.input.isKeyPressed(Keys.D) && stillTime > 0.2) {
				playerPhysicsFixture.setFriction(10f);
				playerSensorFixture.setFriction(10f);
				if(groundedPlatform != null) { // && groundedPlatform.dist == 0) {
					Gdx.app.log("xx",  "group " + groundedPlatform.dir.x);
					player.setLinearVelocity(groundedPlatform.dir.x,  groundedPlatform.dir.y);
					//player.applyLinearImpulse(10 * groundedPlatform.dir.x,  0, player.getPosition().x, player.getPosition().y);				
				}
			}
			else {
				playerPhysicsFixture.setFriction(0.2f);
				playerSensorFixture.setFriction(0.2f);
			}
 
		}		
 
		// apply left impulse, but only if max velocity is not reached yet
		if(Gdx.input.isKeyPressed(Keys.A) && vel.x > -MAX_VELOCITY) {
			player.applyLinearImpulse(-2f, 0, pos.x, pos.y);
		}
 
		// apply right impulse, but only if max velocity is not reached yet
		if(Gdx.input.isKeyPressed(Keys.D) && vel.x < MAX_VELOCITY) {
			player.applyLinearImpulse(2f, 0, pos.x, pos.y);
		}
 
		// jump, but only when grounded
		if(jump) {			
			jump = false;
			if(grounded) {
				player.setLinearVelocity(vel.x, 0);			
				System.out.println("jump before: " + player.getLinearVelocity());
				player.setTransform(pos.x, pos.y + 0.01f, 0);
				player.applyLinearImpulse(0, 30, pos.x, pos.y);			
				System.out.println("jump, " + player.getLinearVelocity());				
			}
		}					
 
		// update platforms
		for(int i = 0; i < platforms.size; i++) {
			MovingPlatform platform = platforms.get(i);
			platform.update(Math.max(1/30.0f, Gdx.graphics.getDeltaTime()));
		}
 
		// le step...			
		world.step(Gdx.graphics.getDeltaTime(), 4, 4);
		player.setAwake(true);		
 
		cam.project(point.set(pos.x, pos.y, 0));
		batch.begin();
		font.drawMultiLine(batch, "friction: " + playerPhysicsFixture.getFriction() + "\ngrounded: " + grounded, point.x+20, point.y);
		batch.end();
	}	
 
	private boolean isPlayerGrounded(float deltaTime) {				
		groundedPlatform = null;
		List<Contact> contactList = world.getContactList();
		for(int i = 0; i < contactList.size(); i++) {
			Contact contact = contactList.get(i);
			if(contact.isTouching() && (contact.getFixtureA() == playerSensorFixture ||
			   contact.getFixtureB() == playerSensorFixture)) {				
 
				Vector2 pos = player.getPosition();
				WorldManifold manifold = contact.getWorldManifold();
				boolean below = true;
				for(int j = 0; j < manifold.getNumberOfContactPoints(); j++) {
					below &= (manifold.getPoints()[j].y < pos.y - 1.5f);
				}
 
				if(below) {
					if(contact.getFixtureA().getUserData() != null && contact.getFixtureA().getUserData().equals("p")) {
						groundedPlatform = (MovingPlatform)contact.getFixtureA().getBody().getUserData();							
					}
 
					if(contact.getFixtureB().getUserData() != null && contact.getFixtureB().getUserData().equals("p")) {
						groundedPlatform = (MovingPlatform)contact.getFixtureB().getBody().getUserData();
					}											
					return true;			
				}
 
				return false;
			}
		}
		return false;
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
		if(keycode == Keys.W) jump = true;
		return false;
	}
 
	@Override
	public boolean keyUp(int keycode) {
		if(keycode == Keys.W) jump = false;
		return false;
	}
 
	Vector2 last = null;
	Vector3 point = new Vector3();
 
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
 
	class MovingPlatform {
		Body platform;		
		Vector2 pos = new Vector2();
		Vector2 dir = new Vector2();
		float dist = 0;
		float maxDist = 0;		
 
		public MovingPlatform(float x, float y, float width, float height, float dx, float dy, float maxDist) {
			platform = createBox(BodyType.KinematicBody, width, height, 1);			
			pos.x = x;
			pos.y = y;
			dir.x = dx;
			dir.y = dy;
			this.maxDist = maxDist;
			platform.setTransform(pos, 0);
			platform.getFixtureList().get(0).setUserData("p");
			platform.setUserData(this);
		}
 
		public void update(float deltaTime) {
			dist += dir.len() * deltaTime;
			if(dist > maxDist) {
				dir.mul(-1);
				dist = 0;
			}
 
			platform.setLinearVelocity(dir);			
		}
	}
}
