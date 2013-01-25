package nl.miraclethings.platformer;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
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
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class PlatformerGame implements ApplicationListener {
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private Texture texture;
	private Sprite sprite;
	
    private World world;
	private Box2DDebugRenderer debugRenderer;
	private Body ball;  

    static final float BOX_STEP=1/60f;  
    static final int BOX_VELOCITY_ITERATIONS=6;  
    static final int BOX_POSITION_ITERATIONS=2;  

	@Override
	public void create() {		
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		
		Gdx.input.setInputProcessor(new InputProcessor(this));

		camera = new OrthographicCamera(w, h);
		camera.position.set(w/2, h/2, 0);
		camera.update();
		
		batch = new SpriteBatch();
		
		texture = new Texture(Gdx.files.internal("data/ball.png"));
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		TextureRegion region = new TextureRegion(texture, 0, 0, texture.getWidth(), texture.getHeight());
		
		sprite = new Sprite(region);
		//sprite.setSize(0.9f, 0.9f * sprite.getHeight() / sprite.getWidth());
		sprite.setOrigin(sprite.getWidth()/2, sprite.getHeight()/2);
		
		world = new World(new Vector2(0, -200), true);
		
        //Ground body  
        BodyDef groundBodyDef =new BodyDef();  
        groundBodyDef.position.set(new Vector2(0, -10));  
        Body groundBody = world.createBody(groundBodyDef);  
        PolygonShape groundBox = new PolygonShape();  
        groundBox.setAsBox(camera.viewportWidth * 2, 10.0f);  
        groundBody.createFixture(groundBox, 0.0f);
        
        //Top body  
        groundBodyDef =new BodyDef();  
        groundBodyDef.position.set(new Vector2(0, camera.viewportHeight+10));  
        groundBody = world.createBody(groundBodyDef);  
        groundBox = new PolygonShape();  
        groundBox.setAsBox(camera.viewportWidth * 2, 10.0f);  
        groundBody.createFixture(groundBox, 0.0f);
        
        //Right body  
        groundBodyDef =new BodyDef();  
        groundBodyDef.position.set(new Vector2(camera.viewportWidth, 0));  
        groundBody = world.createBody(groundBodyDef);  
        groundBox = new PolygonShape();  
        groundBox.setAsBox(10.0f, camera.viewportHeight * 2);  
        groundBody.createFixture(groundBox, 0.0f);
        
        //Left body  
        groundBodyDef =new BodyDef();  
        groundBodyDef.position.set(new Vector2(0, 0));  
        groundBody = world.createBody(groundBodyDef);  
        groundBox = new PolygonShape();  
        groundBox.setAsBox(10.0f, camera.viewportHeight * 2);  
        groundBody.createFixture(groundBox, 0.0f);

        
        //Dynamic Body  
        BodyDef bodyDef = new BodyDef();  
        bodyDef.type = BodyType.DynamicBody;  
        bodyDef.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2);  
        ball = world.createBody(bodyDef);  
        
        CircleShape shape = new CircleShape();  
        shape.setRadius(32f);

        FixtureDef fixtureDef = new FixtureDef();    
        fixtureDef.shape = shape;  
        fixtureDef.density = 1f;  
        fixtureDef.friction = 0.0f;  
        fixtureDef.restitution = 0.95f;  
        ball.createFixture(fixtureDef);
        
        ball.applyAngularImpulse(1000000);
        
        debugRenderer = new Box2DDebugRenderer();  
		
       World.setVelocityThreshold(0.0f);
	}

	@Override
	public void dispose() {
		batch.dispose();
		texture.dispose();
	}

	@Override
	public void render() {		
		
		Gdx.gl.glClearColor(0, 0.7f, 0, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		Vector2 p = ball.getPosition();
		sprite.setPosition(p.x - sprite.getWidth()/2, p.y - sprite.getHeight()/2);
		sprite.setRotation((float)(ball.getAngle() * 180/Math.PI));
		sprite.draw(batch);
		batch.end();
		debugRenderer.render(world, camera.combined);
		world.step(BOX_STEP, BOX_VELOCITY_ITERATIONS, BOX_POSITION_ITERATIONS);  
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	public void randomMovement(int x, int y) {
		double r = 0.5 * Math.PI; //1.0; //Math.random() * Math.PI;
		float scale = 100000000000000f;
		ball.applyForceToCenter(new Vector2((float)(scale * Math.cos(r)), 
											(float)(scale * Math.sin(r))
											));
		//Gdx.app.log("MyTag", "my informative message");
		//
	}
	
	public void move(float dir) {
		Gdx.app.log("hello", "foo");
		float f = 100000000f * dir;
		Vector2 p = ball.getPosition();
		ball.applyLinearImpulse(f, 0f, p.x, p.y + 2); 
	}
}
